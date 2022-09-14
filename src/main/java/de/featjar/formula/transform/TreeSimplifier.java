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

import de.featjar.formula.structure.AuxiliaryRoot;
import de.featjar.formula.structure.Formula;
import de.featjar.formula.structure.formula.Predicate;
import de.featjar.formula.structure.formula.connective.And;
import de.featjar.formula.structure.formula.connective.Connective;
import de.featjar.formula.structure.formula.connective.Or;
import de.featjar.base.tree.visitor.TreeVisitor;
import java.util.Arrays;
import java.util.List;

public class TreeSimplifier implements TreeVisitor<Void, Formula> {

    @Override
    public TraversalAction firstVisit(List<Formula> path) {
        final Formula formula = getCurrentNode(path);
        if (formula instanceof Predicate) {
            return TraversalAction.SKIP_CHILDREN;
        } else if ((formula instanceof AuxiliaryRoot) || (formula instanceof Connective)) {
            return TraversalAction.CONTINUE;
        } else {
            return TraversalAction.FAIL;
        }
    }

    @Override
    public TraversalAction lastVisit(List<Formula> path) {
        final Formula formula = getCurrentNode(path);
        if ((formula instanceof AuxiliaryRoot) || (formula instanceof Connective)) {
            if (formula instanceof And) {
                if (formula.getChildren().stream().anyMatch(c -> c == Formula.FALSE)) {
                    formula.setChildren(Arrays.asList(Formula.FALSE));
                } else {
                    formula.flatReplaceChildren(this::mergeAnd);
                }
            } else if (formula instanceof Or) {
                if (formula.getChildren().stream().anyMatch(c -> c == Formula.TRUE)) {
                    formula.setChildren(Arrays.asList(Formula.TRUE));
                } else {
                    formula.flatReplaceChildren(this::mergeOr);
                }
            }
        }
        return TraversalAction.CONTINUE;
    }

    private List<? extends Formula> mergeAnd(final Formula child) {
        return (child instanceof And)
                        || (!(child instanceof Predicate) && (child.getChildren().size() == 1))
                ? child.getChildren()
                : null;
    }

    private List<? extends Formula> mergeOr(final Formula child) {
        return (child instanceof Or)
                        || (!(child instanceof Predicate) && (child.getChildren().size() == 1))
                ? child.getChildren()
                : null;
    }
}
