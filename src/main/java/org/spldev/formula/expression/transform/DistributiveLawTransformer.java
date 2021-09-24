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

/**
 * Transforms propositional formulas into (clausal) CNF or DNF.
 *
 * @author Sebastian Krieter
 */
public abstract class DistributiveLawTransformer implements Transformer {

	public static class MaximumNumberOfClausesExceededException extends Exception {
	}

	public static class MaximumLengthOfClausesExceededException extends Exception {
	}

	private static class PathElement {
		Expression node;
		List<Expression> newChildren = new ArrayList<>();
		int maxDepth = 0;

		PathElement(Expression node) {
			this.node = node;
		}
	}

	private final int maximumNumberOfClauses;
	private final int maximumLengthOfClauses;
	private final Class<? extends Compound> clauseClass;
	private final Function<Collection<? extends Formula>, Formula> clauseConstructor;

	private List<Formula> children;

	public DistributiveLawTransformer(Class<? extends Compound> clauseClass,
									  Function<Collection<? extends Formula>, Formula> clauseConstructor,
									  int maximumNumberOfClauses, int maximumLengthOfClauses) {
		this.clauseClass = clauseClass;
		this.clauseConstructor = clauseConstructor;
		this.maximumNumberOfClauses = maximumNumberOfClauses;
		this.maximumLengthOfClauses = maximumLengthOfClauses;
	}

	public void transform(Expression node)
			throws MaximumNumberOfClausesExceededException, MaximumLengthOfClausesExceededException {
		if (node != null) {
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
		}
	}

	@SuppressWarnings("unchecked")
	private List<Formula> convert(Expression child)
			throws MaximumNumberOfClausesExceededException, MaximumLengthOfClausesExceededException {
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
		throws MaximumNumberOfClausesExceededException, MaximumLengthOfClausesExceededException {
		if (index == children.size()) {
			if (clauses.size() > maximumNumberOfClauses) {
				throw new MaximumNumberOfClausesExceededException();
			}
			HashSet<Literal> newClause = new HashSet<>(literals);
			if (newClause.size() > maximumLengthOfClauses) {
				throw new MaximumLengthOfClausesExceededException();
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

}
