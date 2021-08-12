/* -----------------------------------------------------------------------------
 * Formula Lib - Library to represent and edit propositional formulas.
 * Copyright (C) 2021  Sebastian Krieter
 * 
 * This file is part of Formula Lib.
 * 
 * Formula Lib is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Formula Lib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Formula Lib.  If not, see <https://www.gnu.org/licenses/>.
 * 
 * See <https://github.com/skrieter/formula> for further information.
 * -----------------------------------------------------------------------------
 */
package org.spldev.formula.expression.atomic.predicate;

import java.util.*;

import org.spldev.formula.expression.*;
import org.spldev.formula.expression.atomic.*;
import org.spldev.formula.expression.term.*;

public abstract class Predicate<D> extends NonTerminal implements Atomic {

	protected Predicate(Collection<Term<D>> nodes) {
		super(nodes);
	}

	@SafeVarargs
	protected Predicate(Term<D>... nodes) {
		super(nodes);
	}

	protected Predicate() {
		super();
	}

	public void setArguments(Term<D> leftArgument, Term<D> rightArgument) {
		setChildren(Arrays.asList(leftArgument, rightArgument));
	}

	@Override
	public String getName() {
		return "=";
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<? extends Term<D>> getChildren() {
		return (List<? extends Term<D>>) super.getChildren();
	}

	public abstract Optional<Boolean> eval(List<D> values);

	@Override
	public abstract Predicate<D> flip();
}
