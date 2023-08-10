/*
 * Copyright (C) 2023 FeatJAR-Development-Team
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
 * See <https://github.com/FeatJAR> for further information.
 */
package de.featjar.formula.visitor;

import de.featjar.base.data.Result;
import de.featjar.base.data.Void;
import de.featjar.base.tree.visitor.ITreeVisitor;
import de.featjar.formula.structure.Expressions;
import de.featjar.formula.structure.formula.IFormula;
import de.featjar.formula.structure.formula.connective.And;
import de.featjar.formula.structure.formula.connective.IConnective;
import de.featjar.formula.structure.formula.connective.Or;
import de.featjar.formula.structure.formula.predicate.False;
import de.featjar.formula.structure.formula.predicate.IPredicate;
import de.featjar.formula.structure.formula.predicate.True;
import java.util.ArrayList;
import java.util.List;

/**
 * Reduces occurrences of {@link True} and {@link False}.
 *
 * @author Elias Kuiter
 */
public class TrueFalseSimplifier implements ITreeVisitor<IFormula, Void> {
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
            if (formula.getChildren().stream().anyMatch(c -> c == Expressions.False)) {
                // false dominates conjunction
                formula.clearChildren();
                formula.addChild(Expressions.False);
            } else {
                // true is neutral to conjunction
                formula.flatReplaceChildren(child -> child instanceof True ? new ArrayList<>() : null);
            }
        }
        if (formula instanceof Or) {
            if (formula.getChildren().stream().anyMatch(c -> c == Expressions.True)) {
                // true dominates disjunction
                formula.clearChildren();
                formula.addChild(Expressions.True);
            } else {
                // false is neutral to disjunction
                formula.flatReplaceChildren(child -> child instanceof False ? new ArrayList<>() : null);
            }
        }
        return TraversalAction.CONTINUE;
    }

    @Override
    public Result<Void> getResult() {
        return Result.ofVoid();
    }
}
