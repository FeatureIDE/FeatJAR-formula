package de.featjar.formula.analysis.sat;

import de.featjar.base.data.IntegerList;
import de.featjar.base.data.Problem;
import de.featjar.base.data.Result;
import de.featjar.formula.analysis.sat.clause.SATClause;
import de.featjar.formula.analysis.sat.solution.SATSolution;
import de.featjar.formula.analysis.solver.Assignment;

import java.util.*;
import java.util.stream.IntStream;

/**
 * An unordered list of literals that are stored as indices to a variable in some unspecified {@link VariableMap}.
 * A literal can be negative, indicating a negated occurrence of its variable,
 * or 0, indicating no occurrence, and it may occur multiple times.
 * This class can be used to represent a set of literals for use in a
 * {@link de.featjar.formula.analysis.solver.SATSolver}.
 * For specific use cases, consider using {@link SATClause} (a disjunction
 * of literals) or {@link SATSolution} (a conjunction of literals).
 * To link a {@link LiteralList} to a specific {@link VariableMap}, consider using a
 * {@link de.featjar.formula.analysis.sat.clause.CNF}.
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public class LiteralList extends IntegerList<LiteralList> implements Assignment<Integer> {
    public LiteralList(int... integers) {
        super(integers);
    }

    public LiteralList(Collection<Integer> integers) {
        super(integers);
    }

    public LiteralList(LiteralList literalList) {
        super(literalList);
    }

    @Override
    protected LiteralList newSortedIntegerList(int[] integers) {
        return new LiteralList(integers);
    }

    public static LiteralList merge(Collection<LiteralList> collection) {
        return new LiteralList(collection.stream()
                .flatMapToInt(l -> Arrays.stream(l.getIntegers()))
                .distinct()
                .toArray());
    }

    public int countConflicts(int[] integers) {
        return (int) Arrays.stream(integers)
                .filter(integer -> indexOf(-integer) >= 0)
                .count();
    }

    public int countConflicts(LiteralList literalList) {
        return countConflicts(literalList.getIntegers());
    }

    public boolean conflictsWith(LiteralList literalList) {
        return countConflicts(literalList.getIntegers()) > 0;
    }

    public LiteralList negate() {
        final int[] negLiterals = new int[integers.length];
        for (int i = 0; i < negLiterals.length; i++) {
            negLiterals[i] = -integers[i];
        }
        return newSortedIntegerList(negLiterals);
    }

    public Optional<LiteralList> clean() { // todo: must this be an optional?
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
        return Optional.of(newSortedIntegerList(uniqueVarArray));
    }

    public Result<LiteralList> adapt(VariableMap oldVariableMap, VariableMap newVariableMap) {
        final int[] oldLiterals = integers;
        final int[] newLiterals = new int[oldLiterals.length];
        for (int i = 0; i < oldLiterals.length; i++) {
            final int l = oldLiterals[i];
            final Optional<String> name = oldVariableMap.get(Math.abs(l));
            if (name.isPresent()) {
                final Optional<Integer> index = newVariableMap.get(name.get());
                if (index.isPresent()) {
                    newLiterals[i] = l < 0 ? -index.get() : index.get();
                } else {
                    return Result.empty(new Problem("No variable named " + name.get(), Problem.Severity.ERROR));
                }
            } else {
                return Result.empty(new Problem("No variable with index " + l, Problem.Severity.ERROR));
            }
        }
        return Result.of(newSortedIntegerList(newLiterals));
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
        for (int otherLiteral : integers) {
            final int index = indexOfVariable(otherLiteral);
            if (index >= 0) {
                count++;
                if (removeMarker != null) {
                    removeMarker[index] = true;
                }
            }
        }
        return count;
    }

    public LiteralList removeAllVariables(int... integers) {
        final boolean[] removeMarker = new boolean[this.integers.length];
        final int count = countVariables(integers, removeMarker);

        final int[] newLiterals = new int[this.integers.length - count];
        int j = 0;
        for (int i = 0; i < this.integers.length; i++) {
            if (!removeMarker[i]) {
                newLiterals[j++] = this.integers[i];
            }
        }
        return newSortedIntegerList(newLiterals);
    }

    public LiteralList removeAllVariables(LiteralList literalList) {
        return removeAllVariables(literalList.integers);
    }

    public LiteralList retainAllVariables(LiteralList literalList) {
        return retainAllVariables(literalList.getIntegers());
    }

    public LiteralList retainAllVariables(int... integers) {
        final boolean[] removeMarker = new boolean[this.integers.length];
        final int count = countVariables(integers, removeMarker);

        final int[] newLiterals = new int[count];
        int j = 0;
        for (int i = 0; i < this.integers.length; i++) {
            if (removeMarker[i]) {
                newLiterals[j++] = this.integers[i];
            }
        }
        return new LiteralList(newLiterals);
    }

    public LiteralList toLiteralList() {
        return new LiteralList(integers);
    }

    public SATClause toClause() {
        return new SATClause(integers);
    }

    public SATSolution toSolution() {
        return new SATSolution(integers);
    }

    @Override
    public Map<Integer, Object> getAll() {
        return null; // todo
    }

    @Override
    public Object get(Integer variable) {
        return null; // todo
    }

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
