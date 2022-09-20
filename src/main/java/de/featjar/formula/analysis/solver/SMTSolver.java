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
package de.featjar.formula.analysis.solver;

/**
 * An SMT (satisfiability modulo theories) solver.
 * Answers optimization queries for a given variable.
 *
 * @param <T> the type of the variable to optimize
 * @param <U> the type of the returned optimization result
 * @author Joshua Sprey
 * @author Sebastian Krieter
 */
public interface SMTSolver<T, U> extends Solver {

    /**
     * {@return the smallest value for a variable to still satisfy the given formula}
     * @param variable the variable to minimize
     */
    U minimize(T variable);

    /**
     * {@return the largest value for a variable to still satisfy the given formula}
     * @param variable the variable to maximize
     */
    U maximize(T variable);
}
