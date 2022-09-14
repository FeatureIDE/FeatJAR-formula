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
package de.featjar.io;

import static de.featjar.io.FormatTest.testLoadAndSave;
import static org.junit.jupiter.api.Assertions.fail;

import de.featjar.formula.io.dimacs.DIMACSFormat;
import de.featjar.formula.structure.Expression;
import de.featjar.formula.structure.formula.predicate.Literal;
import de.featjar.formula.tmp.TermMap;
import de.featjar.formula.structure.formula.connective.And;
import de.featjar.formula.structure.formula.connective.Or;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link DIMACSFormat DIMACS} format.
 *
 * @author Sebastian Krieter
 */
public class DIMACSFormatTest {

    @Test
    public void DIMACS_123_n1n2n3() {
        test("123-n1n2n3");
    }

    @Test
    public void DIMACS_ABC_nAnBnC() {
        test("ABC-nAnBnC");
    }

    @Test
    public void DIMACS_empty() {
        test("empty");
    }

    @Test
    public void DIMACS_empty_1() {
        test("empty-1");
    }

    @Test
    public void DIMACS_empty_A() {
        test("empty-A");
    }

    @Test
    public void DIMACS_empty_ABC() {
        test("empty-ABC");
    }

    @Test
    public void DIMACS_empty_A2C() {
        test("empty-A2C");
    }

    @Test
    public void DIMACS_nA() {
        test("nA");
    }

    @Test
    public void DIMACS_nAB() {
        test("nAB");
    }

    @Test
    public void DIMACS_faulty() {
        test("faulty");
    }

    @Test
    public void DIMACS_void() {
        test("void");
    }

    private static void test(String name) {
        testLoadAndSave(getFormula(name), name, new DIMACSFormat());
    }

    private static Expression getFormula(String name) {
        switch (name) {
            case "faulty": {
                return null;
            }
            case "void": {
                return new And(new Or());
            }
            case "123-n1n2n3": {
                final TermMap map = new TermMap();
                final Literal a = map.createLiteral("1");
                final Literal b = map.createLiteral("2");
                final Literal c = map.createLiteral("3");
                return new And(
                        new Or(a.cloneNode(), b.cloneNode(), c.cloneNode()), new Or(a.invert(), b.invert(), c.invert()));
            }
            case "ABC-nAnBnC": {
                final TermMap map = new TermMap();
                final Literal a = map.createLiteral("A");
                final Literal b = map.createLiteral("B");
                final Literal c = map.createLiteral("C");
                return new And(
                        new Or(a.cloneNode(), b.cloneNode(), c.cloneNode()), new Or(a.invert(), b.invert(), c.invert()));
            }
            case "empty-ABC": {
                final TermMap map = new TermMap();
                map.addBooleanVariable("A");
                map.addBooleanVariable("B");
                map.addBooleanVariable("C");
                return new And();
            }
            case "empty-A2C": {
                final TermMap map = new TermMap();
                map.addBooleanVariable("A");
                map.addBooleanVariable("2");
                map.addBooleanVariable("C");
                return new And();
            }
            case "empty-A": {
                final TermMap map = new TermMap();
                map.addBooleanVariable("A");
                return new And();
            }
            case "empty-1": {
                final TermMap map = new TermMap();
                map.addBooleanVariable("1");
                return new And();
            }
            case "empty": {
                return new And();
            }
            case "nA": {
                final TermMap map = new TermMap();
                final Literal a = new Literal(map.addBooleanVariable("A"));
                return new And(new Or(a.invert()));
            }
            case "nAB": {
                final TermMap map = new TermMap("A", "B");
                final Literal a = map.createLiteral("A");
                final Literal b = map.createLiteral("B");
                return new And(new Or(a.invert(), b.cloneNode()));
            }
            default:
                fail(name);
                return null;
        }
    }
}
