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
package de.featjar.formula.structure.compound;

import java.util.List;

import de.featjar.formula.structure.Formula;

/**
 * A logical connector that is {@code true} iff the left child is {@code false}
 * or the right child is {@code true}.
 *
 * @author Sebastian Krieter
 */
public class Implies extends Compound {

	public Implies(Formula leftNode, Formula rightNode) {
		super(leftNode, rightNode);
	}

	private Implies() {
		super();
	}

	public Implies(List<? extends Formula> nodes) {
		super(nodes);
		if (nodes.size() != 2)
			throw new IllegalArgumentException("implies requires two arguments");
	}

	@Override
	public Implies cloneNode() {
		return new Implies();
	}

	@Override
	public String getName() {
		return "implies";
	}

	@Override
	public Object eval(List<?> values) {
		return (boolean) values.get(1) || !(boolean) values.get(0);
	}
}
