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

import java.util.*;

/**
 * A solver capable of extracting a minimal unsatisfiable subset (MUS) from a
 * propositional formula. The extraction of the subset is only possible when the
 * current problem is not satisfiable.
 *
 * @author Joshua Sprey
 * @author Timo Günther
 * @author Sebastian Krieter
 */
public interface MusSolver<T> extends SatSolver {

	/**
	 * <p>
	 * Returns any minimal unsatisfiable subset (MUS) of the problem. A MUS is any
	 * unsatisfiable subset of the formula which cannot be reduced any further
	 * without becoming satisfiable.
	 * </p>
	 *
	 * <p>
	 * A MUS can act as an explanation for why a formula is unsatisfiable. As such,
	 * the problem must be unsatisfiable for a MUS to be found.
	 * </p>
	 *
	 * @return any minimal unsatisfiable subset; not null
	 * @throws IllegalStateException if the formula in this solver is satisfiable
	 */
	List<T> getMinimalUnsatisfiableSubset() throws IllegalStateException;

	/**
	 * Returns all minimal unsatisfiable subsets of the problem.
	 *
	 * @return all minimal unsatisfiable subsets of the problem
	 * @throws IllegalStateException if the formula in this solver is satisfiable
	 */
	List<List<T>> getAllMinimalUnsatisfiableSubsets() throws IllegalStateException;

}
