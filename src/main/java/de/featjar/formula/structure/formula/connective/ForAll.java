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
import de.featjar.formula.structure.term.Variable;

/**
 * Expresses "for all X such that A" constraints (i.e., universal quantification).
 * Evaluates to {@code true} iff, for all values of the bound variable, the formula evaluates to {@code true}.
 *
 * @author Sebastian Krieter
 */
public class ForAll extends Quantifier {

    protected ForAll(ForAll forAll) {
        super(forAll);
    }

    public ForAll(Variable boundVariable, Formula formula) {
        super(boundVariable, formula);
    }

    @Override
    public String getName() {
        return "forall-" + boundVariable;
    }

    @Override
    public ForAll cloneNode() {
        return new ForAll(this);
    }
}
