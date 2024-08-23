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
import de.featjar.formula.structure.IFormula;
import java.util.Collection;

/**
 * A list of Boolean solutions.
 * Typically used to express solutions to a problem expressed as a {@link IFormula}.
 * Analogous to a {@link de.featjar.formula.assignment.BooleanClauseList},
 * a {@link de.featjar.formula.assignment.BooleanSolutionList}
 * is a low-level representation of a formula in disjunctive normal form (DNF).
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public class BooleanSolutionList extends ABooleanAssignmentList<BooleanSolution> {

    public BooleanSolutionList() {}

    public BooleanSolutionList(int size) {
        super(size);
    }

    public BooleanSolutionList(Collection<? extends BooleanSolution> assignments) {
        super(assignments);
    }

    public BooleanSolutionList(BooleanSolutionList other) {
        super(other);
    }

    @Override
    public ValueSolutionList toValue() {
        return VariableMap.toValue(this);
    }

    @Override
    public String toString() {
        return String.format("BooleanSolutionList[%s]", print());
    }

    public String serialize() {
        StringBuilder sb = new StringBuilder();
        for (BooleanSolution assignment : assignments) {
            sb.append(assignment.print());
            sb.append('\n');
        }
        int length = sb.length();
        if (length > 0) {
            sb.setLength(length - 1);
        }
        return sb.toString();
    }
}
