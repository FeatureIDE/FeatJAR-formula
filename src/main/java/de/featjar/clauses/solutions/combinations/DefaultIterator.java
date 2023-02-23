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
 * Combination iterator that uses the combinatorial number system to enumerate
 * all combinations and then iterates from first to last combination.
 *
 * @author Sebastian Krieter
 */
public class DefaultIterator extends ABinomialCombinationIterator {

    public DefaultIterator(int t, int size) {
        super(size, t);
    }

    @Override
    protected long nextIndex() {
        return counter - 1;
    }
}
