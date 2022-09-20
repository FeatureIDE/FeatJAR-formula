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

import java.util.List;
import java.util.Optional;

/**
 * A solver that extracts a minimal unsatisfiable subset (MUS) from a formula.
 * A minimal unsatisfiable subset is any unsatisfiable subset of a formula that cannot be reduced any
 * further without becoming satisfiable, thus explaining why the formula is unsatisfiable.
 * This extraction is only possible when the given formula is not satisfiable.
 *
 * @author Joshua Sprey
 * @author Timo Günther
 * @author Sebastian Krieter
 */
public interface MUSSolver<T> extends SATSolver {

    /**
     * {@return a minimal unsatisfiable subset (MUS) for the given formula, if any}
     */
    Optional<List<T>> getMinimalUnsatisfiableSubset();

    /**
     * {@return all minimal unsatisfiable subsets (MUS) for the given formula}
     */
    List<List<T>> getAllMinimalUnsatisfiableSubsets();
}
