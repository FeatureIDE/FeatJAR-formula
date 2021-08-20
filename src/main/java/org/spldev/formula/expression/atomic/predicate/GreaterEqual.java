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

import org.spldev.formula.expression.term.*;

/**
 *
 * @author Sebastian Krieter
 */
public class GreaterEqual<D extends Comparable<D>> extends Predicate<D> {

	public GreaterEqual(Term<D> leftArgument, Term<D> rightArgument) {
		super(leftArgument, rightArgument);
	}

	protected GreaterEqual() {
		super();
	}

	@Override
	public void setArguments(Term<D> leftArgument, Term<D> rightArgument) {
		setChildren(Arrays.asList(leftArgument, rightArgument));
	}

	@Override
	public String getName() {
		return ">=";
	}

	@Override
	public Optional<Boolean> eval(List<D> values) {
		if (values.stream().anyMatch(value -> value == null)) {
			return Optional.empty();
		}
		return Optional.of((values.size() == 2) && (values.get(0).compareTo(values.get(1)) >= 0));
	}

	@Override
	public GreaterEqual<D> cloneNode() {
		return new GreaterEqual<>();
	}

	@Override
	public LessThan<D> flip() {
		final List<? extends Term<D>> children = getChildren();
		return new LessThan<>(children.get(0), children.get(1));
	}

}
