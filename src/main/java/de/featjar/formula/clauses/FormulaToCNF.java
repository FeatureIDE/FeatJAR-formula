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
import de.featjar.formula.structure.Expression;
import de.featjar.formula.tmp.Formulas;
import de.featjar.formula.structure.assignment.VariableAssignment;
import de.featjar.formula.structure.formula.predicate.Literal;
import de.featjar.formula.structure.map.TermMap;
import de.featjar.base.task.Monitor;
import de.featjar.base.task.MonitorableFunction;
import de.featjar.base.tree.Trees;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Several methods concerning {@link Expression} framework.
 *
 * @author Sebastian Krieter
 */
public class FormulaToCNF implements MonitorableFunction<Expression, CNF> {

    private boolean keepLiteralOrder;
    private TermMap termMap;

    public static CNF convert(Expression expression) {
        return new FormulaToCNF().apply(expression).get();
    }

    public static CNF convert(Expression expression, TermMap termMap) {
        final FormulaToCNF function = new FormulaToCNF();
        function.setVariableMapping(termMap);
        function.setKeepLiteralOrder(true);
        return function.apply(expression).get();
    }

    @Override
    public Result<CNF> execute(Expression expression, Monitor monitor) {
        if (expression == null) {
            return Result.empty();
        }
        final TermMap mapping = termMap != null
                ? termMap
                : expression.getTermMap().orElseGet(TermMap::new);
        final ClauseList clauses = new ClauseList();
        final Optional<Object> formulaValue = Formulas.evaluate(expression, new VariableAssignment(mapping));
        if (formulaValue.isPresent()) {
            if (formulaValue.get() == Boolean.FALSE) {
                clauses.add(new LiteralList());
            }
        } else {
            final Expression cnf = Formulas.toCNF(Trees.clone(expression)).get();
            cnf.getChildren().stream()
                    .map(exp -> getClause(exp, mapping))
                    .filter(Objects::nonNull)
                    .forEach(clauses::add);
        }
        return Result.of(new CNF(mapping, clauses));
    }

    public boolean isKeepLiteralOrder() {
        return keepLiteralOrder;
    }

    public void setKeepLiteralOrder(boolean keepLiteralOrder) {
        this.keepLiteralOrder = keepLiteralOrder;
    }

    public TermMap getVariableMapping() {
        return termMap;
    }

    public void setVariableMapping(TermMap termMap) {
        this.termMap = termMap;
    }

    private LiteralList getClause(Expression clauseExpression, TermMap mapping) {
        if (clauseExpression instanceof Literal) {
            final Literal literal = (Literal) clauseExpression;
            final int variable = mapping.getVariableSignature(literal.getName())
                    .orElseThrow(RuntimeException::new)
                    .getIndex();
            return new LiteralList(
                    new int[] {literal.isPositive() ? variable : -variable},
                    keepLiteralOrder ? LiteralList.Order.UNORDERED : LiteralList.Order.NATURAL);
        } else {
            final List<? extends Expression> clauseChildren = clauseExpression.getChildren();
            if (clauseChildren.stream().anyMatch(literal -> literal == Expression.TRUE)) {
                return null;
            } else {
                final int[] literals = clauseChildren.stream()
                        .filter(literal -> literal != Expression.FALSE)
                        .filter(literal -> literal instanceof Literal)
                        .mapToInt(literal -> {
                            final int variable = mapping.getVariableSignature(((Literal) literal)
                                            .getVariable()
                                            .getName())
                                    .orElseThrow(RuntimeException::new)
                                    .getIndex();
                            return ((Literal) literal).isPositive() ? variable : -variable;
                        })
                        .toArray();
                return new LiteralList(
                        literals, keepLiteralOrder ? LiteralList.Order.UNORDERED : LiteralList.Order.NATURAL);
            }
        }
    }
}
