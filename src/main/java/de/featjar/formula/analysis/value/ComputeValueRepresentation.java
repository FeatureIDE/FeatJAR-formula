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
package de.featjar.formula.analysis.value;

import de.featjar.base.data.Computation;
import de.featjar.base.data.FutureResult;
import de.featjar.base.data.Pair;
import de.featjar.base.data.Result;
import de.featjar.formula.analysis.VariableMap;
import de.featjar.formula.analysis.bool.*;

/**
 * ...
 *
 * @author Elias Kuiter
 */
public abstract class ComputeValueRepresentation<T extends BooleanRepresentation, U extends ValueRepresentation> implements Computation<U> {
    protected Computation<Pair<T, VariableMap>> booleanRepresentation;

    public ComputeValueRepresentation(Computation<Pair<T, VariableMap>> booleanRepresentation) {
        this.booleanRepresentation = booleanRepresentation;
    }

    @SuppressWarnings("unchecked")
    @Override
    public FutureResult<U> compute() {
        return booleanRepresentation.get().thenComputeResult(((pair, monitor) -> {
                    T t = pair.getKey();
                    VariableMap variableMap = pair.getValue();
                    return (Result<U>) t.toValue(variableMap);
                }));
    }

    public static class OfAssignment extends ComputeValueRepresentation<BooleanAssignment, ValueAssignment> {
        public OfAssignment(Computation<Pair<BooleanAssignment, VariableMap>> booleanRepresentation) {
            super(booleanRepresentation);
        }
    }

    public static class OfClause extends ComputeValueRepresentation<BooleanClause, ValueClause> {
        public OfClause(Computation<Pair<BooleanClause, VariableMap>> booleanRepresentation) {
            super(booleanRepresentation);
        }
    }

    public static class OfSolution extends ComputeValueRepresentation<BooleanSolution, ValueSolution> {
        public OfSolution(Computation<Pair<BooleanSolution, VariableMap>> booleanRepresentation) {
            super(booleanRepresentation);
        }
    }

    public static class OfClauseList extends ComputeValueRepresentation<BooleanClauseList, ValueClauseList> {
        public OfClauseList(Computation<Pair<BooleanClauseList, VariableMap>> booleanRepresentation) {
            super(booleanRepresentation);
        }
    }

    public static class OfSolutionList extends ComputeValueRepresentation<BooleanSolutionList, ValueSolutionList> {
        public OfSolutionList(Computation<Pair<BooleanSolutionList, VariableMap>> booleanRepresentation) {
            super(booleanRepresentation);
        }
    }
}
