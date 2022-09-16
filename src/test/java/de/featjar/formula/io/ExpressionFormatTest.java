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

import static org.junit.jupiter.api.Assertions.fail;

import de.featjar.formula.io.textual.ExpressionFormat;
import de.featjar.formula.structure.Expression;
import de.featjar.formula.structure.formula.predicate.Literal;
import de.featjar.formula.structure.map.TermMap;
import de.featjar.formula.structure.formula.connective.And;
import de.featjar.formula.structure.formula.connective.Not;
import de.featjar.formula.structure.formula.connective.Or;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link ExpressionFormat Formula} format.
 *
 * @author Sebastian Krieter
 */
public class ExpressionFormatTest {

    @Test
    public void Formula_ABC_nAnBnC() {
        test("ABC-nAnBnC");
    }

    @Test
    public void Formula_empty() {
        test("faulty");
    }

    @Test
    public void Formula_nA() {
        test("nA");
    }

    @Test
    public void Formula_nAB() {
        test("nAB");
    }

    private static void test(String name) {
        FormatTest.testLoadAndSave(getFormula(name), name, new ExpressionFormat());
    }

    private static Expression getFormula(String name) {
        switch (name) {
            case "faulty": {
                return null;
            }
            case "ABC-nAnBnC": {
                final TermMap map = new TermMap();
                final Literal a = map.createLiteral("A");
                final Literal b = map.createLiteral("B");
                final Literal c = map.createLiteral("C");
                return new And(
                        new Or(a.cloneNode(), new Or(b.cloneNode(), c.cloneNode())),
                        new Or(new Not(a.cloneNode()), new Or(new Not(b.cloneNode()), new Not(c.cloneNode()))));
            }
            case "nA": {
                final TermMap map = new TermMap();
                final Literal a = map.createLiteral("A");
                return new Not(a.cloneNode());
            }
            case "nAB": {
                final TermMap map = new TermMap();
                final Literal a = map.createLiteral("A");
                final Literal b = map.createLiteral("B");
                return new Or(new Not(a.cloneNode()), b.cloneNode());
            }
            default:
                fail(name);
                return null;
        }
    }
}
