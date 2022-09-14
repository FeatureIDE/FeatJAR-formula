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
package de.featjar.assignment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.featjar.formula.structure.Expression;
import de.featjar.formula.tmp.Formulas;
import de.featjar.formula.tmp.TermMap;
import de.featjar.base.tree.Trees;
import org.junit.jupiter.api.Test;

public class DNFTransformTest {

    @Test
    public void testImplies() {
        testTransform(FormulaCreator.getFormula01());
    }

    @Test
    public void testComplex() {
        testTransform(FormulaCreator.getFormula02());
    }

    private void testTransform(final Expression expressionOrg) {
        final Expression expressionClone = Trees.clone(expressionOrg);
        final TermMap map = expressionOrg.getTermMap().orElseThrow();
        final TermMap mapClone = map.clone();

        final ModelRepresentation rep = new ModelRepresentation(expressionOrg);
        final Expression expressionDNF = rep.get(FormulaComputation.DNF.fromFormula());

        FormulaCreator.testAllAssignments(map, assignment -> {
            final Boolean orgEval =
                    (Boolean) Formulas.evaluate(expressionOrg, assignment).orElseThrow();
            final Boolean dnfEval =
                    (Boolean) Formulas.evaluate(expressionDNF, assignment).orElseThrow();
            assertEquals(orgEval, dnfEval, assignment.toString());
        });
        assertTrue(Trees.equals(expressionOrg, expressionClone));
        assertEquals(mapClone, map);
        assertEquals(mapClone, expressionOrg.getTermMap().get());
    }
}
