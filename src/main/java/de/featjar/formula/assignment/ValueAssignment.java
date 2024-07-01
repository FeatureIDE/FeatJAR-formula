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

import de.featjar.base.data.Result;
import de.featjar.formula.VariableMap;
import java.util.LinkedHashMap;

/**
 * Primary implementation of {@link AValueAssignment}.
 * To be used when neither CNF nor DNF semantics are associated with an assignment.
 *
 * @author Elias Kuiter
 */
public class ValueAssignment extends AValueAssignment {
    public ValueAssignment() {}

    public ValueAssignment(Assignment assignment, VariableMap map) {
        super();
        assignment.getAll().forEach((key, value) -> {
            Result<Integer> index = map.get(key);

            if (index.isEmpty()) {
                throw new IllegalArgumentException("Variable " + key + " does not exist in the variable map.");
            }
            variableValuePairs.put(index.get(), value);
        });
    }

    public ValueAssignment(LinkedHashMap<Integer, Object> variableValuePairs) {
        super(variableValuePairs);
    }

    public ValueAssignment(ValueAssignment predicateClause) {
        this(new LinkedHashMap<>(predicateClause.variableValuePairs));
    }

    public ValueAssignment(Object... variableValuePairs) {
        super(variableValuePairs);
    }

    @Override
    public Result<BooleanAssignment> toBoolean() {
        return VariableMap.toBoolean(this);
    }

    @Override
    public String toString() {
        return String.format("ValueClause[%s]", print());
    }
}
