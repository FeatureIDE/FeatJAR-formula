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
package de.featjar.formula.transform;

import de.featjar.formula.tmp.AuxiliaryRoot;
import de.featjar.formula.structure.Expression;
import de.featjar.formula.structure.formula.predicate.Predicate;
import de.featjar.formula.tmp.TermMap.Variable;
import de.featjar.formula.structure.formula.connective.And;
import de.featjar.formula.structure.formula.connective.AtLeast;
import de.featjar.formula.structure.formula.connective.AtMost;
import de.featjar.formula.structure.formula.connective.Between;
import de.featjar.formula.structure.formula.connective.BiImplies;
import de.featjar.formula.structure.formula.connective.Choose;
import de.featjar.formula.structure.formula.connective.Connective;
import de.featjar.formula.structure.formula.connective.Implies;
import de.featjar.formula.structure.formula.connective.Not;
import de.featjar.formula.structure.formula.connective.Or;
import de.featjar.formula.structure.formula.connective.Quantifier;
import de.featjar.base.tree.visitor.TreeVisitor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EquivalenceVisitor implements TreeVisitor<Expression, Void> {

    private boolean fail;

    @Override
    public void reset() {
        fail = false;
    }

    @Override
    public TraversalAction firstVisit(List<Expression> path) {
        final Expression expression = getCurrentNode(path);
        if (expression instanceof Predicate) {
            return TraversalAction.SKIP_CHILDREN;
        } else if (expression instanceof Connective) {
            if (expression instanceof Quantifier) {
                return TraversalAction.FAIL;
            }
            return TraversalAction.CONTINUE;
        } else if (expression instanceof AuxiliaryRoot) {
            return TraversalAction.CONTINUE;
        } else {
            return TraversalAction.FAIL;
        }
    }

    @Override
    public TraversalAction lastVisit(List<Expression> path) {
        final Expression expression = getCurrentNode(path);
        expression.replaceChildren(this::replace);
        if (fail) {
            return TraversalAction.FAIL;
        }
        return TraversalAction.CONTINUE;
    }

    @SuppressWarnings("unchecked")
    private Expression replace(Expression expression) {
        if (((expression instanceof Variable)
                || (expression instanceof Predicate)
                || (expression instanceof And)
                || (expression instanceof Or)
                || (expression instanceof Not))) {
            return null;
        }
        final List<Expression> children = (List<Expression>) expression.getChildren();
        Expression newExpression;
        if (expression instanceof Implies) {
            newExpression = new Or(new Not(children.get(0)), children.get(1));
        } else if (expression instanceof BiImplies) {
            newExpression = new And( //
                    new Or(new Not(children.get(0)), children.get(1)),
                    new Or(new Not(children.get(1)), children.get(0)));
        } else if (expression instanceof AtLeast) {
            newExpression = new And(atLeastK(children, ((AtLeast) expression).getMinimum()));
        } else if (expression instanceof AtMost) {
            newExpression = new And(atMostK(children, ((AtMost) expression).getMaximum()));
        } else if (expression instanceof Between) {
            final Between between = (Between) expression;
            newExpression = new And(
                    new And(atLeastK(children, between.getMinimum())), new And(atMostK(children, between.getMaximum())));
        } else if (expression instanceof Choose) {
            final Choose choose = (Choose) expression;
            newExpression = new And(new And(atLeastK(children, choose.getK())), new And(atMostK(children, choose.getK())));
        } else {
            fail = true;
            return null;
        }
        return newExpression;
    }

    private List<Expression> atMostK(List<? extends Expression> elements, int k) {
        final int n = elements.size();

        // return tautology
        if (k <= 0) {
            return Arrays.asList(Expression.FALSE);
        }

        // return contradiction
        if (k > n) {
            return Arrays.asList(Expression.TRUE);
        }

        return groupElements(elements.stream().map(Not::new).collect(Collectors.toList()), k, n);
    }

    private List<Expression> atLeastK(List<? extends Expression> elements, int k) {
        final int n = elements.size();

        // return tautology
        if (k <= 0) {
            return Arrays.asList(Expression.TRUE);
        }

        // return contradiction
        if (k > n) {
            return Arrays.asList(Expression.FALSE);
        }

        return groupElements(elements, n - k, n);
    }

    private List<Expression> groupElements(List<? extends Expression> elements, int k, final int n) {
        final List<Expression> groupedElements = new ArrayList<>();
        final Expression[] clause = new Expression[k + 1];
        final int[] index = new int[k + 1];

        // the position that is currently filled in clause
        int level = 0;
        index[level] = -1;

        while (level >= 0) {
            // fill this level with the next element
            index[level]++;
            // did we reach the maximum for this level
            if (index[level] >= (n - (k - level))) {
                // go to previous level
                level--;
            } else {
                clause[level] = elements.get(index[level]);
                if (level == k) {
                    final Expression[] clonedClause = new Expression[clause.length];
                    Arrays.copyOf(clause, clause.length);
                    for (int i = 0; i < clause.length; i++) {
                        //						clonedClause[i] = Trees.cloneTree(clause[i]);
                        clonedClause[i] = clause[i];
                    }
                    groupedElements.add(new Or(clonedClause));
                } else {
                    // go to next level
                    level++;
                    // allow only ascending orders (to prevent from duplicates)
                    index[level] = index[level - 1];
                }
            }
        }
        return groupedElements;
    }
}
