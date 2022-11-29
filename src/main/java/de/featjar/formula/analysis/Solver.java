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
package de.featjar.formula.analysis;

import de.featjar.base.extension.Extension;

/**
 * Solves problems expressed as logical formulas.
 * Is capable of performing various basic {@link de.featjar.formula.analysis.Analysis analyses}.
 *
 * @param <T> the index type of the variables
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public interface Solver<T> extends Extension {
    /**
     * {@return an analysis that computes whether there is a solution for some given formula, if supported by this solver}
     * Satisfiability solvers support this method.
     */
    default HasSolutionAnalysis<?, ?> hasSolutionAnalysis() {
        return null;
    }

    /**
     * {@return an analysis that computes a solution for some given formula, if supported by this solver}
     * Solution solvers support this method.
     */
    default GetSolutionAnalysis<?, ?, ?> getSolutionAnalysis() {
        return null;
    }

    /**
     * {@return an analysis that computes the number of solutions for some given formula, if supported by this solver}
     * #SAT (SharpSAT) solvers support this method.
     */
    default CountSolutionsAnalysis<?, ?> countSolutionsAnalysis() {
        return null;
    }

    /**
     * {@return an analysis that computes all solutions for some given formula, if supported by this solver}
     * All-solution (AllSAT) solvers support this method.
     */
    default GetSolutionsAnalysis<?, ?, ?> getSolutionsAnalysis() {
        return null;
    }

    /**
     * {@return an analysis that computes a minimal unsatisfiable subset (MUS) for some given formula, if supported by this solver}
     * Solvers that extract a MUS from an unsatisfiable formula support this method.
     * A minimal unsatisfiable subset is any unsatisfiable subset of a formula that cannot be reduced any
     * further without becoming satisfiable, thus explaining why the formula is unsatisfiable.
     * This extraction is only possible when the given formula is not satisfiable.
     */
    default Void getMinimalUnsatisfiableSubsetAnalysis() {
        return null;
    }

    /**
     * {@return an analysis that computes all minimal unsatisfiable subset (MUS) for some given formula, if supported by this solver}
     */
    default Void getMinimalUnsatisfiableSubsetsAnalysis() {
        return null;
    }

    /**
     * {@return an analysis that computes the smallest value for a variable to still satisfy some given formula, if supported by this solver}
     * SMT (satisfiability modulo theories) solvers support this method.
     *
     * @param variable the variable to minimize
     */
    default Void minimizeAnalysis(T variable) {
        return null;
    }

    /**
     * {@return an analysis that computes the largest value for a variable to still satisfy some given formula, if supported by this solver}
     * SMT (satisfiability modulo theories) solvers support this method.
     *
     * @param variable the variable to maximize
     */
    default Void maximizeAnalysis(T variable) {
        return null;
    }
}
