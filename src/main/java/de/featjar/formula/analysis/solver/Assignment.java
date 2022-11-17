package de.featjar.formula.analysis.solver;

import java.util.Map;
import java.util.Optional;

/**
 * Assign values to {@link de.featjar.formula.structure.term.value.Variable variables}.
 * Can be used to assume additional facts in any {@link de.featjar.formula.analysis.solver.Solver}.
 *
 * @param <T> the index type of the variable
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public interface Assignment<T> {
    Map<T, Object> getAll();

    Optional<Object> get(T variable);
}
