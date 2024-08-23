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
package de.featjar.formula;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.featjar.base.FeatJAR;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

public class FormulaCommandsTest {

    @Test
    void testConvertFormatCommand() throws IOException {
        String testFile =
                new String(Files.readAllBytes(Path.of("./src/test/resources/testConvertFormatCommand.dimacs")));
        Path outputFile = Files.createTempFile("featjar-test-convert", "");

        int exit = FeatJAR.run(("convert-format "
                        + "--input ../formula/src/testFixtures/resources/GPL/model.xml "
                        + "--format de.featjar.formula.io.dimacs.FormulaDimacsFormat "
                        + "--output " + outputFile.toString())
                .split("\\s+"));

        assertEquals(0, exit);
        assertEquals(testFile, new String(Files.readAllBytes(outputFile), StandardCharsets.UTF_8).trim());
    }

    @Test
    void testConvertCNFFormatCommand() throws IOException {
        String testFile =
                new String(Files.readAllBytes(Path.of("./src/test/resources/testConvertFormatCommand.dimacs")));
        Path outputFile = Files.createTempFile("featjar-test-convert-cnf", "");

        int exit = FeatJAR.run(("convert-cnf-format "
                        + "--input ../formula/src/testFixtures/resources/GPL/model.xml "
                        + "--format de.featjar.formula.io.dimacs.FormulaDimacsFormat "
                        + "--output " + outputFile.toString())
                .split("\\s+"));

        assertEquals(0, exit);
        assertEquals(testFile, new String(Files.readAllBytes(outputFile), StandardCharsets.UTF_8).trim());
    }

    @Test
    void testPrintCommand() throws IOException {
        String testFile = new String(Files.readAllBytes(Path.of("./src/test/resources/testPrintCommand")));
        Path outputFile = Files.createTempFile("featjar-test-print", "");

        int exit = FeatJAR.run(("print "
                        + "--input ../formula/src/testFixtures/resources/GPL/model.xml "
                        + "--tab [tab] "
                        + "--notation PREFIX "
                        + "--separator [separator] "
                        + "--format de.featjar.formula.io.textual.JavaSymbols "
                        + "--newline [newline] "
                        + "--enforce-parentheses "
                        + "--enquote-whitespace "
                        + "--output " + outputFile.toString())
                .split("\\s+"));

        assertEquals(0, exit);
        assertEquals(testFile, new String(Files.readAllBytes(outputFile), StandardCharsets.UTF_8).trim());
    }
}
