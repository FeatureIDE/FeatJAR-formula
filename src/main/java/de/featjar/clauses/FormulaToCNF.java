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
package de.featjar.clauses;

import de.featjar.formula.structure.Formula;
import de.featjar.formula.structure.Formulas;
import de.featjar.formula.structure.atomic.VariableAssignment;
import de.featjar.formula.structure.atomic.literal.BooleanLiteral;
import de.featjar.formula.structure.atomic.literal.Literal;
import de.featjar.formula.structure.atomic.literal.VariableMap;
import de.featjar.util.job.Executor;
import de.featjar.util.job.InternalMonitor;
import de.featjar.util.job.MonitorableFunction;
import de.featjar.util.tree.Trees;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Several methods concerning {@link Formula} framework.
 *
 * @author Sebastian Krieter
 */
public final class FormulaToCNF implements MonitorableFunction<Formula, CNF> {

    private boolean keepLiteralOrder;
    private VariableMap variableMapping;

    public static CNF convert(Formula formula) {
        return Executor.run(new FormulaToCNF(), formula).get();
    }

    public static CNF convert(Formula formula, VariableMap variableMapping) {
        final FormulaToCNF function = new FormulaToCNF();
        function.setVariableMapping(variableMapping);
        function.setKeepLiteralOrder(true);
        return Executor.run(function, formula).get();
    }

    @Override
    public CNF execute(Formula node, InternalMonitor monitor) {
        if (node == null) {
            return null;
        }
        final VariableMap mapping = variableMapping != null
                ? variableMapping
                : node.getVariableMap().orElseGet(VariableMap::new);
        final List<LiteralList> clauses = new ArrayList<>();
        final Optional<Object> formulaValue = Formulas.evaluate(node, new VariableAssignment(mapping));
        if (formulaValue.isPresent()) {
            if (formulaValue.get() == Boolean.FALSE) {
                clauses.add(new LiteralList());
            }
        } else {
            final Formula cnf = Formulas.toCNF(Trees.cloneTree(node)).get();
            cnf.getChildren().stream()
                    .map(exp -> getClause(exp, mapping))
                    .filter(Objects::nonNull)
                    .forEach(clauses::add);
        }
        return new CNF(mapping, clauses);
    }

    public boolean isKeepLiteralOrder() {
        return keepLiteralOrder;
    }

    public void setKeepLiteralOrder(boolean keepLiteralOrder) {
        this.keepLiteralOrder = keepLiteralOrder;
    }

    public VariableMap getVariableMapping() {
        return variableMapping;
    }

    public void setVariableMapping(VariableMap variableMapping) {
        this.variableMapping = variableMapping;
    }

    private LiteralList getClause(Formula clauseExpression, VariableMap mapping) {
        if (clauseExpression instanceof Literal) {
            final Literal literal = (Literal) clauseExpression;
            final int variable = mapping.getVariableSignature(literal.getName())
                    .orElseThrow(RuntimeException::new)
                    .getIndex();
            return new LiteralList(
                    new int[] {literal.isPositive() ? variable : -variable},
                    keepLiteralOrder ? LiteralList.Order.UNORDERED : LiteralList.Order.NATURAL);
        } else {
            final List<? extends Formula> clauseChildren = clauseExpression.getChildren();
            if (clauseChildren.stream().anyMatch(literal -> literal == Literal.True)) {
                return null;
            } else {
                final int[] literals = clauseChildren.stream()
                        .filter(literal -> literal != Literal.False)
                        .filter(literal -> literal instanceof BooleanLiteral)
                        .mapToInt(literal -> {
                            final int variable = mapping.getVariableSignature(((BooleanLiteral) literal)
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
