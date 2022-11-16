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

import de.featjar.base.data.Computation;

import java.util.Optional;
import java.util.Random;

/**
 * Base class for an analysis performed by a {@link Solver solver}.
 * Contains several mixins to control exactly what capabilities a concrete implementation has.
 *
 * @param <T> the type of the (primary) analysis input
 * @param <U> the type of the analysis result
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public interface Analysis<T, U> extends Computation<U> {
    /**
     * {@return the input computation for this analysis}
     */
    Computation<T> getInputComputation();

    /**
     * A potentially long-running analysis that can be canceled if a given time has passed.
     *
     * @param <T> the type of the (primary) analysis input
     * @param <U> the type of the analysis result
     */
    interface WithTimeout<T, U> extends Analysis<T, U> {
        /**
         * {@return the timeout of this analysis in milliseconds, if any}
         */
        Optional<Integer> getTimeout();
    }

    /**
     * An analysis that can be passed a list of further assumptions that should be made.
     *
     * @param <T> the type of the (primary) analysis input
     * @param <U> the type of the analysis result
     * @param <R> the index type of the variables
     */
    interface WithAssumptions<T, U, R> extends Analysis<T, U> {
        /**
         * {@return the list of the additional assumptions made by this analysis}
         */
        ClauseList<R> getAssumptionClauseList();
    }

    /**
     * An analysis that may need to generate pseudorandom numbers.
     *
     * @param <T> the type of the (primary) analysis input
     * @param <U> the type of the analysis result
     */
    interface WithRandom<T, U> extends Analysis<T, U> {
        /**
         * The default seed for the pseudorandom number generator returned by {@link #getRandom()}, if not specified otherwise.
         */
        int DEFAULT_RANDOM_SEED = 0;

        /**
         * {@return the pseudorandom number generator of this analysis}
         */
        Random getRandom();
    }
}
