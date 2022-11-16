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
package de.featjar.formula.analysis.bool;

import de.featjar.formula.analysis.value.ValueClauseList;
import de.featjar.formula.transformer.ToCNF;

import java.util.Collection;
import java.util.List;

/**
 * A list of Boolean solutions.
 * Typically used to express solutions to a problem expressed as a {@link de.featjar.formula.structure.formula.Formula}.
 * Analogous to a {@link de.featjar.formula.analysis.bool.BooleanClauseList},
 * a {@link de.featjar.formula.analysis.bool.BooleanSolutionList}
 * is a low-level representation of a formula in disjunctive normal form (DNF).
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public class BooleanSolutionList extends BooleanAssignmentList<BooleanSolutionList, BooleanSolution> {
    public BooleanSolutionList() {
    }

    public BooleanSolutionList(int size) {
        super(size);
    }

    public BooleanSolutionList(Collection<? extends BooleanSolution> solutions) {
        super(solutions);
    }

    public BooleanSolutionList(BooleanSolutionList other) {
        super(other);
    }

    @Override
    protected BooleanSolutionList newLiteralMatrix(List<BooleanSolution> LiteralSolutions) {
        return new BooleanSolutionList(LiteralSolutions);
    }

//    public SortedIntegerList getVariableAssignment(int variable) {
//        final int[] assignment = new int[solutions.size()];
//        int index = 0;
//        for (final SortedIntegerList solution : solutions) {
//            assignment[index++] = solution.getIntegers()[variable];
//        }
//        return new SortedIntegerList(assignment, SortedIntegerList.Order.UNORDERED);
//    }

//    private String literalToString(int literal) {
//        final Optional<String> name = variables.get(Math.abs(literal));
//        return name.isEmpty() ? "?" : (literal > 0 ? "" : "-") + name.get();
//    }
//
//    public String getSolutionsString() {
//        final StringBuilder sb = new StringBuilder();
//        for (final SortedIntegerList sortedIntegerList : solutions) {
//            sb.append("(");
//            final List<String> literals = Arrays.stream(sortedIntegerList.getIntegers())
//                    .mapToObj(this::literalToString)
//                    .filter(Objects::nonNull)
//                    .collect(Collectors.toList());
//            for (final String literal : literals) {
//                sb.append(literal);
//                sb.append(", ");
//            }
//            if (!literals.isEmpty()) {
//                sb.delete(sb.length() - 2, sb.length());
//            }
//            sb.append("), ");
//        }
//        if (!solutions.isEmpty()) {
//            sb.delete(sb.length() - 2, sb.length());
//        }
//        return sb.toString();
//    }
//
//    public Stream<SortedIntegerList> getInvalidSolutions(CNF cnf) {
//        return solutions.stream() //
//                .filter(s -> cnf.getClauseList().stream() //
//                        .anyMatch(clause -> s.containsAll(clause.negate())));
//    }
//
//    public Stream<SortedIntegerList> getValidSolutions(CNF cnf) {
//        return solutions.stream() //
//                .filter(s -> cnf.getClauseList().stream() //
//                        .allMatch(clause -> !s.isDisjoint(clause)));
//    }
}
