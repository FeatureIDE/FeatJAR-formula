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
import de.featjar.formula.structure.atomic.Atomic;
import de.featjar.formula.structure.connective.And;
import de.featjar.formula.structure.connective.Connective;
import de.featjar.formula.structure.connective.Or;
import de.featjar.base.tree.visitor.TreeVisitor;
import java.util.Arrays;
import java.util.List;

public class TreeSimplifier implements TreeVisitor<Void, Formula> {

    @Override
    public TraversalAction firstVisit(List<Formula> path) {
        final Formula node = getCurrentNode(path);
        if (node instanceof Atomic) {
            return TraversalAction.SKIP_CHILDREN;
        } else if ((node instanceof AuxiliaryRoot) || (node instanceof Connective)) {
            return TraversalAction.CONTINUE;
        } else {
            return TraversalAction.FAIL;
        }
    }

    @Override
    public TraversalAction lastVisit(List<Formula> path) {
        final Formula node = getCurrentNode(path);
        if ((node instanceof AuxiliaryRoot) || (node instanceof Connective)) {
            if (node instanceof And) {
                if (node.getChildren().stream().anyMatch(c -> c == Formula.FALSE)) {
                    node.setChildren(Arrays.asList(Formula.FALSE));
                } else {
                    node.flatReplaceChildren(this::mergeAnd);
                }
            } else if (node instanceof Or) {
                if (node.getChildren().stream().anyMatch(c -> c == Formula.TRUE)) {
                    node.setChildren(Arrays.asList(Formula.TRUE));
                } else {
                    node.flatReplaceChildren(this::mergeOr);
                }
            }
        }
        return TraversalAction.CONTINUE;
    }

    private List<? extends Formula> mergeAnd(final Formula child) {
        return (child instanceof And)
                        || (!(child instanceof Atomic) && (child.getChildren().size() == 1))
                ? child.getChildren()
                : null;
    }

    private List<? extends Formula> mergeOr(final Formula child) {
        return (child instanceof Or)
                        || (!(child instanceof Atomic) && (child.getChildren().size() == 1))
                ? child.getChildren()
                : null;
    }
}
