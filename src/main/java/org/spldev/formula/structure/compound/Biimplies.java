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
package org.spldev.formula.structure.compound;

import java.util.*;

import org.spldev.formula.structure.*;

/**
 * A logical connector that is {@code true} iff the left child has the same
 * value as the right child.
 *
 * @author Sebastian Krieter
 */
public class Biimplies extends Compound {

	public Biimplies(Formula leftNode, Formula rightNode) {
		super(leftNode, rightNode);
	}

	private Biimplies() {
		super();
	}

	public Biimplies(List<? extends Formula> nodes) {
		super(nodes);
		if (nodes.size() != 2)
			throw new IllegalArgumentException("biimplies requires two arguments");
	}

	@Override
	public Biimplies cloneNode() {
		return new Biimplies();
	}

	@Override
	public String getName() {
		return "biimplies";
	}

	@Override
	public Object eval(List<?> values) {
		return (boolean) values.get(1) == (boolean) values.get(0);
	}

}
