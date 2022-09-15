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

import de.featjar.formula.tmp.AuxiliaryRoot;
import de.featjar.formula.structure.Expression;
import de.featjar.formula.structure.formula.predicate.Predicate;
import de.featjar.formula.structure.formula.connective.Connective;
import de.featjar.base.tree.visitor.TreeVisitor;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DistributiveLawCounter implements TreeVisitor<Expression, Integer> {

    private static class StackElement {
        int clauseNumber = 1;
        int clauseSize = 1;
        Expression expression;

        public StackElement(Expression expression) {
            this.expression = expression;
        }
    }

    private ArrayDeque<StackElement> stack = new ArrayDeque<>();

    @Override
    public void reset() {
        stack.clear();
    }

    @Override
    public Optional<Integer> getResult() { // TODO BigInteger?
        return Optional.of(stack.pop().clauseNumber);
    }

    @Override
    public TraversalAction firstVisit(List<Expression> path) {
        final Expression expression = getCurrentNode(path);
        if (expression instanceof Predicate) {
            return TraversalAction.SKIP_CHILDREN;
        } else if ((expression instanceof Connective) || (expression instanceof AuxiliaryRoot)) {
            stack.push(new StackElement((Expression) expression));
            return TraversalAction.CONTINUE;
        } else {
            return TraversalAction.FAIL;
        }
    }

    @Override
    public TraversalAction lastVisit(List<Expression> path) {
        final Expression expression = getCurrentNode(path);
        if (expression instanceof Predicate) {
            stack.push(new StackElement(expression));
        } else {
            final ArrayList<StackElement> children = new ArrayList<>();
            StackElement lastNode = stack.pop();
            boolean invalid = false;
            for (; lastNode.expression != expression; lastNode = stack.pop()) {
                children.add(lastNode);
                if (lastNode.clauseNumber < 0) {
                    invalid = true;
                }
            }
            if (invalid) {
                lastNode.clauseNumber = -1;
            } else {
                try {
                    for (final StackElement child : children) {
                        for (int i = 0; i < child.clauseNumber; i++) {
                            lastNode.clauseNumber = Math.multiplyExact(lastNode.clauseNumber, child.clauseSize);
                        }
                    }
                } catch (final ArithmeticException e) {
                    lastNode.clauseNumber = -1;
                }
                lastNode.clauseSize = children.size();
            }
            stack.push(lastNode);
        }
        return TreeVisitor.super.lastVisit(path);
    }
}
