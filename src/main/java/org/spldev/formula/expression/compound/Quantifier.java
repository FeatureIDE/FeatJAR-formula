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
package org.spldev.formula.expression.compound;

import java.util.*;

import org.spldev.formula.expression.*;
import org.spldev.formula.expression.term.*;

public abstract class Quantifier extends Compound implements Connective {

	protected Variable<?> boundVariable;

	public Quantifier(Variable<?> boundVariable, Formula formula) {
		super(formula);
		setBoundVariable(boundVariable);
	}

	protected Quantifier(Quantifier oldNode) {
		super();
		setBoundVariable(boundVariable);
	}

	public Variable<?> getBoundVariable() {
		return boundVariable;
	}

	public void setBoundVariable(Variable<?> boundVariable) {
		Objects.requireNonNull(boundVariable);
		this.boundVariable = boundVariable;
	}

	public void setFormula(Formula formula) {
		Objects.requireNonNull(formula);
		setChildren(Arrays.asList(formula));
	}

	@Override
	public Quantifier cloneNode() {
		throw new IllegalStateException();
	}

	@Override
	public int computeHashCode() {
		int hashCode = super.computeHashCode();
		hashCode = (37 * hashCode) + Objects.hashCode(boundVariable);
		return hashCode;
	}

	@Override
	public boolean equalsNode(Object other) {
		if (!super.equalsNode(other)) {
			return false;
		}
		return Objects.equals(boundVariable, ((Quantifier) other).boundVariable);
	}

}
