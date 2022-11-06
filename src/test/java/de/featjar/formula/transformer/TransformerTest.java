package de.featjar.formula.transformer;

import de.featjar.base.data.Computation;
import de.featjar.formula.clauses.ToCNF;
import de.featjar.formula.structure.formula.Formula;
import org.junit.jupiter.api.Test;

import static de.featjar.formula.structure.Expressions.*;

class TransformerTest {
    @Test
    public void toNNFFormula() {
        //Formula formula = new Implies(new Literal("a"), Expressions.False); // todo: buggy for some reason??
        Formula formula = not(or(literal("a"), literal("b")));
        Computation.of(formula)
                .then(ToNNFFormula::new)
                .getResult().get().printParseable();
    }

    @Test
    public void toCNFFormula() {
        //Formula formula = new Implies(new Literal("a"), Expressions.False); // todo: buggy for some reason??
        Formula formula = not(or(literal("a"), literal("b")));
        Computation.of(formula)
                .then(ToNNFFormula::new)
                .then(ToCNFFormula::new)
                .then(ToCNF::new)
                .getResult().get();
    }
}