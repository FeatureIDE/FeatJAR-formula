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

import java.io.*;
import java.util.*;

import de.featjar.clauses.LiteralList.*;
import de.featjar.formula.structure.atomic.literal.VariableMap;
import de.featjar.util.data.Result;
import de.featjar.formula.structure.atomic.literal.*;
import de.featjar.util.data.*;

/**
 * Represents an instance of a satisfiability problem in CNF.
 *
 * @author Sebastian Krieter
 */
public class ClauseList extends ArrayList<LiteralList> implements Cloneable, Serializable {

	private static final long serialVersionUID = -4298323253677967328L;

	public ClauseList() {
		super();
	}

	public ClauseList(int size) {
		super(size);
	}

	public ClauseList(Collection<? extends LiteralList> c) {
		super(c);
	}

	public ClauseList(ClauseList otherClauseList) {
		super(otherClauseList.size());
		otherClauseList.stream().map(LiteralList::clone).forEach(this::add);
	}

	@Override
	public ClauseList clone() {
		return new ClauseList(this);
	}

	/**
	 * Negates all clauses in the list (applies De Morgan).
	 *
	 * @return A newly construct {@code ClauseList}.
	 */
	public ClauseList negate() {
		final ClauseList negatedClauseList = new ClauseList();
		stream().map(LiteralList::negate).forEach(negatedClauseList::add);
		return negatedClauseList;
	}

	public Result<ClauseList> adapt(VariableMap oldVariableMap, VariableMap newVariableMap) {
		final ClauseList adaptedClauseList = new ClauseList();
		for (final LiteralList clause : this) {
			final Result<LiteralList> adapted = clause.adapt(oldVariableMap, newVariableMap);
			if (adapted.isEmpty()) {
				return Result.empty(adapted.getProblems());
			}
			adaptedClauseList.add(adapted.get());
		}
		return Result.of(adaptedClauseList);
	}

	/**
	 * Converts CNF to DNF and vice-versa.
	 *
	 * @return A newly construct {@code ClauseList}.
	 */
	public ClauseList convert() {
		final ClauseList convertedClauseList = new ClauseList();
		convert(this, convertedClauseList, new int[size()], 0);
		return convertedClauseList;
	}

	private void convert(ClauseList nf1, ClauseList nf2, int[] literals, int index) {
		if (index == nf1.size()) {
			final LiteralList literalSet = new LiteralList(literals, Order.UNORDERED, false).clean().get();
			if (literalSet != null) {
				nf2.add(literalSet);
			}
		} else {
			for (final int literal : nf1.get(index).getLiterals()) {
				literals[index] = literal;
				convert(nf1, nf2, literals, index + 1);
			}
		}
	}

}
