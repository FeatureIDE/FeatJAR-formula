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
package de.featjar.formula.structure.term.integer;

import java.util.*;

import de.featjar.formula.structure.Formula;
import de.featjar.formula.structure.term.Divide;
import de.featjar.formula.structure.term.Term;
import de.featjar.formula.structure.*;
import de.featjar.formula.structure.term.*;

public class IntDivide extends Divide {

	public IntDivide(Term leftArgument, Term rightArgument) {
		super(leftArgument, rightArgument);
	}

	private IntDivide() {
		super();
	}

	@Override
	public Class<Long> getType() {
		return Long.class;
	}

	@Override
	public IntDivide cloneNode() {
		return new IntDivide();
	}

	@Override
	public Long eval(List<?> values) {
		return Formula.reduce(values, (a, b) -> a / b);
	}

}
