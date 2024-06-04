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

import de.featjar.base.data.Result;
import de.featjar.base.io.format.IFormat;
import de.featjar.formula.assignment.BooleanClause;
import de.featjar.formula.assignment.BooleanClauseList;
import java.util.Objects;

/**
 * Serializes a {@link BooleanClauseList} to a String in DIMACS format.
 *
 * @author Sebastian Krieter
 */
public class CnfDimacsFormat implements IFormat<BooleanClauseList> {

    @Override
    public Result<String> serialize(BooleanClauseList cnf) {
        Objects.requireNonNull(cnf);

        final StringBuilder sb = new StringBuilder();

        // Problem
        sb.append(DimacsConstants.PROBLEM);
        sb.append(' ');
        sb.append(DimacsConstants.CNF);
        sb.append(' ');
        sb.append(cnf.getVariableCount());
        sb.append(' ');
        sb.append(cnf.size());
        sb.append(System.lineSeparator());

        // Clauses
        for (final BooleanClause clause : cnf.getAll()) {
            for (final int l : clause.get()) {
                sb.append(l);
                sb.append(' ');
            }
            sb.append(DimacsConstants.CLAUSE_END);
            sb.append(System.lineSeparator());
        }

        return Result.of(sb.toString());
    }

    @Override
    public boolean supportsSerialize() {
        return true;
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
