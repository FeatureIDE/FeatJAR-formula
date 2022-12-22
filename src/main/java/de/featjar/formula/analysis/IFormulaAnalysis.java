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

import de.featjar.base.computation.IComputation;
import de.featjar.base.computation.Dependency;

/**
 * Base class for an analysis performed by a {@link ISolver solver}.
 * Contains several mixins to control exactly what capabilities a concrete implementation has.
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public interface IFormulaAnalysis {
    /**
     * An analysis that can be passed a further assignment to assume.
     *
     * @param <T> the type of the assignment
     */
    interface WithAssumedAssignment<T extends IAssignment<?>> {
        Dependency<T> getAssumedAssignmentDependency();

        /**
         * {@return a computation for the assignment assumed by this analysis}
         * This analysis can freely interpret this assignment.
         * Usually, it is interpreted as a conjunction (i.e., similar to a {@link ISolution}).
         */
        default IComputation<T> getAssumedAssignment() {
            return getAssumedAssignmentDependency().get((IComputation<?>) this);
        }

        /**
         * Sets the computation for the assignment assumed by this analysis.
         *
         * @param assignment the assignment computation
         * @return this analysis
         */
        default WithAssumedAssignment<T> setAssumedAssignment(IComputation<T> assignment) {
            getAssumedAssignmentDependency().set((IComputation<?>) this, assignment);
            return this;
        }
    }

    /**
     * An analysis that can be passed a further list of clauses to assume.
     * Generalizes {@link WithAssumedAssignment}, but is not supported by each analysis.
     *
     * @param <T> type of the clause list
     */
    interface WithAssumedClauseList<T extends IAssignmentList<? extends IClause<?>>> {
        Dependency<T> getAssumedClauseListDependency();

        /**
         * {@return the computation for the clause list assumed by this analysis}
         * This analysis interprets this list of clauses as a conjunction of
         * disjunctions of literals or equalities (i.e., a CNF).
         */
        default IComputation<T> getAssumedClauseList() {
            return getAssumedClauseListDependency().get((IComputation<?>) this);
        }

        /**
         * Sets the computation for the clause list assumed by this analysis.
         *
         * @param clauseList the clause list computation
         * @return this analysis
         */
        default WithAssumedClauseList<T> setAssumedClauseList(IComputation<T> clauseList) {
            getAssumedClauseListDependency().set((IComputation<?>) this, clauseList);
            return this;
        }
    }
}
