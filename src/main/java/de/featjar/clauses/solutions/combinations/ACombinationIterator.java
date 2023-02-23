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

/**
 * Abstract iterator that implements parts of {@link CombinationIterator}.
 *
 * @author Sebastian Krieter
 */
public abstract class ACombinationIterator implements CombinationIterator {

    protected final int t, n;
    protected final long numCombinations;

    protected long counter;

    public ACombinationIterator(int n, int t) {
        this.t = t;
        this.n = n;
        numCombinations = BinomialCalculator.computeBinomial(n, t);
    }

    public ACombinationIterator(int n, int t, BinomialCalculator binomialCalculator) {
        this.t = t;
        this.n = n;
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
        return computeNext();
    }

    @Override
    public void reset() {
        counter = 0;
    }

    @Override
    public long size() {
        return numCombinations;
    }

    protected abstract int[] computeNext();
}
