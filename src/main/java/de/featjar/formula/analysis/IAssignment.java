package de.featjar.formula.analysis;

import de.featjar.base.data.Pair;
import de.featjar.base.data.Result;
import de.featjar.formula.analysis.bool.BooleanAssignment;
import de.featjar.formula.analysis.value.ValueAssignment;
import java.util.LinkedHashMap;
import java.util.stream.Stream;

/**
 * Assigns values to {@link de.featjar.formula.structure.term.value.Variable variables}.
 * Represents a {@link IClause} or a {@link ISolution} in a {@link ISolver}.
 * For a propositional implementation, see {@link BooleanAssignment},
 * for a first-order implementation, see {@link ValueAssignment}.
 *
 * @param <T> the index type of the variables
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public interface IAssignment<T> {
    /**
     * {@return an ordered map of all variable-value pairs in this assignment}
     * The default implementations of the other methods assume that this map is mutable.
     * If it is not, the other methods must be overridden accordingly.
     * Undefined variables (e.g., for partial assignments) are omitted.
     */
    LinkedHashMap<T, Object> getAll();

    /**
     * {@return the number of variable-value pairs in this assignment}
     */
    default int size() {
        return getAll().size();
    }

    /**
     * {@return whether this assignment is empty}
     */
    default boolean isEmpty() {
        return size() == 0;
    }

    /**
     * {@return a stream of variable-value pairs in this assignment}
     */
    default Stream<Pair<T, Object>> stream() {
        return getAll().entrySet().stream().map(Pair::of);
    }

    /**
     * {@return the value assigned to the given variable, if any}
     *
     * @param variable the variable
     */
    default Result<Object> getValue(T variable) {
        return Result.ofNullable(getAll().get(variable));
    }

    /**
     * {@return an assignment with the same contents of this assignment}
     */
    IAssignment<T> toAssignment();

    /**
     * {@return a clause with the same contents of this assignment}
     */
    IClause<T> toClause();

    /**
     * {@return a solution with the same contents of this assignment}
     */
    ISolution<T> toSolution();
}
