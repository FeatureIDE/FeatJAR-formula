/*
 * Copyright (C) 2023 Sebastian Krieter, Elias Kuiter
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

import static de.featjar.io.FormatTest.testSave;
import static org.junit.jupiter.api.Assertions.fail;

import de.featjar.formula.io.textual.ACTSFormat;
import de.featjar.formula.structure.Formula;
import de.featjar.formula.structure.atomic.literal.Literal;
import de.featjar.formula.structure.atomic.literal.VariableMap;
import de.featjar.formula.structure.compound.And;
import de.featjar.formula.structure.compound.Not;
import de.featjar.formula.structure.compound.Or;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link ACTSFormat} format.
 *
 * @author Sebastian Krieter
 */
public class ACTSFormatTest {

    @Test
    public void Formula_ABC_nAnBnC() {
        test("ABC-nAnBnC");
    }

    private static void test(String name) {
        testSave(getFormula(name), name, new ACTSFormat());
    }

    private static Formula getFormula(String name) {
        switch (name) {
            case "faulty": {
                return null;
            }
            case "ABC-nAnBnC": {
                final VariableMap map = new VariableMap();
                final Literal a = map.createLiteral("A");
                final Literal b = map.createLiteral("B");
                final Literal c = map.createLiteral("C");
                return new And(
                        new Or(a.cloneNode(), new Or(b.cloneNode(), c.cloneNode())),
                        new Or(new Not(a.cloneNode()), new Or(new Not(b.cloneNode()), new Not(c.cloneNode()))));
            }
            case "nA": {
                final VariableMap map = new VariableMap();
                final Literal a = map.createLiteral("A");
                return new Not(a.cloneNode());
            }
            case "nAB": {
                final VariableMap map = new VariableMap();
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
