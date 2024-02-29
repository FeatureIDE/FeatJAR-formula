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
package de.featjar.formula.analysis.bool;

import de.featjar.base.computation.IComputation;
import de.featjar.base.data.Result;
import de.featjar.formula.analysis.VariableMap;
import de.featjar.formula.structure.Expressions;
import de.featjar.formula.structure.IExpression;
import de.featjar.formula.structure.formula.IFormula;
import de.featjar.formula.structure.formula.connective.Reference;
import de.featjar.formula.structure.formula.predicate.Literal;
import java.util.List;
import java.util.Objects;

/**
 * Transforms a formula, which is assumed to be in strict conjunctive normal form, into a {@link BooleanClauseList}.
 *
 * @author Sebastian Krieter
 */
public class ComputeBooleanClauseList extends ComputeBooleanRepresentation<IFormula, BooleanClauseList> {

    /**
     * {@return a formula, which is assumed to be in strict conjunctive normal form, into an indexed CNF representation}
     * @param formula the formula in strict CNF
     * @param variableMap the variable map corresponding to that formula
     */
    public static Result<BooleanClauseList> toBooleanClauseList(IFormula formula) {
        VariableMap variableMap = VariableMap.of(formula);
        if (formula instanceof Reference) {
            formula = ((Reference) formula).getExpression();
        }
        return toBooleanClauseList(formula, variableMap);
    }

    public static Result<BooleanClauseList> toBooleanClauseList(IFormula formula, VariableMap variableMap) {
        final BooleanClauseList clauseList = new BooleanClauseList(variableMap.getVariableCount());
        formula.getChildren().stream()
                .map(expression -> getClause((IFormula) expression, variableMap))
                .filter(Objects::nonNull)
                .forEach(clauseList::add);
        return Result.of(clauseList); // TODO: better error handling when index cannot be found
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

    public ComputeBooleanClauseList(IComputation<IFormula> cnfFormula) {
        super(cnfFormula);
    }

    protected ComputeBooleanClauseList(ComputeBooleanClauseList other) {
        super(other);
    }

}
