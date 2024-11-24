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
import de.featjar.formula.computation.ComputeCNFFormula;
import de.featjar.formula.structure.IFormula;
import java.util.Collection;

/**
 * A list of value clauses. Typically used to express a conjunctive normal form.
 * Compared to a {@link IFormula} in CNF (e.g., computed with
 * {@link ComputeCNFFormula}), a {@link ValueClauseList} is a more low-level
 * representation.
 *
 * @author Elias Kuiter
 */
public class ValueClauseList extends AValueAssignmentList<ValueClause> {

    public ValueClauseList(VariableMap variableMap) {
        super(variableMap);
    }

    public ValueClauseList(VariableMap variableMap, int size) {
        super(variableMap, size);
    }

    public ValueClauseList(VariableMap variableMap, Collection<? extends ValueClause> clauses) {
        super(variableMap, clauses);
    }

    public ValueClauseList(ValueClauseList other) {
        super(other);
    }

    @Override
    public Result<BooleanClauseList> toBoolean() {
        return VariableMap.toBoolean(this);
    }

    @Override
    public String toString() {
        return String.format("ValueClauseList[%s]", print());
    }
}
