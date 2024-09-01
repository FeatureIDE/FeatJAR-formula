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
import java.util.Collection;

/**
 * Primary implementation of {@link ABooleanAssignment}. To be used when neither
 * CNF nor DNF semantics are associated with an assignment.
 *
 * @author Elias Kuiter
 */
public class BooleanAssignment extends ABooleanAssignment {

    private static final long serialVersionUID = 1614980283996088122L;

    public BooleanAssignment(Assignment assignment, VariableMap map) {
        super(new int[assignment.size()]);
        int i = 0;
        for (var keyValue : assignment.getAll().entrySet()) {
            String key = keyValue.getKey();

            Result<Integer> index = map.get(key);
            if (index.isEmpty()) {
                throw new IllegalArgumentException("Variable " + key + " does not exist in the variable map.");
            }
            elements[i++] = index.get();
        }
    }

    public BooleanAssignment(int... integers) {
        super(integers);
    }

    public BooleanAssignment(Collection<Integer> integers) {
        super(integers);
    }

    public BooleanAssignment(BooleanAssignment booleanAssignment) {
        super(booleanAssignment);
    }

    @Override
    public ValueAssignment toValue() {
        return VariableMap.toValue(this);
    }

    @Override
    public String toString() {
        return String.format("BooleanAssignment[%s]", print());
    }

    @Override
    public BooleanAssignment toAssignment() {
        return this;
    }

    @Override
    public BooleanAssignment inverse() {
        return new BooleanAssignment(negate());
    }

    @Override
    public BooleanAssignment addAll(ABooleanAssignment integers) {
        return new BooleanAssignment(addAll(integers.get()));
    }

    @Override
    public BooleanAssignment retainAll(ABooleanAssignment integers) {
        return new BooleanAssignment(retainAll(integers.get()));
    }

    @Override
    public BooleanAssignment retainAllVariables(ABooleanAssignment integers) {
        return new BooleanAssignment(retainAllVariables(integers.get()));
    }

    @Override
    public BooleanAssignment removeAll(ABooleanAssignment integers) {
        return new BooleanAssignment(removeAll(integers.get()));
    }

    @Override
    public BooleanAssignment removeAllVariables(ABooleanAssignment integers) {
        return new BooleanAssignment(removeAllVariables(integers.get()));
    }
}
