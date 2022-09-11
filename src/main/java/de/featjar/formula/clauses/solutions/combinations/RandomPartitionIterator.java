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

import java.util.Random;

/**
 * Combination iterator that uses the combinatorial number system to enumerate
 * all combinations and then alternately iterates over certain randomized
 * partitions of the combination space.
 *
 * @author Sebastian Krieter
 */
public class RandomPartitionIterator extends PartitionIterator {

    public RandomPartitionIterator(int t, int size) {
        this(t, size, new Random(42));
    }

    public RandomPartitionIterator(int t, int size, Random random) {
        super(t, size, 4);

        for (int i = 0; i < dim.length; i++) {
            final int[] dimArray = dim[i];
            for (int j = dimArray.length - 1; j >= 0; j--) {
                final int index = random.nextInt(j + 1);
                final int a = dimArray[index];
                dimArray[index] = dimArray[j];
                dimArray[j] = a;
            }
        }
    }
}
