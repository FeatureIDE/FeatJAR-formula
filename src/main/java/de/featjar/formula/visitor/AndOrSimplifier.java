/*
 * Copyright (C) 2024 FeatJAR-Development-Team
 *
 * This file is part of FeatJAR-formula.
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
import de.featjar.formula.structure.IFormula;
import de.featjar.formula.structure.connective.*;
import de.featjar.formula.structure.predicate.False;
import de.featjar.formula.structure.predicate.IPredicate;
import de.featjar.formula.structure.predicate.Literal;
import de.featjar.formula.structure.predicate.True;

import java.util.List;

/**
 * Merges nested {@link And} and {@link Or} connectives.
 *
 * @author Sebastian Krieter
 */
public class AndOrSimplifier implements ITreeVisitor<IFormula, Void> {
    @Override
    public TraversalAction firstVisit(List<IFormula> path) {
        final IFormula formula = ITreeVisitor.getCurrentNode(path);
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
        final IFormula formula = ITreeVisitor.getCurrentNode(path);
        if (formula instanceof And) {
            formula.flatReplaceChildren(this::mergeAnd);
        } else if (formula instanceof Or) {
            formula.flatReplaceChildren(this::mergeOr);
        }
        formula.replaceChildren((child) -> {
            if (child.getChildrenCount() == 1 && ((child instanceof And) || (child instanceof Or))) {
                return child.getFirstChild().get();
            } else if (child instanceof Not) {
                return simplifyNot(child);
            } else if (child instanceof Implies) {
               return simplifyImplies(child);
            } else if (child instanceof BiImplies) {
                return simplifyBiImplies(child);
            }
            return null;
        });
        return TraversalAction.CONTINUE;
    }

    private IExpression simplifyNot(final IExpression child) {
         return child.getFirstChild().isPresent() && child.getFirstChild().get() instanceof Not ?
                 child.getFirstChild().get().getFirstChild().get() :
                 null;
    }

    private IExpression simplifyImplies(final IExpression child) {
        if (child.getChildren().get(0) instanceof False) {
            return True.INSTANCE;
        } else if (child.getChildren().get(0) instanceof True) {
            return child.getChildren().get(1);
        }
        return null;
    }

    private IExpression simplifyBiImplies(final IExpression child) {
        if ((child.getChildren().get(0) instanceof False && child.getChildren().get(1) instanceof True) ||
                (child.getChildren().get(0) instanceof True && child.getChildren().get(1) instanceof False)) {
            return False.INSTANCE;
        } else if ((child.getChildren().get(0) instanceof True && child.getChildren().get(1) instanceof True) ||
                (child.getChildren().get(0) instanceof False && child.getChildren().get(1) instanceof False)) {
            return True.INSTANCE;
        } else if (child.getChildren().get(0) instanceof False && child.getChildren().get(1) instanceof IFormula) {
            return new Not((IFormula) child.getChildren().get(1));
        } else if (child.getChildren().get(1) instanceof False && child.getChildren().get(0) instanceof IFormula) {
            return new Not((IFormula) child.getChildren().get(0));
        } else if (child.getChildren().get(0) instanceof True) {
            return child.getChildren().get(1);
        } else if (child.getChildren().get(1) instanceof True) {
            return child.getChildren().get(0);
        }
        return null;
    }

    private List<? extends IExpression> mergeAnd(final IExpression child) {
        return (child instanceof And) || (child instanceof Or && (child.getChildrenCount() == 1))
                ? child.getChildren()
                : null;
    }

    private List<? extends IExpression> mergeOr(final IExpression child) {
        return (child instanceof Or) || (child instanceof And && (child.getChildrenCount() == 1))
                ? child.getChildren()
                : null;
    }

    @Override
    public Result<Void> getResult() {
        return Result.ofVoid();
    }
}
