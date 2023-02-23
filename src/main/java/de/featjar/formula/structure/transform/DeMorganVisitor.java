/*
 * Copyright (C) 2023 Sebastian Krieter, Elias Kuiter
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
package de.featjar.formula.structure.transform;

import de.featjar.formula.structure.AuxiliaryRoot;
import de.featjar.formula.structure.Formula;
import de.featjar.formula.structure.atomic.Atomic;
import de.featjar.formula.structure.atomic.literal.Literal;
import de.featjar.formula.structure.compound.And;
import de.featjar.formula.structure.compound.Compound;
import de.featjar.formula.structure.compound.Not;
import de.featjar.formula.structure.compound.Or;
import de.featjar.util.tree.visitor.TreeVisitor;
import java.util.List;
import java.util.stream.Collectors;

public class DeMorganVisitor implements TreeVisitor<Void, Formula> {

    @Override
    public VisitorResult firstVisit(List<Formula> path) {
        final Formula node = TreeVisitor.getCurrentNode(path);
        if (node instanceof Atomic) {
            return VisitorResult.SkipChildren;
        } else if (node instanceof Compound) {
            node.mapChildren(this::replace);
            return VisitorResult.Continue;
        } else if (node instanceof AuxiliaryRoot) {
            node.mapChildren(this::replace);
            return VisitorResult.Continue;
        } else {
            return VisitorResult.Fail;
        }
    }

    private Formula replace(Formula node) {
        Formula newNode = node;
        while (newNode instanceof Not) {
            final Formula notChild = (Formula) newNode.getChildren().iterator().next();
            if (notChild instanceof Literal) {
                newNode = ((Literal) notChild).flip();
            } else if (notChild instanceof Not) {
                newNode = notChild.getChildren().get(0);
            } else if (notChild instanceof Or) {
                newNode = new And(((Compound) notChild)
                        .getChildren().stream().map(Not::new).collect(Collectors.toList()));
            } else if (notChild instanceof And) {
                newNode = new Or(((Compound) notChild)
                        .getChildren().stream().map(Not::new).collect(Collectors.toList()));
            }
        }
        return newNode;
    }
}
