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
import de.featjar.formula.analysis.bool.BooleanAssignmentList;
import de.featjar.formula.analysis.bool.BooleanClauseList;
import de.featjar.formula.io.value.ValueAssignmentListFormat;
import java.io.IOException;
import java.util.Collection;

/**
 * Primary implementation of {@link AValueAssignmentList}.
 * To be used when neither CNF nor DNF semantics are associated with an assignment list.
 *
 * @author Elias Kuiter
 */
public class ValueAssignmentList extends AValueAssignmentList<ValueAssignment> {
    public ValueAssignmentList() {}

    public ValueAssignmentList(int size) {
        super(size);
    }

    public ValueAssignmentList(ValueAssignmentList other) {
        super(other);
    }

    public ValueAssignmentList(Collection<? extends ValueAssignment> assignments) {
        super(assignments);
    }

    @Override
    public Result<BooleanAssignmentList> toBoolean(VariableMap variableMap) {
        return variableMap.toBoolean(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public IComputation<BooleanClauseList> toBoolean(IComputation<VariableMap> variableMap) {
        return (IComputation<BooleanClauseList>) super.toBoolean(variableMap);
    }

    @Override
    public String toString() {
        return String.format("ValueAssignmentList[%s]", print());
    }

    public String print() {
        try {
            return IO.print(this, new ValueAssignmentListFormat<>());
        } catch (IOException e) {
            return e.toString();
        }
    }
}
