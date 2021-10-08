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
import java.util.function.*;

import org.spldev.formula.expression.*;
import org.spldev.formula.expression.atomic.literal.*;
import org.spldev.formula.expression.compound.*;
import org.spldev.util.job.*;

/**
 * Transforms propositional formulas into (clausal) CNF or DNF.
 *
 * @author Sebastian Krieter
 */
public class DistributiveLawTransformer implements MonitorableFunction<Formula, Compound> {

	public static class TransformException extends Exception {
		private static final long serialVersionUID = -988432481171140093L;
	}

	public static class MaximumNumberOfClausesExceededException extends TransformException {
		private static final long serialVersionUID = -4715579178854785874L;
	}

	public static class MaximumNumberOfLiteralsExceededException extends TransformException {
		private static final long serialVersionUID = -6094457508253000012L;
	}

	public static class MaximumLengthOfClausesExceededException extends TransformException {
		private static final long serialVersionUID = 7582471416721588997L;
	}

	private static class PathElement {
		Expression node;
		List<Expression> newChildren = new ArrayList<>();
		int maxDepth = 0;

		PathElement(Expression node) {
			this.node = node;
		}
	}

	private final Function<Collection<? extends Formula>, Formula> clauseConstructor;
	private final Class<? extends Compound> clauseClass;

	private int maximumNumberOfClauses = Integer.MAX_VALUE;
	private int maximumLengthOfClauses = Integer.MAX_VALUE;
	private int maximumNumberOfLiterals = Integer.MAX_VALUE;

	private int numberOfLiterals;

	private List<Formula> children;

	public DistributiveLawTransformer(Class<? extends Compound> clauseClass,
		Function<Collection<? extends Formula>, Formula> clauseConstructor) {
		this.clauseClass = clauseClass;
		this.clauseConstructor = clauseConstructor;
	}

	public void setMaximumNumberOfClauses(int maximumNumberOfClauses) {
		this.maximumNumberOfClauses = maximumNumberOfClauses;
	}

	public void setMaximumLengthOfClauses(int maximumLengthOfClauses) {
		this.maximumLengthOfClauses = maximumLengthOfClauses;
	}

	public void setMaximumNumberOfLiterals(int maximumNumberOfLiterals) {
		this.maximumNumberOfLiterals = maximumNumberOfLiterals;
	}

	@Override
	public Compound execute(Formula node, InternalMonitor monitor) throws TransformException {
		numberOfLiterals = 0;
		final ArrayList<PathElement> path = new ArrayList<>();
		final ArrayDeque<Expression> stack = new ArrayDeque<>();
		stack.addLast(node);
		while (!stack.isEmpty()) {
			final Expression curNode = stack.getLast();
			final boolean firstEncounter = path.isEmpty() || (curNode != path.get(path.size() - 1).node);
			if (firstEncounter) {
				if (curNode instanceof Literal) {
					final PathElement parent = path.get(path.size() - 1);
					parent.newChildren.add(curNode);
					stack.removeLast();
				} else {
					path.add(new PathElement(curNode));
					curNode.getChildren().forEach(stack::addLast);
				}
			} else {
				final PathElement currentElement = path.remove(path.size() - 1);
				curNode.setChildren(currentElement.newChildren);

				if (!path.isEmpty()) {
					final PathElement parentElement = path.get(path.size() - 1);
					parentElement.maxDepth = Math.max(currentElement.maxDepth + 1, parentElement.maxDepth);
				}

				if ((clauseClass == curNode.getClass()) && (currentElement.maxDepth > 0)) {
					final PathElement parentElement = path.get(path.size() - 1);
					parentElement.newChildren.addAll(convert(curNode));
					parentElement.maxDepth = 1;
				} else {
					if (!path.isEmpty()) {
						final PathElement parentElement = path.get(path.size() - 1);
						parentElement.newChildren.add(curNode);
					}
				}
				stack.removeLast();
			}
		}
		return (Compound) node;
	}

	@SuppressWarnings("unchecked")
	private List<Formula> convert(Expression child) throws TransformException {
		if (child instanceof Literal) {
			return null;
		} else {
			final ArrayList<Set<Literal>> newClauseList = new ArrayList<>();
			children = new ArrayList<>((List<Formula>) child.getChildren());
			children.sort(Comparator.comparingInt(c -> c.getChildren().size()));
			convertNF(newClauseList, new LinkedHashSet<>(children.size() << 1), 0);

			final ArrayList<Formula> filteredClauseList;
			newClauseList.sort(Comparator.comparingInt(Set::size));
			final int lastIndex = newClauseList.size();
			int removeCount = 0;
			for (int i = 0; i < lastIndex; i++) {
				final Set<Literal> set = newClauseList.get(i);
				if (set != null) {
					for (int j = i + 1; j < lastIndex; j++) {
						final Set<Literal> set2 = newClauseList.get(j);
						if (set2 != null) {
							if (set2.containsAll(set)) {
								newClauseList.set(j, null);
								removeCount++;
							}
						}
					}
				}
			}
			filteredClauseList = new ArrayList<>(newClauseList.size() - removeCount);
			for (final Set<Literal> children1 : newClauseList) {
				if (children1 != null) {
					filteredClauseList.add(clauseConstructor.apply(children1));
				}
			}
			return filteredClauseList;
		}
	}

	private void convertNF(List<Set<Literal>> clauses, LinkedHashSet<Literal> literals, int index)
		throws TransformException {
		if (index == children.size()) {
			if (clauses.size() > maximumNumberOfClauses) {
				throw new MaximumNumberOfClausesExceededException();
			}
			final HashSet<Literal> newClause = new HashSet<>(literals);
			if (newClause.size() > maximumLengthOfClauses) {
				throw new MaximumLengthOfClausesExceededException();
			}
			numberOfLiterals += newClause.size();
			if (numberOfLiterals > maximumNumberOfLiterals) {
				throw new MaximumNumberOfLiteralsExceededException();
			}
			clauses.add(newClause);
		} else {
			final Formula child = children.get(index);
			if (child instanceof Literal) {
				final Literal clauseLiteral = (Literal) child;
				if (literals.contains(clauseLiteral)) {
					convertNF(clauses, literals, index + 1);
				} else if (!literals.contains(clauseLiteral.flip())) {
					literals.add(clauseLiteral);
					convertNF(clauses, literals, index + 1);
					literals.remove(clauseLiteral);
				}
			} else {
				boolean redundant = false;
				for (final Expression grandChild : child.getChildren()) {
					if (grandChild instanceof Literal) {
						if (literals.contains(grandChild)) {
							redundant = true;
							break;
						}
					} else {
						int redundantCount = 0;
						for (final Expression literal : grandChild.getChildren()) {
							if (literals.contains(literal)) {
								redundantCount++;
							}
						}
						if (redundantCount == grandChild.getChildren().size()) {
							redundant = true;
							break;
						}
					}
				}
				if (redundant) {
					convertNF(clauses, literals, index + 1);
				} else {
					for (final Expression grandChild : child.getChildren()) {
						if (grandChild instanceof Literal) {
							final Literal clauseLiteral = (Literal) grandChild;
							if (!literals.contains(clauseLiteral.flip())) {
								literals.add(clauseLiteral);
								convertNF(clauses, literals, index + 1);
								literals.remove(clauseLiteral);
							}
						} else {
							boolean containsComplement = false;
							for (final Expression literal : grandChild.getChildren()) {
								if (literals.contains(((Literal) literal).flip())) {
									containsComplement = true;
									break;
								}
							}
							if (!containsComplement) {
								final ArrayList<Literal> clauseLiterals = new ArrayList<>(
									grandChild.getChildren().size());
								for (final Expression literal : grandChild.getChildren()) {
									literals.add((Literal) literal);
									clauseLiterals.add((Literal) literal);
								}
								convertNF(clauses, literals, index + 1);
								literals.removeAll(clauseLiterals);
							}
						}
					}
				}
			}
		}
	}

	public int getMaximumNumberOfClauses() {
		return maximumNumberOfClauses;
	}

	public int getMaximumLengthOfClauses() {
		return maximumLengthOfClauses;
	}

	public int getMaximumNumberOfLiterals() {
		return maximumNumberOfLiterals;
	}

}
