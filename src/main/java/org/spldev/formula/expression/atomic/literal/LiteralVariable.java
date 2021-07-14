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
package org.spldev.formula.expression.atomic.literal;

import java.util.*;

/**
 * A variable or negated variable.
 *
 * @author Sebastian Krieter
 */
public final class LiteralVariable extends Literal {

	private final int value;
	private VariableMap map;

	LiteralVariable(int value, VariableMap map) {
		this.map = Objects.requireNonNull(map);
		if (value == 0) {
			throw new IllegalArgumentException();
		}
		this.value = value;
	}

	@Override
	public String getName() {
		return map.getName(Math.abs(value)).orElse("??");
	}

	@Override
	public boolean isPositive() {
		return value > 0;
	}

	@Override
	public LiteralVariable flip() {
		return map.getLiteral(-value).get();
	}

	@Override
	public LiteralVariable cloneNode() {
		return map.getLiteral(value).get();
	}

	@Override
	public int hashCode() {
		return value;
	}

	@Override
	public boolean equalsNode(Object other) {
		if (getClass() != other.getClass()) {
			return false;
		}
		final LiteralVariable otherLiteralVariable = (LiteralVariable) other;
		return (value == otherLiteralVariable.value);
	}

	@Override
	public String toString() {
		return (value > 0 ? "+" : "-") + getName();
	}

}
