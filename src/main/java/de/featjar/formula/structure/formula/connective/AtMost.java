/*
 * Copyright (C) 2023 FeatJAR-Development-Team
 *
 * This file is part of FeatJAR-formula.
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
 * See <https://github.com/FeatJAR> for further information.
 */
package de.featjar.formula.structure.formula.connective;

import de.featjar.base.data.Range;
import de.featjar.formula.structure.formula.IFormula;
import java.util.List;

/**
 * Expresses "at most K" constraints.
 * Evaluates to {@code true} iff at most a given number of its children evaluate to {@code true}.
 *
 * @author Sebastian Krieter
 */
public class AtMost extends ACardinal {
    private AtMost(AtMost atMost) {
        super(atMost);
    }

    public AtMost(int maximum, IFormula... formulas) {
        super(Range.atMost(maximum), formulas);
    }

    public AtMost(int maximum, List<? extends IFormula> formulas) {
        super(Range.atMost(maximum), formulas);
    }

    @Override
    public String getName() {
        return "atmost-" + getMaximum();
    }

    @Override
    public AtMost cloneNode() {
        return new AtMost(this);
    }

    public int getMaximum() {
        return super.getRange().getUpperBound().get();
    }

    public void setMaximum(int maximum) {
        super.setRange(Range.atMost(maximum));
    }
}
