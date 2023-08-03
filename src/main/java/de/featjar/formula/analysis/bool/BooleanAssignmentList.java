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
package de.featjar.formula.analysis.bool;

import de.featjar.base.computation.IComputation;
import de.featjar.base.data.Result;
import de.featjar.formula.analysis.VariableMap;
import de.featjar.formula.analysis.value.ValueAssignmentList;
import de.featjar.formula.analysis.value.ValueClauseList;
import java.util.Collection;

/**
 * Primary implementation of {@link ABooleanAssignmentList}.
 * To be used when neither CNF nor DNF semantics are associated with an assignment list.
 *
 * @author Elias Kuiter
 */
public class BooleanAssignmentList extends ABooleanAssignmentList<BooleanAssignment> {

    public BooleanAssignmentList() {
        super();
    }

    public BooleanAssignmentList(int size) {
        super(size);
    }

    public BooleanAssignmentList(Collection<? extends BooleanAssignment> assignments) {
        super(assignments);
    }

    public BooleanAssignmentList(BooleanAssignmentList other) {
        super(other);
    }

    @Override
    public Result<ValueAssignmentList> toValue(VariableMap variableMap) {
        return variableMap.toValue(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public IComputation<ValueClauseList> toValue(IComputation<VariableMap> variableMap) {
        return (IComputation<ValueClauseList>) super.toValue(variableMap);
    }

    public String print() {
        return VariableMap.toAnonymousValue(this).get().print();
    }

    @Override
    public String toString() {
        return String.format("BooleanAssignmentList[%s]", print());
    }
}
