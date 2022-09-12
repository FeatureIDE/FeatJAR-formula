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

import de.featjar.formula.analysis.solver.RuntimeContradictionException;
import de.featjar.formula.analysis.solver.Solver;
import de.featjar.formula.structure.Formula;
import de.featjar.formula.structure.assignment.Assignment;
import de.featjar.formula.structure.assignment.IndexAssignment;
import de.featjar.base.data.Computation;
import de.featjar.base.data.Result;
import de.featjar.base.task.Monitor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Base class for an analysis using any {@link Solver solver}.
 *
 * @param <U> the type of the analysis result.
 * @param <S> the type of the solver for this analysis.
 * @param <T> the type of the solver input.
 *
 * @author Sebastian Krieter
 */
public abstract class Analysis<U, S extends Solver, T> implements Computation<T, U> {

    protected static Object defaultParameters = new Object();

    protected final Assignment assumptions = new IndexAssignment();

    protected final List<Formula> assumedConstraints = new ArrayList<>();
    protected S solver;
    public void setSolver(S solver) {
        this.solver = solver;
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

    /*
     * 1. Create analysis with MR Create analysis with MR and params
     *
     * 2. Create Sub-analysis within other analysis Create Sub-analysis within other
     * analysis and params
     *
     */

    protected abstract S createSolver(T input) throws RuntimeContradictionException;

    protected void prepareSolver(S solver) {
        updateAssumptions();
    }

    private void updateAssumptions(S solver) {
        solver.getAssumptions().setAll(assumptions.getAll());
        solver.getDynamicFormula().push(assumedConstraints);
    }

    protected abstract U analyze(S solver, Monitor monitor) throws Exception;

    protected void resetSolver(S solver) {
        solver.getAssumptions().unsetAll(assumptions.getAll());
        solver.getDynamicFormula().pop(assumedConstraints.size());
    }
}
