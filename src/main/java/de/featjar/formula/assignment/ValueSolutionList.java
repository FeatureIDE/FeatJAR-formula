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
import de.featjar.formula.structure.IFormula;
import java.util.Collection;

/**
 * A list of value solutions.
 * Typically used to express solutions to a problem expressed as a {@link IFormula}.
 * Analogous to a {@link de.featjar.formula.assignment.ValueClauseList},
 * a {@link de.featjar.formula.assignment.ValueSolutionList}
 * is a low-level representation of a formula in disjunctive normal form (DNF).
 *
 * @author Elias Kuiter
 */
public class ValueSolutionList extends AValueAssignmentList<ValueSolution> {
    public ValueSolutionList() {}

    public ValueSolutionList(int size) {
        super(size);
    }

    public ValueSolutionList(ValueSolutionList other) {
        super(other);
    }

    public ValueSolutionList(Collection<? extends ValueSolution> solutions) {
        super(solutions);
    }

    @Override
    public Result<BooleanSolutionList> toBoolean() {
        return VariableMap.toBoolean(this);
    }

    @Override
    public String toString() {
        return String.format("ValueSolutionList[%s]", print());
    }
}
