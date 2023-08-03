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
package de.featjar.formula.analysis;

import de.featjar.base.data.IntegerList;
import de.featjar.base.data.Maps;
import de.featjar.base.data.Pair;
import de.featjar.base.data.Problem;
import de.featjar.base.data.RangeMap;
import de.featjar.base.data.Result;
import de.featjar.formula.analysis.bool.ABooleanAssignment;
import de.featjar.formula.analysis.bool.ABooleanAssignmentList;
import de.featjar.formula.analysis.bool.BooleanAssignment;
import de.featjar.formula.analysis.bool.BooleanAssignmentList;
import de.featjar.formula.analysis.bool.BooleanClause;
import de.featjar.formula.analysis.bool.BooleanClauseList;
import de.featjar.formula.analysis.bool.BooleanSolution;
import de.featjar.formula.analysis.bool.BooleanSolutionList;
import de.featjar.formula.analysis.value.AValueAssignment;
import de.featjar.formula.analysis.value.AValueAssignmentList;
import de.featjar.formula.analysis.value.IValueRepresentation;
import de.featjar.formula.analysis.value.ValueAssignment;
import de.featjar.formula.analysis.value.ValueAssignmentList;
import de.featjar.formula.analysis.value.ValueClause;
import de.featjar.formula.analysis.value.ValueClauseList;
import de.featjar.formula.analysis.value.ValueSolution;
import de.featjar.formula.analysis.value.ValueSolutionList;
import de.featjar.formula.structure.formula.IFormula;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Maps variable names to indices and vice versa. Used to link a literal index
 * in a {@link ABooleanAssignment} to a
 * {@link de.featjar.formula.structure.term.value.Variable} in a
 * {@link IFormula}.
 *
 * @author Elias Kuiter
 */
public class VariableMap extends RangeMap<String> {
    public VariableMap() {}

    protected VariableMap(IValueRepresentation valueRepresentation) {
        super(valueRepresentation.getVariableNames());
    }

    public VariableMap(VariableMap variableMap) {
        super(variableMap.getObjects());
    }

    /**
     * Creates a variable map from a value representation (e.g., an expression).
     * Indices are numbered by the occurrence of variables in a preorder traversal.
     *
     * @param valueRepresentation the value representation
     */
    public static VariableMap of(IValueRepresentation valueRepresentation) {
        return new VariableMap(valueRepresentation);
    }

    public static VariableMap empty() {
        return new VariableMap();
    }

    public List<String> getVariableNames() {
        return getObjects().stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    public List<String> getVariableNames(IntegerList indices) {
        return indices.stream()
                .filter(i -> isValidIndex(Math.abs(i)))
                .mapToObj(i -> i > 0 ? indexToObject.get(i) : "-" + indexToObject.get(-i))
                .collect(Collectors.toList());
    }

    public List<Integer> getVariableIndices() {
        return stream().map(Pair::getKey).collect(Collectors.toList());
    }

    public int getVariableCount() {
        return getVariableNames().size();
    }

    public String print() {
        return stream()
                .map(pair -> String.format("%d <-> %s", pair.getKey(), pair.getValue()))
                .collect(Collectors.joining(", "));
    }

    @Override
    public String toString() {
        return String.format("VariableMap[%s]", print());
    }

    protected <T extends ABooleanAssignment> Result<T> toBoolean(
            AValueAssignment valueAssignment, Function<List<Integer>, T> constructor) {
        List<Integer> integers = new ArrayList<>();
        List<Problem> problems = new ArrayList<>();
        for (Map.Entry<String, Object> variableValuePair :
                valueAssignment.getAll().entrySet()) {
            if (!(variableValuePair.getValue() instanceof Boolean))
                problems.add(new Problem(
                        "tried to set value " + variableValuePair.getValue() + ", which is not Boolean",
                        Problem.Severity.WARNING));
            else {
                String variable = variableValuePair.getKey();
                Boolean value = (Boolean) variableValuePair.getValue();
                Result<Integer> index = get(variable);
                if (index.isEmpty())
                    problems.add(new Problem(
                            "tried to reference variable " + variable + ", which is not mapped to an index",
                            Problem.Severity.WARNING));
                else {
                    integers.add(value ? index.get() : -index.get());
                }
            }
        }
        return Result.of(constructor.apply(integers), problems);
    }

    public Result<BooleanAssignment> toBoolean(ValueAssignment valueAssignment) {
        return toBoolean(valueAssignment, BooleanAssignment::new);
    }

    public Result<BooleanClause> toBoolean(ValueClause valueClause) {
        return toBoolean(valueClause, BooleanClause::new);
    }

    public Result<BooleanSolution> toBoolean(ValueSolution valueSolution) {
        return toBoolean(valueSolution, BooleanSolution::new);
    }

    protected <T extends ABooleanAssignmentList<U>, U extends ABooleanAssignment> Result<T> toBoolean(
            AValueAssignmentList<?> valueAssignmentList,
            T booleanAssignmentList,
            Function<List<Integer>, U> constructor) {
        List<Problem> problems = new ArrayList<>();
        for (AValueAssignment valueAssignment : valueAssignmentList.getAll()) {
            Result<U> booleanAssignment = toBoolean(valueAssignment, constructor);
            problems.addAll(booleanAssignment.getProblems());
            if (booleanAssignment.isPresent()) booleanAssignmentList.add(booleanAssignment.get());
        }
        return Result.of(booleanAssignmentList, problems);
    }

    public Result<BooleanAssignmentList> toBoolean(ValueAssignmentList valueAssignmentList) {
        return toBoolean(valueAssignmentList, new BooleanAssignmentList(), BooleanAssignment::new);
    }

    public Result<BooleanClauseList> toBoolean(ValueClauseList valueClauseList) {
        return toBoolean(
                valueClauseList, new BooleanClauseList(valueClauseList.getVariableCount()), BooleanClause::new);
    }

    public Result<BooleanSolutionList> toBoolean(ValueSolutionList valueSolutionList) {
        return toBoolean(valueSolutionList, new BooleanSolutionList(), BooleanSolution::new);
    }

    protected <T extends AValueAssignment> Result<T> toValue(
            ABooleanAssignment booleanAssignment, Function<LinkedHashMap<String, Object>, T> constructor) {
        LinkedHashMap<String, Object> variableValuePairs = Maps.empty();
        List<Problem> problems = new ArrayList<>();
        for (int integer : booleanAssignment.get()) {
            int index = Math.abs(integer);
            Result<String> variable = get(index);
            if (variable.isEmpty())
                problems.add(new Problem(
                        "tried to reference index " + index + ", which is not mapped to a variable",
                        Problem.Severity.WARNING));
            else {
                variableValuePairs.put(variable.get(), integer > 0);
            }
        }
        return Result.of(constructor.apply(variableValuePairs), problems);
    }

    public Result<ValueAssignment> toValue(BooleanAssignment booleanAssignment) {
        return toValue(booleanAssignment, ValueAssignment::new);
    }

    public Result<ValueClause> toValue(BooleanClause booleanClause) {
        return toValue(booleanClause, ValueClause::new);
    }

    public Result<ValueSolution> toValue(BooleanSolution booleanSolution) {
        return toValue(booleanSolution, ValueSolution::new);
    }

    protected <T extends AValueAssignmentList<U>, U extends AValueAssignment> Result<T> toValue(
            ABooleanAssignmentList<?> booleanAssignmentList,
            T valueAssignmentList,
            Function<LinkedHashMap<String, Object>, U> constructor) {
        List<Problem> problems = new ArrayList<>();
        for (ABooleanAssignment booleanAssignment : booleanAssignmentList.getAll()) {
            Result<U> valueAssignment = toValue(booleanAssignment, constructor);
            problems.addAll(valueAssignment.getProblems());
            if (valueAssignment.isPresent()) valueAssignmentList.add(valueAssignment.get());
        }
        return Result.of(valueAssignmentList, problems);
    }

    public Result<ValueAssignmentList> toValue(BooleanAssignmentList booleanAssignmentList) {
        return toValue(booleanAssignmentList, new ValueAssignmentList(), ValueAssignment::new);
    }

    public Result<ValueClauseList> toValue(BooleanClauseList booleanClauseList) {
        return toValue(booleanClauseList, new ValueClauseList(booleanClauseList.getVariableCount()), ValueClause::new);
    }

    public Result<ValueSolutionList> toValue(BooleanSolutionList booleanSolutionList) {
        return toValue(booleanSolutionList, new ValueSolutionList(), ValueSolution::new);
    }

    protected static <T extends AValueAssignment> Result<T> toAnonymousValue(
            ABooleanAssignment booleanAssignment, Function<LinkedHashMap<String, Object>, T> constructor) {
        LinkedHashMap<String, Object> variableValuePairs = Maps.empty();
        List<Problem> problems = new ArrayList<>();
        for (int integer : booleanAssignment.get()) {
            int index = Math.abs(integer);
            variableValuePairs.put(String.valueOf(index), integer > 0);
        }
        return Result.of(constructor.apply(variableValuePairs), problems);
    }

    public static Result<ValueAssignment> toAnonymousValue(BooleanAssignment booleanAssignment) {
        return toAnonymousValue(booleanAssignment, ValueAssignment::new);
    }

    public static Result<ValueClause> toAnonymousValue(BooleanClause booleanClause) {
        return toAnonymousValue(booleanClause, ValueClause::new);
    }

    public static Result<ValueSolution> toAnonymousValue(BooleanSolution booleanSolution) {
        return toAnonymousValue(booleanSolution, ValueSolution::new);
    }

    protected static <T extends AValueAssignmentList<U>, U extends AValueAssignment> Result<T> toAnonymousValue(
            ABooleanAssignmentList<?> booleanAssignmentList,
            T valueAssignmentList,
            Function<LinkedHashMap<String, Object>, U> constructor) {
        List<Problem> problems = new ArrayList<>();
        for (ABooleanAssignment booleanAssignment : booleanAssignmentList.getAll()) {
            Result<U> valueAssignment = toAnonymousValue(booleanAssignment, constructor);
            problems.addAll(valueAssignment.getProblems());
            if (valueAssignment.isPresent()) valueAssignmentList.add(valueAssignment.get());
        }
        return Result.of(valueAssignmentList, problems);
    }

    public static Result<ValueAssignmentList> toAnonymousValue(BooleanAssignmentList booleanAssignmentList) {
        return toAnonymousValue(booleanAssignmentList, new ValueAssignmentList(), ValueAssignment::new);
    }

    public static Result<ValueClauseList> toAnonymousValue(BooleanClauseList booleanClauseList) {
        return toAnonymousValue(
                booleanClauseList, new ValueClauseList(booleanClauseList.getVariableCount()), ValueClause::new);
    }

    public static Result<ValueSolutionList> toAnonymousValue(BooleanSolutionList booleanSolutionList) {
        return toAnonymousValue(booleanSolutionList, new ValueSolutionList(), ValueSolution::new);
    }
}
