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

import de.featjar.base.computation.Computations;
import de.featjar.formula.analysis.bool.BooleanAssignmentSpace;
import de.featjar.formula.analysis.bool.BooleanAssignmentSpaceComputation;
import de.featjar.formula.analysis.bool.BooleanRepresentationComputation;
import de.featjar.formula.io.binary.BooleanAssignmentSpaceBinaryFormat;
import de.featjar.formula.test.CommonFormulas;
import de.featjar.formula.transformer.ComputeCNFFormula;
import de.featjar.formula.transformer.ComputeNNFFormula;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link ExpressionFormat Formula} format.
 *
 * @author Sebastian Krieter
 */
public class BinaryFormatTest {

    @Test
    public void Formula_ABC_nAnBnC() {
        test("ABC-nAnBnC");
    }

    @Test
    public void Formula_nA() {
        test("nA");
    }

    @Test
    public void Formula_nAB() {
        test("nAB");
    }

    private static void test(String name) {
        final BooleanAssignmentSpace assignmentSpace = Computations.of(CommonFormulas.getFormula(name))
                .map(ComputeNNFFormula::new)
                .map(ComputeCNFFormula::new)
                .map(BooleanRepresentationComputation::new)
                .map(BooleanAssignmentSpaceComputation::new)
                .compute();

        FormatTest.testSaveAndLoad(assignmentSpace, name, new BooleanAssignmentSpaceBinaryFormat());
    }
}
