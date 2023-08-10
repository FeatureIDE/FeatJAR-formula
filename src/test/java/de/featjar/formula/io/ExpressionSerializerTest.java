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
package de.featjar.formula.io;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.featjar.base.tree.Trees;
import de.featjar.formula.io.textual.ExpressionSerializer;
import de.featjar.formula.io.textual.ExpressionSerializer.Notation;
import de.featjar.formula.io.textual.Symbols;
import de.featjar.formula.structure.formula.IFormula;
import de.featjar.formula.test.CommonFormulas;
import org.junit.jupiter.api.Test;

/**
 * Tests the {@link ExpressionSerializer}.
 *
 * @author Sebastian Krieter
 */
public class ExpressionSerializerTest {

    @Test
    public void Formula_ABC_nAnBnC() {
        final IFormula formula = CommonFormulas.getFormula("ABC-nAnBnC");
        final ExpressionSerializer s = new ExpressionSerializer();
        s.setSymbols(Symbols.JAVA);
        s.setNotation(Notation.INFIX);
        assertEquals(
                "(A || B || C) && (!A || (!B || !C))",
                Trees.traverse(formula, s).get());
        s.setNotation(Notation.PREFIX);
        assertEquals(
                "&&(||(A B C) ||(!(A) ||(!(B) !(C))))",
                Trees.traverse(formula, s).get());
        s.setNotation(Notation.POSTFIX);
        assertEquals("(A B C)|| (A! (B! C!)||)||&&", Trees.traverse(formula, s).get());

        s.setSymbols(Symbols.TEXTUAL);
        s.setNotation(Notation.INFIX);
        assertEquals(
                "(A or B or C) and (not A or (not B or not C))",
                Trees.traverse(formula, s).get());
        s.setNotation(Notation.PREFIX);
        assertEquals(
                "and(or(A B C) or(not(A) or(not(B) not(C))))",
                Trees.traverse(formula, s).get());
        s.setNotation(Notation.POSTFIX);
        assertEquals(
                "(A B C)or (A not (B not C not)or)or and",
                Trees.traverse(formula, s).get());
    }

    @Test
    public void Formula_nAB() {
        final IFormula formula = CommonFormulas.getFormula("nAB");
        final ExpressionSerializer s = new ExpressionSerializer();
        s.setSymbols(Symbols.JAVA);
        s.setNotation(Notation.INFIX);
        assertEquals("!A || B", Trees.traverse(formula, s).get());
        s.setNotation(Notation.PREFIX);
        assertEquals("||(!(A) B)", Trees.traverse(formula, s).get());
        s.setNotation(Notation.POSTFIX);
        assertEquals("A! B||", Trees.traverse(formula, s).get());

        s.setSymbols(Symbols.TEXTUAL);
        s.setNotation(Notation.INFIX);
        assertEquals("not A or B", Trees.traverse(formula, s).get());
        s.setNotation(Notation.PREFIX);
        assertEquals("or(not(A) B)", Trees.traverse(formula, s).get());
        s.setNotation(Notation.POSTFIX);
        assertEquals("A not B or", Trees.traverse(formula, s).get());
    }
}
