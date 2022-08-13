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
package de.featjar.formula.structure.atomic.literal;

import java.util.Collections;
import java.util.List;

import de.featjar.formula.structure.Terminal;
import de.featjar.formula.structure.atomic.Atomic;
import de.featjar.formula.structure.term.Term;

/**
 * A special {@link Atomic} that is always {@code true}.
 *
 * @author Sebastian Krieter
 */
public final class True extends Terminal implements Literal {

	private static final True INSTANCE = new True();

	private True() {
		super();
	}

	public static True getInstance() {
		return INSTANCE;
	}

	@Override
	public List<? extends Term> getChildren() {
		return Collections.emptyList();
	}

	@Override
	public False flip() {
		return Literal.False;
	}

	@Override
	public True cloneNode() {
		return this;
	}

	@Override
	public String getName() {
		return "true";
	}

	@Override
	public int hashCode() {
		return 91;
	}

	@Override
	public boolean equals(Object other) {
		return other == INSTANCE;
	}

	@Override
	public boolean equalsNode(Object other) {
		return other == INSTANCE;
	}

	@Override
	public Boolean eval(List<?> values) {
		return Boolean.TRUE;
	}

}
