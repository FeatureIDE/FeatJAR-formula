package de.featjar.formula.io.textual;

import de.featjar.formula.structure.connective.*;
import de.featjar.formula.structure.predicate.LessEqual;
import de.featjar.formula.structure.term.aggregate.AttributeAverage;
import de.featjar.formula.structure.term.aggregate.AttributeSum;

/**
 * This class represents symbols for LaTex formulas.
 *
 * @author Lara Merza
 * @author Felix Behme
 * @author Jonas Hanke
 */
public class LaTexSymbols extends Symbols {

    public static final Symbols INSTANCE = new LaTexSymbols();

    public LaTexSymbols() {
        super(false);

        setSymbol(Not.class, "\\lnot");
        setSymbol(And.class, "\\land");
        setSymbol(Or.class, "\\lor");
        setSymbol(Implies.class, "\\Rightarrow");
        setSymbol(BiImplies.class, "\\Leftrightarrow");
        setSymbol(ForAll.class, "\\forall");
        setSymbol(Exists.class, "\\exists");
    }
}