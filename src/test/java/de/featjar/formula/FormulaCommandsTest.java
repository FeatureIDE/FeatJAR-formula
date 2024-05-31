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

import de.featjar.base.ProcessOutput;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FormulaCommandsTest {
    private static final String jarString = "java -jar build/libs/formula-0.1.1-SNAPSHOT-all.jar";

    @Test
    void testConvertFormatCommand() throws IOException {
        String testFile =
                new String(Files.readAllBytes(Path.of("./src/test/resources/testConvertFormatCommand.dimacs")));
        ProcessOutput output = ProcessOutput.runProcess(
                jarString
                        + " convert-format --input ../formula/src/testFixtures/resources/GPL/model.xml --format de.featjar.formula.io.dimacs.FormulaDimacsFormat");
        Assertions.assertTrue(output.getErrorString().isBlank());
        Assertions.assertEquals(testFile.trim(), output.getOutputString().trim().substring(20));
    }

    @Test
    void testConvertCNFFormatCommand() throws IOException {
        String testFile =
                new String(Files.readAllBytes(Path.of("./src/test/resources/testConvertFormatCommand.dimacs")));
        ProcessOutput output = ProcessOutput.runProcess(
                jarString
                        + " convert-cnf-format --input ../formula/src/testFixtures/resources/GPL/model.xml --format de.featjar.formula.io.dimacs.FormulaDimacsFormat");
        Assertions.assertTrue(output.getErrorString().isBlank());
        Assertions.assertEquals(testFile.trim(), output.getOutputString().trim().substring(20));
    }

    @Test
    void testPrintCommand() throws IOException {
        String testFile = new String(Files.readAllBytes(Path.of("./src/test/resources/testPrintCommand")));
        ProcessOutput output = ProcessOutput.runProcess(
                jarString
                        + " print --input ../formula/src/testFixtures/resources/GPL/model.xml --tab [tab] --notation PREFIX --separator [separator] --format de.featjar.formula.io.textual.JavaSymbols --newline [newline] --enforce-parentheses --enquote-whitespace");
        Assertions.assertTrue(output.getErrorString().isBlank());
        Assertions.assertEquals(testFile.trim(), output.getOutputString().trim().substring(20));
    }
}
