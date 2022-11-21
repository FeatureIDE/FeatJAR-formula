package de.featjar.formula.structure;

import de.featjar.formula.analysis.value.ValueAssignment;
import de.featjar.formula.structure.formula.Formula;
import de.featjar.formula.structure.term.Term;
import de.featjar.formula.structure.term.value.Constant;
import de.featjar.formula.structure.term.value.Variable;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static de.featjar.formula.structure.Expressions.*;
import static org.junit.jupiter.api.Assertions.*;

class ExpressionTest {
    Formula formula = implies(literal("a"), False);
    Term term = integerAdd(constant(42L), variable("x", Long.class));

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
        assertThrows(NullPointerException.class, () -> formula.evaluate()); // todo: this should not happen, fix this!
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
        assertTrue(formula.getChildrenValidator().test(new Variable("x")));
        assertFalse(formula.getChildrenValidator().test(new Variable("x", Long.class)));
        assertFalse(term.getChildrenValidator().test(new Variable("x")));
        assertTrue(term.getChildrenValidator().test(new Variable("x", Long.class)));
    }

    @Test
    void getVariableStream() {
        assertEquals(List.of(new Variable("a")), formula.getVariableStream().collect(Collectors.toList()));
        assertEquals(List.of(new Variable("x", Long.class)), term.getVariableStream().collect(Collectors.toList()));
    }

    @Test
    void getVariables() {
        assertEquals(List.of(new Variable("a")), formula.getVariables());
        assertEquals(List.of(new Variable("x", Long.class)), term.getVariables());
    }

    @Test
    void getVariableNames() {
        assertEquals(List.of("a"), formula.getVariableNames());
        assertEquals(List.of("x"), term.getVariableNames());
    }

    @Test
    void getConstantStream() {
        assertEquals(List.of(), formula.getConstantStream().collect(Collectors.toList()));
        assertEquals(List.of(new Constant(42L, Long.class)), term.getConstantStream().collect(Collectors.toList()));
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
        // todo
    }
}