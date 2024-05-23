package de.featjar.formula;

import de.featjar.base.FeatJAR;
import de.featjar.base.ProcessOutput;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FormulaCommandsTest {
    private static final String jarString = "java -jar build/libs/formula-0.1.1-SNAPSHOT-all.jar";

    @Test
    void testConvertFormatCommand() throws IOException {
        // TODO: Write test
        System.out.println("Testing ConvertFormatCommand");
        //String testFile = new String(Files.readAllBytes(Path.of("./src/test/java/de/featjar/res/testConvertFormatCommand.dimacs")));
        FeatJAR.main("convert-format --input ../formula/src/testFixtures/resources/GPL/model.xml --format de.featjar.feature.model.io.uvl.UVLFormulaFormat".split(" "));
        //ProcessOutput output = runProcess(sat4jstring + " convert-format-sat4j --input ../formula/src/testFixtures/resources/GPL/model.xml --format de.featjar.formula.io.xml.XMLFeatureModelFormulaFormat");
        //Assertions.assertTrue(output.errorString.isBlank());
        //Assertions.assertEquals(output.outputString.trim(), testFile.trim());
    }

    @Test
    void testConvertCNFFormatCommand() throws IOException {
        System.out.println("Testing ConvertCNFFormatCommand");
        String testFile = new String(Files.readAllBytes(Path.of("./src/test/resources/testConvertFormatCommand.dimacs")));
        ProcessOutput output = ProcessOutput.runProcess(jarString + " convert-cnf-format --input ../formula/src/testFixtures/resources/GPL/model.xml --format de.featjar.formula.io.dimacs.FormulaDimacsFormat");
        Assertions.assertTrue(output.getErrorString().isBlank());
        Assertions.assertEquals(testFile.trim(), output.getOutputString().trim().substring(20));
    }

    @Test
    void testPrintCommand() throws IOException {
        System.out.println("Testing PrintCommand");
        String testFile = new String(Files.readAllBytes(Path.of("./src/test/resources/testPrintCommand")));
        ProcessOutput output = ProcessOutput.runProcess(jarString + " print --input ../formula/src/testFixtures/resources/GPL/model.xml --tab [tab] --notation PREFIX --separator [separator] --format de.featjar.formula.io.textual.JavaSymbols --newline [newline] --enforce-parentheses --enquote-whitespace");
        output.printOutput();
        Assertions.assertTrue(output.getErrorString().isBlank());
        Assertions.assertEquals(testFile.trim(), output.getOutputString().trim().substring(20));
    }
}
