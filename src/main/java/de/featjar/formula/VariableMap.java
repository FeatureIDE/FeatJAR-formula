/*
 * Copyright (C) 2025 FeatJAR-Development-Team
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
import de.featjar.base.data.Problem;
import de.featjar.base.data.RangeMap;
import de.featjar.base.data.Result;
import de.featjar.formula.assignment.Assignment;
import de.featjar.formula.assignment.AssignmentList;
import de.featjar.formula.assignment.BooleanAssignment;
import de.featjar.formula.assignment.BooleanAssignmentList;
import de.featjar.formula.assignment.BooleanClause;
import de.featjar.formula.assignment.BooleanSolution;
import de.featjar.formula.assignment.ValueAssignment;
import de.featjar.formula.assignment.ValueAssignmentList;
import de.featjar.formula.assignment.ValueClause;
import de.featjar.formula.assignment.ValueSolution;
import de.featjar.formula.structure.IExpression;
import de.featjar.formula.structure.IFormula;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Maps variable names to indices and vice versa. Used to link a literal index
 * in a {@link BooleanAssignment} to a
 * {@link de.featjar.formula.structure.term.value.Variable} in a
 * {@link IFormula}.
 *
 * @author Elias Kuiter
 */
public class VariableMap extends RangeMap<String> {

    /**
     * Constructs a new empty variable map.
     */
    public VariableMap() {}

    /**
     * Constructs a new variable map with the given variable names.
     * Indices are mapped in the order of the given collection.
     * @param variableNames the variable names
     */
    public VariableMap(Collection<String> variableNames) {
        super(variableNames);
    }

    /**
     * Constructs a new variable map with all variables occurring in the given expression.
     * Indices are mapped in the order the given by {@link IExpression#getVariableNames()}.
     * @param expression the expression
     */
    public VariableMap(IExpression expression) {
        super(expression.getVariableNames());
    }

    /**
     * Creates a new variable map from the given list of maps by merging them.
     * @param variableMaps the list of variable maps
     */
    public VariableMap(List<VariableMap> variableMaps) {
        super(variableMaps);
    }

    /**
     * Creates a new variable map from the two maps by merging them.
     * @param firstVariableMap the first variable map
     * @param secondVariableMap the second variable map
     */
    public VariableMap(VariableMap firstVariableMap, VariableMap secondVariableMap) {
        super(List.of(firstVariableMap, secondVariableMap));
    }

    /**
     * Copy constructor.
     * @param variableMap the other variable map
     */
    public VariableMap(VariableMap variableMap) {
        super(variableMap.getObjects(true));
    }

    /**
     * {@return an unmodifiable list of all names in this maps}
     * The names are in the same order as their index, but the list does not contain any gaps that may be present in the mapped indices.
     */
    public List<String> getVariableNames() {
        return getObjects(false);
    }

    /**
     * {@return an unmodifiable list of the names mapped to the given list of indices}
     * The names are in the same order as the given indices, but the list does not contain a name for any invalid index.
     * @param indices the list of indices
     */
    public List<String> getVariableNames(IntegerList indices) {
        return indices.stream()
                .filter(i -> isValidIndex(Math.abs(i)))
                .mapToObj(i -> indexToObject.get(i))
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * {@return an unmodifiable list of all indices in this maps}
     */
    public List<Integer> getVariableIndices() {
        return entryStream().map(e -> e.getValue()).collect(Collectors.toUnmodifiableList());
    }

    public List<Integer> getVariableIndices(List<String> names) {
        return stream(names).collect(Collectors.toList());
    }

    public BooleanAssignment getVariables(Collection<String> names) {
        return new BooleanAssignment(names.stream().mapToInt(objectToIndex::get).toArray());
    }

    public BooleanAssignment getVariables() {
        return new BooleanAssignment(entryStream().mapToInt(e -> e.getValue()).toArray());
    }

    public int getVariableCount() {
        return objectToIndex.size();
    }

    /**
     * {@return a human readable mapping}
     */
    public String print() {
        return stream()
                .map(pair -> String.format("%d <-> %s", pair.getKey(), pair.getValue()))
                .collect(Collectors.joining(", "));
    }

    @Override
    public String toString() {
        return String.format("VariableMap[%s]", print());
    }

    protected static <T extends BooleanAssignment> Result<T> toBoolean(
            ValueAssignment valueAssignment, Function<List<Integer>, T> constructor) {
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

    protected static <T extends BooleanAssignmentList, U extends BooleanAssignment> Result<T> toBoolean(
            ValueAssignmentList valueAssignmentList, T booleanAssignmentList, Function<List<Integer>, U> constructor) {
        List<Problem> problems = new ArrayList<>();
        for (ValueAssignment valueAssignment : valueAssignmentList) {
            Result<U> booleanAssignment = toBoolean(valueAssignment, constructor);
            problems.addAll(booleanAssignment.getProblems());
            if (booleanAssignment.isPresent()) booleanAssignmentList.add(booleanAssignment.get());
        }
        return Result.of(booleanAssignmentList, problems);
    }

    public static Result<BooleanAssignmentList> toBoolean(ValueAssignmentList valueAssignmentList) {
        return toBoolean(
                valueAssignmentList,
                new BooleanAssignmentList(valueAssignmentList.getVariableMap()),
                BooleanAssignment::new);
    }

    protected static <T extends ValueAssignment> T toValue(
            BooleanAssignment booleanAssignment, Function<LinkedHashMap<Integer, Object>, T> constructor) {
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

    protected static <T extends ValueAssignmentList, U extends ValueAssignment> T toValue(
            BooleanAssignmentList booleanAssignmentList,
            T valueAssignmentList,
            Function<LinkedHashMap<Integer, Object>, U> constructor) {
        for (BooleanAssignment booleanAssignment : booleanAssignmentList) {
            U valueAssignment = toValue(booleanAssignment, constructor);
            valueAssignmentList.add(valueAssignment);
        }
        return valueAssignmentList;
    }

    public static ValueAssignmentList toValue(BooleanAssignmentList booleanAssignmentList) {
        return toValue(
                booleanAssignmentList,
                new ValueAssignmentList(booleanAssignmentList.getVariableMap()),
                ValueAssignment::new);
    }

    public Result<Assignment> toAssignment(BooleanAssignment booleanAssignment) {
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

    public Result<Assignment> toAssignment(ValueAssignment valueAssignment) {
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

    public Result<AssignmentList> toAssignment(BooleanAssignmentList booleanAssignmentList) {
        AssignmentList assignmentList = new AssignmentList(booleanAssignmentList.size());
        List<Problem> problems = new ArrayList<>();

        for (BooleanAssignment booleanAssignment : booleanAssignmentList) {
            Result<Assignment> assignment = toAssignment(booleanAssignment);
            problems.addAll(assignment.getProblems());
            assignmentList.add(assignment.get());
        }
        return Result.of(assignmentList, problems);
    }

    public Result<AssignmentList> toAssignment(ValueAssignmentList valueAssignmentList) {
        AssignmentList assignmentList = new AssignmentList(valueAssignmentList.size());
        List<Problem> problems = new ArrayList<>();

        for (ValueAssignment valueAssignment : valueAssignmentList) {
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

    /**
     * Adapts each literal from {@code oldLiterals} from its index in this variable map to its index in {@code newVariableMap}.
     * The adapted literals are stored in {@code newLiterals}.
     * Caller must ensure that {@code newLiterals} is at least as large as {@code oldLiterals}.
     *
     * @param oldLiterals the literals to adapt
     * @param newLiterals the space for the adapted literals
     * @param newVariableMap the variable map to adapt to
     * @param integrateOldVariables if {@code true} variables that do not occur in {@code newVariableMap} are added to it, otherwise an exception is thrown in this case.
     */
    public void adapt(int[] oldLiterals, int[] newLiterals, VariableMap newVariableMap, boolean integrateOldVariables) {
        for (int i = 0; i < oldLiterals.length; i++) {
            newLiterals[i] = adapt(oldLiterals[i], newVariableMap, integrateOldVariables);
        }
    }

    /**
     * Adapt {@code oldLiteral} from its index in this variable map to its index in {@code newVariableMap}.
     *
     * @param oldLiteral the literal to adapt
     * @param newVariableMap the variable map to adapt to
     * @param integrateOldVariables if {@code true} a variable that does not occur in {@code newVariableMap} is added to it, otherwise an exception is thrown in this case.
     * @return the adapted literal
     */
    public int adapt(int oldLiteral, VariableMap newVariableMap, boolean integrateOldVariables) {
        if (oldLiteral == 0) {
            return 0;
        } else {
            final Result<String> name = get(Math.abs(oldLiteral));
            if (name.isPresent()) {
                String variableName = name.get();
                final int newLiteral;
                Result<Integer> index = newVariableMap.get(variableName);
                if (index.isEmpty()) {
                    if (integrateOldVariables) {
                        newLiteral = newVariableMap.add(variableName);
                    } else {
                        throw new IllegalArgumentException("No variable named " + variableName);
                    }
                } else {
                    newLiteral = index.get();
                }
                return oldLiteral < 0 ? -newLiteral : newLiteral;
            } else {
                throw new IllegalArgumentException("No variable with index " + oldLiteral);
            }
        }
    }
}
