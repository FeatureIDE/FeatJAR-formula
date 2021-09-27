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
import org.spldev.formula.expression.transform.DistributiveLawTransformer.*;
import org.spldev.formula.expression.transform.NormalForms.*;
import org.spldev.util.job.*;
import org.spldev.util.tree.*;
import org.spldev.util.tree.visitor.*;

public class CNFTseytinTransformer implements Transformer, TreeVisitor<Formula, Formula> {

	private ArrayDeque<Formula> stack = new ArrayDeque<>();
	private List<Formula> substitutes = new ArrayList<>();
	private VariableMap variableMap = null;
	private int count = 0;
	private HashMap<Formula, BoolVariable> newVariables = new HashMap<>();

	private final CNFDistributiveLawTransformer distributiveLawTransformer;

	public CNFTseytinTransformer(int maximumNumberOfClauses, int maximumLengthOfClauses) {
		distributiveLawTransformer = maximumNumberOfClauses > 0 || maximumLengthOfClauses > 0
			? new CNFDistributiveLawTransformer(maximumNumberOfClauses, maximumLengthOfClauses)
			: null;
	}

	@Override
	public void reset() {
		stack.clear();
		substitutes.clear();
		newVariables.clear();
		variableMap = null;
		count = 0;
	}

	@Override
	public Formula execute(Formula formula, InternalMonitor monitor) {
		formula = Trees.cloneTree(formula);
		final NFTester nfTester = NormalForms.getNFTester(formula, NormalForm.CNF);
		if (nfTester.isNf) {
			if (!nfTester.isClausalNf()) {
				formula = NormalForms.toClausalNF(formula, NormalForm.CNF);
			}
		} else {
			variableMap = VariableMap.fromExpression(formula).clone();
			formula = NormalForms.simplifyForNF(formula);
			if (formula instanceof And) {
				final ArrayList<Formula> newChildren = new ArrayList<>();
				final List<Formula> children = ((And) formula).getChildren();
				for (Formula child : children) {
					if (Formulas.isCNF(child)) {
						if (child instanceof And) {
							for (Formula grandChild : ((And) child).getChildren()) {
								grandChild = Trees.cloneTree(grandChild);
								grandChild.setVariableMap(variableMap);
								newChildren.add(grandChild);
							}
						} else {
							child = Trees.cloneTree(child);
							child.setVariableMap(variableMap);
							newChildren.add(child);
						}
					} else {
						if (distributiveLawTransformer != null) {
							final And clonedChild = new And(Trees.cloneTree(child));
							try {
								distributiveLawTransformer.transform(clonedChild);
								newChildren.addAll(clonedChild.getChildren());
							} catch (final MaximumNumberOfClausesExceededException
								| MaximumLengthOfClausesExceededException e) {
								tseytin(newChildren, child);
							}
						} else {
							tseytin(newChildren, child);
						}
					}
				}
				formula = new And(newChildren);
			} else {
				formula = Trees.traverse(formula, this).get();
			}
			formula = NormalForms.toClausalNF(formula, NormalForm.CNF);
		}
		return formula;
	}

	public void tseytin(final ArrayList<Formula> newChildren, Formula child) {
		substitutes.clear();
		stack.clear();
		try {
			Trees.dfsPrePost(child, this);
		} catch (final Exception e) {
		}
		if (!stack.isEmpty()) {
			newChildren.add(stack.pop());
		}
		newChildren.addAll(substitutes);
	}

	@Override
	public Formula getResult() {
		if (!stack.isEmpty()) {
			substitutes.add(stack.pop());
		}
		return new And(substitutes);
	}

	@Override
	public VisitorResult firstVisit(List<Formula> path) {
		final Expression node = TreeVisitor.getCurrentNode(path);
		if (variableMap == null) {
			variableMap = VariableMap.fromExpression(node).clone();
		}
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
			final Formula clonedNode = Trees.cloneTree(node);
			clonedNode.setVariableMap(variableMap);
			stack.push(clonedNode);
		} else {
			final ArrayList<Literal> newChildren = new ArrayList<>();
			Formula lastNode = stack.pop();
			while (lastNode != node) {
				newChildren.add((Literal) lastNode);
				lastNode = stack.pop();
			}

			if (stack.isEmpty()) {
				Collections.sort(newChildren, Comparator.comparing(Expression::toString));
				if (lastNode instanceof And) {
					for (final Formula child : newChildren) {
						substitutes.add(child);
					}
				} else {
					substitutes.add(new Or(newChildren));
				}
			} else {
				final Formula clonedLastNode = Trees.cloneTree(lastNode);
				final ArrayList<? extends Expression> clonedChildren = new ArrayList<>(clonedLastNode.getChildren());
				Collections.sort(clonedChildren, Comparator.comparing(Expression::toString));
				clonedLastNode.setChildren(clonedChildren);

				LiteralPredicate tempLiteral;
				BoolVariable newVariable = newVariables.get(clonedLastNode);
				if (newVariable == null) {
					Optional<BoolVariable> addBooleanVariable;
					do {
						addBooleanVariable = variableMap.addBooleanVariable("__temp_" + count++);
					} while (addBooleanVariable.isEmpty());
					newVariable = addBooleanVariable.get();
					newVariables.put(clonedLastNode, newVariable);
					tempLiteral = new LiteralPredicate(newVariable, true);
					if (clonedLastNode instanceof And) {
						final ArrayList<Literal> flippedChildren = new ArrayList<>();
						for (final Literal l : newChildren) {
							substitutes.add(new Or(tempLiteral.flip(), l.cloneNode()));
							flippedChildren.add(l.flip());
						}
						flippedChildren.add(tempLiteral.cloneNode());
						substitutes.add(new Or(flippedChildren));
					} else if (clonedLastNode instanceof Or) {
						final ArrayList<Literal> flippedChildren = new ArrayList<>();
						for (final Literal l : newChildren) {
							substitutes.add(new Or(tempLiteral.cloneNode(), l.flip()));
							flippedChildren.add(l.cloneNode());
						}
						flippedChildren.add(tempLiteral.flip());
						substitutes.add(new Or(flippedChildren));
					} else {
						throw new RuntimeException(lastNode.getClass().toString());
					}
				} else {
					tempLiteral = new LiteralPredicate(newVariable, true);
				}

				stack.push(tempLiteral);
			}

		}
		return TreeVisitor.super.lastVisit(path);
	}

}
