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

import de.featjar.formula.structure.Formula;
import de.featjar.formula.structure.atomic.literal.Literal;
import de.featjar.formula.structure.compound.Compound;
import de.featjar.util.job.InternalMonitor;
import de.featjar.util.job.MonitorableFunction;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Transforms propositional formulas into (clausal) CNF or DNF.
 *
 * @author Sebastian Krieter
 */
public class DistributiveLawTransformer implements MonitorableFunction<Formula, Compound> {

    public static class MaximumNumberOfLiteralsExceededException extends Exception {
        private static final long serialVersionUID = 7582471416721588997L;
    }

    private static class PathElement {
        Formula node;
        List<Formula> newChildren = new ArrayList<>();
        int maxDepth = 0;

        PathElement(Formula node) {
            this.node = node;
        }
    }

    private final Function<List<? extends Formula>, Formula> clauseConstructor;
    private final Class<? extends Compound> clauseClass;

    private int maximumNumberOfLiterals = Integer.MAX_VALUE;

    private int numberOfLiterals;

    private List<Formula> children;

    public DistributiveLawTransformer(
            Class<? extends Compound> clauseClass, Function<List<? extends Formula>, Formula> clauseConstructor) {
        this.clauseClass = clauseClass;
        this.clauseConstructor = clauseConstructor;
    }

    public void setMaximumNumberOfLiterals(int maximumNumberOfLiterals) {
        this.maximumNumberOfLiterals = maximumNumberOfLiterals;
    }

    @Override
    public Compound execute(Formula node, InternalMonitor monitor) throws MaximumNumberOfLiteralsExceededException {
        final ArrayList<PathElement> path = new ArrayList<>();
        final ArrayDeque<Formula> stack = new ArrayDeque<>();
        stack.addLast(node);
        while (!stack.isEmpty()) {
            final Formula curNode = stack.getLast();
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
    private List<Formula> convert(Formula child) throws MaximumNumberOfLiteralsExceededException {
        if (child instanceof Literal) {
            return null;
        } else {
            numberOfLiterals = 0;
            final ArrayList<Set<Literal>> newClauseList = new ArrayList<>();
            children = new ArrayList<>((List<Formula>) child.getChildren());
            children.sort(Comparator.comparingInt(c -> c.getChildren().size()));
            convertNF(newClauseList, new LinkedHashSet<>(children.size() << 1), 0);

            final List<Formula> filteredClauseList = new ArrayList<>(newClauseList.size());
            newClauseList.sort(Comparator.comparingInt(Set::size));
            final int lastIndex = newClauseList.size();
            for (int i = 0; i < lastIndex; i++) {
                final Set<Literal> set = newClauseList.get(i);
                if (set != null) {
                    for (int j = i + 1; j < lastIndex; j++) {
                        final Set<Literal> set2 = newClauseList.get(j);
                        if (set2 != null) {
                            if (set2.containsAll(set)) {
                                newClauseList.set(j, null);
                            }
                        }
                    }
                    filteredClauseList.add(clauseConstructor.apply(new ArrayList<>(set)));
                }
            }
            return filteredClauseList;
        }
    }

    private void convertNF(List<Set<Literal>> clauses, LinkedHashSet<Literal> literals, int index)
            throws MaximumNumberOfLiteralsExceededException {
        if (index == children.size()) {
            final HashSet<Literal> newClause = new HashSet<>(literals);
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
                if (isRedundant(literals, child)) {
                    convertNF(clauses, literals, index + 1);
                } else {
                    for (final Formula grandChild : child.getChildren()) {
                        if (grandChild instanceof Literal) {
                            final Literal newlyAddedLiteral = (Literal) grandChild;
                            if (!literals.contains(newlyAddedLiteral.flip())) {
                                literals.add(newlyAddedLiteral);
                                convertNF(clauses, literals, index + 1);
                                literals.remove(newlyAddedLiteral);
                            }
                        } else {
                            @SuppressWarnings("unchecked")
                            final List<Literal> greatGrandChildren = (List<Literal>) grandChild.getChildren();
                            if (containsNoComplements(literals, greatGrandChildren)) {
                                final List<Literal> newlyAddedLiterals = greatGrandChildren.stream()
                                        .filter(literals::add)
                                        .collect(Collectors.toList());
                                convertNF(clauses, literals, index + 1);
                                literals.removeAll(newlyAddedLiterals);
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean containsNoComplements(LinkedHashSet<Literal> literals, final List<Literal> greatGrandChildren) {
        return greatGrandChildren.stream().map(Literal::flip).noneMatch(literals::contains);
    }

    private boolean isRedundant(LinkedHashSet<Literal> literals, final Formula child) {
        return child.getChildren().stream().anyMatch(e -> isRedundant(e, literals));
    }

    private static boolean isRedundant(Formula expression, LinkedHashSet<Literal> literals) {
        return (expression instanceof Literal)
                ? literals.contains(expression)
                : expression.getChildren().stream().allMatch(literals::contains);
    }

    public int getMaximumNumberOfLiterals() {
        return maximumNumberOfLiterals;
    }
}
