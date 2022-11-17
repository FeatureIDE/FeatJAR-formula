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
package de.featjar.formula.analysis.bool;

import de.featjar.base.data.Result;
import de.featjar.formula.analysis.AssignmentList;

import java.util.*;

/**
 * A list of Boolean assignments.
 *
 * @param <U> the type of the implementing subclass
 * @param <T> the type of the literal list
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public abstract class BooleanAssignmentList<T extends BooleanAssignmentList<?, U>, U extends BooleanAssignment> implements AssignmentList<U> {
    protected final List<U> assignments;
    protected VariableMap variableMap;

    public BooleanAssignmentList() {
        assignments = new ArrayList<>();
    }

    public BooleanAssignmentList(int size) {
        assignments = new ArrayList<>(size);
    }

    public BooleanAssignmentList(Collection<? extends U> assignments) {
        this.assignments = new ArrayList<>(assignments);
    }

    @SuppressWarnings("unchecked")
    public BooleanAssignmentList(BooleanAssignmentList<T, U> other) {
        assignments = new ArrayList<>(other.size());
        variableMap = other.variableMap.clone();
        other.stream().map(U::clone).forEach(assignment -> add((U) assignment));
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
        BooleanAssignmentList<?, ?> that = (BooleanAssignmentList<?, ?>) o;
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
        final T negatedLiteralMatrix = newAssignmentList(new ArrayList<>());
        stream().map(U::negate).forEach(literalList -> negatedLiteralMatrix.add((U) literalList));
        return negatedLiteralMatrix;
    }

    public VariableMap getVariableMap() {
        return variableMap;
    }

    public void setVariableMap(VariableMap variableMap) {
        this.variableMap = variableMap;
    }

    @SuppressWarnings("unchecked")
    protected Result<T> adapt(VariableMap oldVariableMap, VariableMap newVariableMap) {
        final T adaptedLiteralMatrix = newAssignmentList(new ArrayList<>());
        for (final BooleanAssignment booleanAssignment : assignments) {
            final Result<BooleanAssignment> adapted = booleanAssignment.adapt(oldVariableMap, newVariableMap);
            if (adapted.isEmpty()) {
                return Result.empty(adapted.getProblems());
            }
            adaptedLiteralMatrix.add((U) adapted.get());
        }
        return Result.of(adaptedLiteralMatrix);
    }

    public Result<T> adapt(VariableMap variableMap) {
        return adapt(this.variableMap, variableMap).map(clauseList -> {
            clauseList.setVariableMap(variableMap);
            return clauseList;
        });
    }

//    public CNF randomize(Random random) {
//        final VariableMap newTermMap = (VariableMap) variableMap.clone();
//        newTermMap.randomize(random);
//
//        final LiteralMatrix adaptedLiteralMatrix =
//                clauseList.adapt(variableMap, newTermMap).get();
//        Collections.shuffle(adaptedLiteralMatrix, random);
//
//        return new CNF(newTermMap, adaptedLiteralMatrix);
//    }

//    public LiteralMatrix<T> convert() {
//        final LiteralMatrix<T> convertedLiteralMatrix = new LiteralMatrix<>();
//        convert(this, convertedLiteralMatrix, new int[size()], 0);
//        return convertedLiteralMatrix;
//    }
//
//    private void convert(LiteralMatrix<T> nf1, LiteralMatrix<T> nf2, int[] literals, int index) {
//        if (index == nf1.size()) {
//            final LiteralList literalSet =
//                    new LiteralList(literals).clean().get();
//            if (literalSet != null) {
//                nf2.add(literalSet);
//            }
//        } else {
//            for (final int literal : nf1.get(index).getIntegers()) {
//                literals[index] = literal;
//                convert(nf1, nf2, literals, index + 1);
//            }
//        }
//    }

    /**
     * Compares list of clauses by the number of literals.
     */
    public static class AscendingLengthComparator implements Comparator<BooleanAssignmentList<?, ?>> {
        @Override
        public int compare(BooleanAssignmentList o1, BooleanAssignmentList o2) {
            return addLengths(o1) - addLengths(o2);
        }

        protected int addLengths(BooleanAssignmentList<?, ?> o) {
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
    public static class DescendingClauseListLengthComparator implements Comparator<BooleanAssignmentList<?, ?>> {
        @Override
        public int compare(BooleanAssignmentList o1, BooleanAssignmentList o2) {
            return addLengths(o2) - addLengths(o1);
        }

        protected int addLengths(BooleanAssignmentList<?, ?> o) {
            int count = 0;
            for (final BooleanAssignment literalSet : o.assignments) {
                count += literalSet.getIntegers().length;
            }
            return count;
        }
    }
}
