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
package de.featjar.formula.visitor;

import de.featjar.base.data.Result;
import de.featjar.base.data.Void;
import de.featjar.base.tree.visitor.ITreeVisitor;
import de.featjar.formula.structure.IExpression;
import de.featjar.formula.structure.Expressions;
import de.featjar.formula.structure.formula.IFormula;
import de.featjar.formula.structure.formula.connective.And;
import de.featjar.formula.structure.formula.connective.IConnective;
import de.featjar.formula.structure.formula.connective.Or;
import de.featjar.formula.structure.formula.predicate.IPredicate;

import java.util.Collections;
import java.util.List;

/**
 * Merges nested {@link And} and {@link Or} connectives and reduces
 * occurrences of {@link de.featjar.formula.structure.formula.predicate.True}
 * and {@link de.featjar.formula.structure.formula.predicate.False}.
 *
 * @author Sebastian Krieter
 */
public class AndOrSimplifier implements ITreeVisitor<IFormula, Void> {
    @Override
    public TraversalAction firstVisit(List<IFormula> path) {
        final IFormula formula = getCurrentNode(path);
        if (formula instanceof IPredicate) {
            return TraversalAction.SKIP_CHILDREN;
        } else if (formula instanceof IConnective) {
            return TraversalAction.CONTINUE;
        } else {
            return TraversalAction.FAIL;
        }
    }

    @Override
    public TraversalAction lastVisit(List<IFormula> path) {
        final IFormula formula = getCurrentNode(path);
        if (formula instanceof IConnective) {
            if (formula instanceof And) {
                // TODO: False dominates And, which is implemented, but True is neutral and can be removed
                if (formula.getChildren().stream().anyMatch(c -> c == Expressions.False)) {
                    formula.setChildren(Collections.singletonList(Expressions.False));
                } else {
                    formula.flatReplaceChildren(this::mergeAnd);
                }
            } else if (formula instanceof Or) {
                // TODO: True dominates Or, which is implemented, but False is neutral and can be removed
                if (formula.getChildren().stream().anyMatch(c -> c == Expressions.True)) {
                    formula.setChildren(Collections.singletonList(Expressions.True));
                } else {
                    formula.flatReplaceChildren(this::mergeOr);
                }
            }
        }
        return TraversalAction.CONTINUE;
    }

    private List<? extends IExpression> mergeAnd(final IExpression child) {
        return (child instanceof And)
                        || (child instanceof Or && (child.getChildren().size() == 1))
                ? child.getChildren()
                : null;
    }

    private List<? extends IExpression> mergeOr(final IExpression child) {
        return (child instanceof Or)
                        || (child instanceof And && (child.getChildren().size() == 1))
                ? child.getChildren()
                : null;
    }

    @Override
    public Result<Void> getResult() {
        return Result.ofVoid();
    }
}
