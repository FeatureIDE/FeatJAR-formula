/* -----------------------------------------------------------------------------
 * Formula Lib - Library to represent and edit propositional formulas.
 * Copyright (C) 2021-2022  Sebastian Krieter
 * 
 * This file is part of Formula Lib.
 * 
 * Formula Lib is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Formula Lib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Formula Lib.  If not, see <https://www.gnu.org/licenses/>.
 * 
 * See <https://github.com/skrieter/formula> for further information.
 * -----------------------------------------------------------------------------
 */
package org.spldev.formula.structure.atomic.literal;

import java.util.*;

import org.spldev.formula.structure.atomic.predicate.*;
import org.spldev.formula.structure.term.bool.*;

/**
 * A positive or negative literal. Is associated with a {@link BoolVariable
 * boolean variable}. It can be seen as an expression in the form of
 * {@code x == positive}, where x is the variable and positive is the either
 * {@code true} or {@code false}.
 *
 * @author Sebastian Krieter
 */
public final class LiteralPredicate extends Predicate<Boolean> implements Literal {

	private final boolean positive;

	public LiteralPredicate(BoolVariable variable) {
		this(variable, true);
	}

	public LiteralPredicate(BoolVariable variable, boolean positive) {
		super(Objects.requireNonNull(variable));
		this.positive = positive;
	}

	@Override
	public String getName() {
		// TODO change to this and update all uses of Literal#getName
//		return (positive ? "+" : "-") + children.get(0).getName();
		return children.get(0).getName();
	}

	public int getIndex() {
		return ((BoolVariable) children.get(0)).getIndex();
	}

	public BoolVariable getVariable() {
		return (BoolVariable) children.get(0);
	}

	@Override
	public boolean isPositive() {
		return positive;
	}

	@Override
	public Optional<Boolean> eval(List<Boolean> values) {
		return (values.size() == 1) && (values.get(0) != null)
			? Optional.of(values.get(0) == positive)
			: Optional.empty();
	}

	@Override
	public LiteralPredicate flip() {
		return new LiteralPredicate(getVariable(), !positive);
	}

	@Override
	public LiteralPredicate cloneNode() {
		return new LiteralPredicate(getVariable(), positive);
	}

	@Override
	public int hashCode() {
		return Objects.hash(children.get(0), positive);
	}

	@Override
	public boolean equalsNode(Object other) {
		if (getClass() != other.getClass()) {
			return false;
		}
		final LiteralPredicate otherLiteral = (LiteralPredicate) other;
		return ((positive == otherLiteral.positive)
			&& Objects.equals(children.get(0), otherLiteral.children.get(0)));
	}

	@Override
	public String toString() {
		return (positive ? "+" : "-") + getName();
	}

}
