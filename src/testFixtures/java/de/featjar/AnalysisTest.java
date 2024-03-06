/*
 * Copyright (C) 2024 FeatJAR-Development-Team
 *
 * This file is part of FeatJAR-formula-analysis-sat4j.
 *
 * formula-analysis-sat4j is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3.0 of the License,
 * or (at your option) any later version.
 *
 * formula-analysis-sat4j is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with formula-analysis-sat4j. If not, see <https://www.gnu.org/licenses/>.
 *
 * See <https://github.com/FeatureIDE/FeatJAR-formula-analysis-sat4j> for further information.
 */
package de.featjar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.featjar.base.computation.Computations;
import de.featjar.base.computation.IComputation;
import de.featjar.base.data.Pair;
import de.featjar.base.data.Result;
import de.featjar.formula.analysis.VariableMap;
import de.featjar.formula.analysis.assignment.Assignment;
import de.featjar.formula.analysis.assignment.AssignmentList;
import de.featjar.formula.analysis.bool.BooleanAssignment;
import de.featjar.formula.analysis.bool.BooleanAssignmentList;
import de.featjar.formula.analysis.bool.BooleanSolution;
import de.featjar.formula.analysis.value.AValueAssignment;
import de.featjar.formula.structure.formula.IFormula;
import de.featjar.formula.transformer.ComputeCNFFormula;
import de.featjar.formula.transformer.ComputeNNFFormula;
import java.math.BigInteger;
import java.util.function.Function;

public class AnalysisTest extends Common {

    public static <T> void testSatisfiability(
            Function<IComputation<IFormula>, IComputation<Pair<T, VariableMap>>> mapper,
            Function<IComputation<T>, IComputation<Boolean>> analysis) {
        assertEquals(Boolean.TRUE, compute("GPL/model.xml", mapper, analysis));
    }

    public static <T> void testSolutionCount(
            Function<IComputation<IFormula>, IComputation<Pair<T, VariableMap>>> mapper,
            Function<IComputation<T>, IComputation<BigInteger>> analysis) {
        assertEquals(BigInteger.valueOf(960), compute("GPL/model.xml", mapper, analysis));
    }

    public static <T> void testSolution(
            Function<IComputation<IFormula>, IComputation<Pair<T, VariableMap>>> mapper,
            Function<IComputation<T>, IComputation<BooleanSolution>> analysis) {
        computeAndTestSolution("GPL/model.xml", mapper, analysis);
    }

    public static <T> void testCore(
            Function<IComputation<IFormula>, IComputation<Pair<T, VariableMap>>> mapper,
            Function<IComputation<T>, IComputation<BooleanAssignment>> analysis) {
        computeAndCompareCore(
                "GPL/model.xml",
                new Assignment(
                        "GPL", true,
                        "MainGpl", true,
                        "HiddenGtp", true,
                        "TestProg", true,
                        "Alg", true,
                        "Src", true,
                        "HiddenWgt", true,
                        "WeightOptions", true,
                        "Wgt", true,
                        "Gtp", true,
                        "Implementation", true,
                        "Base", true),
                mapper,
                analysis);
    }

    public static <T> void testAtomicSets(
            Function<IComputation<IFormula>, IComputation<Pair<T, VariableMap>>> mapper,
            Function<IComputation<T>, IComputation<BooleanAssignmentList>> analysis) {
        computeAndCompareAtomicSets("GPL/model.xml", new AssignmentList(), mapper, analysis);
    }

    public static <T> void testIndeterminate(
            Function<IComputation<IFormula>, IComputation<Pair<T, VariableMap>>> mapper,
            Function<IComputation<T>, IComputation<Boolean>> analysis) {
        assertEquals(Boolean.TRUE, compute("GPL/model.xml", mapper, analysis));
    }

    private static <R, T> R compute(
            String modelPath,
            Function<IComputation<IFormula>, IComputation<Pair<T, VariableMap>>> mapper,
            Function<IComputation<T>, IComputation<R>> analysis) {
        return compute(loadFormula(modelPath), mapper, analysis);
    }

    private static <R, T> R compute(
            IFormula loadFormula,
            Function<IComputation<IFormula>, IComputation<Pair<T, VariableMap>>> mapper,
            Function<IComputation<T>, IComputation<R>> analysis) {
        Result<R> result = Computations.of(loadFormula)
                .map(ComputeNNFFormula::new)
                .map(ComputeCNFFormula::new)
                .map(mapper)
                .map(Computations::getKey)
                .map(analysis)
                .computeResult();
        assertTrue(result.isPresent(), result::printProblems);
        return result.get();
    }

    private static <T> void computeAndTestSolution(
            String modelPath,
            Function<IComputation<IFormula>, IComputation<Pair<T, VariableMap>>> mapper,
            Function<IComputation<T>, IComputation<BooleanSolution>> analysis) {
        IFormula formula = loadFormula(modelPath);
        Pair<T, VariableMap> rep = Computations.of(formula)
                .map(ComputeNNFFormula::new)
                .map(ComputeCNFFormula::new)
                .map(mapper)
                .compute();
        T cnf = rep.getKey();
        VariableMap variableMap = rep.getValue();
        Result<? extends AValueAssignment> resultOfcomputedSolution =
                Computations.of(cnf).map(analysis).computeResult().map(VariableMap::toValue);
        assertTrue(resultOfcomputedSolution.isPresent(), resultOfcomputedSolution::printProblems);

        assertEquals(Boolean.TRUE, formula.evaluate(resultOfcomputedSolution.get(), variableMap));
    }

    private static <T> void computeAndCompareCore(
            String modelPath,
            Assignment expectedCore,
            Function<IComputation<IFormula>, IComputation<Pair<T, VariableMap>>> mapper,
            Function<IComputation<T>, IComputation<BooleanAssignment>> analysis) {
        Pair<T, VariableMap> rep = Computations.of(loadFormula(modelPath))
                .map(ComputeNNFFormula::new)
                .map(ComputeCNFFormula::new)
                .map(mapper)
                .compute();
        T cnf = rep.getKey();
        VariableMap variableMap = rep.getValue();
        Result<Assignment> resultOfcomputedCore =
                Computations.of(cnf).map(analysis).computeResult().flatMap(variableMap::toAssignment);
        assertTrue(resultOfcomputedCore.isPresent(), resultOfcomputedCore::printProblems);

        Assignment computedCore = resultOfcomputedCore.get();

        assertTrue(computedCore.containsOtherAssignment(expectedCore), "\n" + expectedCore + "\n!=\n" + computedCore);
        assertTrue(expectedCore.containsOtherAssignment(computedCore), "\n" + expectedCore + "\n!=\n" + computedCore);
    }

    private static <T> void computeAndCompareAtomicSets(
            String modelPath,
            AssignmentList expectedAtomicSets,
            Function<IComputation<IFormula>, IComputation<Pair<T, VariableMap>>> mapper,
            Function<IComputation<T>, IComputation<BooleanAssignmentList>> analysis) {
        Pair<T, VariableMap> rep = Computations.of(loadFormula(modelPath))
                .map(ComputeNNFFormula::new)
                .map(ComputeCNFFormula::new)
                .map(mapper)
                .compute();

        T cnf = rep.getKey();
        VariableMap variableMap = rep.getValue();
        Result<AssignmentList> resultOfcomputedCore =
                Computations.of(cnf).map(analysis).computeResult().flatMap(variableMap::toAssignment);
        assertTrue(resultOfcomputedCore.isPresent(), resultOfcomputedCore::printProblems);

        AssignmentList computedAtomicSets = resultOfcomputedCore.get();

        assertTrue(computedAtomicSets.containsOtherAssignments(expectedAtomicSets));
        assertTrue(expectedAtomicSets.containsOtherAssignments(computedAtomicSets));
    }
}
