package de.featjar.formula.transformer;

import de.featjar.base.data.Computation;
import de.featjar.formula.analysis.bool.ToLiteralClauseList;
import de.featjar.formula.structure.formula.Formula;
import org.junit.jupiter.api.Test;

import static de.featjar.formula.structure.Expressions.*;

class TransformerTest {
    @Test
    public void toNNFFormula() {
        //Formula formula = new Implies(new Literal("a"), Expressions.False); // todo: buggy for some reason??
        Formula formula = not(or(literal("a"), literal("b")));
        Computation.of(formula)
                .then(ToNNF::new)
                .getResult().get().printParseable();
    }

    @Test
    public void toCNFFormula() {
        //Formula formula = new Implies(new Literal("a"), Expressions.False); // todo: buggy for some reason??
        Formula formula = not(or(literal("a"), literal("b")));
        Computation.of(formula)
                .then(ToNNF::new)
                .then(ToCNF::new)
                .then(ToLiteralClauseList::new)
                .getResult().get();
    }
}