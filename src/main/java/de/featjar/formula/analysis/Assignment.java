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

import de.featjar.formula.analysis.solver.Assumable;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;

/**
 * Assigns values to variable names.
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public class Assignment implements Assumable<String> {
    protected final LinkedHashMap<String, Object> variableNameToValue = new LinkedHashMap<>();

    public Assignment(Object... assignmentPairs) {
//        for (int i = 0; i < assignmentPairs.length; i += 2) {
//            set((String) assignmentPairs[i], assignmentPairs[i + 1]);
//        }
    }

//    @Override
//    public List<Pair<String, Object>> get() {
//        return variableNameToValue.entrySet().stream()
//                .map(Pair::of)
//                .collect(Collectors.toList());
//    }

    @Override
    public Optional<Object> get(String variableName) {
        Objects.requireNonNull(variableName);
        return Optional.ofNullable(variableNameToValue.get(variableName));
    }

//    @Override
//    public void set(String variableName, Object value) {
//        Objects.requireNonNull(variableName);
//        if (value == null) {
//            variableNameToValue.remove(variableName);
//        } else {
//            variableNameToValue.put(variableName, value);
//        }
//    }
//
//    @Override
//    public void clear() {
//        variableNameToValue.clear();
//    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (final Entry<String, Object> entry : variableNameToValue.entrySet()) {
            sb.append(entry.getKey());
            sb.append(": ");
            sb.append(entry.getValue());
            sb.append("\n");
        }
        return sb.toString();
    }
}
