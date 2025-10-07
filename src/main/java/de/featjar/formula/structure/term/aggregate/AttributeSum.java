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
package de.featjar.formula.structure.term.aggregate;

import de.featjar.base.data.Problem;
import de.featjar.base.data.Result;
import de.featjar.base.tree.structure.ITree;
import de.featjar.formula.structure.ATerminalExpression;
import de.featjar.formula.structure.IExpression;
import de.featjar.formula.structure.term.ITerm;
import de.featjar.formula.structure.term.function.IfThenElse;
import de.featjar.formula.structure.term.function.IntegerAdd;
import de.featjar.formula.structure.term.function.RealAdd;
import de.featjar.formula.structure.term.value.Constant;
import de.featjar.formula.structure.term.value.Variable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The sum aggregate placeholder sums attribute values from attributes with a specific attribute name.
 * Only boolean features which are selected ({@link Variable} with type {@link Boolean} and value true) will be
 * considered.
 *
 * @author Lara Merza
 * @author Felix Behme
 * @author Jonas Hanke
 */
public class AttributeSum extends ATerminalExpression implements IAttributeAggregate {

    private final String attributeFilter;

    public AttributeSum(String attributeFilter) {
        this.attributeFilter = attributeFilter;
    }

    @Override
    public String getName() {
        return "sum";
    }

    @Override
    public Class<?> getType() {
        return Double.class;
    }

    @Override
    public Optional<?> evaluate(List<?> values) {
        return Optional.empty();
    }

    @Override
    public ITree<IExpression> cloneNode() {
        return new AttributeSum(attributeFilter);
    }

    @Override
    public String getAttributeFilter() {
        return attributeFilter;
    }

    @Override
    public Result<IExpression> translate(List<Variable> variables, List<?> values) {
        if(variables == null || variables.isEmpty() || values == null || values.isEmpty()) {
            return Result.empty(new Problem("Variables or values is null or empty"));
        }

        if(variables.size() != values.size()) {
            return Result.empty(new Problem("Size of variables is unequal to size of values"));
        }

        Constant defaultValue;
        Class<?> type;
        if(values.get(0) instanceof Number) {
            if(values.get(0) instanceof Double || values.get(0) instanceof Float) {
                defaultValue = new Constant(0.0, Double.class);
                type = Double.class;
            } else {
                defaultValue = new Constant(0L, Long.class);
                type = Long.class;
            }
        } else {
            return Result.empty(new Problem("Unsupported type for attribute sum"));
        }

        var termList = new ArrayList<ITerm>();
        for(int i = 0; i < variables.size(); i++) {
           if((values.get(i) instanceof Double || values.get(i) instanceof Float) && type.equals(Double.class)) {
                termList.add(new IfThenElse(variables.get(i),
                        new Constant(((Number) values.get(i)).doubleValue(), Double.class), defaultValue));
           } else if((values.get(i) instanceof Long || values.get(i) instanceof Integer ||
                   values.get(i) instanceof Short || values.get(i) instanceof Byte) && type.equals(Long.class)) {
                termList.add(new IfThenElse(variables.get(i),
                        new Constant(((Number) values.get(i)).longValue(), Long.class), defaultValue));

           } else {
               return Result.empty(new Problem("All attribute types have to be equal"));
           }
        }

        return type.equals(Double.class) ? Result.of(new RealAdd(termList)) : Result.of(new IntegerAdd(termList));
    }
}