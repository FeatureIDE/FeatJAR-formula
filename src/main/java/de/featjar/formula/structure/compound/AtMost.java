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
 * A logical connector that is {@code true} iff at most a given number of its
 * children are {@code true}.
 *
 * @author Sebastian Krieter
 */
public class AtMost extends Cardinal {

	public AtMost(List<? extends Formula> nodes, int max) {
		super(nodes, 0, max);
	}

	private AtMost(AtMost oldNode) {
		super(oldNode);
	}

	@Override
	public AtMost cloneNode() {
		return new AtMost(this);
	}

	@Override
	public String getName() {
		return "atMost-" + max;
	}

	@Override
	public void setMax(int max) {
		super.setMax(max);
	}

}
