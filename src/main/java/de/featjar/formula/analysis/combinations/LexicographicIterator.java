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
package de.featjar.formula.analysis.combinations;

import java.util.Arrays;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Combination iterator that uses the combinatorial number system to process
 * combinations in parallel.
 *
 * @author Sebastian Krieter
 */
public final class LexicographicIterator<T>
        implements Spliterator<de.featjar.formula.analysis.combinations.LexicographicIterator.Combination<T>> {

    public static final class Combination<U> {
        public final int spliteratorId;
        public final U environment;

        public final int[] elementIndices;
        public int combinationIndex;

        private Combination(int t, Function<Combination<U>, U> environmentCreator) {
            spliteratorId = 0;
            combinationIndex = -1;
            elementIndices = new int[t];
            elementIndices[0] = -1;
            for (int i = 1; i < t; i++) {
                elementIndices[i] = i;
            }
            environment = environmentCreator.apply(this);
        }

        private Combination(
                Combination<U> other, int[] nextSpliteratorId, Function<Combination<U>, U> environmentCreator) {
            combinationIndex = other.combinationIndex;
            elementIndices = Arrays.copyOf(other.elementIndices, other.elementIndices.length);

            synchronized (nextSpliteratorId) {
                spliteratorId = nextSpliteratorId[0]++;
            }
            environment = environmentCreator.apply(this);
        }

        @Override
        public String toString() {
            return "Combination [elementIndices=" + Arrays.toString(elementIndices) + ", combinationIndex="
                    + combinationIndex + "]";
        }
    }

    public static Stream<Combination<Void>> stream(int t, int size) {
        return StreamSupport.stream(new LexicographicIterator<>(t, size, c -> null), false);
    }

    public static Stream<Combination<Void>> parallelStream(int t, int size) {
        return StreamSupport.stream(new LexicographicIterator<>(t, size, c -> null), true);
    }

    public static <V> Stream<Combination<V>> stream(int t, int size, Function<Combination<V>, V> environmentCreator) {
        return StreamSupport.stream(new LexicographicIterator<>(t, size, environmentCreator), false);
    }

    public static <V> Stream<Combination<V>> parallelStream(
            int t, int size, Function<Combination<V>, V> environmentCreator) {
        return StreamSupport.stream(new LexicographicIterator<>(t, size, environmentCreator), true);
    }

    private static final int MINIMUM_SPLIT_SIZE = 10;

    private final int t, n, end;
    private final BinomialCalculator binomialCalculator;
    private final Combination<T> combination;

    private final int[] nextSpliteratorId = {1};
    private final Function<Combination<T>, T> environmentCreator;

    public LexicographicIterator(int t, int n, Function<Combination<T>, T> environmentCreator) {
        this.t = t;
        this.n = n;
        this.environmentCreator = environmentCreator;
        combination = new Combination<>(t, environmentCreator);
        if (t > 0) {
            binomialCalculator = new BinomialCalculator(t, n);
            end = Math.toIntExact(binomialCalculator.binomial());
        } else {
            binomialCalculator = null;
            end = 0;
        }
    }

    private LexicographicIterator(LexicographicIterator<T> it) {
        t = it.t;
        n = it.n;
        environmentCreator = it.environmentCreator;
        combination = new Combination<>(it.combination, nextSpliteratorId, environmentCreator);

        binomialCalculator = it.binomialCalculator;
        final int diff = it.end - it.combination.combinationIndex;
        it.setC(it.combination.combinationIndex + (diff / 2) - 1);
        end = it.combination.combinationIndex;
    }

    private void setC(int start) {
        combination.combinationIndex = start;
        int tempIndex = start;
        for (int i = t; i > 0; i--) {
            if (tempIndex <= 0) {
                combination.elementIndices[i - 1] = i - 1;
            } else {
                final double root = 1.0 / i;
                final int p = (int) Math.ceil(Math.pow(tempIndex * binomialCalculator.factorial(i), root));
                for (int j = p; j <= n; j++) {
                    if (binomialCalculator.binomial(j, i) > tempIndex) {
                        combination.elementIndices[i - 1] = j - 1;
                        tempIndex -= binomialCalculator.binomial(j - 1, i);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public int characteristics() {
        return ORDERED | DISTINCT | SIZED | NONNULL | IMMUTABLE | SUBSIZED;
    }

    @Override
    public long estimateSize() {
        return end - combination.combinationIndex;
    }

    @Override
    public Spliterator<Combination<T>> trySplit() {
        return (end - combination.combinationIndex < MINIMUM_SPLIT_SIZE) ? null : new LexicographicIterator<>(this);
    }

    @Override
    public boolean tryAdvance(Consumer<? super Combination<T>> action) {
        if (combination.combinationIndex == end) {
            return false;
        }

        combination.combinationIndex++;

        int i = 0;
        for (; i < t - 1; i++) {
            if (combination.elementIndices[i] + 1 < combination.elementIndices[i + 1]) {
                ++combination.elementIndices[i];
                for (int j = i - 1; j >= 0; j--) {
                    combination.elementIndices[j] = j;
                }
                break;
            }
        }
        if (i == t - 1) {
            int lastIndex = combination.elementIndices[i] + 1;
            if (lastIndex == n) {
                return false;
            }
            combination.elementIndices[i] = lastIndex;
            for (int j = i - 1; j >= 0; j--) {
                combination.elementIndices[j] = j;
            }
        }

        action.accept(combination);
        return true;
    }
}
