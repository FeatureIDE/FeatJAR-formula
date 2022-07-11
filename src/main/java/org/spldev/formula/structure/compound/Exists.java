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
package org.spldev.formula.structure.compound;

import org.spldev.formula.structure.*;
import org.spldev.formula.structure.atomic.literal.VariableMap.*;

public class Exists extends Quantifier {

	public Exists(Variable boundVariable, Formula formula) {
		super(boundVariable, formula);
	}

	private Exists(Exists oldNode) {
		super(oldNode);
	}

	@Override
	public String getName() {
		return "exists";
	}

	@Override
	public Exists cloneNode() {
		return new Exists(this);
	}

}
