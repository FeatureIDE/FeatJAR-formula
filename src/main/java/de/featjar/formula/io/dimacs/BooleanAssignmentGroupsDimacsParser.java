/*
 * Copyright (C) 2024 FeatJAR-Development-Team
 *
 * This file is part of FeatJAR-formula.
 *
 * formula is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3.0 of the License,
 * or (at your option) any later version.
 *
 * formula is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with formula. If not, see <https://www.gnu.org/licenses/>.
 *
 * See <https://github.com/FeatureIDE/FeatJAR-formula> for further information.
 */
package de.featjar.formula.io.dimacs;

import de.featjar.base.io.NonEmptyLineIterator;
import de.featjar.formula.VariableMap;
import de.featjar.formula.assignment.BooleanAssignmentGroups;
import de.featjar.formula.assignment.BooleanClause;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BooleanAssignmentGroupsDimacsParser {

    private static final Pattern commentPattern = Pattern.compile("\\A" + DimacsConstants.COMMENT + "\\s*(.*)\\Z");
    private static final Pattern problemPattern = Pattern.compile(
            "\\A\\s*" + DimacsConstants.PROBLEM + "\\s+" + DimacsConstants.CNF + "\\s+(\\d+)\\s+(\\d+)");

    /** Maps indexes to variables. */
    private final VariableMap indexVariables = new VariableMap();

    /**
     * The amount of variables as declared in the problem definition. May differ
     * from the actual amount of found variables.
     */
    private int variableCount;
    /** The amount of clauses in the problem. */
    private int clauseCount;
    /** True to read the variable directory for naming variables. */
    private boolean readVariableDirectory = false;
    /**
     * True when currently reading the comment section at the beginning of the file
     * and parsing variable names.
     */
    private boolean readingVariables;

    /**
     * <p>
     * Sets the reading variable directory flag. If true, the reader will look for a
     * variable directory in the comments. This contains names for the variables
     * which would otherwise just be numbers.
     * </p>
     *
     * <p>
     * Defaults to false.
     * </p>
     *
     * @param readVariableDirectory whether to read the variable directory
     */
    public void setReadingVariableDirectory(boolean readVariableDirectory) {
        this.readVariableDirectory = readVariableDirectory;
    }

    /**
     * Reads the input.
     *
     * @param nonEmptyLineIterator The source to read from.
     * @return a CNF; not null
     * @throws IOException    if the reader encounters a problem.
     * @throws ParseException if the input does not conform to the DIMACS CNF file
     *                        format
     */
    public BooleanAssignmentGroups parse(NonEmptyLineIterator nonEmptyLineIterator) throws ParseException, IOException {
        indexVariables.clear();
        variableCount = -1;
        clauseCount = -1;
        readingVariables = readVariableDirectory;
        nonEmptyLineIterator.get();

        readComments(nonEmptyLineIterator);
        readProblem(nonEmptyLineIterator);
        readComments(nonEmptyLineIterator);
        readingVariables = false;

        if (readVariableDirectory) {
            for (int i = 1; i <= variableCount; i++) {
                if (!indexVariables.has(i)) {
                    indexVariables.add(i, getUniqueName(i));
                }
            }
        }

        final List<BooleanClause> clauses = readClauses(nonEmptyLineIterator);
        final int actualVariableCount = indexVariables.getVariableCount();
        final int actualClauseCount = clauses.size();
        if (variableCount != actualVariableCount) {
            throw new ParseException(
                    String.format("Found %d instead of %d variables", actualVariableCount, variableCount), 1);
        }
        if (clauseCount != actualClauseCount) {
            throw new ParseException(
                    String.format("Found %d instead of %d clauses", actualClauseCount, clauseCount), 1);
        }
        return new BooleanAssignmentGroups(indexVariables, List.of(clauses));
    }

    private String getUniqueName(int i) {
        String indexName = Integer.toString(i);
        String name = indexName;
        int suffix = 2;
        while (indexVariables.has(name)) {
            name = indexName + "_" + suffix;
            suffix++;
        }
        return name;
    }

    private void readComments(final NonEmptyLineIterator nonemptyLineIterator) {
        for (String line = nonemptyLineIterator.currentLine(); line != null; line = nonemptyLineIterator.get()) {
            final Matcher matcher = commentPattern.matcher(line);
            if (matcher.matches()) {
                readComment(matcher.group(1)); // read comments ...
            } else {
                break; // ... until a non-comment token is found.
            }
        }
    }

    /**
     * Reads the problem definition.
     *
     * @throws ParseException if the input does not conform to the DIMACS CNF file
     *                        format
     */
    private void readProblem(NonEmptyLineIterator nonemptyLineIterator) throws ParseException {
        final String line = nonemptyLineIterator.currentLine();
        if (line == null) {
            throw new ParseException("Invalid problem format", nonemptyLineIterator.getLineCount());
        }
        final Matcher matcher = problemPattern.matcher(line);
        if (!matcher.find()) {
            throw new ParseException("Invalid problem format", nonemptyLineIterator.getLineCount());
        }
        final String trail = line.substring(matcher.end());
        if (trail.trim().isEmpty()) {
            nonemptyLineIterator.get();
        } else {
            nonemptyLineIterator.setCurrentLine(trail);
        }

        try {
            variableCount = Integer.parseInt(matcher.group(1));
        } catch (final NumberFormatException e) {
            throw new ParseException("Variable count is not an integer", nonemptyLineIterator.getLineCount());
        }
        if (variableCount < 0) {
            throw new ParseException("Variable count is not positive", nonemptyLineIterator.getLineCount());
        }

        try {
            clauseCount = Integer.parseInt(matcher.group(2));
        } catch (final NumberFormatException e) {
            throw new ParseException("Clause count is not an integer", nonemptyLineIterator.getLineCount());
        }
        if (clauseCount < 0) {
            throw new ParseException("Clause count is not positive", nonemptyLineIterator.getLineCount());
        }
    }

    /**
     * Reads all clauses.
     *
     * @return all clauses; not null
     * @throws ParseException if the input does not conform to the DIMACS CNF file
     *                        format
     */
    private List<BooleanClause> readClauses(NonEmptyLineIterator nonemptyLineIterator) throws ParseException {
        final LinkedList<String> literalQueue = new LinkedList<>();
        final List<BooleanClause> clauses = new ArrayList<>(clauseCount);
        int readClausesCount = 0;
        for (String line = nonemptyLineIterator.currentLine(); line != null; line = nonemptyLineIterator.get()) {
            if (commentPattern.matcher(line).matches()) {
                continue;
            }
            List<String> literalList = Arrays.asList(line.trim().split("\\s+"));
            literalQueue.addAll(literalList);

            do {
                final int clauseEndIndex = literalList.indexOf("0");
                if (clauseEndIndex < 0) {
                    break;
                }
                final int clauseSize = literalQueue.size() - (literalList.size() - clauseEndIndex);
                if (clauseSize < 0) {
                    throw new ParseException("Invalid clause", nonemptyLineIterator.getLineCount());
                } else if (clauseSize == 0) {
                    clauses.add(new BooleanClause());
                } else {
                    clauses.add(parseClause(readClausesCount, clauseSize, literalQueue, nonemptyLineIterator));
                }
                readClausesCount++;

                if (!DimacsConstants.CLAUSE_END.equals(literalQueue.removeFirst())) {
                    throw new ParseException("Illegal clause end", nonemptyLineIterator.getLineCount());
                }
                literalList = literalQueue;
            } while (!literalQueue.isEmpty());
        }
        if (!literalQueue.isEmpty()) {
            clauses.add(parseClause(readClausesCount, literalQueue.size(), literalQueue, nonemptyLineIterator));
            readClausesCount++;
        }
        if (readClausesCount < clauseCount) {
            throw new ParseException(String.format("Found %d instead of %d clauses", readClausesCount, clauseCount), 1);
        }
        return clauses;
    }

    private BooleanClause parseClause(
            int readClausesCount,
            int clauseSize,
            LinkedList<String> literalQueue,
            NonEmptyLineIterator nonemptyLineIterator)
            throws ParseException {
        if (readClausesCount == clauseCount) {
            throw new ParseException(String.format("Found more than %d clauses", clauseCount), 1);
        }
        final int[] literals = new int[clauseSize];
        for (int j = 0; j < literals.length; j++) {
            final String token = literalQueue.removeFirst();
            final int index;
            try {
                index = Integer.parseInt(token);
            } catch (final NumberFormatException e) {
                throw new ParseException("Illegal literal", nonemptyLineIterator.getLineCount());
            }
            if (index == 0) {
                throw new ParseException("Illegal literal", nonemptyLineIterator.getLineCount());
            }
            final int key = Math.abs(index);
            if (!indexVariables.has(key)) {
                indexVariables.add(key, getUniqueName(key));
            }
            literals[j] = index;
        }
        return new BooleanClause(literals);
    }

    /**
     * Called when a comment is read.
     *
     * @param comment content of the comment; not null
     * @return whether the comment was consumed logically
     */
    private boolean readComment(String comment) {
        return readingVariables && readVariableDirectoryEntry(comment);
    }

    /**
     * Reads an entry of the variable directory.
     *
     * @param comment variable directory entry
     * @return true if an entry was found
     */
    private boolean readVariableDirectoryEntry(String comment) {
        final int firstSeparator = comment.indexOf(' ');
        if (firstSeparator <= 0) {
            return false;
        }
        final int index;
        try {
            index = Integer.parseInt(comment.substring(0, firstSeparator));
        } catch (final NumberFormatException e) {
            return false;
        }
        if (comment.length() < (firstSeparator + 2)) {
            return false;
        }
        final String variable = comment.substring(firstSeparator + 1);
        if (!indexVariables.has(index)) {
            indexVariables.add(index, variable);
        }
        return true;
    }
}
