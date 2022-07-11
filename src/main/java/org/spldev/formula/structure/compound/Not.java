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

import java.util.*;

import org.spldev.formula.structure.*;

/**
 * A logical connector that is {@code true} iff its child is {@code false}.
 *
 * @author Sebastian Krieter
 */
public class Not extends Compound {

	public Not(Formula node) {
		super(node);
	}

	private Not() {
		super();
	}

	public Not(List<? extends Formula> nodes) {
		super(nodes);
		if (nodes.size() != 1)
			throw new IllegalArgumentException("not requires one argument");
	}

	@Override
	public Not cloneNode() {
		return new Not();
	}

	@Override
	public String getName() {
		return "not";
	}

	@Override
	public Boolean eval(List<?> values) {
		return !(boolean) values.get(0);
	}

}
