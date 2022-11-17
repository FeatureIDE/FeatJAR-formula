package de.featjar.formula.analysis.bool;

import de.featjar.base.data.IntegerList;
import de.featjar.base.data.Problem;
import de.featjar.base.data.Result;
import de.featjar.formula.analysis.Assignment;

import java.util.*;
import java.util.stream.IntStream;

/**
 * Assigns Boolean values integer-identified {@link de.featjar.formula.structure.term.value.Variable variables}.
 * Can be used to represent a set of literals for use in a SAT {@link de.featjar.formula.analysis.Solver}.
 * Implemented as an unordered list of indices to variables in some unspecified {@link VariableMap}.
 * An index can be negative, indicating a negated occurrence of its variable,
 * or 0, indicating no occurrence, and it may occur multiple times.
 * For specific use cases, consider using {@link BooleanClause} (a disjunction
 * of literals) or {@link BooleanSolution} (a conjunction of literals).
 * To link a {@link BooleanAssignment} to a specific {@link VariableMap}, consider using a
 * {@link BooleanClauseList}.
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public class BooleanAssignment extends IntegerList<BooleanAssignment> implements Assignment<Integer> {
    public BooleanAssignment(int... integers) {
        super(integers);
    }

    public BooleanAssignment(Collection<Integer> integers) {
        super(integers);
    }

    public BooleanAssignment(BooleanAssignment booleanAssignment) {
        super(booleanAssignment);
    }

    @Override
    protected BooleanAssignment newIntegerList(int[] integers) {
        return new BooleanAssignment(integers);
    }

    public static BooleanAssignment merge(Collection<BooleanAssignment> collection) {
        return new BooleanAssignment(collection.stream()
                .flatMapToInt(l -> Arrays.stream(l.getIntegers()))
                .distinct()
                .toArray());
    }

    public int countConflicts(int[] integers) {
        return (int) Arrays.stream(integers)
                .filter(integer -> indexOf(-integer) >= 0)
                .count();
    }

    public int countConflicts(BooleanAssignment booleanAssignment) {
        return countConflicts(booleanAssignment.getIntegers());
    }

    public boolean conflictsWith(BooleanAssignment booleanAssignment) {
        return countConflicts(booleanAssignment.getIntegers()) > 0;
    }

    public BooleanAssignment negate() {
        final int[] negated = new int[integers.length];
        for (int i = 0; i < negated.length; i++) {
            negated[i] = -integers[i];
        }
        return newIntegerList(negated);
    }

    public Optional<BooleanAssignment> clean() { // todo: must this be an optional?
        final LinkedHashSet<Integer> newIntegerSet = new LinkedHashSet<>();

        for (final int integer : integers) {
            if (newIntegerSet.contains(-integer)) {
                return Optional.empty();
            } else {
                newIntegerSet.add(integer);
            }
        }

        final int[] uniqueVarArray;
        if (newIntegerSet.size() == integers.length) {
            uniqueVarArray = Arrays.copyOf(integers, integers.length);
        } else {
            uniqueVarArray = new int[newIntegerSet.size()];
            int i = 0;
            for (final int lit : newIntegerSet) {
                uniqueVarArray[i++] = lit;
            }
        }
        return Optional.of(newIntegerList(uniqueVarArray));
    }

    public Result<BooleanAssignment> adapt(VariableMap oldVariableMap, VariableMap newVariableMap) {
        final int[] oldIntegers = integers;
        final int[] newIntegers = new int[oldIntegers.length];
        for (int i = 0; i < oldIntegers.length; i++) {
            final int l = oldIntegers[i];
            final Optional<String> name = oldVariableMap.get(Math.abs(l));
            if (name.isPresent()) {
                final Optional<Integer> index = newVariableMap.get(name.get());
                if (index.isPresent()) {
                    newIntegers[i] = l < 0 ? -index.get() : index.get();
                } else {
                    return Result.empty(new Problem("No variable named " + name.get(), Problem.Severity.ERROR));
                }
            } else {
                return Result.empty(new Problem("No variable with index " + l, Problem.Severity.ERROR));
            }
        }
        return Result.of(newIntegerList(newIntegers));
    }

    public boolean containsAnyVariable(int... integers) {
        return Arrays.stream(integers)
                .anyMatch(integer -> indexOfVariable(integer) >= 0);
    }

    public boolean containsAllVariables(int... integers) {
        return Arrays.stream(integers)
                .noneMatch(integer -> indexOfVariable(integer) >= 0);
    }

    public int indexOfVariable(int variableInteger) {
        return IntStream.range(0, integers.length)
                .filter(i -> Math.abs(integers[i]) == variableInteger)
                .findFirst()
                .orElse(-1);
    }

    protected int countVariables(int[] integers, final boolean[] removeMarker) {
        int count = 0;
        for (int integer : integers) {
            final int index = indexOfVariable(integer);
            if (index >= 0) {
                count++;
                if (removeMarker != null) {
                    removeMarker[index] = true;
                }
            }
        }
        return count;
    }

    public BooleanAssignment removeAllVariables(int... integers) {
        final boolean[] removeMarker = new boolean[this.integers.length];
        final int count = countVariables(integers, removeMarker);

        final int[] newIntegers = new int[this.integers.length - count];
        int j = 0;
        for (int i = 0; i < this.integers.length; i++) {
            if (!removeMarker[i]) {
                newIntegers[j++] = this.integers[i];
            }
        }
        return newIntegerList(newIntegers);
    }

    public BooleanAssignment removeAllVariables(BooleanAssignment booleanAssignment) {
        return removeAllVariables(booleanAssignment.integers);
    }

    public BooleanAssignment retainAllVariables(BooleanAssignment booleanAssignment) {
        return retainAllVariables(booleanAssignment.getIntegers());
    }

    public BooleanAssignment retainAllVariables(int... integers) {
        final boolean[] removeMarker = new boolean[this.integers.length];
        final int count = countVariables(integers, removeMarker);

        final int[] newIntegers = new int[count];
        int j = 0;
        for (int i = 0; i < this.integers.length; i++) {
            if (removeMarker[i]) {
                newIntegers[j++] = this.integers[i];
            }
        }
        return new BooleanAssignment(newIntegers);
    }

    public BooleanAssignment toAssignment() {
        return new BooleanAssignment(integers);
    }

    public BooleanClause toClause() {
        return new BooleanClause(integers);
    }

    public BooleanSolution toSolution() {
        return new BooleanSolution(integers);
    }

    @Override
    public Map<Integer, Object> getAll() {
        Map<Integer, Object> map = new HashMap<>();
        for (int integer : integers) {
            if (integer > 0)
                map.put(integer, true);
            else if (integer < 0)
                map.put(-integer, false);
        }
        return map;
    }

    @Override
    public int size() {
        return integers.length;
    }

    @Override
    public boolean isEmpty() {
        return integers.length == 0;
    }

    @Override
    public Optional<Object> getValue(Integer variable) {
        int index = indexOfVariable(variable);
        if (index < 0)
            return Optional.empty();
        int value = get(index);
        return value == 0 ? Optional.empty() : Optional.of(value > 0);
    }

//    @Override
//    public void set(Integer variable, Object value) {
//        int index = indexOfVariable(variable);
//        if (index < 0)
//            integers =
//    }
//
//    @Override
//    public void remove(Integer variable) {
//
//    }
//
//    @Override
//    public void clear() {
//
//    }

    //    /**
//     * Sets the value at position i of solution1 to 0 if the value of solution2 at
//     * position {@code i} is different.
//     *
//     * @param solution1 First solution.
//     * @param solution2 Second solution.
//     */
//    public static void resetConflicts(int[] solution1, int[] solution2) {
//        for (int i = 0; i < solution1.length; i++) {
//            final int x = solution1[i];
//            final int y = solution2[i];
//            if (x != y) {
//                solution1[i] = 0;
//            }
//        }
//    }

//    public static LiteralIndexList getVariables(CNF cnf) {
//        return getVariables(cnf.getVariableMap());
//    }
//
//    public static LiteralIndexList getVariables(VariableMap variableMap) {
//        return new LiteralIndexList(constructVariableStream(variableMap).toArray());
//    }
//
//    public static LiteralIndexList getVariables(VariableMap variableMap, Collection<String> variableNames) {
//        return new LiteralIndexList(
//                constructVariableStream(variableMap, variableNames).toArray());
//    }
//
//    public static LiteralIndexList getLiterals(CNF cnf) {
//        return getLiterals(cnf.getVariableMap());
//    }
//
//    public static LiteralIndexList getLiterals(VariableMap variables) {
//        return new LiteralIndexList(constructVariableStream(variables)
//                .flatMap(n -> IntStream.of(-n, n))
//                .toArray());
//    }
//
//    public static LiteralIndexList getLiterals(VariableMap variableMap, Collection<String> variableNames) {
//        return new LiteralIndexList(constructVariableStream(variableMap, variableNames)
//                .flatMap(n -> IntStream.of(-n, n))
//                .toArray());
//    }
//
//    private static IntStream constructVariableStream(VariableMap variables) {
//        return variables.getValidIndexRange().stream().get();
//    }
//
//    private static IntStream constructVariableStream(VariableMap variableMap, Collection<String> variableNames) {
//        return variableNames.stream()
//                .map(variableMap::get)
//                .flatMap(Optional::stream)
//                .mapToInt(Integer::intValue)
//                .distinct();
//    }
}
