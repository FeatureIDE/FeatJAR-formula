package de.featjar.formula.analysis;

import de.featjar.base.data.Pair;
import de.featjar.formula.analysis.bool.BooleanAssignment;
import de.featjar.formula.analysis.value.ValueAssignment;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Assigns values to {@link de.featjar.formula.structure.term.value.Variable variables}.
 * Represents a {@link Clause} or a {@link Solution} in a {@link Solver}.
 * For a propositional implementation, see {@link BooleanAssignment},
 * for a first-order implementation, see {@link ValueAssignment}.
 *
 * @param <T> the index type of the variables
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public interface Assignment<T> {
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
    default Optional<Object> getValue(T variable) {
        return Optional.ofNullable(getAll().get(variable));
    }

    //TODO: should assignments be mutable?
//    /**
//     * Assigns a given value to the given variable.
//     *
//     * @param variable the variable
//     * @param value the value
//     */
//    default void set(T variable, Object value) {
//        getAll().put(variable, value);
//    }
//
//    /**
//     * Removes the variable-value pair for the given variable in this assignment.
//     *
//     * @param variable the variable
//     * @return the removed variable's assigned value, if any
//     */
//    default Optional<Object> remove(T variable) {
//        return Optional.ofNullable(getAll().remove(variable));
//    }
//
//    /**
//     * Removes all variable-value pairs in this assignment.
//     */
//    default void clear() {
//        getAll().clear();
//    }
}
