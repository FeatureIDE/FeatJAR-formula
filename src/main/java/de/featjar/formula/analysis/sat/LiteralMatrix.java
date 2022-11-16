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
package de.featjar.formula.analysis.sat;

import de.featjar.base.data.Result;

import java.util.*;
import java.util.stream.Stream;

/**
 * A two-dimensional literal list (i.e., a list of literal lists).
 *
 * @param <U> the type of the implementing subclass
 * @param <T> the type of the literal list
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public abstract class LiteralMatrix<T extends LiteralMatrix<?, U>, U extends LiteralList> {
    protected final List<U> literalLists;

    public LiteralMatrix() {
        literalLists = new ArrayList<>();
    }

    public LiteralMatrix(int size) {
        literalLists = new ArrayList<>(size);
    }

    public LiteralMatrix(Collection<? extends U> literalLists) {
        this.literalLists = new ArrayList<>(literalLists);
    }

    @SuppressWarnings("unchecked")
    public LiteralMatrix(LiteralMatrix<T, U> other) {
        literalLists = new ArrayList<>(other.size());
        other.stream().map(U::clone).forEach(literalList -> add((U) literalList));
    }

    protected abstract T newLiteralMatrix(List<U> literalLists);

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public T clone() {
        return newLiteralMatrix(literalLists);
    }

    public U get(int index) {
        return literalLists.get(index);
    }

    public List<U> getAll() {
        return literalLists;
    }

    public int size() {
        return literalLists.size();
    }

    public Stream<U> stream() {
        return literalLists.stream();
    }

    public boolean add(U literalList) {
        return literalLists.add(literalList);
    }

    public boolean addAll(Collection<? extends U> literalLists) {
        return this.literalLists.addAll(literalLists);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LiteralMatrix<?, ?> that = (LiteralMatrix<?, ?>) o;
        return Objects.equals(literalLists, that.literalLists);
    }

    @Override
    public int hashCode() {
        return Objects.hash(literalLists);
    }

    /**
     * {@return a literal matrix that negates all clauses in this literal matrix by applying De Morgan}
     */
    @SuppressWarnings("unchecked")
    public T negate() {
        final T negatedLiteralMatrix = newLiteralMatrix(new ArrayList<>());
        stream().map(U::negate).forEach(literalList -> negatedLiteralMatrix.add((U) literalList));
        return negatedLiteralMatrix;
    }

    @SuppressWarnings("unchecked")
    public Result<T> adapt(VariableMap oldVariableMap, VariableMap newVariableMap) {
        final T adaptedLiteralMatrix = newLiteralMatrix(new ArrayList<>());
        for (final LiteralList literalList : literalLists) {
            final Result<LiteralList> adapted = literalList.adapt(oldVariableMap, newVariableMap);
            if (adapted.isEmpty()) {
                return Result.empty(adapted.getProblems());
            }
            adaptedLiteralMatrix.add((U) adapted.get());
        }
        return Result.of(adaptedLiteralMatrix);
    }

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
    public static class AscendingLengthComparator implements Comparator<LiteralMatrix<?, ?>> {
        @Override
        public int compare(LiteralMatrix o1, LiteralMatrix o2) {
            return addLengths(o1) - addLengths(o2);
        }

        protected int addLengths(LiteralMatrix<?, ?> o) {
            int count = 0;
            for (final LiteralList literalSet : o.literalLists) {
                count += literalSet.getIntegers().length;
            }
            return count;
        }
    }

    /**
     * Compares list of clauses by the number of literals.
     */
    public static class DescendingClauseListLengthComparator implements Comparator<LiteralMatrix<?, ?>> {
        @Override
        public int compare(LiteralMatrix o1, LiteralMatrix o2) {
            return addLengths(o2) - addLengths(o1);
        }

        protected int addLengths(LiteralMatrix<?, ?> o) {
            int count = 0;
            for (final LiteralList literalSet : o.literalLists) {
                count += literalSet.getIntegers().length;
            }
            return count;
        }
    }
}
