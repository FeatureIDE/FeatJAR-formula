/*
 * Copyright (C) 2023 FeatJAR-Development-Team
 *
 * This file is part of FeatJAR-formula.
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
 * See <https://github.com/FeatJAR> for further information.
 */
package de.featjar.formula.analysis.value;

import de.featjar.base.computation.IComputation;
import de.featjar.base.data.Result;
import de.featjar.base.io.IO;
import de.featjar.formula.analysis.VariableMap;
import de.featjar.formula.analysis.bool.BooleanSolutionList;
import de.featjar.formula.io.value.ValueAssignmentListFormat;
import de.featjar.formula.structure.formula.IFormula;
import java.io.IOException;
import java.util.Collection;

/**
 * A list of value solutions.
 * Typically used to express solutions to a problem expressed as a {@link IFormula}.
 * Analogous to a {@link de.featjar.formula.analysis.value.ValueClauseList},
 * a {@link de.featjar.formula.analysis.value.ValueSolutionList}
 * is a low-level representation of a formula in disjunctive normal form (DNF).
 *
 * @author Elias Kuiter
 */
public class ValueSolutionList extends AValueAssignmentList<ValueSolution> {
    public ValueSolutionList() {}

    public ValueSolutionList(int size) {
        super(size);
    }

    public ValueSolutionList(ValueSolutionList other) {
        super(other);
    }

    public ValueSolutionList(Collection<? extends ValueSolution> solutions) {
        super(solutions);
    }

    @Override
    public Result<BooleanSolutionList> toBoolean(VariableMap variableMap) {
        return variableMap.toBoolean(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public IComputation<BooleanSolutionList> toBoolean(IComputation<VariableMap> variableMap) {
        return (IComputation<BooleanSolutionList>) super.toBoolean(variableMap);
    }

    public String print() {
        try {
            return IO.print(this, new ValueAssignmentListFormat<>());
        } catch (IOException e) {
            return e.toString();
        }
    }

    @Override
    public String toString() {
        return String.format("ValueSolutionList[%s]", print());
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
