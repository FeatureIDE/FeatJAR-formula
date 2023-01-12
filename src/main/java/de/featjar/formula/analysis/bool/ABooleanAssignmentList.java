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

import de.featjar.base.data.Range;
import de.featjar.formula.analysis.IAssignmentList;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A list of Boolean assignments.
 *
 * @param <T> the type of the literal list
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public abstract class ABooleanAssignmentList<T extends ABooleanAssignment> implements IAssignmentList<T>, IBooleanRepresentation {
    protected final List<T> assignments;

    public ABooleanAssignmentList() {
        assignments = new ArrayList<>();
    }

    public ABooleanAssignmentList(int size) {
        assignments = new ArrayList<>(size);
    }

    public ABooleanAssignmentList(Collection<? extends T> assignments) {
        this.assignments = new ArrayList<>(assignments);
    }

    public ABooleanAssignmentList(ABooleanAssignmentList<T> other) {
        this(other.getAll());
    }

    @Override
    public List<T> getAll() {
        return assignments;
    }

    @Override
    public BooleanAssignmentList toAssignmentList() {
        return new BooleanAssignmentList(
                assignments.stream().map(ABooleanAssignment::toAssignment).collect(Collectors.toList()));
    }

    @Override
    public BooleanClauseList toClauseList() {
        return new BooleanClauseList(
                assignments.stream().map(ABooleanAssignment::toClause).collect(Collectors.toList()));
    }

    @Override
    public BooleanSolutionList toSolutionList() {
        return new BooleanSolutionList(
                assignments.stream().map(ABooleanAssignment::toSolution).collect(Collectors.toList()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ABooleanAssignmentList<?> that = (ABooleanAssignmentList<?>) o;
        return Objects.equals(assignments, that.assignments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(assignments);
    }

    // todo: currently assumes that the maximum index corresponds to the number of variables
    public int getVariableCount() {
        return assignments.stream()
                .flatMapToInt(assignment -> Arrays.stream(assignment.get()))
                .max()
                .orElse(0);
    }

    public Range getVariableRange() {
        // todo: what if there are no variables?
        return Range.of(1, getVariableCount());
    }

    /**
     * Compares list of clauses by the number of literals.
     */
    public static class AscendingLengthComparator implements Comparator<ABooleanAssignmentList<?>> {
        @Override
        public int compare(ABooleanAssignmentList o1, ABooleanAssignmentList o2) {
            return addLengths(o1) - addLengths(o2);
        }

        protected int addLengths(ABooleanAssignmentList<?> o) {
            int count = 0;
            for (final ABooleanAssignment literalSet : o.assignments) {
                count += literalSet.get().length;
            }
            return count;
        }
    }

    /**
     * Compares list of clauses by the number of literals.
     */
    public static class DescendingClauseListLengthComparator implements Comparator<ABooleanAssignmentList<?>> {
        @Override
        public int compare(ABooleanAssignmentList o1, ABooleanAssignmentList o2) {
            return addLengths(o2) - addLengths(o1);
        }

        protected int addLengths(ABooleanAssignmentList<?> o) {
            int count = 0;
            for (final ABooleanAssignment literalSet : o.assignments) {
                count += literalSet.get().length;
            }
            return count;
        }
    }

    abstract public String print();
}