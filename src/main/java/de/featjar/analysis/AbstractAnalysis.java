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
 * See <https://github.com/FeatJAR/formula> for further information.
 */
package de.featjar.analysis;

import de.featjar.analysis.solver.RuntimeContradictionException;
import de.featjar.analysis.solver.Solver;
import de.featjar.formula.structure.Formula;
import de.featjar.formula.structure.atomic.Assignment;
import de.featjar.formula.structure.atomic.IndexAssignment;
import de.featjar.util.data.Cache;
import de.featjar.util.data.Provider;
import de.featjar.util.data.Result;
import de.featjar.util.job.Executor;
import de.featjar.util.job.InternalMonitor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Base class for an analysis using any {@link Solver solver}.
 *
 * @param <T> Type of the analysis result.
 * @param <S> Type of the solver for this analysis.
 * @param <I> Type of the solver input.
 *
 * @author Sebastian Krieter
 */
public abstract class AbstractAnalysis<T, S extends Solver, I> implements Analysis<T>, Provider<T> {

    protected static Object defaultParameters = new Object();

    // TODO fix caching / improve handling of many results with different parameters
    @Override
    public boolean storeInCache() {
        return false;
    }

    @Override
    public Result<T> apply(Cache c, InternalMonitor m) {
        return Executor.run(this::execute, c, m);
    }

    protected final Assignment assumptions = new IndexAssignment();
    protected final List<Formula> assumedConstraints = new ArrayList<>();
    protected Provider<I> solverInputProvider;
    protected S solver;

    public void setSolver(S solver) {
        this.solver = solver;
    }

    public void setSolverInputProvider(Provider<I> solverInputProvider) {
        this.solverInputProvider = solverInputProvider;
    }

    public Assignment getAssumptions() {
        return assumptions;
    }

    public List<Formula> getAssumedConstraints() {
        return assumedConstraints;
    }

    public void updateAssumptions() {
        updateAssumptions(this.solver);
    }

    public Object getParameters() {
        return Arrays.asList(assumptions, assumedConstraints);
    }

    @Override
    public final T execute(Cache c, InternalMonitor monitor) {
        if (solver == null) {
            solver = createSolver(c.get(solverInputProvider).get());
        }
        return execute(solver, monitor);
    }

    public T execute(S solver, InternalMonitor monitor) {
        if (this.solver == null) {
            this.solver = solver;
        }
        monitor.checkCancel();
        prepareSolver(solver);
        try {
            return analyze(solver, monitor);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            resetSolver(solver);
        }
    }

    /*
     * 1. Create analysis with MR Create analysis with MR and params
     *
     * 2. Create Sub-analysis within other analysis Create Sub-analysis within other
     * analysis and params
     *
     */

    protected abstract S createSolver(I input) throws RuntimeContradictionException;

    protected void prepareSolver(S solver) {
        updateAssumptions();
    }

    private void updateAssumptions(S solver) {
        solver.getAssumptions().setAll(assumptions.getAll());
        solver.getDynamicFormula().push(assumedConstraints);
    }

    protected abstract T analyze(S solver, InternalMonitor monitor) throws Exception;

    protected void resetSolver(S solver) {
        solver.getAssumptions().unsetAll(assumptions.getAll());
        solver.getDynamicFormula().pop(assumedConstraints.size());
    }
}
