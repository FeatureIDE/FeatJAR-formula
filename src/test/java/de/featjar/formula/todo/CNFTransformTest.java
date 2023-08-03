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
package de.featjar.formula.todo;

public class CNFTransformTest {

    //    @Test
    //    public void testImplies() {
    //        testTransform(FormulaCreator.getFormula01());
    //    }
    //
    //    @Test
    //    public void testComplex() {
    //        testTransform(FormulaCreator.getFormula02());
    //    }
    //
    //    private void testTransform(final Expression expressionOrg) {
    //        final Expression expressionClone = Trees.clone(expressionOrg);
    //        final TermMap map = expressionOrg.getTermMap().orElseThrow();
    //        final TermMap mapClone = map.clone();
    //
    //        final ModelRepresentation rep = new ModelRepresentation(expressionOrg);
    //        final Expression expressionCNF = rep.get(FormulaComputation.CNF.fromFormula());
    //
    //        FormulaCreator.testAllAssignments(map, assignment -> {
    //            final Boolean orgEval =
    //                    (Boolean) Formulas.evaluate(expressionOrg, assignment).orElseThrow();
    //            final Boolean cnfEval =
    //                    (Boolean) Formulas.evaluate(expressionCNF, assignment).orElseThrow();
    //            assertEquals(orgEval, cnfEval, assignment.toString());
    //        });
    //        assertTrue(Trees.equals(expressionOrg, expressionClone));
    //        assertEquals(mapClone, map);
    //        assertEquals(mapClone, expressionOrg.getTermMap().get());
    //    }
    //
    //    @Test
    //    public void testKConfigReader() throws IOException {
    //        final Path modelFile = Paths.get("src/test/resources/kconfigreader/min-example.model");
    //        final Path dimacsFile = Paths.get("src/test/resources/kconfigreader/min-example.dimacs");
    //        final Expression expression = IO.load(modelFile, new KConfigReaderFormat()).orElseThrow();
    //
    //        ModelRepresentation rep = new ModelRepresentation(expression);
    //        IO.save(rep.get(FormulaComputation.CNF.fromFormula()), dimacsFile, new DIMACSFormat());
    //        Files.deleteIfExists(dimacsFile);
    //
    //        rep = new ModelRepresentation(expression);
    //        IO.save(rep.get(FormulaComputation.CNF.fromFormula(0)), dimacsFile, new DIMACSFormat());
    //        Files.deleteIfExists(dimacsFile);
    //
    //        rep = new ModelRepresentation(expression);
    //        IO.save(rep.get(FormulaComputation.CNF.fromFormula(100)), dimacsFile, new DIMACSFormat());
    //        Files.deleteIfExists(dimacsFile);
    //    }
}
