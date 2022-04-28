/* -----------------------------------------------------------------------------
 * Formula Lib - Library to represent and edit propositional formulas.
 * Copyright (C) 2021  Sebastian Krieter
 * 
 * This file is part of Formula Lib.
 * 
 * Formula Lib is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Formula Lib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Formula Lib.  If not, see <https://www.gnu.org/licenses/>.
 * 
 * See <https://github.com/skrieter/formula> for further information.
 * -----------------------------------------------------------------------------
 */
package org.spldev.analysis.solver;

/**
 * Sat solver interface that is able to return a solution.
 *
 * @author Sebastian Krieter
 */
public interface SolutionSolver<T> extends SatSolver {

	/**
	 * Returns the last solution found by satisfiability solver. Can only be called
	 * after a successful call of {@link #hasSolution()}.
	 *
	 * @return A representation of a satisfying assignment.
	 *
	 * @see #hasSolution()
	 * @see #findSolution()
	 */
	T getSolution();

	/**
	 * Computes and returns a solution. This is a convenience method that is
	 * equivalent to calling {@link #hasSolution()} and {@link #getSolution()} in
	 * succession.
	 *
	 * @return A representation of a satisfying assignment.
	 *
	 * @see #hasSolution()
	 * @see #findSolution()
	 */
	default T findSolution() {
		return hasSolution() == SatResult.TRUE ? getSolution() : null;
	}

}
