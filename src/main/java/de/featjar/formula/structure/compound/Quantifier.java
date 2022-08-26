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
package de.featjar.formula.structure.compound;

import de.featjar.formula.structure.Formula;
import de.featjar.formula.structure.atomic.literal.VariableMap.Variable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public abstract class Quantifier extends Compound {

    protected Variable boundVariable;

    public Quantifier(Variable boundVariable, Formula formula) {
        super(formula);
        setBoundVariable(boundVariable);
    }

    protected Quantifier(Quantifier oldNode) {
        super();
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
    public Object eval(List<?> values) {
        return null;
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
    public boolean equalsNode(Formula other) {
        if (!super.equalsNode(other)) {
            return false;
        }
        return Objects.equals(boundVariable, ((Quantifier) other).boundVariable);
    }
}
