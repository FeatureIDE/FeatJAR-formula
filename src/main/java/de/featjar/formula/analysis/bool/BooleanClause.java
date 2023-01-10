/*
 * Copyright (C) 2023 Sebastian Krieter, Elias Kuiter
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
 * See <https://github.com/FeatureIDE/FeatJAR-formula> for further information.
 */
package de.featjar.formula.analysis.bool;

import de.featjar.base.computation.IComputation;
import de.featjar.base.data.Result;
import de.featjar.formula.analysis.IClause;
import de.featjar.formula.analysis.ISolver;
import de.featjar.formula.analysis.VariableMap;
import de.featjar.formula.analysis.value.ValueClause;
import java.util.*;

/**
 * A Boolean clause; that is, a disjunction of literals.
 * Implemented as a sorted list of indices.
 * Often used as input to a SAT {@link ISolver}.
 * Indices are ordered naturally; that is, in ascending order, so negative indices come before positive indices.
 * The same index may occur multiple times, but no index may be 0.
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public class BooleanClause extends BooleanAssignment implements IClause<Integer> {
    public BooleanClause(int... integers) {
        this(integers, true);
    }

    public BooleanClause(int[] integers, boolean sort) {
        super(integers);
        if (sort) sort();
    }

    public BooleanClause(Collection<Integer> integers) {
        super(integers);
        sort();
    }

    public BooleanClause(BooleanAssignment booleanAssignment) {
        super(booleanAssignment);
        sort();
    }

    @Override
    protected BooleanClause newIntegerList(int[] integers) {
        return new BooleanClause(integers);
    }

    protected void sort() {
        Arrays.sort(this.integers);
        hashCode = Arrays.hashCode(this.integers);
    }

    @Override
    public int countNegatives() {
        int count = 0;
        for (int integer : integers) {
            if (integer < 0) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    @Override
    public int countPositives() {
        int count = 0;
        for (int i = integers.length - 1; i >= 0; i--) {
            if (integers[i] > 0) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    @Override
    public BooleanClause getPositives() {
        int[] positiveIntegers = Arrays.copyOfRange(integers, integers.length - countPositives(), integers.length);
        return newIntegerList(positiveIntegers);
    }

    @Override
    public BooleanClause getNegatives() {
        int[] negativeIntegers = Arrays.copyOfRange(integers, 0, countNegatives());
        return newIntegerList(negativeIntegers);
    }

    @Override
    public int indexOf(int integer) {
        return Arrays.binarySearch(integers, integer);
    }

    @Override
    public BooleanClause negate() {
        final int[] negated = new int[integers.length];
        final int highestIndex = negated.length - 1;
        for (int i = 0; i < negated.length; i++) {
            negated[highestIndex - i] = -integers[i]; // TODO: what does this do?
        }
        return newIntegerList(negated);
    }

    @Override
    public Result<ValueClause> toValue(VariableMap variableMap) {
        return variableMap.toValue(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public IComputation<ValueClause> toValue(IComputation<VariableMap> variableMap) {
        return (IComputation<ValueClause>) super.toValue(variableMap);
    }

    @Override
    public String toString() {
        return String.format("BooleanClause[%s]", print());
    }
}
