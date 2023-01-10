/*
 * Copyright (C) 2023 Sebastian Krieter, Elias Kuiter
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
 * See <https://github.com/FeatureIDE/FeatJAR-formula> for further information.
 */
package de.featjar.formula.analysis;

import de.featjar.base.computation.IAnalysis;
import de.featjar.base.computation.ITimeoutDependency;

/**
 * Computes all solutions for a given formula.
 * Allows setting an optional timeout.
 * Allows passing an assignment with additional assumptions to make when solving the formula.
 * May return a subset of solutions with a warning if the timeout is reached.
 *
 * @param <T> the type of the analysis input
 * @param <U> the type of the analysis result
 * @param <R> the type of the assignment
 * @author Elias Kuiter
 */
public interface ISolutionsAnalysis<T, U extends IAssignmentList<? extends ISolution<?>>, R extends IAssignment<?>>
        extends IAnalysis<T, U>, ITimeoutDependency, IAssumedAssignmentDependency<R> {}
