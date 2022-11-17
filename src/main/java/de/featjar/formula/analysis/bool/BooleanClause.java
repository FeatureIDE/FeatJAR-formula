package de.featjar.formula.analysis.bool;

import de.featjar.formula.analysis.Solver;
import de.featjar.formula.analysis.Clause;

import java.util.*;

/**
 * A Boolean clause; that is, a disjunction of literals.
 * Implemented as a sorted list of indices.
 * Often used as input to a SAT {@link Solver}.
 * Indices are ordered naturally; that is, in ascending order, so negative indices come before positive indices.
 * The same index may occur multiple times, but no index may be 0.
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public class BooleanClause extends BooleanAssignment implements Clause<Integer> {
    public BooleanClause(int... integers) {
        this(integers, true);
    }

    public BooleanClause(int[] integers, boolean sort) {
        super(integers);
        if (sort)
            sort();
    }

    public BooleanClause(Collection<Integer> integers) {
        super(integers);
        sort();
    }

    public BooleanClause(BooleanAssignment booleanAssignment) {
        super(booleanAssignment);
        sort();
    }

    @Override
    protected BooleanClause newIntegerList(int[] integers) {
        return new BooleanClause(integers);
    }

    protected void sort() {
        Arrays.sort(this.integers);
        hashCode = Arrays.hashCode(this.integers);
    }

    @Override
    public int countNegatives() {
        int count = 0;
        for (int integer : integers) {
            if (integer < 0) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    @Override
    public int countPositives() {
        int count = 0;
        for (int i = integers.length - 1; i >= 0; i--) {
            if (integers[i] > 0) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    @Override
    public BooleanClause getPositives() {
        int[] positiveIntegers = Arrays.copyOfRange(integers, integers.length - countPositives(), integers.length);
        return newIntegerList(positiveIntegers);
    }

    @Override
    public BooleanClause getNegatives() {
        int[] negativeIntegers = Arrays.copyOfRange(integers, 0, countNegatives());
        return newIntegerList(negativeIntegers);
    }

    @Override
    public int indexOf(int integer) {
        return Arrays.binarySearch(integers, integer);
    }

    @Override
    public BooleanClause negate() {
        final int[] negated = new int[integers.length];
                final int highestIndex = negated.length - 1;
                for (int i = 0; i < negated.length; i++) {
                    negated[highestIndex - i] = -integers[i]; // todo: what does this do?
                }
        return newIntegerList(negated);
    }
}
