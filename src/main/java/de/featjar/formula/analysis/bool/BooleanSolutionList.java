/*
 * Copyright (C) 2022 Sebastian Krieter, Elias Kuiter
 *
 * This file is part of formula.
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
package de.featjar.formula.analysis.bool;

import de.featjar.base.computation.IComputation;
import de.featjar.base.data.Result;
import de.featjar.formula.analysis.VariableMap;
import de.featjar.formula.analysis.value.ValueSolutionList;
import de.featjar.formula.structure.formula.IFormula;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A list of Boolean solutions.
 * Typically used to express solutions to a problem expressed as a {@link IFormula}.
 * Analogous to a {@link de.featjar.formula.analysis.bool.BooleanClauseList},
 * a {@link de.featjar.formula.analysis.bool.BooleanSolutionList}
 * is a low-level representation of a formula in disjunctive normal form (DNF).
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public class BooleanSolutionList extends ABooleanAssignmentList<BooleanSolutionList, BooleanSolution> {
    public BooleanSolutionList() {
    }

    public BooleanSolutionList(int size) {
        super(size);
    }

    public BooleanSolutionList(Collection<? extends BooleanSolution> solutions) {
        super(solutions);
    }

    public BooleanSolutionList(BooleanSolutionList other) {
        super(other);
    }

    @Override
    protected BooleanSolutionList newAssignmentList(List<BooleanSolution> solutions) {
        return new BooleanSolutionList(solutions);
    }

    @Override
    public BooleanClauseList toClauseList() {
        return new BooleanClauseList(assignments.stream().map(BooleanSolution::toClause).collect(Collectors.toList()));
    }

    @Override
    public BooleanSolutionList toSolutionList() {
        return new BooleanSolutionList(assignments);
    }

    @Override
    public Result<ValueSolutionList> toValue(VariableMap variableMap) {
        return variableMap.toValue(this);
    }

    @Override
    public IComputation<ValueSolutionList> toValue(IComputation<VariableMap> variableMap) {
        return variableMap.mapResult(ValueSolutionList.class, "toValue", v -> toValue(v).get());
    }

    public String print() {
        return VariableMap.toAnonymousValue(this).getAndLogProblems().print();
    }

    @Override
    public String toString() {
        return String.format("BooleanSolutionList[%s]", print());
    }
}
