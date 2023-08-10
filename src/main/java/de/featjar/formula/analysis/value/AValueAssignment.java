/*
 * Copyright (C) 2023 FeatJAR-Development-Team
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
 * See <https://github.com/FeatJAR> for further information.
 */
package de.featjar.formula.analysis.value;

import de.featjar.base.data.Maps;
import de.featjar.formula.analysis.IAssignment;
import de.featjar.formula.analysis.ISolver;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Objects;

/**
 * Assigns values of any type to string-identified {@link de.featjar.formula.structure.term.value.Variable variables}.
 * Can be used to represent a set of equalities for use in an SMT {@link ISolver}.
 *
 * @author Elias Kuiter
 */
public abstract class AValueAssignment implements IAssignment<String, Object>, IValueRepresentation {
    final LinkedHashMap<String, Object> variableValuePairs;

    public AValueAssignment() {
        this(Maps.empty());
    }

    public AValueAssignment(LinkedHashMap<String, Object> variableValuePairs) {
        this.variableValuePairs = variableValuePairs;
    }

    public AValueAssignment(Object... variableValuePairs) {
        this.variableValuePairs = Maps.empty();
        if (variableValuePairs.length % 2 == 1)
            throw new IllegalArgumentException("expected a list of variable-value pairs for this value assignment");
        for (int i = 0; i < variableValuePairs.length; i += 2) {
            this.variableValuePairs.put((String) variableValuePairs[i], variableValuePairs[i + 1]);
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
    public LinkedHashSet<String> getVariableNames() {
        return new LinkedHashSet<>(variableValuePairs.keySet());
    }

    @Override
    public LinkedHashMap<String, Object> getAll() {
        return variableValuePairs;
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

    public abstract String print();
}
