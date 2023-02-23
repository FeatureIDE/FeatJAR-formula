/*
 * Copyright (C) 2023 Sebastian Krieter, Elias Kuiter
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
package de.featjar.analysis.solver;

import java.util.Collection;

/**
 * Handles assumptions for solvers.
 *
 * @param <T> the type of the assumptions
 *
 * @author Sebastian Krieter
 */
public interface Assumptions<T> {

    void push(T assumption);

    default void pushAll(Collection<? extends T> assumptions) {
        for (final T assumption : assumptions) {
            push(assumption);
        }
    }

    default void replaceLast(T assumption) {
        pop();
        push(assumption);
    }

    T peek();

    T pop();

    default void pop(int size) {
        for (int i = 0; i < size; i++) {
            pop();
        }
    }

    void clear();

    default void clear(int newSize) {
        pop(size() - newSize);
    }

    int size();
}
