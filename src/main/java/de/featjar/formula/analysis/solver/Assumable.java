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
package de.featjar.formula.analysis.solver;

import de.featjar.base.data.Pair;

import java.util.List;
import java.util.Optional;

/**
 * Assumes values for {@link de.featjar.formula.structure.term.value.Variable variables}.
 * Can be used to assume additional facts in any {@link de.featjar.formula.analysis.solver.Solver}.
 *
 * @param <T> the index type of the variable
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public interface Assumable<T> {
    /**
     * {@return all variables and their assigned values in this assumable}
     */
    //List<Pair<T, Object>> getAll();

    /**
     * {@return the value assigned to a variable in this assumable}
     *
     * @param variable the variable
     */
    Optional<Object> get(T variable);

//    /**
//     * Assigns a value to a variable in this assumable.
//     *
//     * @param variable the variable
//     * @param value the value
//     */
//    void set(T variable, Object value);
//
//    /**
//     * Assigns values to variables in this assumable.
//     *
//     * @param assignmentPairs the assignment pairs
//     */
//    default void set(Collection<Pair<T, Object>> assignmentPairs) {
//        for (final Pair<T, Object> pair : assignmentPairs) {
//            set(pair.getKey(), pair.getValue());
//        }
//    }
//
//    /**
//     * Removes a variable in this assumable.
//     *
//     * @param variable the variable
//     */
//    default void remove(T variable) {
//        set(variable, null);
//    }
//
//    /**
//     * Removes variables in this assumable.
//     *
//     * @param variables the variables
//     */
//    default void remove(Collection<T> variables) {
//        for (final T variable : variables) {
//            set(variable, null);
//        }
//    }
//
//    /**
//     * Clears this assumable.
//     */
//    void clear();
}
