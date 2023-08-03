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
import de.featjar.formula.structure.ANonTerminalExpression;
import de.featjar.formula.structure.IExpression;
import de.featjar.formula.structure.formula.IFormula;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Expresses cardinality constraints.
 * Evaluates to {@code true} iff the number of its children that evaluate to {@code true} is larger than
 * or equal to a lower bound and smaller than or equal to an upper bound.
 *
 * @author Sebastian Krieter
 */
public abstract class ACardinal extends ANonTerminalExpression implements IConnective {
    protected Range range;

    protected ACardinal(Range range, IFormula... formulas) {
        super();
        this.range = range;
        if (formulas.length > 0) super.setChildren(Arrays.asList(formulas));
    }

    protected ACardinal(Range range, List<? extends IFormula> formulas) {
        super();
        this.range = range;
        if (formulas.size() > 0) super.setChildren(formulas);
    }

    protected ACardinal(ACardinal cardinal) {
        this.range = cardinal.range;
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
                (int) values.stream().filter(v -> Boolean.TRUE.equals(v)).count();
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
    public boolean equalsNode(IExpression other) {
        return super.equalsNode(other) && Objects.equals(range, ((ACardinal) other).range);
    }

    @Override
    public int hashCodeNode() {
        return Objects.hash(super.hashCodeNode(), range);
    }
}
