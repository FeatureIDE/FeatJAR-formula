package de.featjar.formula.analysis;

import de.featjar.base.computation.Dependency;
import de.featjar.base.computation.IComputation;

/**
 * An analysis that can be passed a further assignment to assume.
 * Assumes that the implementing class can be cast to {@link IComputation}.
 *
 * @param <T> the type of the assignment
 */
public interface IAssumedAssignmentDependency<T extends IAssignment<?>> {
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
    default IAssumedAssignmentDependency<T> setAssumedAssignment(IComputation<T> assignment) {
        getAssumedAssignmentDependency().set((IComputation<?>) this, assignment);
        return this;
    }
}
