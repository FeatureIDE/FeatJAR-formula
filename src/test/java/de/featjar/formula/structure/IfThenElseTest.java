package de.featjar.formula.structure;

import de.featjar.base.FeatJAR;
import de.featjar.base.tree.Trees;
import de.featjar.formula.assignment.Assignment;
import de.featjar.formula.structure.term.ITerm;
import de.featjar.formula.structure.term.function.IfThenElse;
import de.featjar.formula.structure.term.function.IntegerAdd;
import de.featjar.formula.structure.term.value.Variable;
import de.featjar.formula.visitor.Evaluator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IfThenElseTest {

    Variable feature1;
    Variable feature2;

    ITerm attribute1;
    ITerm attribute2;
    ITerm defaultValue;

    public IfThenElseTest() {
        feature1 = new Variable("feature1", Boolean.class);
        feature2 = new Variable("feature2", Boolean.class);

        attribute1 = Expressions.constant(10L, Long.class);
        attribute2 = Expressions.constant(100L, Long.class);
        defaultValue = Expressions.constant(0L, Long.class);
    }

    @BeforeAll
    public static void init() {
        FeatJAR.testConfiguration().initialize();
    }

    @Test
    public void ifThenElseTest1() {
        IfThenElse ifThenElse = new IfThenElse(feature1,  attribute1, defaultValue, Long.class);
        var result = Trees.traverse(ifThenElse, new Evaluator(new Assignment("feature1", true)));

        assertTrue(result.get().isPresent());
        assertEquals(10L, ((long) result.get().get()));
    }

    @Test
    public void ifThenElseTest2() {
        IfThenElse ifThenElse = new IfThenElse(feature1,  attribute1, defaultValue, Long.class);
        var result = Trees.traverse(ifThenElse, new Evaluator(new Assignment("feature1", false)));

        assertTrue(result.get().isPresent());
        assertEquals(0L, ((long) result.get().get()));
    }


    @Test
    public void ifThenElseSumTest1() {
        IntegerAdd sum = new IntegerAdd(new IfThenElse(feature1, attribute1, defaultValue, Long.class),
                new IfThenElse(feature2, attribute2, defaultValue, Long.class));

        var result = Trees.traverse(sum, new Evaluator(new Assignment("feature1", true, "feature2", false)));

        assertTrue(result.get().isPresent());
        assertEquals(10L, ((long) result.get().get()));
    }

    @Test
    public void ifThenElseSumTest2() {
        IntegerAdd sum = new IntegerAdd(new IfThenElse(feature1, attribute1, defaultValue, Long.class),
                new IfThenElse(feature2, attribute2, defaultValue, Long.class));

        var result = Trees.traverse(sum, new Evaluator(new Assignment("feature1", true, "feature2", true)));

        assertTrue(result.get().isPresent());
        assertEquals(110L, ((long) result.get().get()));
    }

    @Test
    public void ifThenElseSumTest3() {
        IntegerAdd sum = new IntegerAdd(new IfThenElse(feature1, attribute1, defaultValue, Long.class),
                new IfThenElse(feature2, attribute2, defaultValue, Long.class));

        var result = Trees.traverse(sum, new Evaluator(new Assignment("feature1", false, "feature2", false)));

        assertTrue(result.get().isPresent());
        assertEquals(0L, ((long) result.get().get()));
    }

    @Test
    public void ifThenElseErrorTest() {
        var feature3 = Expressions.variable("feature3", Integer.class);
        IfThenElse ifThenElse = new IfThenElse(feature3,  attribute1, defaultValue, Long.class);
        var result = Trees.traverse(ifThenElse, new Evaluator(new Assignment("feature3", 5)));

        assertTrue(result.get().isEmpty());
    }
}