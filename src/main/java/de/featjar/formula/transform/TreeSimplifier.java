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
import de.featjar.formula.structure.formula.connective.And;
import de.featjar.formula.structure.formula.connective.Connective;
import de.featjar.formula.structure.formula.connective.Or;
import de.featjar.base.tree.visitor.TreeVisitor;
import java.util.Arrays;
import java.util.List;

public class TreeSimplifier implements TreeVisitor<Expression, Void> {

    @Override
    public TraversalAction firstVisit(List<Expression> path) {
        final Expression expression = getCurrentNode(path);
        if (expression instanceof Predicate) {
            return TraversalAction.SKIP_CHILDREN;
        } else if ((expression instanceof AuxiliaryRoot) || (expression instanceof Connective)) {
            return TraversalAction.CONTINUE;
        } else {
            return TraversalAction.FAIL;
        }
    }

    @Override
    public TraversalAction lastVisit(List<Expression> path) {
        final Expression expression = getCurrentNode(path);
        if ((expression instanceof AuxiliaryRoot) || (expression instanceof Connective)) {
            if (expression instanceof And) {
                if (expression.getChildren().stream().anyMatch(c -> c == Expression.FALSE)) {
                    expression.setChildren(Arrays.asList(Expression.FALSE));
                } else {
                    expression.flatReplaceChildren(this::mergeAnd);
                }
            } else if (expression instanceof Or) {
                if (expression.getChildren().stream().anyMatch(c -> c == Expression.TRUE)) {
                    expression.setChildren(Arrays.asList(Expression.TRUE));
                } else {
                    expression.flatReplaceChildren(this::mergeOr);
                }
            }
        }
        return TraversalAction.CONTINUE;
    }

    private List<? extends Expression> mergeAnd(final Expression child) {
        return (child instanceof And)
                        || (!(child instanceof Predicate) && (child.getChildren().size() == 1))
                ? child.getChildren()
                : null;
    }

    private List<? extends Expression> mergeOr(final Expression child) {
        return (child instanceof Or)
                        || (!(child instanceof Predicate) && (child.getChildren().size() == 1))
                ? child.getChildren()
                : null;
    }
}
