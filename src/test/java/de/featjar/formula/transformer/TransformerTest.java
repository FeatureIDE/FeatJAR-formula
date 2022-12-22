package de.featjar.formula.transformer;

import de.featjar.base.computation.Computable;
import de.featjar.base.data.Result;
import de.featjar.formula.structure.formula.Formula;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class TransformerTest {
    public static void traverseAndAssertSameFormula(Formula oldFormula, Function<Computable<Formula>, Computable<Formula>> formulaComputationFunction) {
        Result<Formula> result = formulaComputationFunction.apply(Computable.of(oldFormula)).getResult();
        assertTrue(result.isPresent());
        assertEquals(oldFormula, result.get());
    }

    public static void traverseAndAssertFormulaEquals(Formula oldFormula, Function<Computable<Formula>, Computable<Formula>> formulaComputationFunction, Formula assertFormula) {
        Result<Formula> result = formulaComputationFunction.apply(Computable.of(oldFormula)).getResult();
        assertTrue(result.isPresent());
        assertNotEquals(oldFormula, result.get());
        assertEquals(assertFormula, result.get());
    }
}