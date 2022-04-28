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
package org.spldev.formula.structure.transform;

import java.util.*;

import org.spldev.formula.structure.*;
import org.spldev.formula.structure.atomic.*;
import org.spldev.formula.structure.atomic.literal.*;
import org.spldev.formula.structure.compound.*;
import org.spldev.formula.structure.term.bool.*;
import org.spldev.util.job.*;
import org.spldev.util.tree.*;
import org.spldev.util.tree.visitor.*;

public class TseytinTransformer implements MonitorableFunction<Formula, List<TseytinTransformer.Substitute>>,
	TreeVisitor<Formula, Formula> {

	public static final class Substitute {
		private Formula orgFormula;
		private BoolVariable variable;
		private List<Formula> clauses = new ArrayList<>();

		private Substitute(Formula orgFormula, BoolVariable variable, int numberOfClauses) {
			this.orgFormula = orgFormula;
			this.variable = variable;
			clauses = new ArrayList<>(numberOfClauses);
		}

		private Substitute(Formula orgFormula, BoolVariable variable, Formula clause) {
			this.orgFormula = orgFormula;
			this.variable = variable;
			clauses = new ArrayList<>(1);
			clauses.add(clause);
		}

		private Substitute(Formula orgFormula, BoolVariable variable, List<? extends Formula> clauses) {
			this.orgFormula = orgFormula;
			this.variable = variable;
			this.clauses = new ArrayList<>(clauses);
		}

		private void addClause(Formula clause) {
			clauses.add(clause);
		}

		public BoolVariable getVariable() {
			return variable;
		}

		public List<Formula> getClauses() {
			return clauses;
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(orgFormula);
		}

		@Override
		public boolean equals(Object obj) {
			return (obj != null) && (getClass() == obj.getClass())
				&& Objects.equals(orgFormula, ((Substitute) obj).orgFormula);
		}

	}

	private final List<Substitute> substitutes = new ArrayList<>();

	private VariableMap variableMap;
	private int count = 0;

	public void setVariableMap(VariableMap variableMap) {
		this.variableMap = variableMap;
	}

	private BoolVariable newVariable(final ArrayList<Literal> newChildren, final Formula clonedLastNode) {
		Optional<BoolVariable> addBooleanVariable;
		do {
			addBooleanVariable = variableMap.addBooleanVariable("__temp__" + count++);
		} while (addBooleanVariable.isEmpty());
		final Substitute substitute = new Substitute(clonedLastNode, addBooleanVariable.get(), newChildren.size() + 1);
		substitutes.add(substitute);

		final LiteralPredicate tempLiteral = new LiteralPredicate(substitute.variable, true);
		if (clonedLastNode instanceof And) {
			final ArrayList<Literal> flippedChildren = new ArrayList<>();
			for (final Literal l : newChildren) {
				substitute.addClause(new Or(tempLiteral.flip(), l.cloneNode()));
				flippedChildren.add(l.flip());
			}
			flippedChildren.add(tempLiteral.cloneNode());
			substitute.addClause(new Or(flippedChildren));
		} else if (clonedLastNode instanceof Or) {
			final ArrayList<Literal> flippedChildren = new ArrayList<>();
			for (final Literal l : newChildren) {
				substitute.addClause(new Or(tempLiteral.cloneNode(), l.flip()));
				flippedChildren.add(l.cloneNode());
			}
			flippedChildren.add(tempLiteral.flip());
			substitute.addClause(new Or(flippedChildren));
		} else {
			throw new RuntimeException(clonedLastNode.getClass().toString());
		}
		return substitute.variable;
	}

	private final ArrayDeque<Formula> stack = new ArrayDeque<>();

	@Override
	public List<Substitute> execute(Formula child, InternalMonitor monitor) {
		substitutes.clear();
		stack.clear();

		Trees.sortTree(child);
		if (variableMap == null) {
			variableMap = VariableMap.fromExpression(child).clone();
		}

		try {
			Trees.dfsPrePost(child, this);
		} catch (final Exception ignored) {
		}
		return substitutes;
	}

	@Override
	public VisitorResult firstVisit(List<Formula> path) {
		final Expression node = TreeVisitor.getCurrentNode(path);
		if (node instanceof Atomic) {
			return VisitorResult.SkipChildren;
		} else if ((node instanceof Compound) || (node instanceof AuxiliaryRoot)) {
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
			final Formula clonedNode = node;
			if (path.isEmpty()) {
				substitutes.add(new Substitute(clonedNode, null, clonedNode));
			} else {
				stack.push(clonedNode);
			}
		} else {
			final ArrayList<Literal> newChildren = new ArrayList<>();
			Formula lastNode = stack.pop();
			while (lastNode != node) {
				newChildren.add((Literal) lastNode);
				lastNode = stack.pop();
			}

			if (stack.isEmpty()) {
				final Formula clonedLastNode = lastNode;
				if (lastNode instanceof And) {
					substitutes.add(new Substitute(clonedLastNode, null, newChildren));
				} else {
					substitutes.add(new Substitute(clonedLastNode, null, new Or(newChildren)));
				}
			} else {
				final Formula clonedLastNode = lastNode;
				final BoolVariable variable = newVariable(newChildren, clonedLastNode);
				stack.push(new LiteralPredicate(variable, true));
			}

		}
		return TreeVisitor.super.lastVisit(path);
	}

}
