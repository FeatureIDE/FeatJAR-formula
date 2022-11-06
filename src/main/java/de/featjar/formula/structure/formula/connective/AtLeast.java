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

import de.featjar.base.data.Range;
import de.featjar.formula.structure.formula.Formula;

import java.util.List;

/**
 * Expresses "at least K" constraints.
 * Evaluates to {@code true} iff at least a given number of its children evaluate to {@code true}.
 *
 * @author Sebastian Krieter
 */
public class AtLeast extends Cardinal {
    private AtLeast(AtLeast atLeast) {
        super(atLeast);
    }

    public AtLeast(int minimum, Formula... formulas) {
        super(Range.atLeast(minimum), formulas);
    }

    public AtLeast(int minimum, List<? extends Formula> formulas) {
        super(Range.atLeast(minimum), formulas);
    }

    @Override
    public String getName() {
        return "atleast-" + getMinimum();
    }

    @Override
    public AtLeast cloneNode() {
        return new AtLeast(this);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public int getMinimum() {
        return super.getRange().getLowerBound().get();
    }

    public void setMinimum(int minimum) {
        super.setRange(Range.atLeast(minimum));
    }
}
