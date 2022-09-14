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

import de.featjar.base.data.Range;
import de.featjar.formula.structure.Formula;
import de.featjar.formula.structure.NonTerminalFormula;

import java.util.List;
import java.util.Objects;

/**
 * Expresses cardinality constraints.
 * Evaluates to {@code true} iff the number of its children that evaluate to {@code true} is larger than
 * or equal to a lower bound and smaller than or equal to an upper bound.
 *
 * @author Sebastian Krieter
 */
public abstract class Cardinal extends NonTerminalFormula implements Connective {
    protected Range range;

    protected Cardinal(Range range, List<? extends Formula> formulas) {
        super(formulas);
        setRange(range);
    }

    protected Cardinal(Cardinal oldNode) {
        setRange(oldNode.range);
    }

    protected Range getRange() {
        return range;
    }

    protected void setRange(Range range) {
        assertChildrenCountInRange(getChildrenCount(), atLeastTheLargerBound(range));
        this.range = range;
    }

    private Range atLeastTheLargerBound(Range range) {
        return Range.atLeast(range.getLargerBound().orElse(null));
    }

    @Override
    public Range getChildrenCountRange() {
        return atLeastTheLargerBound(range);
    }

    @Override
    public Object evaluate(List<?> values) {
        final int trueCount =
                (int) values.stream().filter(v -> v == Boolean.TRUE).count();
        final int nullCount = (int) values.stream().filter(Objects::isNull).count();
        if (!range.testLowerBound(trueCount + nullCount) || !range.testUpperBound(trueCount)) {
            return Boolean.FALSE;
        }
        if (range.testLowerBound(trueCount) && range.testUpperBound(trueCount + nullCount)) {
            return Boolean.TRUE;
        }
        return null;
    }

    @Override
    public boolean equalsNode(Formula other) {
        return super.equalsNode(other) && Objects.equals(range, ((Cardinal) other).range);
    }
}
