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
 * An analysis that can be passed a further assignment to assume.
 * Assumes that the implementing class can be cast to {@link IComputation}.
 *
 * @param <T> the type of the assignment
 */
public interface IAssumedAssignmentDependency<T extends IAssignment<?, ?>> extends IDependent {
    Dependency<T> getAssumedAssignmentDependency();

    /**
     * {@return a computation for the assignment assumed by this analysis}
     * This analysis can freely interpret this assignment.
     * Usually, it is interpreted as a conjunction (i.e., similar to a {@link ISolution}).
     */
    default IComputation<T> getAssumedAssignment() {
        return getDependency(getAssumedAssignmentDependency()).orElse(null);
    }

    /**
     * Sets the computation for the assignment assumed by this analysis.
     *
     * @param assignment the assignment computation
     * @return this analysis
     */
    default IAssumedAssignmentDependency<T> setAssumedAssignment(IComputation<T> assignment) {
        setDependencyComputation(getAssumedAssignmentDependency(), assignment);
        return this;
    }
}
