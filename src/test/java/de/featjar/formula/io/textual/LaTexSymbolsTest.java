package de.featjar.formula.io.textual;

import de.featjar.base.FeatJAR;
import de.featjar.base.tree.Trees;
import de.featjar.formula.structure.Expressions;
import de.featjar.formula.structure.IFormula;
import de.featjar.formula.structure.connective.*;
import de.featjar.formula.structure.predicate.LessThan;
import de.featjar.formula.structure.term.aggregate.AttributeSum;
import de.featjar.formula.structure.term.value.Constant;
import de.featjar.formula.structure.term.value.Variable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LaTexSymbolsTest {

    private ExpressionSerializer expressionSerializer;

    @BeforeAll
    public static void initialize() {
        FeatJAR.testConfiguration();
    }

    @BeforeEach
    public void setUp() {
        expressionSerializer = new ExpressionSerializer();
        expressionSerializer.setSymbols(LaTexSymbols.INSTANCE);
    }

    @Test
    public void testInfixNoParenthesisNoEnquote1() {
        IFormula formula = new Implies(
                Expressions.literal("A"),
                Expressions.literal("B")
        );
        Trees.traverse(formula, expressionSerializer);

        assertTrue(expressionSerializer.getResult().isPresent());
        assertEquals("A \\Rightarrow B", expressionSerializer.getResult().get());
    }

    @Test
    public void testInfixNoParenthesisNoEnquote2() {
        IFormula formula = new And(
                new Implies(
                        Expressions.literal("A"),
                        Expressions.literal("B")),
                Expressions.literal("C"),
                new BiImplies(
                        Expressions.literal("A"),
                        Expressions.literal("D")
                )
        );
        Trees.traverse(formula, expressionSerializer);

        assertTrue(expressionSerializer.getResult().isPresent());
        assertEquals("(A \\Rightarrow B) \\land C \\land (A \\Leftrightarrow D)", expressionSerializer.getResult().get());
    }

    @Test
    public void testInfixNoParenthesisWithEnquote1() {
        IFormula formula = new And(
                new Implies(
                        Expressions.literal("A"),
                        Expressions.literal("B")),
                Expressions.literal("C"),
                new BiImplies(
                        Expressions.literal("A"),
                        Expressions.literal("D")
                )
        );
        expressionSerializer.setEnquoteAlways(true);
        Trees.traverse(formula, expressionSerializer);

        assertTrue(expressionSerializer.getResult().isPresent());
        assertEquals("(\"A\" \\Rightarrow \"B\") \\land \"C\" \\land (\"A\" \\Leftrightarrow \"D\")", expressionSerializer.getResult().get());
    }

    @Test
    public void testInfixWithParenthesisWithEnquote1() {
        IFormula formula = new And(
                new Implies(
                        Expressions.literal("A"),
                        Expressions.literal("B")),
                Expressions.literal("C"),
                new BiImplies(
                        Expressions.literal("A"),
                        Expressions.literal("D")
                )
        );
        expressionSerializer.setEnquoteAlways(true);
        expressionSerializer.setEnforceParentheses(true);
        Trees.traverse(formula, expressionSerializer);

        assertTrue(expressionSerializer.getResult().isPresent());
        assertEquals("((\"A\" \\Rightarrow \"B\") \\land \"C\" \\land (\"A\" \\Leftrightarrow \"D\"))", expressionSerializer.getResult().get());
    }

    @Test
    public void testInfixNoParenthesisWithEnquote2() {
        IFormula formula = new And(
                new ForAll(
                        new Variable("A"),
                        new Implies(
                                Expressions.literal("A"),
                                Expressions.literal("B")
                        )
                ),
                Expressions.literal("C"),
                new BiImplies(
                        new Not(Expressions.literal("A")),
                        new Exists(new Variable("D"), Expressions.literal("D"))
                )
        );
        expressionSerializer.setEnquoteAlways(true);
        Trees.traverse(formula, expressionSerializer);

        assertTrue(expressionSerializer.getResult().isPresent());
        assertEquals("\\forall(\"A\" \\Rightarrow \"B\") \\land \"C\" \\land (\\lnot\"A\" \\Leftrightarrow \\exists(\"D\"))", expressionSerializer.getResult().get());
    }

    @Test
    public void testInfixNoParenthesisWithEnquote3() {
        IFormula formula = new And(
                new Implies(
                        new LessThan(
                                new AttributeSum("cost"),
                                new Constant(10L, Long.class)
                        ),
                        Expressions.literal("B")
                ),
                Expressions.literal("C"),
                new BiImplies(
                        Expressions.literal("A"),
                        Expressions.literal("D")
                )
        );
        expressionSerializer.setEnquoteAlways(true);
        Trees.traverse(formula, expressionSerializer);

        assertTrue(expressionSerializer.getResult().isPresent());
        assertEquals("(>(\"sum(cost)\" \"10\") \\Rightarrow \"B\") \\land \"C\" \\land (\"A\" \\Leftrightarrow \"D\")", expressionSerializer.getResult().get());
    }
}