package de.featjar.formula.analysis.bool;

import de.featjar.base.computation.IComputation;
import de.featjar.base.data.Result;
import de.featjar.formula.analysis.ISolver;
import de.featjar.formula.analysis.ISolution;
import de.featjar.formula.analysis.VariableMap;
import de.featjar.formula.analysis.value.ValueSolution;

import java.util.*;

/**
 * A (partial) Boolean solution; that is, a conjunction of literals.
 * Implemented as a sorted list of indices.
 * Often holds output of a SAT {@link ISolver}.
 * Indices are ordered such that the array index {@code i} either holds {@code -i}, {@code 0}, or {@code i}.
 * That is, the largest occurring index mandates the minimum length of the underlying array.
 * The same index may not occur multiple times, but indices may be 0 for partial solutions.
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public class BooleanSolution extends BooleanAssignment implements ISolution<Integer> {
    public BooleanSolution(int... integers) {
        this(integers, true);
    }

    public BooleanSolution(int[] integers, boolean sort) {
        super(integers);
        if (sort)
            sort();
    }

    public BooleanSolution(Collection<Integer> integers) {
        super(integers);
        sort();
    }

    public BooleanSolution(BooleanAssignment booleanAssignment) {
        super(booleanAssignment);
        sort();
    }

    @Override
    protected BooleanSolution newIntegerList(int[] integers) {
        return new BooleanSolution(integers);
    }

    protected void sort() {
        final int[] sortedIntegers = new int[integers.length];
        Arrays.stream(integers)
                .filter(integer -> integer != 0)
                .forEach(integer -> sortedIntegers[Math.abs(integer) - 1] = integer);
        System.arraycopy(sortedIntegers, 0, integers, 0, integers.length);
        hashCode = Arrays.hashCode(this.integers);
    }

    // TODO: this is a weird method, maybe refactor?
    public static int[] resetConflicts(int[] s1, int[] s2) {
        int[] integers = new int[s1.length];
        for (int i = 0; i < s1.length; i++) {
            final int x = s1[i];
            final int y = s2[i];
            integers[i] = x != y ? 0 : x;
        }
        return integers;
    }

    public BooleanSolution resetConflicts(BooleanSolution booleanSolution) {
        List<Integer> integers = new ArrayList<>(size());
        for (int i = 0; i < size(); i++) {
            final int x = get(i);
            final int y = booleanSolution.get(i);
            integers.set(i, x != y ? 0 : x);
        }
        return new BooleanSolution(integers);
    }

    @Override
    public BooleanSolution getPositives() {
        int[] positiveIntegers = Arrays.copyOfRange(integers, integers.length - countPositives(), integers.length);
        return newIntegerList(positiveIntegers);
    }

    @Override
    public BooleanSolution getNegatives() {
        int[] negativeIntegers = Arrays.copyOfRange(integers, 0, countNegatives());
        return newIntegerList(negativeIntegers);
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
    public int size() {
        return countPositives() + countNegatives();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public Result<ValueSolution> toValue(VariableMap variableMap) {
        return variableMap.toValue(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public IComputation<ValueSolution> toValue(IComputation<VariableMap> variableMap) {
        return (IComputation<ValueSolution>) super.toValue(variableMap);
    }

    @Override
    public String toString() {
        return String.format("BooleanSolution[%s]", print());
    }
}
