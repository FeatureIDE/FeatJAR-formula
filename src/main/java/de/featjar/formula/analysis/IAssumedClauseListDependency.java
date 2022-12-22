package de.featjar.formula.analysis;

import de.featjar.base.computation.Dependency;
import de.featjar.base.computation.IComputation;

/**
 * An analysis that can be passed a further list of clauses to assume.
 * Generalizes {@link IAssumedAssignmentDependency}, but is not supported by each analysis.
 * Assumes that the implementing class can be cast to {@link IComputation}.
 *
 * @param <T> type of the clause list
 */
public interface IAssumedClauseListDependency<T extends IAssignmentList<? extends IClause<?>>> {
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
    default IAssumedClauseListDependency<T> setAssumedClauseList(IComputation<T> clauseList) {
        getAssumedClauseListDependency().set((IComputation<?>) this, clauseList);
        return this;
    }
}
