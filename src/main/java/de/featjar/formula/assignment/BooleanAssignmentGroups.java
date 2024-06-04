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
import java.util.List;

/**
 * {@link AAssignmentGroups} implementation for {@link ABooleanAssignment}.
 *
 * @author Sebastian Krieter
 */
public class BooleanAssignmentGroups extends AAssignmentGroups<ABooleanAssignment> {

    public BooleanAssignmentGroups(
            VariableMap variableMap, List<? extends List<? extends ABooleanAssignment>> assignment) {
        super(variableMap, assignment);
    }

    public BooleanClauseList toClauseList() {
        return toClauseList(0);
    }

    public BooleanSolutionList toSolutionList() {
        return toSolutionList(0);
    }

    public BooleanClauseList toClauseList(int groupIndex) {
        List<? extends ABooleanAssignment> group = assignmentGroups.get(groupIndex);
        final BooleanClauseList list = new BooleanClauseList(group.size(), variableMap.getVariableCount());
        group.stream().map(ABooleanAssignment::toClause).forEach(list::add);
        return list;
    }

    public BooleanSolutionList toSolutionList(int groupIndex) {
        List<? extends ABooleanAssignment> group = assignmentGroups.get(groupIndex);
        final BooleanSolutionList list = new BooleanSolutionList(group.size());
        group.stream().map(ABooleanAssignment::toSolution).forEach(list::add);
        return list;
    }
}
