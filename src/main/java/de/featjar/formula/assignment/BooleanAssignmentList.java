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

import de.featjar.formula.VariableMap;
import java.util.Collection;

/**
 * Primary implementation of {@link ABooleanAssignmentList}.
 * To be used when neither CNF nor DNF semantics are associated with an assignment list.
 *
 * @author Elias Kuiter
 */
public class BooleanAssignmentList extends ABooleanAssignmentList<BooleanAssignment> {

    public BooleanAssignmentList(VariableMap variableMap) {
        super(variableMap);
    }

    public BooleanAssignmentList(VariableMap variableMap, int size) {
        super(variableMap, size);
    }

    public BooleanAssignmentList(VariableMap variableMap, Collection<? extends BooleanAssignment> assignments) {
        super(variableMap, assignments);
    }

    public BooleanAssignmentList(VariableMap variableMap, BooleanAssignmentList other) {
        super(other);
    }

    @Override
    public ValueAssignmentList toValue() {
        return VariableMap.toValue(this);
    }

    @Override
    public String toString() {
        return String.format("BooleanAssignmentList[%s]", print());
    }
}
