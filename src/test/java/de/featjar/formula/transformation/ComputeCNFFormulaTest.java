/*
 * Copyright (C) 2023 Sebastian Krieter, Elias Kuiter
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
package de.featjar.formula.transformation;

import static de.featjar.formula.structure.Expressions.*;
import static org.junit.jupiter.api.Assertions.*;

import de.featjar.base.FeatJAR;
import de.featjar.base.computation.Computations;
import de.featjar.base.computation.IComputation;
import de.featjar.base.io.IO;
import de.featjar.formula.io.FormulaFormats;
import de.featjar.formula.structure.formula.IFormula;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

class ComputeCNFFormulaTest {
    public static final Path fmDirectory = Paths.get("src/test/resources/testFeatureModels");

    IComputation<IFormula> toCNF(IComputation<IFormula> formula) {
        return formula.map(ComputeNNFFormula::new).map(ComputeCNFFormula::new);
    }

    @Test
    public void doesNothing() {
        TransformationTest.traverseAndAssertSameFormula(
                and(or(literal("a"), literal("b")), or(literal("c"))), this::toCNF);
    }

    @Test
    public void toCNF() {
        TransformationTest.traverseAndAssertFormulaEquals(
                or(and(literal("a"), literal("b")), and(literal("c"))),
                this::toCNF,
                and(or(literal("c"), literal("b")), or(literal("c"), literal("a"))));
    }

    @Test
    void basic() {
        IFormula formula = FeatJAR.apply(featJAR -> Computations.of(
                        IO.load(fmDirectory.resolve("basic.xml"), FeatJAR.extensionPoint(FormulaFormats.class))
                                .get())
                .getResult()
                .get());
        assertEquals(
                and(
                        literal("Root"),
                        or(literal(false, "A"), literal("Root")),
                        or(literal(false, "B"), literal("Root")),
                        literal("A"),
                        literal("B")),
                formula);
        IFormula finalFormula = formula;
        formula = Computations.of(finalFormula)
                .map(ComputeNNFFormula::new)
                .map(ComputeCNFFormula::new)
                .getResult()
                .get();
        assertEquals(
                and(
                        or(literal("Root")),
                        or(literal(false, "A"), literal("Root")),
                        or(literal(false, "B"), literal("Root")),
                        or(literal("A")),
                        or(literal("B"))),
                formula);
    }
}
