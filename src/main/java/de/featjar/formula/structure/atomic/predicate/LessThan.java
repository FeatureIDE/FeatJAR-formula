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
 * See <https://github.com/FeatJAR/formula> for further information.
 */
package de.featjar.formula.structure.atomic.predicate;

import java.util.List;

import de.featjar.formula.structure.term.Term;

/**
 *
 * @author Sebastian Krieter
 */
public class LessThan extends ComparingPredicate {

	public LessThan(Term leftArgument, Term rightArgument) {
		super(leftArgument, rightArgument);
	}

	protected LessThan() {
		super();
	}

	@Override
	public String getName() {
		return "<";
	}

	@Override
	public LessThan cloneNode() {
		return new LessThan();
	}

	@Override
	public GreaterEqual flip() {
		final List<? extends Term> children = getChildren();
		return new GreaterEqual(children.get(0), children.get(1));
	}

	@Override
	protected boolean compareDiff(int diff) {
		return diff < 0;
	}
}
