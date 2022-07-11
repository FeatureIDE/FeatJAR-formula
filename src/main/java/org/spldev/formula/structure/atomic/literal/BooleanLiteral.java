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
package org.spldev.formula.structure.atomic.literal;

import java.util.*;

import org.spldev.formula.structure.*;
import org.spldev.formula.structure.atomic.literal.NamedTermMap.*;
import org.spldev.formula.structure.atomic.literal.VariableMap.*;

/**
 * A positive or negative literal. Is associated with a {@link Variable
 * variable}. It can be seen as an expression in the form of
 * {@code x == positive}, where x is the variable and positive is the either
 * {@code true} or {@code false}.
 *
 * @author Sebastian Krieter
 */
public final class BooleanLiteral extends NonTerminal implements Literal {

	private final boolean positive;

	public BooleanLiteral(ValueTerm valueTerm) {
		this(valueTerm, true);
	}

	public BooleanLiteral(ValueTerm valueTerm, boolean positive) {
		super(valueTerm);
		this.positive = positive;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<? extends ValueTerm> getChildren() {
		return (List<? extends ValueTerm>) super.getChildren();
	}

	@Override
	public String getName() {
		// TODO change to this and update all uses of Literal#getName
//		return (positive ? "+" : "-") + children.get(0).getName();
		return getVariable().getName();
	}

	public int getIndex() {
		return getVariable().getIndex();
	}

	public ValueTerm getVariable() {
		return (ValueTerm) children.get(0);
	}

	@Override
	public boolean isPositive() {
		return positive;
	}

	@Override
	public BooleanLiteral flip() {
		return new BooleanLiteral(getVariable(), !positive);
	}

	@Override
	public BooleanLiteral cloneNode() {
		return new BooleanLiteral(getVariable(), positive);
	}

	@Override
	public int hashCode() {
		return Objects.hash(getVariable(), positive);
	}

	@Override
	public boolean equalsNode(Object other) {
		if (getClass() != other.getClass()) {
			return false;
		}
		final BooleanLiteral otherLiteral = (BooleanLiteral) other;
		return ((positive == otherLiteral.positive)
			&& Objects.equals(getVariable(), otherLiteral.getVariable()));
	}

	@Override
	public String toString() {
		return (positive ? "+" : "-") + getName();
	}

	@Override
	public Boolean eval(List<?> values) {
		assert Formula.checkValues(1, values);
		assert Formula.checkValues(Boolean.class, values);
		final Boolean b = (Boolean) values.get(0);
		return b != null
			? positive == b
			: null;
	}

}
