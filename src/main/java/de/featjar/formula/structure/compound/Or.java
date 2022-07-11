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
package de.featjar.formula.structure.compound;

import java.util.*;

import de.featjar.formula.structure.Formula;
import de.featjar.formula.structure.*;

/**
 * A logical connector that is {@code true} iff at least one of its children is
 * {@code true}.
 *
 * @author Sebastian Krieter
 */
public class Or extends Compound {

	public Or(List<? extends Formula> nodes) {
		super(nodes);
	}

	public Or(Formula... nodes) {
		super(nodes);
	}

	private Or() {
		super();
	}

	@Override
	public Or cloneNode() {
		return new Or();
	}

	@Override
	public String getName() {
		return "or";
	}

	@Override
	public Object eval(List<?> values) {
		if (values.stream().anyMatch(v -> v == Boolean.TRUE)) {
			return Boolean.TRUE;
		}
		return values.stream().filter(v -> v == Boolean.FALSE).count() == children.size() ? Boolean.FALSE : null;
	}

}
