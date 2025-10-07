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
import de.featjar.formula.structure.term.aggregate.AttributeSum;
import de.featjar.formula.structure.term.function.IfThenElse;
import de.featjar.formula.structure.term.function.IntegerAdd;
import de.featjar.formula.structure.term.function.RealAdd;
import de.featjar.formula.structure.term.value.Constant;
import de.featjar.formula.structure.term.value.Variable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AttributeSumTest {

    @BeforeAll
    public static void init() {
        FeatJAR.testConfiguration().initialize();
        FeatJAR.log().info("Loaded");
    }

    @Test
    public void sum1() {
        List<Variable> variables = List.of(new Variable("A", Boolean.class),
                new Variable("B", Boolean.class));
        List<Integer> values = List.of(1, 2);
        Constant defaultValue = new Constant(0L, Long.class);

        AttributeSum attributeSum = new AttributeSum("key");
        Result<IExpression> test = attributeSum.translate(variables, values);

        IExpression comparison = new IntegerAdd(new IfThenElse(variables.get(0), new Constant(1L, Long.class), defaultValue),
                new IfThenElse(variables.get(1), new Constant(2L, Long.class), defaultValue));

        assertTrue(test.isPresent());
        assertTrue(test.get().equalsTree(comparison));
    }

    @Test
    public void sum2() {
        List<Variable> variables = List.of(new Variable("A", Boolean.class),
                new Variable("B", Boolean.class));
        List<Float> values = List.of(1.0f, 2.0f);
        Constant defaultValue = new Constant(0.0, Double.class);

        AttributeSum attributeSum = new AttributeSum("key");
        Result<IExpression> test = attributeSum.translate(variables, values);

        IExpression comparison = new RealAdd(new IfThenElse(variables.get(0), new Constant(1.0, Double.class), defaultValue),
                new IfThenElse(variables.get(1), new Constant(2.0, Double.class), defaultValue));

        assertTrue(test.isPresent());
        assertTrue(test.get().equalsTree(comparison));
    }

    @Test
    public void sum3() {
        List<Variable> variables = List.of(new Variable("A", Boolean.class));
        List<Long> values = List.of(1L);
        Constant defaultValue = new Constant(0L, Long.class);

        AttributeSum attributeSum = new AttributeSum("key");
        Result<IExpression> test = attributeSum.translate(variables, values);

        IExpression comparison = new IntegerAdd(new IfThenElse(variables.get(0), new Constant(1L, Long.class), defaultValue));

        assertTrue(test.isPresent());
        assertTrue(test.get().equalsTree(comparison));
    }

    @Test
    public void sum4() {
        List<Variable> variables = List.of(new Variable("A", Boolean.class),
                new Variable("B", Boolean.class), new Variable("C", Boolean.class));
        List<Short> values = List.of((short) 1, (short) 2, (short) 3);
        Constant defaultValue = new Constant(0L, Long.class);

        AttributeSum attributeSum = new AttributeSum("key");
        Result<IExpression> test = attributeSum.translate(variables, values);

        IExpression comparison = new IntegerAdd(new IfThenElse(variables.get(0), new Constant(1L, Long.class), defaultValue),
                new IfThenElse(variables.get(1), new Constant(2L, Long.class), defaultValue),
                new IfThenElse(variables.get(2), new Constant(3L, Long.class), defaultValue));

        assertTrue(test.isPresent());
        assertTrue(test.get().equalsTree(comparison));
    }

    @Test
    public void sum5() {
        List<Variable> variables = List.of(new Variable("A", Boolean.class),
                new Variable("B", Boolean.class), new Variable("C", Boolean.class));
        List<Byte> values = List.of((byte) 1, (byte) 2, (byte) 3);
        Constant defaultValue = new Constant(0L, Long.class);

        AttributeSum attributeSum = new AttributeSum("key");
        Result<IExpression> test = attributeSum.translate(variables, values);

        IExpression comparison = new IntegerAdd(new IfThenElse(variables.get(0), new Constant(1L, Long.class), defaultValue),
                new IfThenElse(variables.get(1), new Constant(2L, Long.class), defaultValue),
                new IfThenElse(variables.get(2), new Constant(3L, Long.class), defaultValue));

        assertTrue(test.isPresent());
        assertTrue(test.get().equalsTree(comparison));
    }

    @Test
    public void valuesAreNull() {
        List<Variable> variables = List.of(new Variable("A", Boolean.class),
                new Variable("B", Boolean.class), new Variable("C", Boolean.class));

        AttributeSum attributeSum = new AttributeSum("key");
        Result<IExpression> test = attributeSum.translate(variables, null);

        assertTrue(test.isEmpty());
        assertTrue(test.getProblems().get(0).getMessage().contains("Variables or values is null or empty"));
    }

    @Test
    public void sizeMismatch() {
        List<Variable> variables = List.of(new Variable("A", Boolean.class),
                new Variable("B", Boolean.class), new Variable("C", Boolean.class));
        List<Double> values = List.of(1.0, 2.0);

        AttributeSum attributeSum = new AttributeSum("key");
        Result<IExpression> test = attributeSum.translate(variables, values);

        assertTrue(test.isEmpty());
        assertTrue(test.getProblems().get(0).getMessage().contains("Size of variables is unequal to size of values"));
    }

    @Test
    public void unsupportedType() {
        List<Variable> variables = List.of(new Variable("A", Boolean.class),
                new Variable("B", Boolean.class));
        List<Boolean> values = List.of(true, false);

        AttributeSum attributeSum = new AttributeSum("key");
        Result<IExpression> test = attributeSum.translate(variables, values);

        assertTrue(test.isEmpty());
        assertTrue(test.getProblems().get(0).getMessage().contains("Unsupported type for attribute sum"));
    }

    @Test
    public void differentValueTypes() {
        List<Variable> variables = List.of(new Variable("A", Boolean.class),
                new Variable("B", Boolean.class));
        List<Object> values = List.of(1L, 1.0);

        AttributeSum attributeSum = new AttributeSum("key");
        Result<IExpression> test = attributeSum.translate(variables, values);

        assertTrue(test.isEmpty());
        assertTrue(test.getProblems().get(0).getMessage().contains("All attribute types have to be equal"));
    }
}