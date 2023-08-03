/*
 * Copyright (C) 2023 FeatJAR-Development-Team
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
 * See <https://github.com/FeatJAR> for further information.
 */
package de.featjar.formula.io.dimacs;

import de.featjar.base.io.format.IFormat;
import de.featjar.formula.analysis.bool.BooleanClauseList;

/**
 * Serializes a {@link BooleanClauseList} to a String in DIMACS format.
 *
 * @author Sebastian Krieter
 * @deprecated
 */
@Deprecated
public class DIMACSCNFFormat implements IFormat<BooleanClauseList> {

    //    @Override
    //    public Result<String> serialize(BooleanClauseList cnf) {
    //        Objects.requireNonNull(cnf);
    //
    //        final StringBuilder sb = new StringBuilder();
    //
    //        // Variables
    //        int index = 1;
    //        for (final String name : cnf.getVariableMap().getVariableNames()) {
    //            sb.append(DIMACSConstants.COMMENT_START);
    //            sb.append(index++);
    //            sb.append(' ');
    //            sb.append(name);
    //            sb.append(System.lineSeparator());
    //        }
    //
    //        // Problem
    //        sb.append(DIMACSConstants.PROBLEM);
    //        sb.append(' ');
    //        sb.append(DIMACSConstants.CNF);
    //        sb.append(' ');
    //        sb.append(cnf.getVariableMap().getVariableNames().size());
    //        sb.append(' ');
    //        sb.append(cnf.size());
    //        sb.append(System.lineSeparator());
    //
    //        // Clauses
    //        for (final BooleanClause LiteralClause : cnf.getAll()) {
    //            for (final int l : LiteralClause.getIntegers()) {
    //                sb.append(l);
    //                sb.append(' ');
    //            }
    //            sb.append(DIMACSConstants.CLAUSE_END);
    //            sb.append(System.lineSeparator());
    //        }
    //
    //        return Result.of(sb.toString());
    //    }

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
