/*
 * Copyright (C) 2023 FeatJAR-Development-Team
 *
 * This file is part of FeatJAR-formula.
 *
 * formula is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3.0 of the License,
 * or (at your option) any later version.
 *
 * formula is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with formula. If not, see <https://www.gnu.org/licenses/>.
 *
 * See <https://github.com/FeatJAR> for further information.
 */
package de.featjar.formula.structure;

import static de.featjar.formula.structure.Expressions.*;
import static org.junit.jupiter.api.Assertions.*;

import de.featjar.formula.analysis.value.ValueAssignment;
import de.featjar.formula.structure.formula.connective.And;
import org.junit.jupiter.api.Test;

class ExpressionsTest {
    @Test
    void _true() {
        assertEquals("true", True.toString());
        assertEquals("true", True.printParseable());
        assertTrue((Boolean) True.evaluate());
        assertTrue((Boolean) or(literal("x"), True).evaluate());
        assertNull(and(literal("x"), True).evaluate());
    }

    @Test
    void _false() {
        assertEquals("false", False.toString());
        assertEquals("false", False.printParseable());
        assertFalse((Boolean) False.evaluate());
        assertFalse((Boolean) and(literal("x"), False).evaluate());
        assertNull(or(literal("x"), False).evaluate());
    }

    @Test
    void andOrNotLiteral() {
        And and = and(literal("x"), or(literal("y"), not(literal("x"))));
        assertEquals("and(+, or)", and.toString());
        assertEquals("x (y x-)|&", and.printParseable());
        assertFalse((Boolean) and.evaluate(new ValueAssignment("x", false)));
        assertNull(and.evaluate(new ValueAssignment("x", true)));
        assertTrue((Boolean) and.evaluate(new ValueAssignment("x", true, "y", true)));
        assertFalse((Boolean) and.evaluate(new ValueAssignment("x", true, "y", false)));
        assertThrows(NullPointerException.class, and::evaluate); // TODO: this throws, but it should return null!
    }

    // TODO: tests for other operators
}
