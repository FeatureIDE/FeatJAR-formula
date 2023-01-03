package de.featjar.formula.transformer;

import de.featjar.base.FeatJAR;
import de.featjar.base.computation.Computations;
import de.featjar.base.io.IO;
import de.featjar.formula.io.FormulaFormats;
import de.featjar.formula.structure.formula.IFormula;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static de.featjar.formula.structure.Expressions.*;
import static org.junit.jupiter.api.Assertions.*;

class ComputeCNFFormulaTest {
    public static final Path fmDirectory = Paths.get("src/test/resources/testFeatureModels");

    //@Test
    public void doesNothing() {
        TransformerTest.traverseAndAssertSameFormula(and(or(literal("a"), literal("b")), or(literal("c"))), ComputeCNFFormula::new);
    }

    // TODO: currently broken, as ToNormalForm is not deterministic (probably due to usage of Set<>)
    //@Test
    public void toCNF() {
        TransformerTest.traverseAndAssertFormulaEquals(
                or(and(literal("a"), literal("b")), and(literal("c"))),
                ComputeCNFFormula::new,
                and(or(literal("c"), literal("b")), or(literal("c"), literal("a"))));
        TransformerTest.traverseAndAssertFormulaEquals(
                or(and(literal("a"), literal("b")), literal("c")),
                ComputeCNFFormula::new,
                and(or(literal("c"), literal("b")), or(literal("a"), literal("c"))));
    }

    @Test
    void basic() {
        IFormula formula =
                FeatJAR.apply(featJAR ->
                        Computations.of(IO.load(fmDirectory.resolve("basic.xml"), FeatJAR.extensionPoint(FormulaFormats.class)).get())
                                .getResult().get());
        assertEquals(and(
                literal("Root"),
                or(literal(false, "A"), literal("Root")),
                or(literal(false, "B"), literal("Root")),
                literal("A"),
                literal("B")), formula);
        IFormula finalFormula = formula;
        formula = FeatJAR.apply(featJAR ->
                Computations.of(finalFormula)
                        .map(ComputeNNFFormula::new)
                        .map(ComputeCNFFormula::new)
                        .getResult().get());
        assertEquals(and(
                or(literal("Root")),
                or(literal(false, "A"), literal("Root")),
                or(literal(false, "B"), literal("Root")),
                or(literal("A")),
                or(literal("B"))), formula);
    }
}