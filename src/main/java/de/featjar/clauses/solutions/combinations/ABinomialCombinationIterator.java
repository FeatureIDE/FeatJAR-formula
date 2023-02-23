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

/**
 * Abstract iterator that implements parts of {@link CombinationIterator}.
 *
 * @author Sebastian Krieter
 */
public abstract class ABinomialCombinationIterator extends ACombinationIterator {

    protected final BinomialCalculator binomialCalculator;

    public ABinomialCombinationIterator(int size, int t) {
        this(size, t, new BinomialCalculator(size, t));
    }

    public ABinomialCombinationIterator(int size, int t, BinomialCalculator binomialCalculator) {
        super(size, t, binomialCalculator);
        this.binomialCalculator = binomialCalculator;
    }

    @Override
    protected int[] computeNext() {
        return binomialCalculator.combination(nextIndex());
    }

    protected abstract long nextIndex();
}
