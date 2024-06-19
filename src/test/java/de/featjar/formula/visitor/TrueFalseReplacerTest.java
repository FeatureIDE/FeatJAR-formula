package de.featjar.formula.visitor;

import de.featjar.formula.structure.predicate.Literal;
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
        Map<Literal, Boolean> map = new HashMap<>();
        map.put(literal("b"), false);
        map.put(literal("c"), true);
        VisitorTest.traverseAndAssertFormulaEquals(oldFormula, new TrueFalseReplacer(map), newFormula);
    }

    @Test
    void testDeepReplacement() {
        IFormula oldFormula = or(
                and(
                        implies(literal("a"), literal("b")),
                        biImplies(literal("c"), literal("d"))),
                and(
                        biImplies(literal("d"), literal("e")),
                        implies(literal("f"), literal("g"))));
        IFormula newFormula = or(
                and(
                        implies(literal("a"), False),
                        biImplies(True, literal("d"))),
                and(
                        biImplies(literal("d"), literal("e")),
                        implies(literal("f"), True)));
        Map<Literal, Boolean> map = new HashMap<>();
        map.put(literal("b"), false);
        map.put(literal("c"), true);
        map.put(literal("g"), true);
        VisitorTest.traverseAndAssertFormulaEquals(oldFormula, new TrueFalseReplacer(map), newFormula);
    }
}