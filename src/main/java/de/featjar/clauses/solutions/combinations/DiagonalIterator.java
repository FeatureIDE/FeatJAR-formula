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

import java.util.Arrays;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Combination iterator that iterates of the combinations in lexicographical
 * order.
 *
 * @author Sebastian Krieter
 */
public class DiagonalIterator extends ACombinationIterator {

	public static void main(String[] args) {
		DiagonalIterator diagonalIterator = new DiagonalIterator(2, 10);
		while (true) {
			int[] next = diagonalIterator.next();
			if (next == null) {
				break;
			}
			System.out.println(Arrays.toString(next));
		}
	}

	public static Stream<int[]> stream(int t, int size) {
		return StreamSupport.stream(new DiagonalIterator(t, size).spliterator(), false);
	}

	private final int[] c;
	int dist = 0;

	public DiagonalIterator(int t, int size) {
		super(t, size);
		if (t != 2) {
			throw new IllegalArgumentException("t != 2");
		}
		c = new int[t];
		c[1] = n-1;
	}

	@Override
	protected int[] computeCombination(long index) {
		if (c[1] == n - 1) {
			if (dist == n - 2) {
				return null;
			}
			dist++;
			c[0] = 0;
			c[1] = dist;
		} else {
			for (int i = 0; i < t; i++) {
				c[i]++;
			}
		}
		return c;
	}

	@Override
	protected long nextIndex() {
		return 0;
	}

	@Override
	public long getIndex() {
		long index = 0;
		for (int i = 0; i < c.length; i++) {
			index += binomialCalculator.binomial(c[i], i + 1);
		}
		return index;
	}

	@Override
	public void reset() {
		super.reset();
		for (int i = 0; i < (c.length - 1); i++) {
			c[i] = i;
		}
		c[t - 1] = t - 2;
	}
}
