/*
 * Copyright (C) 2023 Sebastian Krieter, Elias Kuiter
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
package de.featjar.formula.analysis.value;

import de.featjar.base.computation.IComputation;
import de.featjar.base.data.Result;
import de.featjar.base.io.IO;
import de.featjar.formula.analysis.VariableMap;
import de.featjar.formula.analysis.bool.BooleanClauseList;
import de.featjar.formula.io.value.ValueAssignmentListFormat;
import de.featjar.formula.structure.formula.IFormula;
import de.featjar.formula.transformation.ComputeCNFFormula;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A list of value clauses.
 * Typically used to express a conjunctive normal form.
 * Compared to a {@link IFormula} in CNF (e.g., computed with
 * {@link ComputeCNFFormula}), a {@link ValueClauseList} is a more low-level representation.
 *
 * @author Elias Kuiter
 */
public class ValueClauseList extends AValueAssignmentList<ValueClause> {
    public ValueClauseList() {}

    public ValueClauseList(int size) {
        super(size);
    }

    public ValueClauseList(ValueClauseList other) {
        super(other);
    }

    public ValueClauseList(Collection<? extends ValueClause> clauses) {
        super(clauses);
    }

    @Override
    public Result<BooleanClauseList> toBoolean(VariableMap variableMap) {
        return variableMap.toBoolean(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public IComputation<BooleanClauseList> toBoolean(IComputation<VariableMap> variableMap) {
        return (IComputation<BooleanClauseList>) super.toBoolean(variableMap);
    }

    public String print() {
        try {
            return IO.print(this, new ValueAssignmentListFormat<>());
        } catch (IOException e) {
            return e.toString();
        }
    }

    @Override
    public String toString() {
        return String.format("ValueClauseList[%s]", print());
    }
}
