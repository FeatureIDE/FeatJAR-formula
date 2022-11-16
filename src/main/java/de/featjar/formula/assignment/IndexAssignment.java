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
package de.featjar.formula.assignment;

import de.featjar.base.data.Pair;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * Assigns values to variable indices.
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public class IndexAssignment implements Assignment<Integer> {
    protected final LinkedHashMap<Integer, Object> indexToValue = new LinkedHashMap<>();

    public IndexAssignment(Object... assignmentPairs) {
        for (int i = 0; i < assignmentPairs.length; i += 2) {
            set((Integer) assignmentPairs[i], assignmentPairs[i + 1]);
        }
    }

    @Override
    public List<Pair<Integer, Object>> get() {
        return indexToValue.entrySet().stream()
                .map(Pair::of)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Object> get(Integer index) {
        Objects.requireNonNull(index);
        return Optional.ofNullable(indexToValue.get(index));
    }

    @Override
    public void set(Integer index, Object value) {
        Objects.requireNonNull(index);
        if (index > 0) {
            if (value == null) {
                indexToValue.remove(index);
            } else {
                indexToValue.put(index, value);
            }
        }
    }

    @Override
    public void clear() {
        indexToValue.clear();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (final Entry<Integer, Object> entry : indexToValue.entrySet()) {
            sb.append(entry.getKey());
            sb.append(": ");
            sb.append(entry.getValue());
            sb.append("\n");
        }
        return sb.toString();
    }
}
