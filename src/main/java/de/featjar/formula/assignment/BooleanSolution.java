/*
 * Copyright (C) 2024 FeatJAR-Development-Team
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
package de.featjar.formula.assignment;

import de.featjar.analysis.ISolver;
import de.featjar.formula.VariableMap;
import java.util.Arrays;
import java.util.Collection;

/**
 * A (partial) Boolean solution; that is, a conjunction of literals. Implemented
 * as a sorted list of indices. Often holds output of a SAT {@link ISolver}.
 * Indices are ordered such that the array index {@code i - 1} either holds
 * {@code -i}, {@code 0}, or {@code i}. That is, the largest occurring index
 * mandates the minimum length of the underlying array. The same index may not
 * occur multiple times, but indices may be 0 for partial solutions.
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public class BooleanSolution extends ABooleanAssignment implements ISolution<Integer, Boolean> {

    private static final long serialVersionUID = -6609620937538213007L;

    public BooleanSolution(int... integers) {
        this(integers, true);
    }

    public BooleanSolution(int[] integers, boolean sort) {
        super(integers);
        assert Arrays.stream(integers)
                                .map(Math::abs) //
                                .max()
                                .orElse(0)
                        <= integers.length //
                : String.format(
                        "max index %d is larger than number of elements %d, elements = %s",
                        Arrays.stream(integers).map(Math::abs).max().orElse(0),
                        integers.length,
                        Arrays.toString(integers));
        assert sort
                        || Arrays.stream(integers)
                                        .map(Math::abs) //
                                        .reduce(0, (a, b) -> b == 0 ? a + 1 : a + 1 == b ? b : -2)
                                == integers.length
                : "unsorted: " + Arrays.toString(integers);
        if (sort) sort();
    }

    public BooleanSolution(Collection<Integer> integers) {
        super(integers);
        assert integers.stream().mapToInt(a -> a).map(Math::abs).max().getAsInt() == integers.size();
        sort();
    }

    public BooleanSolution(int variableCount, int[] integers) {
        super(new int[variableCount]);
        Arrays.stream(integers)
                .filter(integer -> integer != 0)
                .forEach(integer -> elements[Math.abs(integer) - 1] = integer);
    }

    public BooleanSolution(BooleanSolution booleanSolution) {
        super(booleanSolution);
    }

    protected void sort() {
        hashCodeValid = false;
        final int[] sortedIntegers = new int[elements.length];
        Arrays.stream(elements)
                .filter(integer -> integer != 0)
                .forEach(integer -> sortedIntegers[Math.abs(integer) - 1] = integer);
        System.arraycopy(sortedIntegers, 0, elements, 0, elements.length);
    }

    public int countConflicts(int... integers) {
        return (int) Arrays.stream(integers)
                .filter(integer -> indexOf(-integer) >= 0)
                .count();
    }

    public boolean conflictsWith(int... integers) {
        return Arrays.stream(integers).anyMatch(integer -> indexOf(-integer) >= 0);
    }

    public static int[] removeConflicts(int[] integers1, int[] integers2) {
        if (integers1.length != integers2.length) {
            throw new IllegalArgumentException(
                    String.format("Arguments have different lengths (%d != %d)", integers1.length, integers2.length));
        }
        int[] integers = new int[integers1.length];
        for (int i = 0; i < integers1.length; i++) {
            final int x = integers1[i];
            final int y = integers2[i];
            integers[i] = x != y ? 0 : x;
        }
        return integers;
    }

    public static void removeConflictsInplace(int[] integers1, int[] integers2) {
        if (integers1.length != integers2.length) {
            throw new IllegalArgumentException(
                    String.format("Arguments have different lengths (%d != %d)", integers1.length, integers2.length));
        }
        for (int i = 0; i < integers1.length; i++) {
            final int x = integers1[i];
            final int y = integers2[i];
            integers1[i] = x != y ? 0 : x;
        }
    }

    public int[] removeConflicts(int... literals) {
        return removeConflicts(elements, literals);
    }

    @Override
    public int indexOf(int literal) {
        final int index = Math.abs(literal) - 1;
        return literal != 0 && index < size() && elements[index] == literal ? index : -1;
    }

    @Override
    public int indexOfVariable(int variable) {
        final int index = variable - 1;
        return variable > 0 && index < size() && elements[index] != 0 ? index : -1;
    }

    @Override
    public int[] indicesOf(int integer) {
        return new int[] {indexOf(integer)};
    }

    @Override
    public int[] indicesOfVariable(int variable) {
        return new int[] {indexOfVariable(variable)};
    }

    @Override
    public ValueSolution toValue() {
        return VariableMap.toValue(this);
    }

    @Override
    public String toString() {
        return String.format("BooleanSolution[%s]", print());
    }

    @Override
    public BooleanSolution toSolution() {
        return this;
    }

    @Override
    public BooleanSolution inverse() {
        return new BooleanSolution(negate(), false);
    }
}
