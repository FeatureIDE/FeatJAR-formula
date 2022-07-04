/* -----------------------------------------------------------------------------
 * Formula Lib - Library to represent and edit propositional formulas.
 * Copyright (C) 2021-2022  Sebastian Krieter
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
package org.spldev.formula.structure.atomic.predicate;

import java.util.*;

import org.spldev.formula.structure.term.*;

/**
 *
 * @author Sebastian Krieter
 */
public class NotEquals extends ComparingPredicate {

	public NotEquals(Term leftArgument, Term rightArgument) {
		super(leftArgument, rightArgument);
	}

	protected NotEquals() {
		super();
	}

	@Override
	public String getName() {
		return "!=";
	}

	@Override
	public NotEquals cloneNode() {
		return new NotEquals();
	}

	@Override
	public Equals flip() {
		final List<? extends Term> children = getChildren();
		return new Equals(children.get(0), children.get(1));
	}

	@Override
	protected boolean compareDiff(int diff) {
		return diff != 0;
	}

}
