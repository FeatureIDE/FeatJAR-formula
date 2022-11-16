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

import de.featjar.base.data.Problem;
import de.featjar.base.data.Result;

/**
 * A satisfiability solver that returns some solution for a given formula.
 *
 * @param <T> the type of the returned solution
 * @author Sebastian Krieter
 */
public interface SolutionSolver<T> extends SATSolver {

    /**
     * {@return the last solution for the given formula found by this solver}
     * Can only be called after a successful {@link #hasSolution()} call.
     */
    Result<T> getSolution();

    /**
     * {@return a solution for the given formula, if any}
     */
    default Result<T> findSolution() {
        return hasSolution()
                .flatMap(s -> s.equals(true)
                        ? getSolution()
                        : Result.empty(new Problem("has no solution", Problem.Severity.ERROR)));
    }
}
