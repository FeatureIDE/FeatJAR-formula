/* -----------------------------------------------------------------------------
 * formula - Propositional and first-order formulas
 * Copyright (C) 2020 Sebastian Krieter
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
 * See <https://github.com/FeatJAR/formula> for further information.
 * -----------------------------------------------------------------------------
 */
package de.featjar.clauses;

import java.util.Comparator;
import java.util.List;

/**
 * Compares list of clauses by he number of literals.
 *
 * @author Sebastian Krieter
 */
public class ClauseListLengthComparatorDsc implements Comparator<List<LiteralList>> {

	@Override
	public int compare(List<LiteralList> o1, List<LiteralList> o2) {
		return addLengths(o2) - addLengths(o1);
	}

	protected int addLengths(List<LiteralList> o) {
		int count = 0;
		for (final LiteralList literalSet : o) {
			count += literalSet.getLiterals().length;
		}
		return count;
	}

}
