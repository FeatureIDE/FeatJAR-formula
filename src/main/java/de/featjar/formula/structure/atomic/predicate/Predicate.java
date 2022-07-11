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
package de.featjar.formula.structure.atomic.predicate;

import java.util.Arrays;
import java.util.List;

import de.featjar.formula.structure.NonTerminal;
import de.featjar.formula.structure.atomic.Atomic;
import de.featjar.formula.structure.term.Term;

public abstract class Predicate extends NonTerminal implements Atomic {

	protected Predicate(List<Term> nodes) {
		super(nodes);
	}

	@SafeVarargs
	protected Predicate(Term... nodes) {
		super(nodes);
	}

	protected Predicate() {
		super();
	}

	public void setArguments(Term leftArgument, Term rightArgument) {
		setChildren(Arrays.asList(leftArgument, rightArgument));
	}

	@Override
	public String getName() {
		return "=";
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<? extends Term> getChildren() {
		return (List<? extends Term>) super.getChildren();
	}

	@Override
	public abstract Predicate flip();

}
