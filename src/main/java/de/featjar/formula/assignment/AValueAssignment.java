/*
 * Copyright (C) 2024 FeatJAR-Development-Team
 *
 * This file is part of FeatJAR-formula.
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

import de.featjar.analysis.ISolver;
import de.featjar.base.data.Maps;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Assigns values of any type to string-identified {@link de.featjar.formula.structure.term.value.Variable variables}.
 * Can be used to represent a set of equalities for use in an SMT {@link ISolver}.
 *
 * @author Elias Kuiter
 */
public abstract class AValueAssignment implements IAssignment<Integer, Object>, IValueRepresentation {
    final LinkedHashMap<Integer, Object> variableValuePairs;

    public AValueAssignment() {
        this(Maps.empty());
    }

    public AValueAssignment(LinkedHashMap<Integer, Object> variableValuePairs) {
        this.variableValuePairs = variableValuePairs;
    }

    public AValueAssignment(Object... variableValuePairs) {
        this.variableValuePairs = Maps.empty();
        if (variableValuePairs.length % 2 == 1)
            throw new IllegalArgumentException("expected a list of variable-value pairs for this value assignment");
        for (int i = 0; i < variableValuePairs.length; i += 2) {
            this.variableValuePairs.put((Integer) variableValuePairs[i], variableValuePairs[i + 1]);
        }
    }

    public AValueAssignment(AValueAssignment valueAssignment) {
        this(new LinkedHashMap<>(valueAssignment.variableValuePairs));
    }

    @Override
    public ValueAssignment toAssignment() {
        return new ValueAssignment(variableValuePairs);
    }

    @Override
    public ValueClause toClause() {
        return new ValueClause(variableValuePairs);
    }

    @Override
    public ValueSolution toSolution() {
        return new ValueSolution(variableValuePairs);
    }

    @Override
    public Map<Integer, Object> getAll() {
        return Collections.unmodifiableMap(variableValuePairs);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AValueAssignment that = (AValueAssignment) o;
        return Objects.equals(variableValuePairs, that.variableValuePairs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variableValuePairs);
    }

    @Override
    public String print() {
        return variableValuePairs.entrySet().stream()
                .map(e -> {
                    Integer variable = e.getKey();
                    Object value = e.getValue();
                    if (value instanceof Boolean) {
                        return String.format("%s%s", ((boolean) value) ? "+" : "-", variable);
                    } else {
                        return String.format("%s=%s", variable, value);
                    }
                })
                .collect(Collectors.joining(","));
    }
}
