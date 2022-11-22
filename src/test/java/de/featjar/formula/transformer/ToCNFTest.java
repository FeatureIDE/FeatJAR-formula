package de.featjar.formula.transformer;

import de.featjar.base.Feat;
import de.featjar.base.data.Computation;
import de.featjar.base.io.IO;
import de.featjar.formula.io.FormulaFormats;
import de.featjar.formula.structure.formula.Formula;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static de.featjar.formula.structure.Expressions.*;
import static org.junit.jupiter.api.Assertions.*;

class ToCNFTest {
    public static final Path fmDirectory = Paths.get("src/test/resources/testFeatureModels");

    //@Test
    public void doesNothing() {
        TransformerTest.traverseAndAssertSameFormula(and(or(literal("a"), literal("b")), or(literal("c"))), ToCNF::new);
    }

    // todo: currently broken, as ToNormalForm is not deterministic (probably due to usage of Set<>)
    //@Test
    public void toCNF() {
        TransformerTest.traverseAndAssertFormulaEquals(
                or(and(literal("a"), literal("b")), and(literal("c"))),
                ToCNF::new,
                and(or(literal("c"), literal("b")), or(literal("c"), literal("a"))));
        TransformerTest.traverseAndAssertFormulaEquals(
                or(and(literal("a"), literal("b")), literal("c")),
                ToCNF::new,
                and(or(literal("c"), literal("b")), or(literal("a"), literal("c"))));
    }

    @Test
    void basic() {
        Formula formula =
                Feat.apply(featJAR ->
                        Computation.of(IO.load(fmDirectory.resolve("basic.xml"), Feat.extensionPoint(FormulaFormats.class)).get())
                                .getResult().get());
        assertEquals(and(
                literal("Root"),
                or(literal(false, "A"), literal("Root")),
                or(literal(false, "B"), literal("Root")),
                literal("A"),
                literal("B")), formula);
        Formula finalFormula = formula;
        formula = Feat.apply(featJAR ->
                Computation.of(finalFormula)
                        .then(ToNNF.class)
                        .then(ToCNF.class)
                        .getResult().get());
        assertEquals(and(
                or(literal("Root")),
                or(literal(false, "A"), literal("Root")),
                or(literal(false, "B"), literal("Root")),
                or(literal("A")),
                or(literal("B"))), formula);
    }
}