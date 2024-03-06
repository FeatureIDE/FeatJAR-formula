/*
 * Copyright (C) 2024 FeatJAR-Development-Team
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
 * See <https://github.com/FeatureIDE/FeatJAR-formula> for further information.
 */
package de.featjar.formula.structure;

import static de.featjar.formula.structure.Expressions.False;
import static de.featjar.formula.structure.Expressions.True;
import static de.featjar.formula.structure.Expressions.and;
import static de.featjar.formula.structure.Expressions.literal;
import static de.featjar.formula.structure.Expressions.not;
import static de.featjar.formula.structure.Expressions.or;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.featjar.formula.analysis.assignment.Assignment;
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
        assertFalse((Boolean) and.evaluate(new Assignment("x", false)));
        assertNull(and.evaluate(new Assignment("x", true)));
        assertTrue((Boolean) and.evaluate(new Assignment("x", true, "y", true)));
        assertFalse((Boolean) and.evaluate(new Assignment("x", true, "y", false)));
        assertNull(and.evaluate());
    }

    // TODO: tests for other operators
}
