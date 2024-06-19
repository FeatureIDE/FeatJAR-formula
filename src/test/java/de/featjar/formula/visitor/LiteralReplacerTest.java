package de.featjar.formula.visitor;

import de.featjar.formula.structure.IFormula;
import de.featjar.formula.structure.predicate.Literal;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static de.featjar.formula.structure.Expressions.*;
import static de.featjar.formula.structure.Expressions.literal;

public class LiteralReplacerTest {
    @Test
    void testNoReplacement() {
        IFormula oldFormula = or(and(literal("a"), literal("b")), and(literal("c"), literal("d")));
        VisitorTest.traverseAndAssertSameFormula(oldFormula, new LiteralReplacer(new HashMap<>()));
    }

    @Test
    void testReplacement() {
        IFormula oldFormula = or(and(literal("a"), literal("b")), and(literal(false, "c"), literal("d")));
        IFormula newFormula = or(and(literal("a"), literal(false, "b")), and(literal(true, "x"), literal("d")));
        Map<Literal, Literal> map = new HashMap<>();
        map.put(literal(true, "b"), literal(false, "b"));
        map.put(literal(false, "c"), literal(true, "x"));
        map.put(literal(false, "d"), literal(true, "x"));
        VisitorTest.traverseAndAssertFormulaEquals(oldFormula, new LiteralReplacer(map), newFormula);
    }

    @Test
    void testDeepReplacement() {
        IFormula oldFormula = or(
                and(
                        implies(literal("a"), literal(false, "b")),
                        biImplies(literal(false, "c"), literal("d"))),
                and(
                        biImplies(literal(false, "d"), literal("e")),
                        implies(literal("f"), literal(false, "g"))));
        IFormula newFormula = or(
                and(
                        implies(literal("a"), literal(true, "b")),
                        biImplies(literal(true, "x"), literal("d"))),
                and(
                        biImplies(literal(false, "d"), literal(false, "y")),
                        implies(literal("f"), literal(false, "g"))));
        Map<Literal, Literal> map = new HashMap<>();
        map.put(literal(false, "b"), literal(true, "b"));
        map.put(literal(false, "c"), literal(true, "x"));
        map.put(literal(true, "e"), literal(false, "y"));
        map.put(literal(false, "f"), literal(true, "z"));
        VisitorTest.traverseAndAssertFormulaEquals(oldFormula, new LiteralReplacer(map), newFormula);
    }
}
