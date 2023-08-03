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

import de.featjar.base.computation.IComputation;
import de.featjar.base.data.Result;
import de.featjar.base.io.IO;
import de.featjar.formula.analysis.VariableMap;
import de.featjar.formula.analysis.bool.BooleanAssignment;
import de.featjar.formula.io.value.ValueAssignmentFormat;
import java.io.IOException;
import java.util.LinkedHashMap;

/**
 * Primary implementation of {@link AValueAssignment}.
 * To be used when neither CNF nor DNF semantics are associated with an assignment.
 *
 * @author Elias Kuiter
 */
public class ValueAssignment extends AValueAssignment {
    public ValueAssignment() {}

    public ValueAssignment(LinkedHashMap<String, Object> variableValuePairs) {
        super(variableValuePairs);
    }

    public ValueAssignment(ValueAssignment predicateClause) {
        this(new LinkedHashMap<>(predicateClause.variableValuePairs));
    }

    public ValueAssignment(Object... variableValuePairs) {
        super(variableValuePairs);
    }

    @Override
    public Result<BooleanAssignment> toBoolean(VariableMap variableMap) {
        return variableMap.toBoolean(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public IComputation<BooleanAssignment> toBoolean(IComputation<VariableMap> variableMap) {
        return (IComputation<BooleanAssignment>) super.toBoolean(variableMap);
    }

    public String print() {
        try {
            return IO.print(this, new ValueAssignmentFormat<>(ValueAssignment::new));
        } catch (IOException e) {
            return e.toString();
        }
    }

    @Override
    public String toString() {
        return String.format("ValueClause[%s]", print());
    }
}
