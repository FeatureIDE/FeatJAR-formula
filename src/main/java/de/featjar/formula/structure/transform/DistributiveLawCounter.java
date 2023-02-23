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
import de.featjar.formula.structure.compound.Compound;
import de.featjar.util.tree.visitor.TreeVisitor;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DistributiveLawCounter implements TreeVisitor<Integer, Formula> {

    private static class StackElement {
        int clauseNumber = 1;
        int clauseSize = 1;
        Formula node;

        public StackElement(Formula node) {
            this.node = node;
        }
    }

    private ArrayDeque<StackElement> stack = new ArrayDeque<>();

    @Override
    public void reset() {
        stack.clear();
    }

    @Override
    public Optional<Integer> getResult() { // TODO BigInteger?
        return Optional.of(stack.pop().clauseNumber);
    }

    @Override
    public VisitorResult firstVisit(List<Formula> path) {
        final Formula node = TreeVisitor.getCurrentNode(path);
        if (node instanceof Atomic) {
            return VisitorResult.SkipChildren;
        } else if ((node instanceof Compound) || (node instanceof AuxiliaryRoot)) {
            stack.push(new StackElement((Formula) node));
            return VisitorResult.Continue;
        } else {
            return VisitorResult.Fail;
        }
    }

    @Override
    public VisitorResult lastVisit(List<Formula> path) {
        final Formula node = TreeVisitor.getCurrentNode(path);
        if (node instanceof Atomic) {
            stack.push(new StackElement(node));
        } else {
            final ArrayList<StackElement> children = new ArrayList<>();
            StackElement lastNode = stack.pop();
            boolean invalid = false;
            for (; lastNode.node != node; lastNode = stack.pop()) {
                children.add(lastNode);
                if (lastNode.clauseNumber < 0) {
                    invalid = true;
                }
            }
            if (invalid) {
                lastNode.clauseNumber = -1;
            } else {
                try {
                    for (final StackElement child : children) {
                        for (int i = 0; i < child.clauseNumber; i++) {
                            lastNode.clauseNumber = Math.multiplyExact(lastNode.clauseNumber, child.clauseSize);
                        }
                    }
                } catch (final ArithmeticException e) {
                    lastNode.clauseNumber = -1;
                }
                lastNode.clauseSize = children.size();
            }
            stack.push(lastNode);
        }
        return TreeVisitor.super.lastVisit(path);
    }
}
