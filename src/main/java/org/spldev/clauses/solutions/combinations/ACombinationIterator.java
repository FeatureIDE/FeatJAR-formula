/* -----------------------------------------------------------------------------
 * formula - Propositional and first-order formulas
 * Copyright (C) 2020 Sebastian Krieter
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
 * See <https://github.com/FeatJAR/formula> for further information.
 * -----------------------------------------------------------------------------
 */
package org.spldev.clauses.solutions.combinations;

import java.util.*;

/**
 * Abstract iterator that implements parts of {@link CombinationIterator}.
 *
 * @author Sebastian Krieter
 */
public abstract class ACombinationIterator implements CombinationIterator {

	protected final int t, n;
	protected final long numCombinations;
	protected final BinomialCalculator binomialCalculator;

	protected long counter = 0;
	private long index = 0;

	public ACombinationIterator(int t, int size) {
		this(t, size, new BinomialCalculator(t, size));
	}

	public ACombinationIterator(int t, int size, BinomialCalculator binomialCalculator) {
		this.t = t;
		n = size;
		this.binomialCalculator = binomialCalculator;
		numCombinations = binomialCalculator.binomial(n, t);
	}

	@Override
	public boolean hasNext() {
		return counter < numCombinations;
	}

	@Override
	public int[] next() {
		if (counter++ >= numCombinations) {
			return null;
		}
		index = nextIndex();
		return computeCombination(index);
	}

	@Override
	public long getIndex() {
		return index;
	}

	protected abstract long nextIndex();

	@Override
	public void reset() {
		counter = 0;
		index = 0;
	}

	protected int[] computeCombination(long index) {
		final int[] combination = new int[t];
		for (int i = t; i > 0; i--) {
			if (index <= 0) {
				combination[i - 1] = i - 1;
			} else {
				final double root = 1.0 / i;
				final int p = (int) Math.ceil(Math.pow(index, root) * Math.pow(binomialCalculator.factorial(i), root));
				for (int j = p; j <= n; j++) {
					if (binomialCalculator.binomial(j, i) > index) {
						combination[i - 1] = j - 1;
						index -= binomialCalculator.binomial(j - 1, i);
						break;
					}
				}
			}
		}
		return combination;
	}

	@Override
	public Iterator<int[]> iterator() {
		return this;
	}

	@Override
	public long size() {
		return numCombinations;
	}

}
