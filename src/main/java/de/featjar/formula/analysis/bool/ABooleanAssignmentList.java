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

import de.featjar.formula.analysis.IAssignmentList;
import java.util.*;

/**
 * A list of Boolean assignments.
 *
 * @param <U> the type of the implementing subclass
 * @param <T> the type of the literal list
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public abstract class ABooleanAssignmentList<T extends ABooleanAssignmentList<?, U>, U extends BooleanAssignment>
        implements IAssignmentList<U>, IBooleanRepresentation {
    protected final List<U> assignments;

    public ABooleanAssignmentList() {
        assignments = new ArrayList<>();
    }

    public ABooleanAssignmentList(int size) {
        assignments = new ArrayList<>(size);
    }

    public ABooleanAssignmentList(Collection<? extends U> assignments) {
        this.assignments = new ArrayList<>(assignments);
    }

    public ABooleanAssignmentList(ABooleanAssignmentList<T, U> other) {
        assignments = new ArrayList<>(other.getAll());
    }

    protected abstract T newAssignmentList(List<U> assignments);

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public T clone() {
        return newAssignmentList(assignments);
    }

    @Override
    public List<U> getAll() {
        return assignments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ABooleanAssignmentList<?, ?> that = (ABooleanAssignmentList<?, ?>) o;
        return Objects.equals(assignments, that.assignments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(assignments);
    }

    /**
     * {@return a literal matrix that negates all clauses in this literal matrix by applying De Morgan}
     */
    @SuppressWarnings("unchecked")
    public T negate() {
        final T negatedAssignmentList = newAssignmentList(new ArrayList<>());
        stream().map(U::negate).forEach(literalList -> negatedAssignmentList.add((U) literalList));
        return negatedAssignmentList;
    }

    // assumes that the maximum index corresponds to the number of variables
    public int getVariableCount() {
        return assignments.stream()
                .flatMapToInt(assignment -> Arrays.stream(assignment.getIntegers()))
                .max()
                .orElse(0);
    }

    /**
     * Compares list of clauses by the number of literals.
     */
    public static class AscendingLengthComparator implements Comparator<ABooleanAssignmentList<?, ?>> {
        @Override
        public int compare(ABooleanAssignmentList o1, ABooleanAssignmentList o2) {
            return addLengths(o1) - addLengths(o2);
        }

        protected int addLengths(ABooleanAssignmentList<?, ?> o) {
            int count = 0;
            for (final BooleanAssignment literalSet : o.assignments) {
                count += literalSet.getIntegers().length;
            }
            return count;
        }
    }

    /**
     * Compares list of clauses by the number of literals.
     */
    public static class DescendingClauseListLengthComparator implements Comparator<ABooleanAssignmentList<?, ?>> {
        @Override
        public int compare(ABooleanAssignmentList o1, ABooleanAssignmentList o2) {
            return addLengths(o2) - addLengths(o1);
        }

        protected int addLengths(ABooleanAssignmentList<?, ?> o) {
            int count = 0;
            for (final BooleanAssignment literalSet : o.assignments) {
                count += literalSet.getIntegers().length;
            }
            return count;
        }
    }
}
