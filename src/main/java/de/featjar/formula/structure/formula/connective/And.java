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
package de.featjar.formula.structure.formula.connective;

import de.featjar.formula.structure.Formula;
import de.featjar.formula.structure.NonTerminalFormula;

import java.util.List;

/**
 * Expresses "A and B" constraints (i.e., conjunction).
 * Evaluates to {@code true} iff all of its children evaluate to {@code true}.
 *
 * @author Sebastian Krieter
 */
public class And extends NonTerminalFormula implements Connective {
    protected And() {
    }

    public And(Formula... formulas) {
        super(formulas);
    }

    public And(List<? extends Formula> formulas) {
        super(formulas);
    }

    @Override
    public String getName() {
        return "and";
    }

    @Override
    public Object evaluate(List<?> values) {
        if (values.stream().anyMatch(v -> v == Boolean.FALSE)) {
            return Boolean.FALSE;
        }
        return values.stream().filter(v -> v == Boolean.TRUE).count() == getChildrenCount() ? Boolean.TRUE : null;
    }

    @Override
    public And cloneNode() {
        return new And();
    }
}
