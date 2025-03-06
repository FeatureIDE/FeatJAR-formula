/*
 * Copyright (C) 2025 FeatJAR-Development-Team
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

import de.featjar.base.data.Result;
import de.featjar.formula.VariableMap;
import java.util.Collection;
import java.util.function.Function;

/**
 * Generic serializer for the DIMACS format.
 *
 * @author Sebastian Krieter
 */
public class DimacsSerializer {
    /** Token leading a (single-line) comment. */
    public static final String COMMENT = "c";

    /** Token leading the problem definition. */
    public static final String PROBLEM = "p";
    /** Token identifying the problem type as CNF. */
    public static final String CNF = "cnf";
    /** Token denoting the end of a clause. */
    public static final String CLAUSE_END = "0";

    public static <C> Result<String> serialize(
            VariableMap variableMap, Collection<C> clauses, Function<C, int[]> serializer) {
        final StringBuilder sb = new StringBuilder();
        writeVariables(sb, variableMap);
        writeProblem(sb, variableMap.getVariableCount(), clauses.size());
        writeClauses(sb, clauses, serializer);
        return Result.of(sb.toString());
    }

    public static <C> void writeClauses(final StringBuilder sb, Collection<C> clauses, Function<C, int[]> serializer) {
        for (final C clause : clauses) {
            for (final int l : serializer.apply(clause)) {
                sb.append(l);
                sb.append(' ');
            }
            sb.append(CLAUSE_END);
            sb.append(System.lineSeparator());
        }
    }

    public static void writeProblem(final StringBuilder sb, int variableCount, int clauseCount) {
        sb.append(PROBLEM);
        sb.append(' ');
        sb.append(CNF);
        sb.append(' ');
        sb.append(variableCount);
        sb.append(' ');
        sb.append(clauseCount);
        sb.append(System.lineSeparator());
    }

    public static void writeVariables(final StringBuilder sb, VariableMap variableMap) {
        variableMap.stream().forEach(e -> {
            if (e.getValue() != null) {
                sb.append(COMMENT);
                sb.append(' ');
                sb.append(e.getKey());
                sb.append(' ');
                sb.append(e.getValue());
                sb.append(System.lineSeparator());
            }
        });
    }
}
