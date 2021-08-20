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
import org.spldev.formula.expression.atomic.literal.*;
import org.spldev.formula.expression.compound.*;
import org.spldev.formula.expression.term.bool.*;
import org.spldev.util.tree.*;
import org.spldev.util.tree.visitor.*;

public class TseytinTransformer implements TreeVisitor<Formula, Formula> {

	private ArrayDeque<Formula> stack = new ArrayDeque<>();
	private List<Formula> substitutes = new ArrayList<>();
	private VariableMap variableMap = null;
	private int count = 0;

	@Override
	public void reset() {
		stack.clear();
		substitutes.clear();
		variableMap = null;
		count = 0;
	}

	@Override
	public Formula getResult() {
		substitutes.add((Formula) stack.pop());
		return new And(substitutes);
	}

	@Override
	public VisitorResult firstVisit(List<Formula> path) {
		final Expression node = TreeVisitor.getCurrentNode(path);
		if (variableMap == null) {
			variableMap = VariableMap.fromExpression(node);
		}
		if (node instanceof Atomic) {
			return VisitorResult.SkipChildren;
		} else if (node instanceof Compound) {
			stack.push((Formula) node);
			return VisitorResult.Continue;
		} else if (node instanceof AuxiliaryRoot) {
			stack.push((Formula) node);
			return VisitorResult.Continue;
		} else {
			return VisitorResult.Fail;
		}
	}

	@Override
	public VisitorResult lastVisit(List<Formula> path) {
		final Formula node = TreeVisitor.getCurrentNode(path);
		if (node instanceof Atomic) {
			stack.push(Trees.cloneTree(node));
		} else {
			final LiteralPredicate tempLiteral = newSubstitute();
			ArrayList<Literal> newChildren = new ArrayList<>();
			Formula pop = stack.pop();
			while (pop != node) {
				newChildren.add((Literal) pop);
				pop = stack.pop();
			}

			stack.push(tempLiteral);
			
			if (pop instanceof And) {
				ArrayList<Literal> flippedChildren = new ArrayList<>();
				for (Literal l : newChildren) {
					substitutes.add(new Or(tempLiteral.flip(), l.cloneNode()));
					flippedChildren.add(l.flip());
				}
				flippedChildren.add(tempLiteral.cloneNode());
				substitutes.add(new Or(flippedChildren));
			} else if (pop instanceof Or) {
				ArrayList<Literal> flippedChildren = new ArrayList<>();
				for (Literal l : newChildren) {
					substitutes.add(new Or(tempLiteral.cloneNode(), l.flip()));
					flippedChildren.add(l.cloneNode());
				}
				flippedChildren.add(tempLiteral.flip());
				substitutes.add(new Or(flippedChildren));
			} else {
				throw new RuntimeException(pop.getClass().toString());
			}
		}
		return TreeVisitor.super.lastVisit(path);
	}

	private LiteralPredicate newSubstitute() {
		Optional<BoolVariable> addBooleanVariable;
		do {
			addBooleanVariable = variableMap.addBooleanVariable("__temp_" + count++);
		} while (addBooleanVariable.isEmpty());
		final LiteralPredicate literal = new LiteralPredicate(addBooleanVariable.get(), true);
		return literal;
	}

}
