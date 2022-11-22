/*
 * Copyright (C) 2022 Sebastian Krieter, Elias Kuiter
 *
 * This file is part of formula.
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
package de.featjar.formula.io;

import static de.featjar.formula.structure.Expressions.*;
import static org.junit.jupiter.api.Assertions.fail;

import de.featjar.formula.io.dimacs.DIMACSFormulaFormat;
import de.featjar.formula.structure.formula.Formula;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link de.featjar.formula.io.dimacs.DIMACSFormulaFormat DIMACS} format.
 *
 * @author Sebastian Krieter
 */
public class DIMACSFormatTest {

    // TODO: something is wrong with the Dimacs Serializer, go figure :-)
//    @Test
//    public void DIMACS_123_n1n2n3() {
//        test("123-n1n2n3");
//    }
//
//    @Test
//    public void DIMACS_ABC_nAnBnC() {
//        test("ABC-nAnBnC");
//    }
//
//    @Test
//    public void DIMACS_empty() {
//        test("empty");
//    }
//
//    @Test
//    public void DIMACS_empty_1() {
//        test("empty-1");
//    }
//
//    @Test
//    public void DIMACS_empty_A() {
//        test("empty-A");
//    }
//
//    @Test
//    public void DIMACS_empty_ABC() {
//        test("empty-ABC");
//    }
//
//    @Test
//    public void DIMACS_empty_A2C() {
//        test("empty-A2C");
//    }
//
//    @Test
//    public void DIMACS_nA() {
//        test("nA");
//    }
//
//    @Test
//    public void DIMACS_nAB() {
//        test("nAB");
//    }
//
//    @Test
//    public void DIMACS_faulty() {
//        test("faulty");
//    }
//
//    @Test
//    public void DIMACS_void() {
//        test("void");
//    }

    private static void test(String name) {
        FormatTest.testLoadAndSave(getFormula(name), name, new DIMACSFormulaFormat());
    }

    private static Formula getFormula(String name) {
        switch (name) {
            case "faulty": {
                return null;
            }
            case "void": {
                return and(or());
            }
            case "123-n1n2n3": {
                return and(
                        or(literal("1"), literal("2"), literal("3")),
                        or(literal(false, "1"), literal(false, "2"), literal(false, "3")));
            }
            case "ABC-nAnBnC": {
                return and(
                        or(literal("a"), literal("b"), literal("c")),
                        or(literal(false, "a"), literal(false, "b"), literal(false, "c")));
            }
            case "empty": {
                return and();
            }
            case "nA": {
                return and(or(literal("a").invert()));
            }
            case "nAB": {
                return and(or(literal("a").invert(), literal("b")));
            }
            default:
                fail(name);
                return null;
        }
    }
}
