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
package de.featjar.formula.analysis.bool;

import de.featjar.base.FeatJAR;
import de.featjar.base.computation.*;
import de.featjar.base.data.Pair;
import de.featjar.base.data.Result;
import de.featjar.formula.analysis.VariableMap;
import de.featjar.formula.analysis.value.*;
import de.featjar.formula.structure.Expressions;
import de.featjar.formula.structure.IExpression;
import de.featjar.formula.structure.formula.IFormula;
import de.featjar.formula.structure.formula.predicate.Literal;
import java.util.List;
import java.util.Objects;

/**
 * Transforms a formula, which is assumed to be in conjunctive normal form, into an indexed CNF representation.
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public class BooleanRepresentationComputation<T extends IValueRepresentation, U extends IBooleanRepresentation>
        extends AComputation<Pair<U, VariableMap>> {

    protected static final Dependency<Object> VALUE_REPRESENTATION = Dependency.newDependency();

    public static Result<BooleanClauseList> toBooleanClauseList(IFormula formula, VariableMap variableMap) {
        final BooleanClauseList clauseList = new BooleanClauseList(variableMap.getVariableCount());
        formula.getChildren().stream()
                .map(expression -> getClause((IFormula) expression, variableMap))
                .filter(Objects::nonNull)
                .forEach(clauseList::add);
        // }
        return Result.of(clauseList); // todo: better error handling when index cannot be found
    }

    protected static BooleanClause getClause(IFormula formula, VariableMap variableMap) {
        if (formula instanceof Literal) {
            final Literal literal = (Literal) formula;
            final int index = variableMap.get(literal.getExpression().getName()).orElseThrow();
            return new BooleanClause(literal.isPositive() ? index : -index);
        } else {
            final List<? extends IExpression> children = formula.getChildren();
            if (children.stream().anyMatch(literal -> literal == Expressions.True)) {
                return null;
            } else {
                final int[] literals = children.stream()
                        .filter(literal -> literal != Expressions.False)
                        .filter(literal -> literal instanceof Literal)
                        .mapToInt(literal -> {
                            final int variable = variableMap
                                    .get(((Literal) literal).getExpression().getName())
                                    .orElseThrow();
                            return ((Literal) literal).isPositive() ? variable : -variable;
                        })
                        .toArray();
                return new BooleanClause(literals);
            }
        }
    }

    public BooleanRepresentationComputation(IComputation<T> valueRepresentation) {
        super(valueRepresentation);
    }

    protected BooleanRepresentationComputation(BooleanRepresentationComputation<T, U> other) {
        super(other);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Result<Pair<U, VariableMap>> compute(List<Object> dependencyList, Progress progress) {
        T vp = (T) VALUE_REPRESENTATION.get(dependencyList);
        FeatJAR.log().debug("initializing variable map for " + vp.getClass().getName());
        VariableMap variableMap = VariableMap.of(vp);
        FeatJAR.log().debug(variableMap);
        return vp.toBoolean(variableMap).map(u -> new Pair<>((U) u, variableMap));
    }
}
