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
package de.featjar.formula.transformer;

import de.featjar.base.data.Result;
import de.featjar.formula.structure.Expression;
import de.featjar.formula.structure.formula.predicate.Predicate;
import de.featjar.formula.structure.formula.predicate.Literal;
import de.featjar.formula.structure.formula.connective.And;
import de.featjar.formula.structure.formula.connective.Connective;
import de.featjar.formula.structure.formula.connective.Or;
import de.featjar.base.task.Monitor;
import de.featjar.base.task.MonitorableFunction;
import de.featjar.base.tree.Trees;
import de.featjar.base.tree.visitor.TreeVisitor;
import de.featjar.formula.structure.term.value.Variable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Transforms a formula into (clausal) conjunctive or disjunctive normal form by introducing Tseitin variables.
 *
 * @author Sebastian Krieter
 */
public class TseitinTransformer
        implements MonitorableFunction<Expression, List<TseitinTransformer.Substitute>>, TreeVisitor<Expression, Expression> {

    public static class Substitute {
        private final Expression orgExpression;
        private final Variable variable;
        private final List<Expression> clauses;

        private Substitute(Expression orgExpression, Variable variable, int numberOfClauses) {
            this.orgExpression = orgExpression;
            this.variable = variable;
            clauses = new ArrayList<>(numberOfClauses);
        }

        private Substitute(Expression orgExpression, Variable variable, Expression clause) {
            this.orgExpression = orgExpression;
            this.variable = variable;
            clauses = new ArrayList<>(1);
            clauses.add(clause);
        }

        private Substitute(Expression orgExpression, Variable variable, List<? extends Expression> clauses) {
            this.orgExpression = orgExpression;
            this.variable = variable;
            this.clauses = new ArrayList<>(clauses);
        }

        private void addClause(Expression clause) {
            clauses.add(clause);
        }

        public Variable getVariable() {
            return variable;
        }

        public List<Expression> getClauses() {
            return clauses;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(orgExpression);
        }

        @Override
        public boolean equals(Object obj) {
            return (obj != null)
                    && (getClass() == obj.getClass())
                    && Objects.equals(orgExpression, ((Substitute) obj).orgExpression);
        }
    }

    private final List<Substitute> substitutes = new ArrayList<>();
    private int i = 0;

    private Variable newVariable(final ArrayList<Literal> newChildren, final Expression clonedLastNode) {
        Variable addBooleanVariable = new Variable("__tmp__" + (++i));
        final Substitute substitute = new Substitute(clonedLastNode, addBooleanVariable, newChildren.size() + 1);
        substitutes.add(substitute);

        final Literal tempLiteral = new Literal(substitute.variable);
        if (clonedLastNode instanceof And) {
            final ArrayList<Literal> flippedChildren = new ArrayList<>();
            for (final Literal l : newChildren) {
                substitute.addClause(new Or(tempLiteral.invert(), l.cloneNode()));
                flippedChildren.add(l.invert());
            }
            flippedChildren.add(tempLiteral.cloneNode());
            substitute.addClause(new Or(flippedChildren));
        } else if (clonedLastNode instanceof Or) {
            final ArrayList<Literal> flippedChildren = new ArrayList<>();
            for (final Literal l : newChildren) {
                substitute.addClause(new Or(tempLiteral.cloneNode(), l.invert()));
                flippedChildren.add(l.cloneNode());
            }
            flippedChildren.add(tempLiteral.invert());
            substitute.addClause(new Or(flippedChildren));
        } else {
            throw new RuntimeException(clonedLastNode.getClass().toString());
        }
        return substitute.variable;
    }

    private final ArrayDeque<Expression> stack = new ArrayDeque<>();

    @Override
    public Result<List<Substitute>> execute(Expression child, Monitor monitor) {
        substitutes.clear();
        stack.clear();

        Trees.sort(child);

        try {
            child.traverse(this);
        } catch (final Exception ignored) {
        }
        return Result.of(substitutes);
    }

    @Override
    public TraversalAction firstVisit(List<Expression> path) {
        final Expression expression = getCurrentNode(path);
        if (expression instanceof Predicate) {
            return TraversalAction.SKIP_CHILDREN;
        } else if ((expression instanceof Connective)) {
            stack.push(expression);
            return TraversalAction.CONTINUE;
        } else {
            return TraversalAction.FAIL;
        }
    }

    @Override
    public TraversalAction lastVisit(List<Expression> path) {
        final Expression expression = getCurrentNode(path);
        if (expression instanceof Predicate) {
            final Expression clonedExpression = expression;
            if (path.isEmpty()) {
                substitutes.add(new Substitute(clonedExpression, null, clonedExpression));
            } else {
                stack.push(clonedExpression);
            }
        } else {
            final ArrayList<Literal> newChildren = new ArrayList<>();
            Expression lastNode = stack.pop();
            while (lastNode != expression) {
                newChildren.add((Literal) lastNode);
                lastNode = stack.pop();
            }

            if (stack.isEmpty()) {
                final Expression clonedLastNode = lastNode;
                if (lastNode instanceof And) {
                    substitutes.add(new Substitute(clonedLastNode, null, newChildren));
                } else {
                    substitutes.add(new Substitute(clonedLastNode, null, new Or(newChildren)));
                }
            } else {
                final Expression clonedLastNode = lastNode;
                final Variable variable = newVariable(newChildren, clonedLastNode);
                stack.push(new Literal(variable));
            }
        }
        return TreeVisitor.super.lastVisit(path);
    }
}
