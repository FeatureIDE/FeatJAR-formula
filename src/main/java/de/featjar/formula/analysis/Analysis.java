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

import de.featjar.base.data.Pair;
import de.featjar.formula.analysis.solver.RuntimeContradictionException;
import de.featjar.formula.analysis.solver.Solver;
import de.featjar.formula.structure.Expression;
import de.featjar.formula.assignment.Assignment;
import de.featjar.formula.assignment.IndexAssignment;
import de.featjar.base.data.Computation;
import de.featjar.base.data.Result;
import de.featjar.base.task.Monitor;
import de.featjar.formula.structure.formula.Formula;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Base class for an analysis using any {@link Solver solver}.
 *
 * @param <U> the type of the analysis result
 * @param <S> the type of the solver
 * @param <T> the type of the solver input
 *
 * @author Sebastian Krieter
 */
public abstract class Analysis<U, S extends Solver, T> implements Computation<T, U> {
    protected S solver;
    protected final Assignment<?> assumptions = new IndexAssignment();

    public Assignment<?> getAssumptions() {
        return assumptions;
    }

    public void updateAssumptions() {
        updateAssumptions(this.solver);
    }

    public void setSolver(S solver) {
        this.solver = solver;
    }

    @Override
    public final Result<U> execute(T input, Monitor monitor) {
        if (solver == null) {
            solver = createSolver(input);
        }
        return execute(solver, monitor);
    }

    public Result<U> execute(S solver, Monitor monitor) {
        if (this.solver == null) {
            this.solver = solver;
        }
        monitor.checkCancel();
        prepareSolver(solver);
        try {
            return Result.of(analyze(solver, monitor));
        } catch (final Exception e) {
            return Result.empty(e);
        } finally {
            resetSolver(solver);
        }
    }

    protected abstract S createSolver(T input) throws RuntimeContradictionException;

    protected void prepareSolver(S solver) {
        updateAssumptions();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void updateAssumptions(S solver) {
        solver.getAssumptions().set((Collection) assumptions.get());
    }

    protected abstract U analyze(S solver, Monitor monitor) throws Exception;

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void resetSolver(S solver) {
        solver.getAssumptions().remove((Collection) assumptions.get());
    }
}
