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
package de.featjar.formula.clauses.solutions.combinations;

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
public class ParallelLexicographicIterator implements Spliterator<int[]> {

    public static Stream<int[]> stream(int t, int size) {
        return StreamSupport.stream(new ParallelLexicographicIterator(t, size), true);
    }

    protected final int t, n;
    protected final BinomialCalculator binomialCalculator;

    protected final long end;
    private long index;

    private final int[] c;

    public ParallelLexicographicIterator(int t, int size) {
        this.t = t;
        n = size;
        binomialCalculator = new BinomialCalculator(t, size);
        end = binomialCalculator.binomial(n, t);
        index = 0;

        c = new int[t];
        c[0] = -1;
        for (int i = 1; i < t; i++) {
            c[i] = i;
        }
    }

    private ParallelLexicographicIterator(ParallelLexicographicIterator it) {
        t = it.t;
        n = it.n;
        binomialCalculator = it.binomialCalculator;
        index = it.index;
        c = Arrays.copyOf(it.c, it.c.length);

        final long diff = it.end - it.index;
        it.setC(it.index + (diff >> 1) - 1);

        end = it.index;
    }

    private void setC(long start) {
        this.index = start;
        long tempIndex = start;
        for (int i = t; i > 0; i--) {
            if (tempIndex <= 0) {
                c[i - 1] = i - 1;
            } else {
                final double root = 1.0 / i;
                final int p = (int) Math.ceil(Math.pow(tempIndex * binomialCalculator.factorial(i), root));
                for (int j = p; j <= n; j++) {
                    if (binomialCalculator.binomial(j, i) > tempIndex) {
                        c[i - 1] = j - 1;
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
        return end - index;
    }

    @Override
    public Spliterator<int[]> trySplit() {
        return (end - index < 10) ? null : new ParallelLexicographicIterator(this);
    }

    @Override
    public boolean tryAdvance(Consumer<? super int[]> action) {
        if (index == end) {
            return false;
        }

        index++;

        int i = 0;
        for (; i < t - 1; i++) {
            if (c[i] + 1 < c[i + 1]) {
                c[i]++;
                for (int j = i - 1; j >= 0; j--) {
                    c[j] = j;
                }
                break;
            }
        }
        if (i == t - 1) {
            int lastIndex = c[i] + 1;
            if (lastIndex == n) {
                return false;
            }
            c[i] = lastIndex;
            for (int j = i - 1; j >= 0; j--) {
                c[j] = j;
            }
        }

        action.accept(c);
        return true;
    }
}
