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

import de.featjar.base.data.Computation;
import de.featjar.base.data.FutureResult;
import de.featjar.formula.analysis.solver.SolverContradictionException;
import de.featjar.formula.analysis.solver.Solver;
import de.featjar.formula.assignment.VariableAssignment;

import java.util.Random;
import java.util.function.Function;

/**
 * Base class for an analysis using any {@link Solver solver}.
 *
 * @param <T> the type of the analysis result
 * @param <S> the type of the solver
 * @param <U> the type the solver operates on
 *
 * @author Sebastian Krieter
 */
public abstract class Analysis<T, S extends Solver, U> implements Computation<T> {
    public static final int DEFAULT_TIMEOUT_IN_MS = 0;
    public static final int DEFAULT_RANDOM_SEED = 0;
    protected final Computation<U> inputComputation;
    protected final Function<U, S> solverFactory; // todo: or use Computation<S>, which then has to be cloned before usage? this requires a general cloning mechanism for computation inputs (T implements Cloneable)
    protected final VariableAssignment assumptions;
    protected final long timeoutInMs;
    protected final Random random;

    protected Analysis(Computation<U> inputComputation, Function<U, S> solverFactory) {
        this(inputComputation, solverFactory, new VariableAssignment(), DEFAULT_TIMEOUT_IN_MS, DEFAULT_RANDOM_SEED);
    }

    protected Analysis(Computation<U> inputComputation, Function<U, S> solverFactory, VariableAssignment assumptions, long timeoutInMs, long randomSeed) {
        this.inputComputation = inputComputation;
        this.solverFactory = solverFactory;
        this.assumptions = assumptions;
        this.timeoutInMs = timeoutInMs;
        this.random = new Random(randomSeed);
    }

    protected FutureResult<S> getSolver() throws SolverContradictionException {
        return inputComputation.get().thenCompute((input, monitor) -> {
            S solver = solverFactory.apply(input); // need to clone input? probably note
            solver.setAssumptions(assumptions);
            solver.setTimeout(timeoutInMs);
            return solver;
        });
    }
}
