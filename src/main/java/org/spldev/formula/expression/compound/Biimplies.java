/* -----------------------------------------------------------------------------
 * Formula-Lib - Library to represent and edit propositional formulas.
 * Copyright (C) 2021  Sebastian Krieter
 * 
 * This file is part of Formula-Lib.
 * 
 * Formula-Lib is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Formula-Lib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Formula-Lib.  If not, see <https://www.gnu.org/licenses/>.
 * 
 * See <https://github.com/skrieter/formula> for further information.
 * -----------------------------------------------------------------------------
 */
package org.spldev.formula.expression.compound;

import org.spldev.formula.expression.*;

/**
 * A logical connector that is {@code true} iff the left child has the same
 * value as the right child.
 *
 * @author Sebastian Krieter
 */
public class Biimplies extends Compound implements Connective {

	public Biimplies(Formula leftNode, Formula rightNode) {
		super(leftNode, rightNode);
	}

	private Biimplies() {
		super();
	}

	@Override
	public Biimplies cloneNode() {
		return new Biimplies();
	}

	@Override
	public String getName() {
		return "biimplies";
	}

}
