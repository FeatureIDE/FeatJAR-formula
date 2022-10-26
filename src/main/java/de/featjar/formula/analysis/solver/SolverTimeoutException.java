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
 * Thrown when a {@link Solver} experiences a timeout.
 * Doesn't need to be caught explicitly.
 *
 * @author Sebastian Krieter
 */
public class SolverTimeoutException extends RuntimeException { // todo: return this in Solver as Result.empty(timeout)

    public SolverTimeoutException() {
    }

    public SolverTimeoutException(String message) {
        super(message);
    }

    public SolverTimeoutException(Throwable cause) {
        super(cause);
    }

    public SolverTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
