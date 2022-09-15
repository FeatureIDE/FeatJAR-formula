/*
 * Copyright (C) 2022 Sebastian Krieter, Elias Kuiter
 *
 * This file is part of formula.
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
package de.featjar.formula.clauses;

import de.featjar.base.data.RangeMap.ValueTerm;
import de.featjar.formula.structure.map.TermMap;
import de.featjar.base.data.Problem;
import de.featjar.base.data.Problem.Severity;
import de.featjar.base.data.Result;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.stream.IntStream;

// TODO add methods for adding literals (e.g. addAll, union, ...)
/**
 * A sorted list of literals. Can be used as a clause of a CNF or DNF.
 *
 * @author Sebastian Krieter
 */
public class LiteralList implements Comparable<LiteralList>, Serializable {

    private static final long serialVersionUID = 8360925003112707082L;

    public enum Order {
        NATURAL,
        INDEX,
        UNORDERED,
    }

    protected final int[] literals;

    private int hashCode;

    private Order order = null;

    /**
     * Sets the value at position i of solution1 to 0 if the value of solution2 at
     * position i is different.
     *
     * @param solution1 First solution.
     * @param solution2 Second solution.
     */
    public static void resetConflicts(final int[] solution1, int[] solution2) {
        for (int i = 0; i < solution1.length; i++) {
            final int x = solution1[i];
            final int y = solution2[i];
            if (x != y) {
                solution1[i] = 0;
            }
        }
    }

    public static LiteralList getVariables(CNF cnf) {
        return getVariables(cnf.getVariableMap());
    }

    public static LiteralList getVariables(TermMap termMap) {
        return new LiteralList(constructVariableStream(termMap).toArray());
    }

    public static LiteralList getVariables(TermMap termMap, Collection<String> variableNames) {
        return new LiteralList(
                constructVariableStream(termMap, variableNames).toArray());
    }

    public static LiteralList getLiterals(CNF cnf) {
        return getLiterals(cnf.getVariableMap());
    }

    public static LiteralList getLiterals(TermMap variables) {
        return new LiteralList(constructVariableStream(variables)
                .flatMap(n -> IntStream.of(-n, n))
                .toArray());
    }

    public static LiteralList getLiterals(TermMap termMap, Collection<String> variableNames) {
        return new LiteralList(constructVariableStream(termMap, variableNames)
                .flatMap(n -> IntStream.of(-n, n))
                .toArray());
    }

    private static IntStream constructVariableStream(TermMap variables) {
        return IntStream.rangeClosed(1, variables.getVariableCount());
    }

    private static IntStream constructVariableStream(TermMap termMap, Collection<String> variableNames) {
        return variableNames.stream()
                .map(termMap::getVariable)
                .flatMap(Optional::stream)
                .mapToInt(ValueTerm::getIndex)
                .distinct();
    }

    public static LiteralList merge(Collection<LiteralList> collection) {
        return new LiteralList(collection.stream()
                .flatMapToInt(l -> Arrays.stream(l.getLiterals()))
                .distinct()
                .toArray());
    }

    /**
     * Constructs a deep copy of the given clause.
     *
     * @param clause the old clause
     */
    public LiteralList(LiteralList clause) {
        literals = Arrays.copyOf(clause.literals, clause.literals.length);
        hashCode = clause.hashCode;
        order = clause.order;
    }

    public LiteralList(LiteralList clause, Order literalOrder) {
        literals = Arrays.copyOf(clause.literals, clause.literals.length);
        setOrder(literalOrder);
    }

    /**
     * Constructs a new clause from the given literals. <br>
     * <b>The resulting clause is backed by the given literal array. The array will
     * be sorted.</b>
     *
     * @param literals literals of the clause
     */
    public LiteralList(int... literals) {
        this(literals, Order.NATURAL);
    }

    public LiteralList(int[] literals, Order literalOrder) {
        this(literals, literalOrder, true);
    }

    public LiteralList(int[] literals, Order literalOrder, boolean sort) {
        this.literals = literals;
        if (sort) {
            setOrder(literalOrder);
        } else {
            hashCode = Arrays.hashCode(literals);
            order = literalOrder;
        }
    }

    public LiteralList(Collection<Integer> literals) {
        this(literals.stream().mapToInt(Integer::intValue).toArray());
    }

    public LiteralList(Collection<Integer> literals, Order literalOrder) {
        this(literals.stream().mapToInt(Integer::intValue).toArray(), literalOrder);
    }

    public LiteralList(Collection<Integer> literals, Order literalOrder, boolean sort) {
        this(literals.stream().mapToInt(Integer::intValue).toArray(), literalOrder, sort);
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        if (this.order != order) {
            sortLiterals(order);
            this.order = order;
        }
    }

    private void sortLiterals(Order newOrder) {
        switch (newOrder) {
            case INDEX:
                final int[] sortedLiterals = new int[literals.length];
                for (int i = 0; i < literals.length; i++) {
                    final int literal = literals[i];
                    if (literal != 0) {
                        sortedLiterals[Math.abs(literal) - 1] = literal;
                    }
                }
                System.arraycopy(sortedLiterals, 0, literals, 0, literals.length);
                break;
            case NATURAL:
                Arrays.sort(literals);
                break;
            case UNORDERED:
                break;
            default:
                break;
        }
        hashCode = Arrays.hashCode(literals);
    }

    public int[] getLiterals() {
        return literals;
    }

    public int get(int index) {
        return literals[index];
    }

    public int[] get(int start, int end) {
        return Arrays.copyOfRange(literals, start, end);
    }

    public boolean containsAnyLiteral(int... literals) {
        for (final int literal : literals) {
            if (indexOfLiteral(literal) >= 0) {
                return true;
            }
        }
        return false;
    }

    public boolean containsAllLiterals(int... literals) {
        for (final int literal : literals) {
            if (indexOfLiteral(literal) < 0) {
                return false;
            }
        }
        return true;
    }

    public boolean containsAnyVariable(int... variables) {
        for (final int variable : variables) {
            if (indexOfVariable(variable) >= 0) {
                return true;
            }
        }
        return false;
    }

    public boolean containsAllVariables(int... variables) {
        for (final int variable : variables) {
            if (indexOfVariable(variable) >= 0) {
                return false;
            }
        }
        return true;
    }

    public boolean containsAny(LiteralList otherLiteralSet) {
        for (final int otherLiteral : otherLiteralSet.getLiterals()) {
            if (indexOfLiteral(otherLiteral) >= 0) {
                return true;
            }
        }
        return false;
    }

    public boolean containsAll(LiteralList otherLiteralSet) {
        for (final int otherLiteral : otherLiteralSet.getLiterals()) {
            if (indexOfLiteral(otherLiteral) < 0) {
                return false;
            }
        }
        return true;
    }

    public int indexOfLiteral(int literal) {
        switch (order) {
            case UNORDERED:
                for (int i = 0; i < literals.length; i++) {
                    if (literal == literals[i]) {
                        return i;
                    }
                }
                return -1;
            case INDEX:
                final int index = Math.abs(literal) - 1;
                return literal == 0 ? -1 : literals[index] == literal ? index : -1;
            case NATURAL:
                return Arrays.binarySearch(literals, literal);
            default:
                throw new AssertionError(order);
        }
    }

    public int indexOfVariable(int variable) {
        switch (order) {
            case INDEX:
                return (variable > 0) && (variable < size()) ? (variable - 1) : -1;
            case UNORDERED:
            case NATURAL:
                for (int i = 0; i < literals.length; i++) {
                    if (Math.abs(literals[i]) == variable) {
                        return i;
                    }
                }
                return -1;
            default:
                throw new AssertionError(order);
        }
    }

    public int countNegative() {
        int count = 0;
        switch (order) {
            case UNORDERED:
            case INDEX:
                for (final int literal : literals) {
                    if (literal < 0) {
                        count++;
                    }
                }
                break;
            case NATURAL:
                for (int i = 0; i < literals.length; i++) {
                    if (literals[i] < 0) {
                        count++;
                    } else {
                        break;
                    }
                }
                break;
        }
        return count;
    }

    public int countPositive() {
        int count = 0;
        switch (order) {
            case UNORDERED:
            case INDEX:
                for (final int literal : literals) {
                    if (literal > 0) {
                        count++;
                    }
                }
                break;
            case NATURAL:
                for (int i = literals.length - 1; i >= 0; i--) {
                    if (literals[i] > 0) {
                        count++;
                    } else {
                        break;
                    }
                }
                break;
        }
        return count;
    }

    public int size() {
        return literals.length;
    }

    public LiteralList getVariables() {
        final int[] absoluteLiterals = new int[literals.length];
        for (int i = 0; i < literals.length; i++) {
            absoluteLiterals[i] = Math.abs(literals[i]);
        }
        return new LiteralList(absoluteLiterals);
    }

    public LiteralList addAll(LiteralList otherLiterals) {
        final boolean[] marker = new boolean[literals.length];
        final int count = countDuplicates(otherLiterals.literals, marker);

        final int[] newLiterals = new int[literals.length + otherLiterals.literals.length - count];
        int j = 0;
        for (int i = 0; i < literals.length; i++) {
            if (!marker[i]) {
                newLiterals[j++] = literals[i];
            }
        }
        System.arraycopy(otherLiterals.literals, 0, newLiterals, j, otherLiterals.literals.length);
        return new LiteralList(newLiterals, Order.UNORDERED, false);
    }

    public LiteralList removeAll(LiteralList otherLiterals) {
        final boolean[] removeMarker = new boolean[literals.length];
        final int count = countDuplicates(otherLiterals.literals, removeMarker);

        final int[] newLiterals = new int[literals.length - count];
        int j = 0;
        for (int i = 0; i < literals.length; i++) {
            if (!removeMarker[i]) {
                newLiterals[j++] = literals[i];
            }
        }
        return new LiteralList(newLiterals, order, false);
    }

    public LiteralList removeVariables(LiteralList variables) {
        return removeVariables(variables.literals);
    }

    public LiteralList removeVariables(int... variables) {
        final boolean[] removeMarker = new boolean[literals.length];
        final int count = countVariables(variables, removeMarker);

        final int[] newLiterals = new int[literals.length - count];
        int j = 0;
        for (int i = 0; i < literals.length; i++) {
            if (!removeMarker[i]) {
                newLiterals[j++] = literals[i];
            }
        }
        return new LiteralList(newLiterals, order, false);
    }

    public LiteralList retainAll(LiteralList otherLiterals) {
        final boolean[] marker = new boolean[literals.length];
        final int count = countDuplicates(otherLiterals.literals, marker);

        final int[] newLiterals = new int[count];
        int j = 0;
        for (int i = 0; i < literals.length; i++) {
            if (marker[i]) {
                newLiterals[j++] = literals[i];
            }
        }
        return new LiteralList(newLiterals, Order.UNORDERED, false);
    }

    public LiteralList retainVariables(LiteralList variables) {
        return retainVariables(variables.getLiterals());
    }

    public LiteralList retainVariables(int... variables) {
        final boolean[] removeMarker = new boolean[literals.length];
        final int count = countVariables(variables, removeMarker);

        final int[] newLiterals = new int[count];
        int j = 0;
        for (int i = 0; i < literals.length; i++) {
            if (removeMarker[i]) {
                newLiterals[j++] = literals[i];
            }
        }
        return new LiteralList(newLiterals, order, false);
    }

    private int countDuplicates(int[] otherLiterals, final boolean[] removeMarker) {
        int count = 0;
        for (int i = 0; i < otherLiterals.length; i++) {
            final int index = indexOfLiteral(otherLiterals[i]);
            if (index >= 0) {
                count++;
                if (removeMarker != null) {
                    removeMarker[index] = true;
                }
            }
        }
        return count;
    }

    private int countVariables(int[] otherLiterals, final boolean[] removeMarker) {
        int count = 0;
        for (int i = 0; i < otherLiterals.length; i++) {
            final int index = indexOfVariable(otherLiterals[i]);
            if (index >= 0) {
                count++;
                if (removeMarker != null) {
                    removeMarker[index] = true;
                }
            }
        }
        return count;
    }

    public boolean hasDuplicates(LiteralList variables) {
        final int[] otherLiterals = variables.getLiterals();
        for (int i = 0; i < otherLiterals.length; i++) {
            if (indexOfLiteral(otherLiterals[i]) >= 0) {
                return true;
            }
        }
        return false;
    }

    public int countDuplicates(LiteralList variables) {
        return countDuplicates(variables.literals, null);
    }

    public boolean hasConflicts(LiteralList literals) {
        return hasConflicts(literals.getLiterals());
    }

    public boolean hasConflicts(final int[] otherLiterals) {
        for (int i = 0; i < otherLiterals.length; i++) {
            if (indexOfLiteral(-otherLiterals[i]) >= 0) {
                return true;
            }
        }
        return false;
    }

    public int countConflicts(LiteralList variables) {
        final int[] otherLiterals = variables.getLiterals();
        int count = 0;
        for (int i = 0; i < otherLiterals.length; i++) {
            if (indexOfLiteral(-otherLiterals[i]) >= 0) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns a copy of the given array with all entries negated.
     *
     * @return Array with negated entries.
     */
    public LiteralList negate() {
        final int[] negLiterals = new int[literals.length];
        switch (order) {
            case INDEX:
            case UNORDERED:
                for (int i = 0; i < negLiterals.length; i++) {
                    negLiterals[i] = -literals[i];
                }
                break;
            case NATURAL:
                final int highestIndex = negLiterals.length - 1;
                for (int i = 0; i < negLiterals.length; i++) {
                    negLiterals[highestIndex - i] = -literals[i];
                }
                break;
        }
        return new LiteralList(negLiterals, order, false);
    }

    public LiteralList getPositiveLiterals() {
        final int countPositive = countPositive();
        final int[] positiveLiterals;
        switch (order) {
            case INDEX:
            case UNORDERED:
                positiveLiterals = new int[countPositive];
                int i = 0;
                for (final int literal : literals) {
                    if (literal > 0) {
                        positiveLiterals[i++] = literal;
                    }
                }
                break;
            case NATURAL:
                positiveLiterals = Arrays.copyOfRange(literals, literals.length - countPositive, literals.length);
                break;
            default:
                throw new AssertionError(order);
        }
        return new LiteralList(positiveLiterals, order, false);
    }

    public LiteralList getNegativeLiterals() {
        final int countNegative = countNegative();
        final int[] negativeLiterals;
        switch (order) {
            case INDEX:
            case UNORDERED:
                negativeLiterals = new int[countNegative];
                int i = 0;
                for (final int literal : literals) {
                    if (literal < 0) {
                        negativeLiterals[i++] = literal;
                    }
                }
                break;
            case NATURAL:
                negativeLiterals = Arrays.copyOfRange(literals, 0, countNegative);
                break;
            default:
                throw new AssertionError(order);
        }
        return new LiteralList(negativeLiterals, order, false);
    }

    public Optional<LiteralList> clean() {
        final LinkedHashSet<Integer> newLiteralSet = new LinkedHashSet<>();

        for (final int literal : literals) {
            if (newLiteralSet.contains(-literal)) {
                return Optional.empty();
            } else {
                newLiteralSet.add(literal);
            }
        }

        final int[] uniqueVarArray;
        if (newLiteralSet.size() == literals.length) {
            uniqueVarArray = Arrays.copyOf(literals, literals.length);
        } else {
            uniqueVarArray = new int[newLiteralSet.size()];
            int i = 0;
            for (final int lit : newLiteralSet) {
                uniqueVarArray[i++] = lit;
            }
        }
        return Optional.of(new LiteralList(uniqueVarArray, order, false));
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }
        return Arrays.equals(literals, ((LiteralList) obj).literals);
    }

    @Override
    public String toString() {
        return "Clause " + toLiteralString();
    }

    @Override
    public LiteralList clone() {
        return new LiteralList(this);
    }

    public boolean isEmpty() {
        return literals.length == 0;
    }

    @Override
    public int compareTo(LiteralList o) {
        final int lengthDiff = literals.length - o.literals.length;
        if (lengthDiff != 0) {
            return lengthDiff;
        }
        for (int i = 0; i < literals.length; i++) {
            final int diff = literals[i] - o.literals[i];
            if (diff != 0) {
                return diff;
            }
        }
        return lengthDiff;
    }

    public Result<LiteralList> adapt(TermMap oldVariables, TermMap newVariables) {
        final int[] oldLiterals = literals;
        final int[] newLiterals = new int[oldLiterals.length];
        for (int i = 0; i < oldLiterals.length; i++) {
            final int l = oldLiterals[i];
            final Optional<String> name = oldVariables.getVariableName(Math.abs(l));
            if (name.isPresent()) {
                final Optional<Integer> index = newVariables.getVariableIndex(name.get());
                if (index.isPresent()) {
                    newLiterals[i] = l < 0 ? -index.get() : index.get();
                } else {
                    return Result.empty(new Problem("No variable named " + name.get(), Severity.ERROR));
                }
            } else {
                return Result.empty(new Problem("No variable with index " + l, Severity.ERROR));
            }
        }
        return Result.of(new LiteralList(newLiterals, order, true));
    }

    public String toBinaryString() {
        final StringBuilder sb = new StringBuilder(literals.length);
        for (final int literal : literals) {
            sb.append(literal == 0 ? '?' : literal < 0 ? '0' : '1');
        }
        return sb.toString();
    }

    public String toLiteralString() {
        return Arrays.toString(literals);
    }
}
