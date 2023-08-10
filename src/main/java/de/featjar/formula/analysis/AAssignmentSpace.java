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
package de.featjar.formula.analysis;

import de.featjar.formula.analysis.bool.ABooleanAssignment;
import java.util.List;
import java.util.Objects;

/**
 * Combines multiple groups of lists of {@link ABooleanAssignment assignments} with a corresponding {@link VariableMap variable map}.
 *
 * @author Sebastian Krieter
 */
public class AAssignmentSpace<T extends IAssignment<?, ?>> {

    protected final VariableMap variableMap;
    protected final List<List<T>> assignmentGroups;

    public AAssignmentSpace(VariableMap variableMap, List<List<T>> assignmentGroups) {
        this.variableMap = variableMap;
        this.assignmentGroups = assignmentGroups;
    }

    public VariableMap getVariableMap() {
        return variableMap;
    }

    public List<List<T>> getGroups() {
        return assignmentGroups;
    }

    @Override
    public int hashCode() {
        return Objects.hash(assignmentGroups, variableMap);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AAssignmentSpace<?> other = (AAssignmentSpace<?>) obj;
        return Objects.equals(assignmentGroups, other.assignmentGroups)
                && Objects.equals(variableMap, other.variableMap);
    }

    @Override
    public String toString() {
        return "AssignmentSpace [map=" + variableMap + ", groups=" + assignmentGroups + "]";
    }
}
