package de.featjar.formula.io;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;

import de.featjar.base.FeatJAR;
import de.featjar.base.io.input.FileInputMapper;
import de.featjar.formula.VariableMap;
import de.featjar.formula.assignment.BooleanAssignmentList;
import de.featjar.formula.assignment.BooleanSolution;
import de.featjar.formula.io.csv.BooleanAssignmentListCSVFormat;

public class BooleanAssignmentListCSVFormatParserTest {
	static BooleanAssignmentListCSVFormat csvFile;
	
	@BeforeAll
    public static void begin() {
        FeatJAR.testConfiguration().initialize();
        csvFile = new BooleanAssignmentListCSVFormat();
    }

    @AfterAll
    public static void end() {
        FeatJAR.deinitialize();
    }

     FileInputMapper csvSelect(String count) {
    	FileInputMapper file = null;
    	String relPath = "./src/testFixtures/resources/formats/csv/csv_";
    	relPath += count;
    	relPath += ".csv";
    	
    	try {
    		Path path = Path.of(relPath);
    		file = new FileInputMapper(path, StandardCharsets.UTF_8);
    	} catch (IOException e) {
    	
    	}
    	
    	return file;
    }
     
     
    	
    @Test
	void csvTest_EmptyFile() {
		var file = csvSelect("0");
		var result = csvFile.parse(file);
		assertTrue(result.isEmpty());
	}
	
    @Test
	void csvTest_Positive1() {
		var file = csvSelect("1");
		var result = csvFile.parse(file);
		
		assertEquals(result.get(), new BooleanAssignmentList(new VariableMap(Arrays.asList("A","B","C","D","E")), 
	    		Arrays.asList(new BooleanSolution(1,2,3,-4,-5), new BooleanSolution(1,-2,3,-4,-5), new BooleanSolution(-1,-2,3,-4,0), new BooleanSolution(1,2,3,4,5), new BooleanSolution(-1,-2,-3,4,5))));
	}
		
    @Test
	void csvTest_Positive2() {
		var file = csvSelect("2");
		var result = csvFile.parse(file);
		assertEquals(result.get(), new BooleanAssignmentList(new VariableMap(Arrays.asList("A","B","C")), 
	    		Arrays.asList(new BooleanSolution(1,2,3), new BooleanSolution(1,-2,-3), new BooleanSolution(1,-2,3))));
	}
	
    @Test
	void csvtest_Positive3() {
		var file = csvSelect("3");
		var result = csvFile.parse(file);
		assertEquals(result.get(), new BooleanAssignmentList(new VariableMap(Arrays.asList("A","B","C","D","E","F")), 
	    		Arrays.asList(new BooleanSolution(1,2,3,-4,-5,0), new BooleanSolution(1,-2,-3,4,5,0), new BooleanSolution(-1,-2,3,4,-5,0))));
	}
	
    @Test
	void csvTest_Nonsense() {
		var file = csvSelect("4");
		var result = csvFile.parse(file);
		assertTrue(result.hasProblems());
		assertTrue(result.getProblems().get(0).getMessage().contains("First column name must be Configuration"));
	}
	
    @Test
	void csvTest_ValueMismatch() {
		var file = csvSelect("5");
		var result = csvFile.parse(file);
		assertTrue(result.hasProblems());
		assertTrue(result.getProblems().get(0).getMessage().contains("Number of values"));
	}
	
    @Test
	void csvTest_MissingRowConfig() {
		var file = csvSelect("6");
		var result = csvFile.parse(file);
		assertTrue(result.hasProblems());
		assertTrue(result.getProblems().get(0).getMessage().contains("First value must be"));
	}
	
    @Test
	void csvTest_UnrecognisedValue() {
		var file = csvSelect("7");
		var result = csvFile.parse(file);
		assertTrue(result.hasProblems());
		assertTrue(result.getProblems().get(0).getMessage().contains("Unknown value"));
	}
	
    @Test
	void csvTest_VariableMismatch() {
		var file = csvSelect("8");
		var result = csvFile.parse(file);
		assertTrue(result.hasProblems());
		assertTrue(result.getProblems().get(0).getMessage().contains("Number of values"));
	}
}

