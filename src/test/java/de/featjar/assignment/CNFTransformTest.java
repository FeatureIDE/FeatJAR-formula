/* -----------------------------------------------------------------------------
 * formula - Propositional and first-order formulas
 * Copyright (C) 2020 Sebastian Krieter
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
 * See <https://github.com/FeatJAR/formula> for further information.
 * -----------------------------------------------------------------------------
 */
package de.featjar.assignment;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.*;

import de.featjar.formula.ModelRepresentation;
import de.featjar.formula.io.KConfigReaderFormat;
import de.featjar.formula.io.dimacs.DIMACSFormat;
import de.featjar.formula.structure.Formula;
import de.featjar.formula.structure.FormulaProvider;
import de.featjar.formula.structure.Formulas;
import de.featjar.formula.structure.atomic.literal.VariableMap;
import de.featjar.util.io.IO;
import de.featjar.util.tree.Trees;
import org.junit.jupiter.api.*;
import de.featjar.formula.*;
import de.featjar.formula.io.*;
import de.featjar.formula.io.dimacs.*;
import de.featjar.formula.structure.*;
import de.featjar.formula.structure.atomic.literal.*;
import de.featjar.util.io.*;
import de.featjar.util.io.format.*;
import de.featjar.util.tree.*;

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
		final VariableMap map = formulaOrg.getVariableMap().orElseThrow();
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
		assertEquals(mapClone, formulaOrg.getVariableMap().get());
	}

	@Test
	public void testKConfigReader() throws IOException {
		final Path modelFile = Paths.get("src/test/resources/kconfigreader/min-example.model");
		final Path dimacsFile = Paths.get("src/test/resources/kconfigreader/min-example.dimacs");
		final Formula formula = IO.load(modelFile, new KConfigReaderFormat()).orElseThrow();

		ModelRepresentation rep = new ModelRepresentation(formula);
		IO.save(rep.get(FormulaProvider.CNF.fromFormula()), dimacsFile, new DIMACSFormat());
		Files.deleteIfExists(dimacsFile);

		rep = new ModelRepresentation(formula);
		IO.save(rep.get(FormulaProvider.CNF.fromFormula(0)), dimacsFile, new DIMACSFormat());
		Files.deleteIfExists(dimacsFile);

		rep = new ModelRepresentation(formula);
		IO.save(rep.get(FormulaProvider.CNF.fromFormula(100)), dimacsFile, new DIMACSFormat());
		Files.deleteIfExists(dimacsFile);
	}

}
