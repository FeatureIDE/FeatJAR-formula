/*
 * Copyright (C) 2023 Sebastian Krieter, Elias Kuiter
 *
 * This file is part of formula.
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

import de.featjar.formula.structure.atomic.literal.VariableMap;
import de.featjar.util.io.NonEmptyLineIterator;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ADimacsReader<T> {

    protected static final Pattern commentPattern = Pattern.compile("\\A" + DIMACSConstants.COMMENT + "\\s*(.*)\\Z");
    protected static final Pattern problemPattern = Pattern.compile(
            "\\A\\s*" + DIMACSConstants.PROBLEM + "\\s+" + DIMACSConstants.CNF + "\\s+(\\d+)\\s+(\\d+)");

    /** Maps indexes to variables. */
    protected final Map<Integer, String> indexVariables = new LinkedHashMap<>();

    protected VariableMap map;

    /**
     * The amount of variables as declared in the problem definition. May differ
     * from the actual amount of variables found.
     */
    protected int variableCount;
    /** The amount of clauses in the problem. */
    protected int clauseCount;
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
     * @param in The source to read from.
     * @return a CNF; not null
     * @throws IOException    if the reader encounters a problem.
     * @throws ParseException if the input does not conform to the DIMACS CNF file
     *                        format
     */
    public T read(Reader in) throws ParseException, IOException {
        indexVariables.clear();
        variableCount = -1;
        clauseCount = -1;
        readingVariables = readVariableDirectory;
        if (!readVariableDirectory) {
            map = new VariableMap();
        }
        try (final BufferedReader reader = new BufferedReader(in)) {
            final NonEmptyLineIterator nonemptyLineIterator = new NonEmptyLineIterator(reader);
            nonemptyLineIterator.get();

            readComments(nonemptyLineIterator);
            readProblem(nonemptyLineIterator);
            readComments(nonemptyLineIterator);
            readingVariables = false;

            if (readVariableDirectory) {
                for (int i = 1; i <= variableCount; i++) {
                    indexVariables.putIfAbsent(i, Integer.toString(i));
                }
                map = new VariableMap();
                indexVariables.forEach((i, n) -> map.addBooleanVariable(n, i));
            }

            return get(nonemptyLineIterator);
        }
    }

    protected abstract T get(final NonEmptyLineIterator nonemptyLineIterator) throws ParseException, IOException;

    protected void checkVariableCount(final int actualVariableCount) throws ParseException {
        if (variableCount != actualVariableCount) {
            throw new ParseException(
                    String.format("Found %d instead of %d variables", actualVariableCount, variableCount), 1);
        }
    }

    protected void checkClauseCount(final int actualClauseCount) throws ParseException {
        if (clauseCount != actualClauseCount) {
            throw new ParseException(
                    String.format("Found %d instead of %d clauses", actualClauseCount, clauseCount), 1);
        }
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
     * Reads the input. Calls {@link #read(Reader)}.
     *
     * @param in The string to read from.
     * @return a CNF; not null
     * @throws IOException    if the reader encounters a problem.
     * @throws ParseException if the input does not conform to the DIMACS CNF file
     *                        format
     */
    public T read(String in) throws ParseException, IOException {
        return read(new StringReader(in));
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
        if (!indexVariables.containsKey(index)) {
            indexVariables.put(index, variable);
        }
        return true;
    }
}
