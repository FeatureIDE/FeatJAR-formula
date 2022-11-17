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
package de.featjar.formula.analysis.sat.clause;

import de.featjar.base.data.Computation;
import de.featjar.base.data.FutureResult;
import de.featjar.base.data.Result;
import de.featjar.formula.analysis.sat.VariableMap;
import de.featjar.formula.structure.Expression;
import de.featjar.formula.structure.Expressions;
import de.featjar.formula.structure.formula.Formula;
import de.featjar.formula.structure.formula.predicate.Literal;

import java.util.List;
import java.util.Objects;

/**
 * Transforms a formula, which is assumed to be in conjunctive normal form, into an indexed CNF representation.
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public class ToCNF implements Computation<CNF> {
    protected final Computation<Formula> cnfFormulaComputation;
    protected final Computation<VariableMap> variableMapComputation;

    public ToCNF(Computation<Formula> cnfFormulaComputation) {
        this(cnfFormulaComputation, cnfFormulaComputation.map(VariableMap::of));
    }

    public ToCNF(Computation<Formula> cnfFormulaComputation, Computation<VariableMap> variableMapComputation) {
        this.cnfFormulaComputation = cnfFormulaComputation;
        this.variableMapComputation = variableMapComputation;
    }

    public static Result<CNF> convert(Formula formula) {
        return Computation.of(formula).then(ToCNF::new).getResult();
    }

    public static Result<CNF> convert(Formula formula, VariableMap variableMap) {
        return Computation.of(formula).then(ToCNF.class, variableMap, true).getResult();
    }

    @Override
    public FutureResult<CNF> compute() {
        return Computation.allOf(cnfFormulaComputation, variableMapComputation)
                .get().thenComputeResult(((list, monitor) -> {
                    Formula formula = (Formula) list.get(0);
                    VariableMap variableMap = (VariableMap) list.get(1);
                    final ClauseList clauses = new ClauseList();
                    //final Object formulaValue = formula.evaluate();
//                    if (formulaValue != null) { //todo
//                        if (formulaValue == Boolean.FALSE) {
//                            clauses.add(new LiteralList());
//                        }
//                    } else {
                        formula.getChildren().stream()
                                .map(expression -> getClause((Formula) expression, variableMap))
                                .filter(Objects::nonNull)
                                .forEach(clauses::add);
                    //}
                    return Result.of(new CNF(clauses, variableMap));
                }));
    }

    protected SATClause getClause(Formula formula, VariableMap variableMap) {
        if (formula instanceof Literal) {
            final Literal literal = (Literal) formula;
            final int index = variableMap.get(literal.getExpression().getName()).orElseThrow(RuntimeException::new);
            return new SATClause(literal.isPositive() ? index : -index);
        } else {
            final List<? extends Expression> children = formula.getChildren();
            if (children.stream().anyMatch(literal -> literal == Expressions.True)) {
                return null;
            } else {
                final int[] literals = children.stream()
                        .filter(literal -> literal != Expressions.False)
                        .filter(literal -> literal instanceof Literal)
                        .mapToInt(literal -> {
                            final int variable = variableMap.get(((Literal) literal).getExpression().getName())
                                    .orElseThrow(RuntimeException::new);
                            return ((Literal) literal).isPositive() ? variable : -variable;
                        })
                        .toArray();
                return new SATClause(literals);
            }
        }
    }
}
