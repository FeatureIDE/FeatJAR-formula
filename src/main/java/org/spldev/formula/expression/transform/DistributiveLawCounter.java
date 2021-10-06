/* -----------------------------------------------------------------------------
 * Formula Lib - Library to represent and edit propositional formulas.
 * Copyright (C) 2021  Sebastian Krieter
 * 
 * This file is part of Formula Lib.
 * 
 * Formula Lib is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Formula Lib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Formula Lib.  If not, see <https://www.gnu.org/licenses/>.
 * 
 * See <https://github.com/skrieter/formula> for further information.
 * -----------------------------------------------------------------------------
 */
package org.spldev.formula.expression.transform;

import java.util.*;

import org.spldev.formula.expression.*;
import org.spldev.formula.expression.atomic.*;
import org.spldev.formula.expression.compound.*;
import org.spldev.util.tree.visitor.*;

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
	public Integer getResult() { // TODO BigInteger?
		return stack.pop().clauseNumber;
	}

	@Override
	public VisitorResult firstVisit(List<Formula> path) {
		final Expression node = TreeVisitor.getCurrentNode(path);
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
