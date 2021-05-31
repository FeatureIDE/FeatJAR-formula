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
package org.spldev.formula.expression.term;

import java.util.*;

import org.spldev.formula.expression.*;

public class Variable<T> extends Terminal implements Term<T> {

	protected String name;
	protected final Class<T> type;
	protected T defaultValue;

	private boolean hasHashCode;
	private int hashCode;

	public Variable(String name, Class<T> type) {
		this.name = name;
		this.type = type;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Class<T> getType() {
		return type;
	}

	public T getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(T defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	public List<Term<T>> getChildren() {
		return Collections.emptyList();
	}

	@Override
	public Variable<T> cloneNode() {
		final Variable<T> variable = new Variable<>(name, type);
		variable.defaultValue = defaultValue;
		variable.hasHashCode = hasHashCode;
		variable.hashCode = hashCode;
		return variable;
	}

	@Override
	public int hashCode() {
		if (!hasHashCode) {
			hashCode = Objects.hash(name, type);
			hasHashCode = true;
		}
		return hashCode;
	}

	@Override
	public boolean equalsNode(Object other) {
		if (getClass() != other.getClass()) {
			return false;
		}
		final Variable<?> otherVariable = (Variable<?>) other;
		return (Objects.equals(name, otherVariable.name) &&
			Objects.equals(type, otherVariable.type));
	}

}
