/*
 * Copyright (C) 2023 Sebastian Krieter, Elias Kuiter
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
package de.featjar.clauses.solutions.combinations;

import java.util.Arrays;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Combination iterator that uses the combinatorial number system to process
 * combinations in parallel.
 *
 * @author Sebastian Krieter
 */
public final class LexicographicIterator
        implements Spliterator<de.featjar.clauses.solutions.combinations.LexicographicIterator.Combination> {

    public static class Combination {
        private Combination orgCombination;
        private int nextSpliteratorId;

        public final int spliteratorId;
        public final int[] elementIndices;
        public int combinationIndex;

        public Combination(int t) {
            this.spliteratorId = 0;
            this.combinationIndex = -1;
            elementIndices = new int[t];
            elementIndices[0] = -1;
            for (int i = 1; i < t; i++) {
                elementIndices[i] = i;
            }

            orgCombination = this;
            nextSpliteratorId = spliteratorId + 1;
        }

        public Combination(Combination other) {
            combinationIndex = other.combinationIndex;
            elementIndices = Arrays.copyOf(other.elementIndices, other.elementIndices.length);

            orgCombination = other.orgCombination;
            synchronized (orgCombination) {
                this.spliteratorId = orgCombination.nextSpliteratorId++;
            }
        }
    }

    public static Stream<Combination> stream(int t, int size) {
        return StreamSupport.stream(new LexicographicIterator(t, size), false);
    }

    public static Stream<Combination> parallelStream(int t, int size) {
        return StreamSupport.stream(new LexicographicIterator(t, size), true);
    }

    private final int t, n;
    private final BinomialCalculator binomialCalculator;

    private final int end;
    private final Combination combination;

    public LexicographicIterator(int t, int size) {
        this.t = t;
        n = size;
        combination = new Combination(t);
        if (t > 0) {
            binomialCalculator = new BinomialCalculator(t, size);
            end = Math.toIntExact(binomialCalculator.binomial());
        } else {
            binomialCalculator = null;
            end = 0;
        }
    }

    private LexicographicIterator(LexicographicIterator it) {
        t = it.t;
        n = it.n;
        binomialCalculator = it.binomialCalculator;
        combination = new Combination(it.combination);

        it.setC(it.combination.combinationIndex + ((it.end - it.combination.combinationIndex) / 2) - 1);
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
    public Spliterator<Combination> trySplit() {
        return (end - combination.combinationIndex < 10) ? null : new LexicographicIterator(this);
    }

    @Override
    public boolean tryAdvance(Consumer<? super Combination> action) {
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

        combination.elementIndices[t] = combination.combinationIndex;
        action.accept(combination);
        return true;
    }
}
