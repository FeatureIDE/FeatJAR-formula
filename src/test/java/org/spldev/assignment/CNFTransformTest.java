/* -----------------------------------------------------------------------------
 * Formula Lib - Library to represent and edit propositional formulas.
 * Copyright (C) 2021-2022  Sebastian Krieter
 * 
 * This file is part of Formula Lib.
 * 
 * Formula Lib is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Formula Lib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Formula Lib.  If not, see <https://www.gnu.org/licenses/>.
 * 
 * See <https://github.com/skrieter/formula> for further information.
 * -----------------------------------------------------------------------------
 */
package org.spldev.assignment;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.*;

import org.junit.jupiter.api.*;
import org.spldev.formula.*;
import org.spldev.formula.io.*;
import org.spldev.formula.io.dimacs.*;
import org.spldev.formula.structure.*;
import org.spldev.formula.structure.atomic.literal.*;
import org.spldev.util.io.*;
import org.spldev.util.io.format.*;
import org.spldev.util.tree.*;

public class CNFTransformTest {

	@Test
	public void testImplies() {
		testTransform(FormulaCreator.getFormula01());
	}

	@Test
	public void testComplex() {
		testTransform(FormulaCreator.getFormula02());
	}

	private void testTransform(final Formula formulaOrg) {
		final Formula formulaClone = Trees.cloneTree(formulaOrg);
		final VariableMap map = VariableMap.fromExpression(formulaOrg);
		final VariableMap mapClone = map.clone();

		final ModelRepresentation rep = new ModelRepresentation(formulaOrg);
		final Formula formulaCNF = rep.get(FormulaProvider.CNF.fromFormula());

		FormulaCreator.testAllAssignments(map, assignment -> {
			final Boolean orgEval = (Boolean) Formulas.evaluate(formulaOrg, assignment).orElseThrow();
			final Boolean cnfEval = (Boolean) Formulas.evaluate(formulaCNF, assignment).orElseThrow();
			assertEquals(orgEval, cnfEval, assignment.toString());
		});
		assertTrue(Trees.equals(formulaOrg, formulaClone));
		assertEquals(mapClone, map);
		assertEquals(mapClone, VariableMap.fromExpression(formulaOrg));
	}

	@Test
	public void testKConfigReader() throws IOException {
		final Path modelFile = Paths.get("src/test/resources/kconfigreader/min-example.model");
		final Path dimacsFile = Paths.get("src/test/resources/kconfigreader/min-example.dimacs");
		final Formula formula = FileHandler.load(modelFile, FormatSupplier.of(new KConfigReaderFormat())).orElseThrow();

		ModelRepresentation rep = new ModelRepresentation(formula);
		FileHandler.save(rep.get(FormulaProvider.CNF.fromFormula()), dimacsFile, new DIMACSFormat());
		Files.deleteIfExists(dimacsFile);

		rep = new ModelRepresentation(formula);
		FileHandler.save(rep.get(FormulaProvider.CNF.fromFormula(0)), dimacsFile, new DIMACSFormat());
		Files.deleteIfExists(dimacsFile);

		rep = new ModelRepresentation(formula);
		FileHandler.save(rep.get(FormulaProvider.CNF.fromFormula(100)), dimacsFile, new DIMACSFormat());
		Files.deleteIfExists(dimacsFile);
	}

}
