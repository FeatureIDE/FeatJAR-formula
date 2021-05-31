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

public class Constant<T> extends Terminal implements Term<T> {

	protected String name;
	protected T value;

	private boolean hasHashCode;
	private int hashCode;

	public Constant(String name, T value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	@Override
	public List<Term<T>> getChildren() {
		return Collections.emptyList();
	}

	@Override
	public Constant<T> cloneNode() {
		final Constant<T> constant = new Constant<>(name, value);
		constant.hasHashCode = hasHashCode;
		constant.hashCode = hashCode;
		return constant;
	}

	@Override
	public int hashCode() {
		if (!hasHashCode) {
			hashCode = Objects.hash(name, value);
			hasHashCode = true;
		}
		return hashCode;
	}

	@Override
	public boolean equalsNode(Object other) {
		if (getClass() != other.getClass()) {
			return false;
		}
		final Constant<?> otherConstant = (Constant<?>) other;
		return (Objects.equals(name, otherConstant.name) &&
			Objects.equals(value, otherConstant.value));
	}

}
