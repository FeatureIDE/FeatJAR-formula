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

import java.util.*;

import org.spldev.formula.expression.*;

/**
 * A logical connector that is {@code true} iff at least a given number of its
 * children are {@code true}.
 *
 * @author Sebastian Krieter
 */
public class AtLeast extends Cardinal {

	public AtLeast(List<Formula> nodes, int min) {
		super(nodes, min, Integer.MAX_VALUE);
	}

	private AtLeast(AtLeast oldNode) {
		super(oldNode);
	}

	@Override
	public AtLeast cloneNode() {
		return new AtLeast(this);
	}

	@Override
	public String getName() {
		return "atLeast-" + min;
	}

	@Override
	public int getMin() {
		return super.getMin();
	}

	@Override
	public void setMin(int min) {
		super.setMin(min);
	}

}
