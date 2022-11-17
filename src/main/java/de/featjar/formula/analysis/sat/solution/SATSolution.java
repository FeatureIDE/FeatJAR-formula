package de.featjar.formula.analysis.sat.solution;

import de.featjar.formula.analysis.sat.clause.CNF;
import de.featjar.formula.analysis.sat.LiteralList;
import de.featjar.formula.analysis.solver.Solution;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * A (partial) solution to a {@link CNF}, implemented as a sorted list of literals.
 * Often holds output of a {@link de.featjar.formula.analysis.solver.SATSolver}.
 * Literals are ordered by indices; that is, index {@code i} either holds {@code -i}, {@code 0}, or {@code i}.
 * The same literal may not occur multiple times, but literals may be 0 for partial solutions.
 * While solutions are usually interpreted as conjunctions of literals, this class is not
 * linked to a fixed interpretation.
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public class SATSolution extends LiteralList implements Solution<Integer> {
    public SATSolution(int... integers) {
        this(integers, true);
    }

    public SATSolution(int[] integers, boolean sort) {
        super(integers);
        if (sort)
            sort();
    }

    public SATSolution(Collection<Integer> integers) {
        super(integers);
        sort();
    }

    public SATSolution(LiteralList literalList) {
        super(literalList);
        sort();
    }

    @Override
    protected SATSolution newSortedIntegerList(int[] integers) {
        return new SATSolution(integers);
    }

    protected void sort() {
        final int[] sortedIntegers = new int[integers.length];
        // todo: what if we have both literal -1 AND literal 1? is this forbidden?
        // todo: what if we have literal 10, but the length is only 5? is this also forbidden?
        Arrays.stream(integers)
                .filter(integer -> integer != 0)
                .forEach(integer -> sortedIntegers[Math.abs(integer) - 1] = integer);
        System.arraycopy(sortedIntegers, 0, integers, 0, integers.length);
        hashCode = Arrays.hashCode(this.integers);
    }

    @Override
    public SATSolution getPositives() {
        int[] positiveIntegers = Arrays.copyOfRange(integers, integers.length - countPositives(), integers.length);
        return newSortedIntegerList(positiveIntegers);
    }

    @Override
    public SATSolution getNegatives() {
        int[] negativeIntegers = Arrays.copyOfRange(integers, 0, countNegatives());
        return newSortedIntegerList(negativeIntegers);
    }

    @Override
    public int indexOf(int integer) {
        final int index = Math.abs(integer) - 1;
        return integer == 0 ? -1 : integers[index] == integer ? index : -1;
    }

    @Override
    public int indexOfVariable(int integer) {
        return (integer > 0) && (integer < size()) ? (integer - 1) : -1;
    }

    @Override
    public Map<Integer, Object> getAll() {
        return null; // todo
    }

    @Override
    public Object get(Integer variable) {
        return null; // todo
    }
}
