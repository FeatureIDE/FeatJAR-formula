package de.featjar.formula.transformer;

import de.featjar.base.computation.IComputation;
import de.featjar.base.data.Result;
import de.featjar.formula.structure.formula.IFormula;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class TransformerTest {
    public static void traverseAndAssertSameFormula(IFormula oldFormula, Function<IComputation<IFormula>, IComputation<IFormula>> formulaComputationFunction) {
        Result<IFormula> result = formulaComputationFunction.apply(IComputation.of(oldFormula)).getResult();
        assertTrue(result.isPresent());
        assertEquals(oldFormula, result.get());
    }

    public static void traverseAndAssertFormulaEquals(IFormula oldFormula, Function<IComputation<IFormula>, IComputation<IFormula>> formulaComputationFunction, IFormula assertFormula) {
        Result<IFormula> result = formulaComputationFunction.apply(IComputation.of(oldFormula)).getResult();
        assertTrue(result.isPresent());
        assertNotEquals(oldFormula, result.get());
        assertEquals(assertFormula, result.get());
    }
}