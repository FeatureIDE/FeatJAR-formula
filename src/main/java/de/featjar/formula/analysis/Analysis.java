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
import de.featjar.base.data.FutureResult;
import de.featjar.base.data.Pair;
import de.featjar.base.data.Result;

import java.util.Optional;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;

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
    // todo: allow reporting of partial results (e.g., for generating samples)

    /**
     * {@return the input computation of this analysis}
     * This analysis uses the result of this computation as its primary input (e.g., the formula to analyze).
     */
    Computation<T> getInput();

    /**
     * Sets the input computation of this analysis.
     *
     * @param input the input computation
     * @return itself
     */
    Analysis<T, U> setInput(Computation<T> input);

    /**
     * An analysis that can be passed a further assignment to assume.
     *
     * @param <T> the type of the assignment
     */
    interface WithAssumedAssignment<T extends Assignment<?>> {
        /**
         * {@return a computation for the assignment assumed by this analysis}
         * This analysis can freely interpret this assignment.
         * Usually, it is interpreted as a conjunction (i.e., similar to a {@link Solution}).
         */
        Computation<T> getAssumedAssignment();

        /**
         * Sets the computation for the assignment assumed by this analysis.
         *
         * @param assignment the assignment computation
         * @return itself
         */
        WithAssumedAssignment<T> setAssumedAssignment(Computation<T> assignment);
    }

    /**
     * An analysis that can be passed a further list of clauses to assume.
     * Generalizes {@link WithAssumedAssignment}, but is not supported by each analysis.
     *
     * @param <T> type of the clause list
     */
    interface WithAssumedClauseList<T extends AssignmentList<? extends Clause<?>>> {
        /**
         * {@return the computation for the clause list assumed by this analysis}
         * This analysis interprets this list of clauses as a conjunction of
         * disjunctions of literals or equalities (i.e., a CNF).
         */
        Computation<T> getAssumedClauseList();

        /**
         * Sets the computation for the clause list assumed by this analysis.
         *
         * @param clauseList the clause list computation
         * @return itself
         */
        WithAssumedClauseList<T> setAssumedClauseList(Computation<T> clauseList);
    }

    /**
     * A potentially long-running analysis that can be canceled if a given time has passed.
     */
    interface WithTimeout { // todo: how to handle partial results?
        /**
         * {@return the timeout of this analysis in milliseconds, if any}
         * This analysis terminates with an empty {@link de.featjar.base.data.Result} when it has
         * not terminated until the timeout passes.
         */
        Optional<Long> getTimeout();

        /**
         * Sets the timeout of this analysis in milliseconds.
         *
         * @param timeout the timeout in milliseconds, if any
         * @return itself
         */
        WithTimeout setTimeout(Long timeout);
    }

    /**
     * An analysis that may need to generate pseudorandom numbers.
     */
    interface WithRandom {
        /**
         * The default seed for the pseudorandom number generator returned by {@link #getRandom()}, if not specified otherwise.
         */
        long DEFAULT_RANDOM_SEED = 0;

        /**
         * {@return the pseudorandom number generator of this analysis}
         */
        Random getRandom();

        /**
         * Sets the pseudorandom number generator of this analysis.
         *
         * @param random the pseudorandom number generator
         * @return itself
         */
        WithRandom setRandom(Random random);

        /**
         * Sets the pseudorandom number generator of this analysis based on a given seed.
         * Uses Java's default PRNG implementation.
         * If no seed is given, uses the default seed.
         *
         * @param seed the seed
         * @return itself
         */
        default WithRandom setRandom(Long seed) {
            setRandom(new Random(seed != null ? seed : DEFAULT_RANDOM_SEED));
            return this;
        }
    }
}
