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
 * See <https://github.com/FeatJAR/formula> for further information.
 */
package de.featjar.clauses.solutions.metrics;

import de.featjar.clauses.LiteralList;

/**
 * Computes the number of positive literals in a solution.
 *
 * @author Sebastian Krieter
 */
public class PositiveCount implements CountFunction {

    @Override
    public double compute(LiteralList literals) {
        return (double) literals.countPositive() / literals.size();
    }

    @Override
    public String getName() {
        return "Positive";
    }
}
