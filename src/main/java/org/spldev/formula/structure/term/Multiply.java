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
package org.spldev.formula.structure.term;

import java.util.*;

public abstract class Multiply extends Function {

	public Multiply(Term leftArgument, Term rightArgument) {
		super(leftArgument, rightArgument);
	}

	public Multiply(List<Term> arguments) {
		super(arguments);
	}

	protected Multiply() {
		super();
	}

	@Override
	public String getName() {
		return "*";
	}

	@Override
	public abstract Multiply cloneNode();

}
