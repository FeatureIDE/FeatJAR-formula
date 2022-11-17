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
package de.featjar.formula.analysis.sat.clause;

import de.featjar.base.Feat;
import de.featjar.base.data.Computation;
import de.featjar.formula.analysis.sat.VariableMap;
import de.featjar.formula.io.FormulaFormats;
import de.featjar.formula.structure.formula.Formula;
import de.featjar.formula.structure.formula.predicate.Literal;
import de.featjar.formula.structure.formula.connective.And;
import de.featjar.formula.structure.formula.connective.Or;
import de.featjar.base.data.Result;
import de.featjar.base.io.IO;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CNFs {
//    public static SortedIntegerList getVariables(Collection<SortedIntegerList> sortedIntegerLists) {
//        return new SortedIntegerList(sortedIntegerLists.stream()
//                .flatMapToInt(c -> Arrays.stream(c.getIntegers()))
//                .distinct()
//                .toArray());
//    }
//
//    public static SortedIntegerList getLiterals(VariableMap variables) {
//        return new SortedIntegerList(variables.getValidIndexRange().stream().get()
//                .flatMap(i -> IntStream.of(-i, i))
//                .toArray());
//    }
//
//    /**
//     * Negates all clauses in the list (applies De Morgan).
//     *
//     * @param sortedIntegerLists collection of clauses
//     * @return A newly constructed {@code ClauseList}.
//     */
//    public static Stream<SortedIntegerList> negate(Collection<SortedIntegerList> sortedIntegerLists) {
//        return sortedIntegerLists.stream().map(SortedIntegerList::negate);
//    }
//
//    public static Result<SortedIntegerList> adapt(SortedIntegerList sortedIntegerList, VariableMap oldVariables, VariableMap newVariables) {
//        return sortedIntegerList.adapt(oldVariables, newVariables);
//    }
//
//    public static int adapt(int literal, VariableMap oldVariables, VariableMap newVariables) {
//        final String name = oldVariables.get(Math.abs(literal)).orElse(null);
//        final int index = newVariables.get(name).orElse(0);
//        return literal < 0 ? -index : index;
//    }
//
//    public static CNF convertToCNF(Formula expression) {
//        return Computation.of(expression).then(ToCNF::new).getResult().get();
//    }
//
//    public static CNF convertToCNF(Formula expression, VariableMap variableMap) {
//        return Computation.of(expression).then(ToCNF.class, Computation.of(variableMap)).getResult().get();
//    }
//
//    public static CNF convertToDNF(Formula expression) {
//        final CNF cnf = convertToCNF(expression);
//        return new CNF(cnf.getVariableMap(), convertNF(cnf.getClauseList()));
//    }
//
//    public static CNF convertToDNF(Formula expression, VariableMap termMap) {
//        final CNF cnf = convertToCNF(expression, new VariableMap());
//        return new CNF(termMap, convertNF(cnf.getClauseList()));
//    }
//
//    /**
//     * Converts CNF to DNF and vice-versa.
//     *
//     * @param literalLists list of clauses
//     * @return A newly constructed {@code ClauseList}.
//     */
//    public static List<SortedIntegerList> convertNF(ClauseList literalLists) {
//        final List<SortedIntegerList> convertedLiteralListIndexList = new ArrayList<>();
//        convertNF(literalLists, convertedLiteralListIndexList, new int[literalLists.size()], 0);
//        return convertedLiteralListIndexList;
//    }
//
//    private static void convertNF(ClauseList cnf, List<SortedIntegerList> dnf, int[] literals, int index) {
//        if (index == cnf.size()) {
//            final int[] newClauseLiterals = new int[literals.length];
//            int count = 0;
//            for (final int literal : literals) {
//                if (literal != 0) {
//                    newClauseLiterals[count++] = literal;
//                }
//            }
//            if (count < newClauseLiterals.length) {
//                dnf.add(new SortedIntegerList(Arrays.copyOf(newClauseLiterals, count)));
//            } else {
//                dnf.add(new SortedIntegerList(newClauseLiterals));
//            }
//        } else {
//            final HashSet<Integer> literalSet = new HashSet<>();
//            for (int i = 0; i <= index; i++) {
//                literalSet.add(literals[i]);
//            }
//            int redundantCount = 0;
//            final int[] literals2 = cnf.get(index).getIntegers();
//            for (final int literal : literals2) {
//                if (!literalSet.contains(-literal)) {
//                    if (!literalSet.contains(literal)) {
//                        literals[index] = literal;
//                        convertNF(cnf, dnf, literals, index + 1);
//                    } else {
//                        redundantCount++;
//                    }
//                }
//            }
//            literals[index] = 0;
//            if (redundantCount == literals2.length) {
//                convertNF(cnf, dnf, literals, index + 1);
//            }
//        }
//    }
//
//    public static CNF open(Path path) {
//        return IO.load(path, Feat.extensionPoint(FormulaFormats.class))
//                .map(CNFs::convertToCNF)
//                .orElse(p -> Feat.log().problems(p));
//    }
//
//    public static Result<CNF> load(Path path) {
//        return IO.load(path, Feat.extensionPoint(FormulaFormats.class)).map(CNFs::convertToCNF);
//    }
//
//    public static Or toOrClause(SortedIntegerList sortedIntegerList, VariableMap termMap) {
//        return new Or(toLiterals(sortedIntegerList, termMap));
//    }
//
//    public static And toAndClause(SortedIntegerList sortedIntegerList, VariableMap termMap) {
//        return new And(toLiterals(sortedIntegerList, termMap));
//    }
//
//    public static List<Literal> toLiterals(SortedIntegerList sortedIntegerList, VariableMap termMap) {
//        return Arrays.stream(sortedIntegerList.getIntegers())
//                .mapToObj(l -> new Literal(l > 0, String.valueOf(Math.abs(l))))
//                .collect(Collectors.toList());
//    }
}
