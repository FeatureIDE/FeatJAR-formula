package de.featjar.formula.structure;

import static de.featjar.formula.structure.Expressions.*;
import static org.junit.jupiter.api.Assertions.*;

import de.featjar.base.data.Sets;
import de.featjar.formula.analysis.value.ValueAssignment;
import de.featjar.formula.structure.formula.IFormula;
import de.featjar.formula.structure.term.ITerm;
import de.featjar.formula.structure.term.value.Constant;
import de.featjar.formula.structure.term.value.Variable;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class ExpressionTest {
    IFormula formula = implies(literal("a"), False);
    ITerm term = integerAdd(constant(42L), variable("x", Long.class));

    @Test
    void getName() {
        assertEquals("implies", formula.getName());
        assertEquals("+", term.getName());
    }

    @Test
    void getType() {
        assertEquals(Boolean.class, formula.getType());
        assertEquals(Long.class, term.getType());
    }

    @Test
    void evaluate() {
        assertEquals(true, formula.evaluate(new ValueAssignment("a", false)));
        assertEquals(false, formula.evaluate(new ValueAssignment("a", true)));
        assertThrows(NullPointerException.class, () -> formula.evaluate()); // TODO: this should not happen, fix this!
        assertEquals(43L, term.evaluate(new ValueAssignment("x", 1L)));
        assertNull(term.evaluate(new ValueAssignment("x", null)));
        assertNull(term.evaluate());
    }

    @Test
    void getChildrenType() {
        assertEquals(Boolean.class, formula.getChildrenType());
        assertEquals(Long.class, term.getChildrenType());
    }

    @Test
    void getChildrenValidator() {
        assertTrue(formula.getChildValidator().test(new Variable("x")));
        assertFalse(formula.getChildValidator().test(new Variable("x", Long.class)));
        assertFalse(term.getChildValidator().test(new Variable("x")));
        assertTrue(term.getChildValidator().test(new Variable("x", Long.class)));
    }

    @Test
    void getVariableStream() {
        assertEquals(List.of(new Variable("a")), formula.getVariableStream().collect(Collectors.toList()));
        assertEquals(
                List.of(new Variable("x", Long.class)), term.getVariableStream().collect(Collectors.toList()));
    }

    @Test
    void getVariables() {
        assertEquals(List.of(new Variable("a")), formula.getVariables());
        assertEquals(List.of(new Variable("x", Long.class)), term.getVariables());
    }

    @Test
    void getVariableNames() {
        assertEquals(Sets.of("a"), formula.getVariableNames());
        assertEquals(Sets.of("x"), term.getVariableNames());
    }

    @Test
    void getConstantStream() {
        assertEquals(List.of(), formula.getConstantStream().collect(Collectors.toList()));
        assertEquals(
                List.of(new Constant(42L, Long.class)), term.getConstantStream().collect(Collectors.toList()));
    }

    @Test
    void getConstants() {
        assertEquals(List.of(), formula.getConstants());
        assertEquals(List.of(new Constant(42L, Long.class)), term.getConstants());
    }

    @Test
    void getConstantValues() {
        assertEquals(List.of(), formula.getConstantValues());
        assertEquals(List.of(42L), term.getConstantValues());
    }

    @Test
    void printParseable() {
        // TODO
    }
}
