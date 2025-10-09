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
import de.featjar.base.data.Result;
import de.featjar.formula.structure.term.aggregate.AttributeAverage;
import de.featjar.formula.structure.term.aggregate.AttributeSum;
import de.featjar.formula.structure.term.function.IfThenElse;
import de.featjar.formula.structure.term.function.RealAdd;
import de.featjar.formula.structure.term.function.RealDivide;
import de.featjar.formula.structure.term.value.Constant;
import de.featjar.formula.structure.term.value.Variable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AttributeAverageTest {

    @BeforeAll
    public static void init() {
        FeatJAR.testConfiguration().initialize();
        FeatJAR.log().info("Loaded");
    }

    @Test
    public void average1() {
        List<Variable> variables = List.of(new Variable("A", Boolean.class),
                new Variable("B", Boolean.class));
        List<Integer> values = List.of(1, 2);
        Constant defaultValue = new Constant(0.0, Double.class);

        AttributeAverage attributeAverage = new AttributeAverage("key");
        Result<IExpression> test = attributeAverage.translate(variables, values);

        IExpression comparison = new RealDivide(new RealAdd(new IfThenElse(variables.get(0), new Constant(1.0, Double.class), defaultValue), new IfThenElse(variables.get(1), new Constant(2.0, Double.class), defaultValue)),
                new RealAdd(new IfThenElse(variables.get(0), new Constant(1.0, Double.class), defaultValue), new IfThenElse(variables.get(1), new Constant(1.0, Double.class), defaultValue)));

        assertTrue(test.isPresent());
        assertTrue(test.get().equalsTree(comparison));
    }

    @Test
    public void average2() {
        List<Variable> variables = List.of(new Variable("A", Boolean.class),
                new Variable("B", Boolean.class), new Variable("C", Boolean.class));
        List<Double> values = List.of(1.0, 2.0, 3.0);
        Constant defaultValue = new Constant(0.0, Double.class);

        AttributeAverage attributeAverage = new AttributeAverage("key");
        Result<IExpression> test = attributeAverage.translate(variables, values);

        IExpression comparison = new RealDivide(new RealAdd(new IfThenElse(variables.get(0), new Constant(1.0, Double.class), defaultValue), new IfThenElse(variables.get(1), new Constant(2.0, Double.class), defaultValue), new IfThenElse(variables.get(2), new Constant(3.0, Double.class), defaultValue)),
                new RealAdd(new IfThenElse(variables.get(0), new Constant(1.0, Double.class), defaultValue), new IfThenElse(variables.get(1), new Constant(1.0, Double.class), defaultValue), new IfThenElse(variables.get(2), new Constant(1.0, Double.class), defaultValue)));

        assertTrue(test.isPresent());
        assertTrue(test.get().equalsTree(comparison));
    }

    @Test
    public void average3() {
        List<Variable> variables = List.of(new Variable("A", Boolean.class));
        List<Float> values = List.of(1.0f);
        Constant defaultValue = new Constant(0.0, Double.class);

        AttributeAverage attributeAverage = new AttributeAverage("key");
        Result<IExpression> test = attributeAverage.translate(variables, values);

        IExpression comparison = new RealDivide(new RealAdd(new IfThenElse(variables.get(0), new Constant(1.0, Double.class), defaultValue)),
                new RealAdd(new IfThenElse(variables.get(0), new Constant(1.0, Double.class), defaultValue)));

        assertTrue(test.isPresent());
        assertTrue(test.get().equalsTree(comparison));
    }

    @Test
    public void variablesAreNull() {
        List<Double> values = List.of(1.0, 2.0);

        AttributeSum attributeSum = new AttributeSum("key");
        Result<IExpression> test = attributeSum.translate(null, values);

        assertTrue(test.isEmpty());
        assertTrue(test.getProblems().get(0).getMessage().contains("Variables or values is null or empty"));
    }

    @Test
    public void sizeMismatch() {
        List<Variable> variables = List.of(new Variable("A", Boolean.class),
                new Variable("B", Boolean.class), new Variable("C", Boolean.class));
        List<Double> values = List.of(1.0, 2.0);

        AttributeAverage attributeAverage = new AttributeAverage("key");
        Result<IExpression> test = attributeAverage.translate(variables, values);

        assertTrue(test.isEmpty());
        assertTrue(test.getProblems().get(0).getMessage().contains("Size of variables is unequal to size of values"));
    }

    @Test
    public void notNumeric() {
        List<Variable> variables = List.of(new Variable("A", Boolean.class),
                new Variable("B", Boolean.class));
        List<Boolean> values = List.of(true, false);

        AttributeAverage attributeAverage = new AttributeAverage("key");
        Result<IExpression> test = attributeAverage.translate(variables, values);

        assertTrue(test.isEmpty());
        assertEquals("Unsupported type for attribute average", test.getProblems().get(0).getMessage());
    }
}