package de.featjar.formula.analysis;

import de.featjar.formula.analysis.bool.BooleanAssignmentList;
import de.featjar.formula.analysis.value.ValueAssignmentList;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * A list of assignments.
 * Represents a {@link ClauseList} or a {@link SolutionList} in a {@link Solver}.
 * For a propositional implementation, see {@link BooleanAssignmentList},
 * for a first-order implementation, see {@link ValueAssignmentList}.
 *
 * @param <T> the type of the assignment
 * @author Elias Kuiter
 */
public interface AssignmentList<T extends Assignment<?>> {
    /**
     * {@return a list of all assignments in this assignment list}
     * The default implementations of the other methods assume that this list is mutable.
     * If it is not, the other methods must be overridden accordingly.
     */
    List<T> getAll();

    /**
     * {@return the number of assignments in this assignment list}
     */
    default int size() {
        return getAll().size();
    }

    /**
     * {@return whether this assignment list is empty}
     */
    default boolean isEmpty() {
        return getAll().isEmpty();
    }

    /**
     * {@return a stream of assignments in this assignment list}
     */
    default Stream<T> stream() {
        return getAll().stream();
    }

    /**
     * {@return the assignment at the given index in this assignment list, if any}
     *
     * @param index the index
     */
    default Optional<T> get(int index) {
        if (index < 0 || index >= size())
            return Optional.empty();
        return Optional.of(getAll().get(index));
    }

    /**
     * Adds the given assignment at the given index to this assignment list.
     *
     * @param index the index
     * @param assignment the assignment
     */
    default void add(int index, T assignment) {
        if (index < 0 || index > size())
            throw new IllegalArgumentException();
        getAll().add(index, assignment);
    }

    /**
     * Removes the assignment at the given index in this assignment list.
     *
     * @param index the index
     * @return the removed assignment, if any
     */
    default Optional<T> remove(int index) {
        if (index < 0 || index >= size())
            return Optional.empty();
        return Optional.of(getAll().remove(index));
    }

    /**
     * Adds the given assignment to the end of this assignment list.
     *
     * @param assignment the assignment
     */
    default void add(T assignment) {
        add(size(), assignment);
    }

    /**
     * Adds the given assignments to the end of this assignment list.
     *
     * @param assignments the assignments
     */
    default void addAll(Collection<T> assignments) {
        getAll().addAll(assignments);
    }

    /**
     * Removes the last assignment in this assignment list.
     *
     * @return the removed assignment, if any
     */
    default Optional<T> pop() {
        return remove(size() - 1);
    }

    /**
     * Removes all assignments in this assignment list.
     */
    default void clear() {
        getAll().clear();
    }
}
