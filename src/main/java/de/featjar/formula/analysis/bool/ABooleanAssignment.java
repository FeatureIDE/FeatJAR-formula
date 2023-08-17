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
package de.featjar.formula.analysis.bool;

import de.featjar.base.data.*;
import de.featjar.formula.analysis.IAssignment;
import de.featjar.formula.analysis.ISolver;
import de.featjar.formula.analysis.VariableMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.stream.IntStream;

/**
 * Assigns Boolean values to integer-identified {@link de.featjar.formula.structure.term.value.Variable variables}.
 * Can be used to represent a set of literals for use in a satisfiability {@link ISolver}.
 * Implemented as an unordered list of indices to variables in some unspecified {@link VariableMap}.
 * An index can be negative, indicating a negated occurrence of its variable,
 * or 0, indicating no occurrence, and it may occur multiple times.
 * For specific use cases, consider using {@link BooleanClause} (a disjunction
 * of literals) or {@link BooleanSolution} (a conjunction of literals).
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public abstract class ABooleanAssignment extends IntegerList
        implements IAssignment<Integer, Boolean>, IBooleanRepresentation {
    public ABooleanAssignment(int... integers) {
        super(integers);
    }

    public ABooleanAssignment(Collection<Integer> integers) {
        super(integers);
    }

    public ABooleanAssignment(ABooleanAssignment booleanAssignment) {
        super(booleanAssignment);
    }

    public int[] getNegatedValues() {
        final int[] negated = new int[array.length];
        for (int i = 0; i < negated.length; i++) {
            negated[i] = -array[i];
        }
        return negated;
    }

    public int[] simplify() {
        final LinkedHashSet<Integer> integerSet = Sets.empty();
        for (final int integer : array) {
            if (integer != 0 && integerSet.contains(-integer)) {
                // If this assignment is a contradiction or tautology, it can be simplified.
                return new int[] {integer, -integer};
            } else {
                integerSet.add(integer);
            }
        }
        if (integerSet.size() == array.length) {
            return copy();
        }
        int[] newArray = new int[integerSet.size()];
        int i = 0;
        for (final int lit : integerSet) {
            newArray[i++] = lit;
        }
        return newArray;
    }

    public Result<int[]> adapt(VariableMap oldVariableMap, VariableMap newVariableMap) {
        final int[] oldIntegers = array;
        final int[] newIntegers = new int[oldIntegers.length];
        for (int i = 0; i < oldIntegers.length; i++) {
            final int l = oldIntegers[i];
            final Result<String> name = oldVariableMap.get(Math.abs(l));
            if (name.isPresent()) {
                final Result<Integer> index = newVariableMap.get(name.get());
                if (index.isPresent()) {
                    newIntegers[i] = l < 0 ? -index.get() : index.get();
                } else {
                    return Result.empty(new Problem("No variable named " + name.get(), Problem.Severity.ERROR));
                }
            } else {
                return Result.empty(new Problem("No variable with index " + l, Problem.Severity.ERROR));
            }
        }
        return Result.of(newIntegers);
    }

    public boolean containsAnyVariable(int... integers) {
        return Arrays.stream(integers).anyMatch(integer -> indexOfVariable(integer) >= 0);
    }

    public boolean containsAllVariables(int... integers) {
        return Arrays.stream(integers).noneMatch(integer -> indexOfVariable(integer) >= 0);
    }

    public int indexOfVariable(int variableInteger) {
        return IntStream.range(0, array.length)
                .filter(i -> Math.abs(array[i]) == variableInteger)
                .findFirst()
                .orElse(-1);
    }

    protected int countVariables(int[] integers, boolean[] intersectionMarker) {
        int count = 0;
        for (int integer : integers) {
            final int index = indexOfVariable(integer);
            if (index >= 0) {
                count++;
                if (intersectionMarker != null) {
                    intersectionMarker[index] = true;
                }
            }
        }
        return count;
    }

    public int[] removeAllVariables(int... integers) {
        boolean[] intersectionMarker = new boolean[this.array.length];
        int count = countVariables(integers, intersectionMarker);

        int[] newIntegers = new int[this.array.length - count];
        int j = 0;
        for (int i = 0; i < this.array.length; i++) {
            if (!intersectionMarker[i]) {
                newIntegers[j++] = this.array[i];
            }
        }
        return newIntegers;
    }

    public int[] retainAllVariables(int... integers) {
        boolean[] intersectionMarker = new boolean[this.array.length];
        int count = countVariables(integers, intersectionMarker);

        int[] newIntegers = new int[count];
        int j = 0;
        for (int i = 0; i < this.array.length; i++) {
            if (intersectionMarker[i]) {
                newIntegers[j++] = this.array[i];
            }
        }
        return newIntegers;
    }

    @Override
    public BooleanAssignment toAssignment() {
        return new BooleanAssignment(Arrays.copyOf(array, array.length));
    }

    @Override
    public BooleanClause toClause() {
        return new BooleanClause(Arrays.copyOf(array, array.length));
    }

    @Override
    public BooleanSolution toSolution() {
        return new BooleanSolution(Arrays.copyOf(array, array.length));
    }

    @Override
    public LinkedHashMap<Integer, Boolean> getAll() {
        LinkedHashMap<Integer, Boolean> map = Maps.empty();
        for (int integer : array) {
            if (integer > 0) map.put(integer, true);
            else if (integer < 0) map.put(-integer, false);
        }
        return map;
    }

    @Override
    public int size() {
        return array.length;
    }

    @Override
    public boolean isEmpty() {
        return array.length == 0;
    }

    @Override
    public Result<Boolean> getValue(Integer variable) {
        int index = indexOfVariable(variable);
        if (index < 0) return Result.empty();
        int value = get(index);
        return value == 0 ? Result.empty() : Result.of(value > 0);
    }

    public abstract String print();
}
