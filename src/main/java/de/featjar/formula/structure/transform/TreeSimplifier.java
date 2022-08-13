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
 * See <https://github.com/FeatJAR/formula> for further information.
 */
package de.featjar.formula.structure.transform;

import de.featjar.formula.structure.AuxiliaryRoot;
import de.featjar.formula.structure.Formula;
import de.featjar.formula.structure.atomic.Atomic;
import de.featjar.formula.structure.atomic.literal.Literal;
import de.featjar.formula.structure.compound.And;
import de.featjar.formula.structure.compound.Compound;
import de.featjar.formula.structure.compound.Or;
import de.featjar.util.tree.visitor.TreeVisitor;
import java.util.Arrays;
import java.util.List;

public class TreeSimplifier implements TreeVisitor<Void, Formula> {

    @Override
    public VisitorResult firstVisit(List<Formula> path) {
        final Formula node = TreeVisitor.getCurrentNode(path);
        if (node instanceof Atomic) {
            return VisitorResult.SkipChildren;
        } else if ((node instanceof AuxiliaryRoot) || (node instanceof Compound)) {
            return VisitorResult.Continue;
        } else {
            return VisitorResult.Fail;
        }
    }

    @Override
    public VisitorResult lastVisit(List<Formula> path) {
        final Formula node = TreeVisitor.getCurrentNode(path);
        if ((node instanceof AuxiliaryRoot) || (node instanceof Compound)) {
            if (node instanceof And) {
                if (node.getChildren().stream().anyMatch(c -> c == Literal.False)) {
                    node.setChildren(Arrays.asList(Literal.False));
                } else {
                    node.flatMapChildren(this::mergeAnd);
                }
            } else if (node instanceof Or) {
                if (node.getChildren().stream().anyMatch(c -> c == Literal.True)) {
                    node.setChildren(Arrays.asList(Literal.True));
                } else {
                    node.flatMapChildren(this::mergeOr);
                }
            }
        }
        return VisitorResult.Continue;
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
