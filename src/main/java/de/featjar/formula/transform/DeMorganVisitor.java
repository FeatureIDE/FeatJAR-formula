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
import de.featjar.formula.structure.formula.predicate.Literal;
import de.featjar.formula.structure.formula.connective.And;
import de.featjar.formula.structure.formula.connective.Connective;
import de.featjar.formula.structure.formula.connective.Not;
import de.featjar.formula.structure.formula.connective.Or;
import de.featjar.base.tree.visitor.TreeVisitor;
import java.util.List;
import java.util.stream.Collectors;

public class DeMorganVisitor implements TreeVisitor<Expression, Void> {

    @Override
    public TraversalAction firstVisit(List<Expression> path) {
        final Expression expression = getCurrentNode(path);
        if (expression instanceof Predicate) {
            return TraversalAction.SKIP_CHILDREN;
        } else if (expression instanceof Connective) {
            expression.replaceChildren(this::replace);
            return TraversalAction.CONTINUE;
        } else if (expression instanceof AuxiliaryRoot) {
            expression.replaceChildren(this::replace);
            return TraversalAction.CONTINUE;
        } else {
            return TraversalAction.FAIL;
        }
    }

    private Expression replace(Expression expression) {
        Expression newExpression = expression;
        while (newExpression instanceof Not) {
            final Expression notChild = newExpression.getChildren().iterator().next();
            if (notChild instanceof Literal) {
                newExpression = ((Literal) notChild).invert();
            } else if (notChild instanceof Not) {
                newExpression = notChild.getChildren().get(0);
            } else if (notChild instanceof Or) {
                newExpression = new And(((Connective) notChild)
                        .getChildren().stream().map(Not::new).collect(Collectors.toList()));
            } else if (notChild instanceof And) {
                newExpression = new Or(((Connective) notChild)
                        .getChildren().stream().map(Not::new).collect(Collectors.toList()));
            }
        }
        return newExpression;
    }
}
