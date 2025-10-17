/*
 * Copyright (C) 2025 FeatJAR-Development-Team
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
package de.featjar.formula.structure;

import de.featjar.base.FeatJAR;
import de.featjar.base.tree.Trees;
import de.featjar.formula.assignment.Assignment;
import de.featjar.formula.structure.connective.Implies;
import de.featjar.formula.structure.predicate.Literal;
import de.featjar.formula.structure.predicate.NotEquals;
import de.featjar.formula.structure.term.ITerm;
import de.featjar.formula.structure.term.function.IfThenElse;
import de.featjar.formula.structure.term.function.IntegerAdd;
import de.featjar.formula.visitor.Evaluator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IfThenElseTest {

    Literal feature1;
    Literal feature2;

    IFormula feature3;
    IFormula feature4;

    ITerm attribute1;
    ITerm attribute2;
    ITerm defaultValue;

    public IfThenElseTest() {
        feature1 = Expressions.literal("feature1");
        feature2 = Expressions.literal("feature2");

        feature3 = new NotEquals(Expressions.variable("feature3", Double.class), Expressions.constant(3.5));
        feature4 = new Implies(feature1, feature2);

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
        IfThenElse ifThenElse = new IfThenElse(feature1,  attribute1, defaultValue);
        var result = Trees.traverse(ifThenElse, new Evaluator(new Assignment("feature1", true)));

        assertTrue(result.get().isPresent());
        assertEquals(10L, ((long) result.get().get()));
    }

    @Test
    public void ifThenElseTest2() {
        IfThenElse ifThenElse = new IfThenElse(feature1,  attribute1, defaultValue);
        var result = Trees.traverse(ifThenElse, new Evaluator(new Assignment("feature1", false)));

        assertTrue(result.get().isPresent());
        assertEquals(0L, ((long) result.get().get()));
    }

    @Test
    public void ifThenElseTest3() {
        IfThenElse ifThenElse = new IfThenElse(feature3,  attribute1, defaultValue);
        var result = Trees.traverse(ifThenElse, new Evaluator(new Assignment("feature3", 1.0)));

        assertTrue(result.get().isPresent());
        assertEquals(10L, ((long) result.get().get()));
    }

    @Test
    public void ifThenElseTest4() {
        IfThenElse ifThenElse = new IfThenElse(feature3,  attribute1, defaultValue);
        var result = Trees.traverse(ifThenElse, new Evaluator(new Assignment("feature3", 3.5)));

        assertTrue(result.get().isPresent());
        assertEquals(0L, ((long) result.get().get()));
    }

    @Test
    public void ifThenElseTest5() {
        IfThenElse ifThenElse = new IfThenElse(feature4,  attribute1, defaultValue);
        var result = Trees.traverse(ifThenElse, new Evaluator(new Assignment("feature1", true, "feature2", false)));

        assertTrue(result.get().isPresent());
        assertEquals(0L, ((long) result.get().get()));
    }

    @Test
    public void ifThenElseSumTest1() {
        IntegerAdd sum = new IntegerAdd(new IfThenElse(feature1, attribute1, defaultValue),
                new IfThenElse(feature2, attribute2, defaultValue));

        var result = Trees.traverse(sum, new Evaluator(new Assignment("feature1", true, "feature2", false)));

        assertTrue(result.get().isPresent());
        assertEquals(10L, ((long) result.get().get()));
    }

    @Test
    public void ifThenElseSumTest2() {
        IntegerAdd sum = new IntegerAdd(new IfThenElse(feature1, attribute1, defaultValue),
                new IfThenElse(feature2, attribute2, defaultValue));

        var result = Trees.traverse(sum, new Evaluator(new Assignment("feature1", true, "feature2", true)));

        assertTrue(result.get().isPresent());
        assertEquals(110L, ((long) result.get().get()));
    }

    @Test
    public void ifThenElseSumTest3() {
        IntegerAdd sum = new IntegerAdd(new IfThenElse(feature1, attribute1, defaultValue),
                new IfThenElse(feature2, attribute2, defaultValue));

        var result = Trees.traverse(sum, new Evaluator(new Assignment("feature1", false, "feature2", false)));

        assertTrue(result.get().isPresent());
        assertEquals(0L, ((long) result.get().get()));
    }

    @Test
    public void ifThenElseSumTest4() {
        IntegerAdd sum = new IntegerAdd(new IfThenElse(feature3, attribute1, defaultValue),
                new IfThenElse(feature4, attribute2, defaultValue));

        var result = Trees.traverse(sum, new Evaluator(new Assignment("feature1", true, "feature2", false, "feature3", 1.0)));

        assertTrue(result.get().isPresent());
        assertEquals(10L, ((long) result.get().get()));
    }
}