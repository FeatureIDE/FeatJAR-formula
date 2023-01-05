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
 * See <https://github.com/FeatureIDE/FeatJAR-formula> for further information.
 */
package de.featjar.formula.transformation;

import de.featjar.base.computation.*;
import de.featjar.base.data.Result;
import de.featjar.base.computation.Progress;
import de.featjar.base.tree.structure.ITree;
import de.featjar.formula.structure.IExpression;
import de.featjar.formula.structure.formula.IFormula;
import de.featjar.formula.structure.formula.connective.And;
import de.featjar.formula.structure.formula.connective.IConnective;
import de.featjar.formula.structure.formula.connective.Or;
import de.featjar.formula.structure.formula.predicate.Literal;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Transforms a formula into clausal conjunctive or disjunctive normal form using the distributive law.
 *
 * @author Sebastian Krieter
 */
public class ComputeNormalFormFormula extends AComputation<IFormula> implements ITransformation<IFormula> {
    protected static final Dependency<IFormula> NNF_FORMULA = newRequiredDependency();

    public static class MaximumNumberOfLiteralsExceededException extends Exception {
    }

    private static class PathElement {
        IExpression expression;
        List<IExpression> newChildren = new ArrayList<>();
        int maxDepth = 0;

        PathElement(IExpression expression) {
            this.expression = expression;
        }
    }

    private final IFormula.NormalForm normalForm; //todo: input as dependency
    private final Class<? extends IConnective> clauseClass;
    private final Function<List<? extends IFormula>, IFormula> clauseConstructor;

    private int maximumNumberOfLiterals = Integer.MAX_VALUE;

    private int numberOfLiterals;

    private List<IExpression> children;

    public ComputeNormalFormFormula(IComputation<IFormula> nnfFormula, IFormula.NormalForm normalForm) {
        dependOn(NNF_FORMULA);
        setInput(nnfFormula);
        this.normalForm = normalForm;
        switch (normalForm) {
            case CNF:
                clauseClass = Or.class;
                clauseConstructor = Or::new;
                break;
            case DNF:
                clauseClass = And.class;
                clauseConstructor = And::new;
                break;
            default:
                throw new IllegalStateException("unsupported normal form");
        }
    }

    @Override
    public Dependency<IFormula> getInputDependency() {
        return NNF_FORMULA;
    }

    public void setMaximumNumberOfLiterals(int maximumNumberOfLiterals) {
        this.maximumNumberOfLiterals = maximumNumberOfLiterals;
    }

    @Override
    public Result<IFormula> computeResult(List<?> results, Progress progress) {
        IFormula formula = NNF_FORMULA.get(results);
        if (normalForm.equals(IFormula.NormalForm.CNF))
            formula = (formula instanceof And) ? (And) formula : new And(formula);
        if (normalForm.equals(IFormula.NormalForm.DNF))
            formula = (formula instanceof Or) ? (Or) formula : new Or(formula);

        final ArrayList<PathElement> path = new ArrayList<>();
        final ArrayDeque<IExpression> stack = new ArrayDeque<>();
        stack.addLast(formula);
        while (!stack.isEmpty()) {
            final IExpression curNode = stack.getLast();
            final boolean firstEncounter = path.isEmpty() || (curNode != path.get(path.size() - 1).expression);
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
                    try {
                        parentElement.newChildren.addAll(convert(curNode));
                    } catch (MaximumNumberOfLiteralsExceededException e) {
                        return Result.empty(e);
                    }
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
        return Result.of(formula);
    }

    private List<IExpression> convert(IExpression child) throws MaximumNumberOfLiteralsExceededException {
        if (child instanceof Literal) {
            return new ArrayList<>();
        } else {
            numberOfLiterals = 0;
            final ArrayList<LinkedHashSet<Literal>> newClauseList = new ArrayList<>();
            children = new ArrayList<>(child.getChildren());
            children.sort(Comparator.comparingInt(ITree::getChildrenCount));
            convertNF(newClauseList, new LinkedHashSet<>(children.size() << 1), 0);

            final List<IExpression> filteredClauseList = new ArrayList<>(newClauseList.size());
            newClauseList.sort(Comparator.comparingInt(Set::size));
            final int lastIndex = newClauseList.size();
            for (int i = 0; i < lastIndex; i++) {
                final LinkedHashSet<Literal> set = newClauseList.get(i);
                if (set != null) {
                    for (int j = i + 1; j < lastIndex; j++) {
                        final LinkedHashSet<Literal> set2 = newClauseList.get(j);
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

    private void convertNF(List<LinkedHashSet<Literal>> clauses, LinkedHashSet<Literal> literals, int index)
            throws MaximumNumberOfLiteralsExceededException {
        if (index == children.size()) {
            final LinkedHashSet<Literal> newClause = new LinkedHashSet<>(literals);
            numberOfLiterals += newClause.size();
            if (numberOfLiterals > maximumNumberOfLiterals) {
                throw new MaximumNumberOfLiteralsExceededException();
            }
            clauses.add(newClause);
        } else {
            final IExpression child = children.get(index);
            if (child instanceof Literal) {
                final Literal clauseLiteral = (Literal) child;
                if (literals.contains(clauseLiteral)) {
                    convertNF(clauses, literals, index + 1);
                } else if (!literals.contains(clauseLiteral.invert())) {
                    literals.add(clauseLiteral);
                    convertNF(clauses, literals, index + 1);
                    literals.remove(clauseLiteral);
                }
            } else {
                if (isRedundant(literals, child)) {
                    convertNF(clauses, literals, index + 1);
                } else {
                    for (final IExpression grandChild : child.getChildren()) {
                        if (grandChild instanceof Literal) {
                            final Literal newlyAddedLiteral = (Literal) grandChild;
                            if (!literals.contains(newlyAddedLiteral.invert())) {
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
                                newlyAddedLiterals.forEach(literals::remove);
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean containsNoComplements(LinkedHashSet<Literal> literals, final List<Literal> greatGrandChildren) {
        return greatGrandChildren.stream().map(Literal::invert).noneMatch(literals::contains);
    }

    private boolean isRedundant(LinkedHashSet<Literal> literals, final IExpression child) {
        return child.getChildren().stream().anyMatch(e -> isRedundant(e, literals));
    }

    private static boolean isRedundant(IExpression expression, LinkedHashSet<Literal> literals) {
        return (expression instanceof Literal)
                ? literals.contains(expression)
                : literals.containsAll(expression.getChildren());
    }

    public int getMaximumNumberOfLiterals() {
        return maximumNumberOfLiterals;
    }

    @Override
    public ITree<IComputation<?>> cloneNode() {
        return new ComputeNormalFormFormula(getInput(), normalForm);
    }
}
