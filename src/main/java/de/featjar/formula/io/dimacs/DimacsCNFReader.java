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

import de.featjar.clauses.CNF;
import de.featjar.clauses.LiteralList;
import de.featjar.util.io.NonEmptyLineIterator;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class DimacsCNFReader extends ADimacsReader<CNF> {

    @Override
    protected CNF get(final NonEmptyLineIterator nonemptyLineIterator) throws ParseException, IOException {
        final List<LiteralList> clauses = readClauses(nonemptyLineIterator);
        checkVariableCount(indexVariables.size());
        checkClauseCount(clauses.size());
        return new CNF(map, clauses);
    }

    /**
     * Reads all clauses.
     *
     * @return all clauses; not null
     * @throws ParseException if the input does not conform to the DIMACS CNF file
     *                        format
     * @throws IOException
     */
    private List<LiteralList> readClauses(NonEmptyLineIterator nonemptyLineIterator)
            throws ParseException, IOException {
        final LinkedList<String> literalQueue = new LinkedList<>();
        final List<LiteralList> clauses = new ArrayList<>(clauseCount);
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
                    clauses.add(new LiteralList());
                } else {
                    clauses.add(parseClause(readClausesCount, clauseSize, literalQueue, nonemptyLineIterator));
                }
                readClausesCount++;

                if (!DIMACSConstants.CLAUSE_END.equals(literalQueue.removeFirst())) {
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

    private LiteralList parseClause(
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
            final Integer key = Math.abs(index);
            String variableName = indexVariables.get(key);
            if (variableName == null) {
                variableName = String.valueOf(key);
                indexVariables.put(key, variableName);
            }
            if (map.getVariableIndex(variableName).isEmpty()) {
                map.addBooleanVariable(variableName);
            }
            literals[j] = index;
        }
        return new LiteralList(literals);
    }
}
