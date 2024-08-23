/*
 * Copyright (C) 2024 FeatJAR-Development-Team
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
 * See <https://github.com/FeatureIDE/FeatJAR-formula> for further information.
 */
package de.featjar.formula;

import de.featjar.base.data.IntegerList;
import de.featjar.base.data.Maps;
import de.featjar.base.data.Pair;
import de.featjar.base.data.Problem;
import de.featjar.base.data.RangeMap;
import de.featjar.base.data.Result;
import de.featjar.formula.assignment.ABooleanAssignment;
import de.featjar.formula.assignment.ABooleanAssignmentList;
import de.featjar.formula.assignment.AValueAssignment;
import de.featjar.formula.assignment.AValueAssignmentList;
import de.featjar.formula.assignment.Assignment;
import de.featjar.formula.assignment.AssignmentList;
import de.featjar.formula.assignment.BooleanAssignment;
import de.featjar.formula.assignment.BooleanAssignmentList;
import de.featjar.formula.assignment.BooleanClause;
import de.featjar.formula.assignment.BooleanClauseList;
import de.featjar.formula.assignment.BooleanSolution;
import de.featjar.formula.assignment.BooleanSolutionList;
import de.featjar.formula.assignment.ValueAssignment;
import de.featjar.formula.assignment.ValueAssignmentList;
import de.featjar.formula.assignment.ValueClause;
import de.featjar.formula.assignment.ValueClauseList;
import de.featjar.formula.assignment.ValueSolution;
import de.featjar.formula.assignment.ValueSolutionList;
import de.featjar.formula.structure.IExpression;
import de.featjar.formula.structure.IFormula;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

    protected VariableMap(Collection<String> variableNames) {
        super(variableNames);
    }

    protected VariableMap(IExpression valueRepresentation) {
        super(valueRepresentation.getVariableNames());
    }

    protected VariableMap(VariableMap variableMap) {
        super(variableMap.getObjects());
    }

    /**
     * Creates a variable map from a value representation (e.g., an expression).
     * Indices are numbered by the occurrence of variables in a preorder traversal.
     *
     * @param valueRepresentation the value representation
     */
    public static VariableMap of(IExpression valueRepresentation) {
        return new VariableMap(valueRepresentation);
    }

    /**
     * Creates a variable map from a list of variable name.
     *
     * @param variableNames the list of variable names
     */
    public static VariableMap of(Collection<String> variableNames) {
        return new VariableMap(variableNames);
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

    public List<Integer> getVariableIndices(List<String> names) {
        return stream(names).collect(Collectors.toList());
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

    protected static <T extends ABooleanAssignment> Result<T> toBoolean(
            AValueAssignment valueAssignment, Function<List<Integer>, T> constructor) {
        List<Integer> integers = new ArrayList<>();
        List<Problem> problems = new ArrayList<>();
        for (Map.Entry<Integer, Object> variableValuePair :
                valueAssignment.getAll().entrySet()) {
            if (!(variableValuePair.getValue() instanceof Boolean))
                problems.add(new Problem(
                        "tried to set value " + variableValuePair.getValue() + ", which is not Boolean",
                        Problem.Severity.WARNING));
            else {
                Integer index = variableValuePair.getKey();
                Boolean value = (Boolean) variableValuePair.getValue();
                integers.add(value ? index : -index);
            }
        }
        return Result.of(constructor.apply(integers), problems);
    }

    public static Result<BooleanAssignment> toBoolean(ValueAssignment valueAssignment) {
        return toBoolean(valueAssignment, BooleanAssignment::new);
    }

    public static Result<BooleanClause> toBoolean(ValueClause valueClause) {
        return toBoolean(valueClause, BooleanClause::new);
    }

    public static Result<BooleanSolution> toBoolean(ValueSolution valueSolution) {
        return toBoolean(valueSolution, BooleanSolution::new);
    }

    protected static <T extends ABooleanAssignmentList<U>, U extends ABooleanAssignment> Result<T> toBoolean(
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

    public static Result<BooleanAssignmentList> toBoolean(ValueAssignmentList valueAssignmentList) {
        return toBoolean(valueAssignmentList, new BooleanAssignmentList(), BooleanAssignment::new);
    }

    public static Result<BooleanClauseList> toBoolean(ValueClauseList valueClauseList) {
        return toBoolean(
                valueClauseList, new BooleanClauseList(valueClauseList.getVariableCount()), BooleanClause::new);
    }

    public static Result<BooleanSolutionList> toBoolean(ValueSolutionList valueSolutionList) {
        return toBoolean(valueSolutionList, new BooleanSolutionList(), BooleanSolution::new);
    }

    protected static <T extends AValueAssignment> T toValue(
            ABooleanAssignment booleanAssignment, Function<LinkedHashMap<Integer, Object>, T> constructor) {
        LinkedHashMap<Integer, Object> variableValuePairs = Maps.empty();
        for (int integer : booleanAssignment.get()) {
            variableValuePairs.put(Math.abs(integer), integer > 0);
        }
        return constructor.apply(variableValuePairs);
    }

    public static ValueAssignment toValue(BooleanAssignment booleanAssignment) {
        return toValue(booleanAssignment, ValueAssignment::new);
    }

    public static ValueClause toValue(BooleanClause booleanClause) {
        return toValue(booleanClause, ValueClause::new);
    }

    public static ValueSolution toValue(BooleanSolution booleanSolution) {
        return toValue(booleanSolution, ValueSolution::new);
    }

    protected static <T extends AValueAssignmentList<U>, U extends AValueAssignment> T toValue(
            ABooleanAssignmentList<?> booleanAssignmentList,
            T valueAssignmentList,
            Function<LinkedHashMap<Integer, Object>, U> constructor) {
        for (ABooleanAssignment booleanAssignment : booleanAssignmentList.getAll()) {
            U valueAssignment = toValue(booleanAssignment, constructor);
            valueAssignmentList.add(valueAssignment);
        }
        return valueAssignmentList;
    }

    public static ValueAssignmentList toValue(BooleanAssignmentList booleanAssignmentList) {
        return toValue(booleanAssignmentList, new ValueAssignmentList(), ValueAssignment::new);
    }

    public static ValueClauseList toValue(BooleanClauseList booleanClauseList) {
        return toValue(booleanClauseList, new ValueClauseList(booleanClauseList.getVariableCount()), ValueClause::new);
    }

    public static ValueSolutionList toValue(BooleanSolutionList booleanSolutionList) {
        return toValue(booleanSolutionList, new ValueSolutionList(), ValueSolution::new);
    }

    public Result<Assignment> toAssignment(ABooleanAssignment booleanAssignment) {
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
        return Result.of(new Assignment(variableValuePairs), problems);
    }

    public Result<Assignment> toAssignment(AValueAssignment valueAssignment) {
        LinkedHashMap<String, Object> variableValuePairs = Maps.empty();
        List<Problem> problems = new ArrayList<>();
        for (Entry<Integer, Object> entry : valueAssignment.getAll().entrySet()) {
            int index = entry.getKey();
            Result<String> variable = get(index);
            if (variable.isEmpty())
                problems.add(new Problem(
                        "tried to reference index " + index + ", which is not mapped to a variable",
                        Problem.Severity.WARNING));
            else {
                variableValuePairs.put(variable.get(), entry.getValue());
            }
        }
        return Result.of(new Assignment(variableValuePairs), problems);
    }

    public Result<AssignmentList> toAssignment(ABooleanAssignmentList<?> booleanAssignmentList) {
        AssignmentList assignmentList = new AssignmentList(booleanAssignmentList.size());
        List<Problem> problems = new ArrayList<>();

        for (ABooleanAssignment booleanAssignment : booleanAssignmentList.getAll()) {
            Result<Assignment> assignment = toAssignment(booleanAssignment);
            problems.addAll(assignment.getProblems());
            assignmentList.add(assignment.get());
        }
        return Result.of(assignmentList, problems);
    }

    public Result<AssignmentList> toAssignment(AValueAssignmentList<?> valueAssignmentList) {
        AssignmentList assignmentList = new AssignmentList(valueAssignmentList.size());
        List<Problem> problems = new ArrayList<>();

        for (AValueAssignment valueAssignment : valueAssignmentList.getAll()) {
            Result<Assignment> assignment = toAssignment(valueAssignment);
            problems.addAll(assignment.getProblems());
            assignmentList.add(assignment.get());
        }
        return Result.of(assignmentList, problems);
    }

    @Override
    public VariableMap clone() {
        return new VariableMap(this);
    }
}
