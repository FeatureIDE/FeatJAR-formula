/*
 * Copyright (C) 2023 FeatJAR-Development-Team
 *
 * This file is part of FeatJAR-formula.
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
 * See <https://github.com/FeatJAR> for further information.
 */
package de.featjar.formula.analysis;

import de.featjar.base.computation.Dependency;
import de.featjar.base.computation.IComputation;
import de.featjar.base.computation.IDependent;

/**
 * An analysis that can be passed a further list of clauses to assume.
 * Generalizes {@link IAssumedAssignmentDependency}, but is not supported by each analysis.
 * Assumes that the implementing class can be cast to {@link IComputation}.
 *
 * @param <T> type of the clause list
 */
public interface IAssumedClauseListDependency<T extends IAssignmentList<? extends IClause<?, ?>>> extends IDependent {
    Dependency<T> getAssumedClauseListDependency();

    /**
     * {@return the computation for the clause list assumed by this analysis}
     * This analysis interprets this list of clauses as a conjunction of
     * disjunctions of literals or equalities (i.e., a CNF).
     */
    default IComputation<T> getAssumedClauseList() {
        return getDependency(getAssumedClauseListDependency()).orElse(null);
    }

    /**
     * Sets the computation for the clause list assumed by this analysis.
     *
     * @param clauseList the clause list computation
     * @return this analysis
     */
    default IAssumedClauseListDependency<T> setAssumedClauseList(IComputation<T> clauseList) {
        setDependencyComputation(getAssumedClauseListDependency(), clauseList);
        return this;
    }
}
