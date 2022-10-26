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

import de.featjar.formula.assignment.VariableAssignment;
import de.featjar.formula.structure.formula.Formula;

/**
 * A solver that analyzes a given formula.
 *
 * @author Sebastian Krieter
 */
public interface Solver {

    /**
     * {@return the formula analyzed by this solver}
     */
    Formula getFormula();

    /**
     * {@return the formula analyzed by this solver in the solver's internal format}
     */
    SolverFormula<?> getSolverFormula();

    /**
     * {@return additional assumptions this solver should consider}
     */
    VariableAssignment getAssumptions();

    /**
     * Sets additional assumptions this solver should consider.
     *
     * @param assumptions the assumptions in form of a (partial) variable assignment
     */
    void setAssumptions(VariableAssignment assumptions) throws SolverContradictionException; // todo: exception needed?

    long getTimeout();

    void setTimeout(long timeoutInMs);

    /**
     * Resets any internal state of this solver.
     * Should be overridden to allow for reusing this solver instance. todo: is this a good idea with multithreading? or rather use solverSupplier in Analysis?
     */
    default void reset() {}
}
