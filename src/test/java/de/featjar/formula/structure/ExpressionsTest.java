package de.featjar.formula.structure;

import de.featjar.formula.analysis.value.ValueAssignment;
import de.featjar.formula.structure.formula.connective.And;
import de.featjar.formula.structure.formula.predicate.Literal;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static de.featjar.formula.structure.Expressions.*;

class ExpressionsTest {
    @Test
    void _true() {
        assertEquals("true", True.toString());
        // TODO: this throws because the serializer does not support it yet, it should not throw!
        assertThrows(ClassCastException.class, () -> assertEquals("", True.printParseable()));
        assertTrue((Boolean) True.evaluate());
        assertTrue((Boolean) or(literal("x"), True).evaluate());
        assertNull(and(literal("x"), True).evaluate());
    }

    @Test
    void _false() {
        assertEquals("false", False.toString());
        // TODO: this throws because the serializer does not support it yet, it should not throw!
        assertThrows(ClassCastException.class, () -> assertEquals("", False.printParseable()));
        assertFalse((Boolean) False.evaluate());
        assertFalse((Boolean) and(literal("x"), False).evaluate());
        assertNull(or(literal("x"), False).evaluate());
    }

    @Test
    void andOrNotLiteral() {
        And and = and(literal("x"), or(literal("y"), not(literal("x"))));
        assertEquals("and(+, or)", and.toString());
        assertEquals("x & (y | -x)", and.printParseable());
        assertFalse((Boolean) and.evaluate(new ValueAssignment("x", false)));
        assertNull(and.evaluate(new ValueAssignment("x", true)));
        assertTrue((Boolean) and.evaluate(new ValueAssignment("x", true, "y", true)));
        assertFalse((Boolean) and.evaluate(new ValueAssignment("x", true, "y", false)));
        assertThrows(NullPointerException.class, and::evaluate); // TODO: this throws, but it should return null!
    }

    // TODO: tests for other operators
}