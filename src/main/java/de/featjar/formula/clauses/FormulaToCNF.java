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
package de.featjar.formula.clauses;

import de.featjar.base.data.Result;
import de.featjar.base.task.Monitor;
import de.featjar.base.task.MonitorableFunction;
import de.featjar.formula.structure.Expression;
import de.featjar.formula.structure.Expressions;
import de.featjar.formula.structure.formula.Formula;
import de.featjar.formula.structure.formula.predicate.Literal;

import java.util.List;
import java.util.Objects;

/**
 * Several methods concerning {@link Expression} framework.
 *
 * @author Sebastian Krieter
 */
public class FormulaToCNF implements MonitorableFunction<Formula, CNF> {

    private boolean keepLiteralOrder;
    private VariableMap termMap;

    public static CNF convert(Formula expression) {
        return new FormulaToCNF().apply(expression).get();
    }

    public static CNF convert(Formula expression, VariableMap termMap) {
        final FormulaToCNF function = new FormulaToCNF();
        function.setVariableMapping(termMap);
        function.setKeepLiteralOrder(true);
        return function.apply(expression).get();
    }

    @Override
    public Result<CNF> execute(Formula expression, Monitor monitor) {
        if (expression == null) {
            return Result.empty();
        }
        final ClauseList clauses = new ClauseList();
        //final Optional<Object> formulaValue = expression.evaluate();
//        if (formulaValue.isPresent()) {
//            if (formulaValue.get() == Boolean.FALSE) {
//                clauses.add(new LiteralList());
//            }
//        } else {
            final Expression cnf = expression.toCNF().get();
            VariableMap variableMap = VariableMap.of(cnf);
            cnf.getChildren().stream()
                    .map(exp -> getClause(exp, variableMap))
                    .filter(Objects::nonNull)
                    .forEach(clauses::add);
        //}
        return Result.of(new CNF(variableMap, clauses));
    }

    public boolean isKeepLiteralOrder() {
        return keepLiteralOrder;
    }

    public void setKeepLiteralOrder(boolean keepLiteralOrder) {
        this.keepLiteralOrder = keepLiteralOrder;
    }

    public VariableMap getVariableMapping() {
        return termMap;
    }

    public void setVariableMapping(VariableMap termMap) {
        this.termMap = termMap;
    }

    private LiteralList getClause(Expression clauseExpression, VariableMap mapping) {
        if (clauseExpression instanceof Literal) {
            final Literal literal = (Literal) clauseExpression;
            final int variable = mapping.get(literal.getName())
                    .orElseThrow(RuntimeException::new);
            return new LiteralList(
                    new int[] {literal.isPositive() ? variable : -variable},
                    keepLiteralOrder ? LiteralList.Order.UNORDERED : LiteralList.Order.NATURAL);
        } else {
            final List<? extends Expression> clauseChildren = clauseExpression.getChildren();
            if (clauseChildren.stream().anyMatch(literal -> literal == Expressions.True)) {
                return null;
            } else {
                final int[] literals = clauseChildren.stream()
                        .filter(literal -> literal != Expressions.False)
                        .filter(literal -> literal instanceof Literal)
                        .mapToInt(literal -> {
                            final int variable = mapping.get(literal.getName())
                                    .orElseThrow(RuntimeException::new);
                            return ((Literal) literal).isPositive() ? variable : -variable;
                        })
                        .toArray();
                return new LiteralList(
                        literals, keepLiteralOrder ? LiteralList.Order.UNORDERED : LiteralList.Order.NATURAL);
            }
        }
    }
}
