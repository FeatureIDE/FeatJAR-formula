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
import de.featjar.formula.computation.ComputeCNFFormula;
import de.featjar.formula.structure.IFormula;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A list of Boolean clauses.
 * Typically used to express a conjunctive normal form.
 * Compared to a {@link IFormula} in CNF (e.g., computed with
 * {@link ComputeCNFFormula}), a {@link ValueClauseList} is a more low-level representation.
 * A Boolean clause list only contains indices into a {@link VariableMap}, which links
 * a {@link BooleanClauseList} to the {@link de.featjar.formula.structure.term.value.Variable variables}
 * in the original {@link IFormula}.
 * TODO: more error checking for consistency of clauses with variables
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public class BooleanClauseList extends ABooleanAssignmentList<BooleanClause> {

    public BooleanClauseList(VariableMap variableMap) {
        super(variableMap);
    }

    public BooleanClauseList(VariableMap variableMap, int size) {
        super(variableMap, size);
    }

    public BooleanClauseList(VariableMap variableMap, Collection<? extends BooleanAssignment> assignments) {
        super(variableMap, assignments.stream().map(BooleanAssignment::toClause));
    }

    public BooleanClauseList(BooleanClauseList other) {
        super(other);
    }

    public BooleanClauseList adapt(VariableMap newVariables) {
        List<BooleanClause> adaptedAssignments = new ArrayList<>();
        for (BooleanClause oldClause : assignments) {
            adaptedAssignments.add(
                    new BooleanClause(oldClause.adapt(variableMap, newVariables).orElseThrow()));
        }
        return new BooleanClauseList(newVariables, adaptedAssignments);
    }

    @Override
    public ValueClauseList toValue() {
        return VariableMap.toValue(this);
    }

    @Override
    public String toString() {
        return String.format("BooleanClauseList[%s]", print());
    }
}
