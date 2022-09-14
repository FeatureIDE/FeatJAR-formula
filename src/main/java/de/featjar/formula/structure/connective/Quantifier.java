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
 * See <https://github.com/FeatureIDE/FeatJAR-formula> for further information.
 */
package de.featjar.formula.structure.connective;

import de.featjar.formula.structure.Formula;
import de.featjar.formula.structure.NonTerminalFormula;
import de.featjar.formula.structure.UnaryFormula;
import de.featjar.formula.structure.TermMap.Variable;

import java.util.List;
import java.util.Objects;

/**
 * Expresses quantified Boolean formulas.
 * Evaluates to {@code true} iff there is a value of the bound variable such that the formula evaluates to {@code true}.
 *
 * @author Sebastian Krieter
 */
public abstract class Quantifier extends NonTerminalFormula implements Connective, UnaryFormula {
    protected Variable boundVariable;

    protected Quantifier(Quantifier oldNode) {
        setBoundVariable(oldNode.boundVariable);
    }

    public Quantifier(Variable boundVariable, Formula formula) {
        super(formula);
        setBoundVariable(boundVariable);
    }

    public Variable getBoundVariable() {
        return boundVariable;
    }

    public void setBoundVariable(Variable boundVariable) {
        Objects.requireNonNull(boundVariable);
        this.boundVariable = boundVariable;
    }

    @Override
    public Object evaluate(List<?> values) {
        return null; // todo
    }

    @Override
    public Quantifier cloneNode() {
        throw new IllegalStateException(); // todo
    }

    @Override
    public boolean equalsNode(Formula other) {
        return super.equalsNode(other) && Objects.equals(boundVariable, ((Quantifier) other).boundVariable);
    }
}
