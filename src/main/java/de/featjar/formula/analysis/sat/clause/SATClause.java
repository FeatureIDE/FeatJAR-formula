package de.featjar.formula.analysis.sat.clause;

import de.featjar.formula.analysis.sat.LiteralList;
import de.featjar.formula.analysis.solver.Assumable;
import de.featjar.formula.analysis.solver.Clause;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * A clause in a {@link CNF}, implemented as a sorted list of literals.
 * Often used as input to a {@link de.featjar.formula.analysis.solver.SATSolver}.
 * Literals are ordered naturally; that is, in ascending order, so negative literals come before positive literals.
 * The same literal may occur multiple times, but no literal may be 0.
 * While clauses are usually interpreted as disjunctions of literals, this class is not
 * linked to a fixed interpretation.
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public class SATClause extends LiteralList implements Assumable<Integer>, Clause<Integer> {
    public SATClause(int... integers) {
        this(integers, true);
    }

    public SATClause(int[] integers, boolean sort) {
        super(integers);
        if (sort)
            sort();
    }

    public SATClause(Collection<Integer> integers) {
        super(integers);
        sort();
    }

    public SATClause(LiteralList literalList) {
        super(literalList);
        sort();
    }

    @Override
    protected SATClause newSortedIntegerList(int[] integers) {
        return new SATClause(integers);
    }

    @Override
    public Map<Integer, Object> getAll() {
        return null; // todo
    }

    @Override
    public Optional<Object> get(Integer variableInteger) {
        int value = get(indexOfVariable(variableInteger));
        return value == 0 ? Optional.empty() : Optional.of(value > 0);
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
    public SATClause getPositives() {
        int[] positiveIntegers = Arrays.copyOfRange(integers, integers.length - countPositives(), integers.length);
        return newSortedIntegerList(positiveIntegers);
    }

    @Override
    public SATClause getNegatives() {
        int[] negativeIntegers = Arrays.copyOfRange(integers, 0, countNegatives());
        return newSortedIntegerList(negativeIntegers);
    }

    @Override
    public int indexOf(int integer) {
        return Arrays.binarySearch(integers, integer);
    }

    @Override
    public SATClause negate() {
        final int[] negLiterals = new int[integers.length];
                final int highestIndex = negLiterals.length - 1;
                for (int i = 0; i < negLiterals.length; i++) {
                    negLiterals[highestIndex - i] = -integers[i]; // todo: what does this do?
                }
        return newSortedIntegerList(negLiterals);
    }
}
