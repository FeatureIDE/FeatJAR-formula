/*
 * Copyright (C) 2022 Sebastian Krieter, Elias Kuiter
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

import de.featjar.base.io.format.Format;
import de.featjar.formula.analysis.bool.BooleanAssignmentList;
import de.featjar.formula.analysis.bool.BooleanClause;
import de.featjar.formula.analysis.bool.BooleanClauseList;

import java.util.Objects;

/**
 * Serializes a {@link CNF} to a String in DIMACS format.
 *
 * @author Sebastian Krieter
 */
public class DIMACSCNFFormat implements Format<BooleanClauseList> {

    @Override
    public String serialize(BooleanClauseList cnf) {
        Objects.requireNonNull(cnf);

        final StringBuilder sb = new StringBuilder();

        // Variables
        int index = 1;
        for (final String name : cnf.getVariableMap().getVariableNames()) {
            sb.append(DIMACSConstants.COMMENT_START);
            sb.append(index++);
            sb.append(' ');
            sb.append(name);
            sb.append(System.lineSeparator());
        }

        // Problem
        sb.append(DIMACSConstants.PROBLEM);
        sb.append(' ');
        sb.append(DIMACSConstants.CNF);
        sb.append(' ');
        sb.append(cnf.getVariableMap().getVariableNames().size());
        sb.append(' ');
        sb.append(cnf.size());
        sb.append(System.lineSeparator());

        // Clauses
        for (final BooleanClause LiteralClause : cnf.getAll()) {
            for (final int l : LiteralClause.getIntegers()) {
                sb.append(l);
                sb.append(' ');
            }
            sb.append(DIMACSConstants.CLAUSE_END);
            sb.append(System.lineSeparator());
        }

        return sb.toString();
    }

    @Override
    public boolean supportsSerialize() {
        return true;
    }

    @Override
    public DIMACSCNFFormat getInstance() {
        return this;
    }

    @Override
    public String getName() {
        return "DIMACS";
    }

    @Override
    public String getFileExtension() {
        return "dimacs";
    }
}
