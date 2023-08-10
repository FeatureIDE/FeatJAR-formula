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

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import de.featjar.base.io.IO;
import de.featjar.formula.io.dimacs.FormulaDimacsFormat;
import de.featjar.formula.test.CommonFormulas;
import java.io.IOException;
import java.io.OutputStream;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link de.featjar.formula.io.dimacs.FormulaDimacsFormat DIMACS} format.
 *
 * @author Sebastian Krieter
 */
public class DIMACSFormatTest {

    // TODO: something is wrong with the Dimacs Serializer, go figure :-)
    @Test
    public void DIMACS_123_n1n2n3() {
        test("123-n1n2n3");
    }

    @Test
    public void DIMACS_ABC_nAnBnC() {
        testException("ABC-nAnBnC");
    }

    @Test
    public void DIMACS_empty() {
        test("empty");
    }

    //        @Test
    //        public void DIMACS_empty_1() {
    //            test("empty-1");
    //        }
    //
    //        @Test
    //        public void DIMACS_empty_A() {
    //            test("empty-A");
    //        }
    //
    //        @Test
    //        public void DIMACS_empty_ABC() {
    //            test("empty-ABC");
    //        }
    //
    //        @Test
    //        public void DIMACS_empty_A2C() {
    //            test("empty-A2C");
    //        }

    @Test
    public void DIMACS_nA() {
        testException("nA");
    }

    @Test
    public void DIMACS_nAB() {
        testException("nAB");
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
        FormatTest.testSaveAndLoad(CommonFormulas.getFormula(name), name, new FormulaDimacsFormat());
    }

    private void testException(final String name) {
        RuntimeException wrapperException = assertThrows(
                RuntimeException.class,
                () -> {
                    try {
                        IO.save(
                                CommonFormulas.getFormula(name),
                                OutputStream.nullOutputStream(),
                                new FormulaDimacsFormat());
                    } catch (IOException e) {
                        e.printStackTrace();
                        fail(e);
                    }
                },
                "Formula is not in CNF");
        assertInstanceOf(IllegalArgumentException.class, wrapperException.getCause());
    }
}
