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
import de.featjar.formula.analysis.ISolution;
import de.featjar.formula.analysis.ISolver;
import de.featjar.formula.analysis.VariableMap;
import de.featjar.formula.analysis.value.ValueSolution;
import java.util.*;

/**
 * A (partial) Boolean solution; that is, a conjunction of literals.
 * Implemented as a sorted list of indices.
 * Often holds output of a SAT {@link ISolver}.
 * Indices are ordered such that the array index {@code i - 1} either holds {@code -i}, {@code 0}, or {@code i}.
 * That is, the largest occurring index mandates the minimum length of the underlying array.
 * The same index may not occur multiple times, but indices may be 0 for partial solutions.
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public class BooleanSolution extends ABooleanAssignment implements ISolution<Integer> {
    public BooleanSolution(int... integers) {
        this(integers, true);
    }

    public BooleanSolution(int[] integers, boolean sort) {
        super(integers);
        if (sort) sort();
    }

    public BooleanSolution(Collection<Integer> integers) {
        super(integers);
        sort();
    }

    public BooleanSolution(BooleanSolution booleanSolution) {
        super(booleanSolution);
    }

    protected void sort() {
        hashCodeValid = false;
        final int[] sortedIntegers = new int[array.length];
        Arrays.stream(array)
                .filter(integer -> integer != 0)
                .forEach(integer -> sortedIntegers[Math.abs(integer) - 1] = integer);
        System.arraycopy(sortedIntegers, 0, array, 0, array.length);
    }

    public int countConflicts(int... integers) {
        return (int) Arrays.stream(integers)
                .filter(integer -> indexOf(-integer) >= 0)
                .count();
    }

    public boolean conflictsWith(int... integers) {
        return countConflicts(integers) > 0;
    }

    public static int[] removeConflicts(int[] integers1, int[] integers2) {
        if (integers1.length != integers2.length)
            throw new IllegalArgumentException();
        int[] integers = new int[integers1.length];
        for (int i = 0; i < integers1.length; i++) {
            final int x = integers1[i];
            final int y = integers2[i];
            integers[i] = x != y ? 0 : x;
        }
        return integers;
    }

    public int[] removeConflicts(int... integers) {
        return removeConflicts(array, integers);
    }

    @Override
    public int indexOf(int integer) {
        final int index = Math.abs(integer) - 1;
        return integer != 0 && array[index] == integer ? index : -1;
    }

    @Override
    public int indexOfVariable(int integer) {
        return integer > 0 && integer < size() ? integer - 1 : -1;
    }

    @Override
    public int size() {
        return countPositives() + countNegatives();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public Result<ValueSolution> toValue(VariableMap variableMap) {
        return variableMap.toValue(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public IComputation<ValueSolution> toValue(IComputation<VariableMap> variableMap) {
        return (IComputation<ValueSolution>) super.toValue(variableMap);
    }

    public String print() {
        return VariableMap.toAnonymousValue(this).get().print();
    }

    @Override
    public String toString() {
        return String.format("BooleanSolution[%s]", print());
    }
}
