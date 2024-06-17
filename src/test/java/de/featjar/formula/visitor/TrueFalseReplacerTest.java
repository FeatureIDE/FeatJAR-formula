package de.featjar.formula.visitor;

import org.junit.jupiter.api.Test;

import static de.featjar.formula.structure.Expressions.*;
import de.featjar.formula.structure.IFormula;

import java.util.HashMap;
import java.util.Map;

public class TrueFalseReplacerTest {

    @Test
    void testNoReplacement() {
        IFormula oldFormula = or(and(literal("a"), literal("b")), and(literal("c"), literal("d")));
        VisitorTest.traverseAndAssertSameFormula(oldFormula, new TrueFalseReplacer(new HashMap<>()));
    }

    @Test
    void testReplacement() {
        IFormula oldFormula = or(and(literal("a"), literal("b")), and(literal("c"), literal("d")));
        IFormula newFormula = or(and(literal("a"), False), and(True, literal("d")));
        Map<String, Boolean> map = new HashMap<>();
        map.put("b", false);
        map.put("c", true);
        VisitorTest.traverseAndAssertFormulaEquals(oldFormula, new TrueFalseReplacer(map), newFormula);
    }
}