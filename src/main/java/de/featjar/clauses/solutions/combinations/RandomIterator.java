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
package de.featjar.clauses.solutions.combinations;

import java.util.Random;
import java.util.stream.LongStream;

/**
 * Combination iterator that uses the combinatorial number system to enumerate
 * all combinations and then iterates from first to last combination.
 *
 * @author Sebastian Krieter
 */
public class RandomIterator extends ABinomialCombinationIterator {

    long[] index;

    public RandomIterator(int t, int size) {
        super(size, t);
        if (numCombinations > Integer.MAX_VALUE) {
            throw new RuntimeException();
        }

        index = LongStream.range(0, numCombinations).toArray();
        final Random rand = new Random(123);

        for (int i = 0; i < index.length; i++) {
            final int randomIndexToSwap = rand.nextInt(index.length);
            final long temp = index[randomIndexToSwap];
            index[randomIndexToSwap] = index[i];
            index[i] = temp;
        }
    }

    @Override
    protected long nextIndex() {
        return index[(int) (counter - 1)];
    }
}
