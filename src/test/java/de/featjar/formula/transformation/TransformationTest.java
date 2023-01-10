package de.featjar.formula.transformation;

import static org.junit.jupiter.api.Assertions.*;

import de.featjar.base.computation.Computations;
import de.featjar.base.computation.IComputation;
import de.featjar.base.data.Result;
import de.featjar.formula.structure.formula.IFormula;
import java.util.function.Function;

class TransformationTest {
    public static void traverseAndAssertSameFormula(
            IFormula oldFormula, Function<IComputation<IFormula>, IComputation<IFormula>> formulaComputationFunction) {
        Result<IFormula> result =
                formulaComputationFunction.apply(Computations.of(oldFormula)).getResult();
        assertTrue(result.isPresent());
        assertEquals(oldFormula, result.get());
    }

    public static void traverseAndAssertFormulaEquals(
            IFormula oldFormula,
            Function<IComputation<IFormula>, IComputation<IFormula>> formulaComputationFunction,
            IFormula assertFormula) {
        Result<IFormula> result =
                formulaComputationFunction.apply(Computations.of(oldFormula)).getResult();
        assertTrue(result.isPresent());
        System.out.println(oldFormula.printParseable());
        System.out.println(result.get().printParseable());
        assertNotEquals(oldFormula, result.get());
        assertEquals(assertFormula, result.get());
    }
}
