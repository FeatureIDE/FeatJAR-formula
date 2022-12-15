package de.featjar.formula.analysis.bool;

import de.featjar.base.data.Pair;
import de.featjar.base.data.Problem;
import de.featjar.base.data.RangeMap;
import de.featjar.base.data.Result;
import de.featjar.formula.analysis.value.*;
import de.featjar.formula.structure.Expression;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Maps variable names to indices and vice versa.
 * Used to link a literal index in a {@link BooleanAssignment} to a {@link de.featjar.formula.structure.term.value.Variable}
 * in a {@link de.featjar.formula.structure.formula.Formula}.
 *
 * @author Elias Kuiter
 */
public class VariableMap extends RangeMap<String> {
    public VariableMap() {
    }

    protected VariableMap(Expression expression) {
        super(expression.getVariableNames());
    }

    public VariableMap(VariableMap variableMap) {
        super(variableMap.getObjects());
    }

    /**
     * Creates a variable map from an expression.
     * Indices are numbered by the occurrence of variables in a preorder traversal.
     *
     * @param expression the expression
     */
    public static VariableMap of(Expression expression) {
       return new VariableMap(expression);
    }

    public static VariableMap empty() {
        return new VariableMap();
    }

    public List<String> getVariableNames() {
        return getObjects().stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    public List<Integer> getVariableIndices() {
        return stream().map(Pair::getKey).collect(Collectors.toList());
    }

    public int getVariableCount() {
        return getVariableNames().size();
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public VariableMap clone() {
        return new VariableMap(this);
    }

    protected <T extends BooleanAssignment> Result<T> toBoolean(ValueAssignment valueAssignment, Function<List<Integer>, T> constructor) {
        List<Integer> integers = new ArrayList<>();
        List<Problem> problems = new ArrayList<>();
        for (Map.Entry<String, Object> variableValuePair : valueAssignment.getAll().entrySet()) {
            if (!(variableValuePair.getValue() instanceof Boolean))
                problems.add(new Problem("tried to set value " + variableValuePair.getValue() + ", which is not Boolean", Problem.Severity.WARNING));
            else {
                String variable = variableValuePair.getKey();
                Boolean value = (Boolean) variableValuePair.getValue();
                Optional<Integer> index = get(variable);
                if (index.isEmpty())
                    problems.add(new Problem("tried to reference variable " + variable + ", which is not mapped to an index", Problem.Severity.WARNING));
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

    protected <T extends BooleanAssignmentList<?, U>, U extends BooleanAssignment> Result<T> toBoolean(
            ValueAssignmentList<?, ?> valueAssignmentList,
            Supplier<T> listConstructor, Function<List<Integer>, U> constructor) {
        T booleanAssignmentList = listConstructor.get();
        booleanAssignmentList.setVariableMap(this);
        List<Problem> problems = new ArrayList<>();
        for (ValueAssignment valueAssignment : valueAssignmentList.getAll()) {
            Result<U> booleanAssignment = toBoolean(valueAssignment, constructor);
            problems.addAll(booleanAssignment.getProblems());
            if (booleanAssignment.isPresent())
                booleanAssignmentList.add(booleanAssignment.get());
        }
        return Result.of(booleanAssignmentList, problems);
    }

    public Result<BooleanClauseList> toBoolean(ValueClauseList valueClauseList) {
        return toBoolean(valueClauseList, BooleanClauseList::new, BooleanClause::new);
    }

    public Result<BooleanSolutionList> toBoolean(ValueSolutionList valueSolutionList) {
        return toBoolean(valueSolutionList, BooleanSolutionList::new, BooleanSolution::new);
    }

    protected <T extends ValueAssignment> Result<T> toValue(BooleanAssignment booleanAssignment, Function<LinkedHashMap<String, Object>, T> constructor) {
        LinkedHashMap<String, Object> variableValuePairs = new LinkedHashMap<>();
        List<Problem> problems = new ArrayList<>();
        for (int integer : booleanAssignment.getIntegers()) {
            int index = Math.abs(integer);
            Optional<String> variable = get(index);
            if (variable.isEmpty())
                problems.add(new Problem("tried to reference index " + index + ", which is not mapped to a variable", Problem.Severity.WARNING));
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

    protected <T extends ValueAssignmentList<?, U>, U extends ValueAssignment> Result<T> toValue(
            BooleanAssignmentList<?, ?> booleanAssignmentList,
            Supplier<T> listConstructor, Function<LinkedHashMap<String, Object>, U> constructor) {
        T valueAssignmentList = listConstructor.get();
        List<Problem> problems = new ArrayList<>();
        for (BooleanAssignment booleanAssignment : booleanAssignmentList.getAll()) {
            Result<U> valueAssignment = toValue(booleanAssignment, constructor);
            problems.addAll(valueAssignment.getProblems());
            if (valueAssignment.isPresent())
                valueAssignmentList.add(valueAssignment.get());
        }
        return Result.of(valueAssignmentList, problems);
    }

    public Result<ValueClauseList> toValue(BooleanClauseList booleanClauseList) {
        return toValue(booleanClauseList, ValueClauseList::new, ValueClause::new);
    }

    public Result<ValueSolutionList> toValue(BooleanSolutionList booleanSolutionList) {
        return toValue(booleanSolutionList, ValueSolutionList::new, ValueSolution::new);
    }

    protected static <T extends ValueAssignment> Result<T> toAnonymousValue(BooleanAssignment booleanAssignment, Function<LinkedHashMap<String, Object>, T> constructor) {
        LinkedHashMap<String, Object> variableValuePairs = new LinkedHashMap<>();
        List<Problem> problems = new ArrayList<>();
        for (int integer : booleanAssignment.getIntegers()) {
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

    protected static <T extends ValueAssignmentList<?, U>, U extends ValueAssignment> Result<T> toAnonymousValue(
            BooleanAssignmentList<?, ?> booleanAssignmentList,
            Supplier<T> listConstructor, Function<LinkedHashMap<String, Object>, U> constructor) {
        T valueAssignmentList = listConstructor.get();
        List<Problem> problems = new ArrayList<>();
        for (BooleanAssignment booleanAssignment : booleanAssignmentList.getAll()) {
            Result<U> valueAssignment = toAnonymousValue(booleanAssignment, constructor);
            problems.addAll(valueAssignment.getProblems());
            if (valueAssignment.isPresent())
                valueAssignmentList.add(valueAssignment.get());
        }
        return Result.of(valueAssignmentList, problems);
    }

    public static Result<ValueClauseList> toAnonymousValue(BooleanClauseList booleanClauseList) {
        return toAnonymousValue(booleanClauseList, ValueClauseList::new, ValueClause::new);
    }

    public static Result<ValueSolutionList> toAnonymousValue(BooleanSolutionList booleanSolutionList) {
        return toAnonymousValue(booleanSolutionList, ValueSolutionList::new, ValueSolution::new);
    }
}
