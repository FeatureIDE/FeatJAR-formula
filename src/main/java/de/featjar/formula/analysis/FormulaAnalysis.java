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

import de.featjar.base.computation.Computable;
import de.featjar.base.computation.Dependency;

/**
 * Base class for an analysis performed by a {@link Solver solver}.
 * Contains several mixins to control exactly what capabilities a concrete implementation has.
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public interface FormulaAnalysis {
    /**
     * An analysis that can be passed a further assignment to assume.
     *
     * @param <T> the type of the assignment
     */
    interface WithAssumedAssignment<T extends Assignment<?>> {
        Dependency<T> getAssumedAssignmentDependency();

        /**
         * {@return a computation for the assignment assumed by this analysis}
         * This analysis can freely interpret this assignment.
         * Usually, it is interpreted as a conjunction (i.e., similar to a {@link Solution}).
         */
        default Computable<T> getAssumedAssignment() {
            return getAssumedAssignmentDependency().get((Computable<?>) this);
        }

        /**
         * Sets the computation for the assignment assumed by this analysis.
         *
         * @param assignment the assignment computation
         * @return itself
         */
        default WithAssumedAssignment<T> setAssumedAssignment(Computable<T> assignment) {
            getAssumedAssignmentDependency().set((Computable<?>) this, assignment);
            return this;
        }
    }

    /**
     * An analysis that can be passed a further list of clauses to assume.
     * Generalizes {@link WithAssumedAssignment}, but is not supported by each analysis.
     *
     * @param <T> type of the clause list
     */
    interface WithAssumedClauseList<T extends AssignmentList<? extends Clause<?>>> {
        Dependency<T> getAssumedClauseListDependency();

        /**
         * {@return the computation for the clause list assumed by this analysis}
         * This analysis interprets this list of clauses as a conjunction of
         * disjunctions of literals or equalities (i.e., a CNF).
         */
        default Computable<T> getAssumedClauseList() {
            return getAssumedClauseListDependency().get((Computable<?>) this);
        }

        /**
         * Sets the computation for the clause list assumed by this analysis.
         *
         * @param clauseList the clause list computation
         * @return itself
         */
        default WithAssumedClauseList<T> setAssumedClauseList(Computable<T> clauseList) {
            getAssumedClauseListDependency().set((Computable<?>) this, clauseList);
            return this;
        }
    }
}
