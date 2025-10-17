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
import de.featjar.formula.structure.IFormula;
import de.featjar.formula.structure.term.ITerm;
import de.featjar.formula.structure.term.function.IfThenElse;
import de.featjar.formula.structure.term.function.RealAdd;
import de.featjar.formula.structure.term.function.RealDivide;
import de.featjar.formula.structure.term.value.Constant;
import de.featjar.formula.structure.term.value.Variable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The average aggregate placeholder sums attribute values from attributes with a specific attribute name and divides
 * the sum by the number of attributes.
 * Only boolean features which are selected ({@link de.featjar.formula.structure.term.value.Variable} with
 * type {@link Boolean} and value true) will be considered.
 *
 * @author Lara Merza
 * @author Felix Behme
 * @author Jonas Hanke
 */
public class AttributeAverage extends ATerminalExpression implements IAttributeAggregate {

    private final String attributeFilter;

    public AttributeAverage(String attributeFilter) {
        this.attributeFilter = attributeFilter;
    }

    @Override
    public String getName() {
        return "avg(" + attributeFilter + ")";
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
        return new AttributeAverage(attributeFilter);
    }

    @Override
    public String getAttributeFilter() {
        return attributeFilter;
    }

    @Override
    public Result<IExpression> translate(List<IFormula> formulas, List<?> values) {
        if(formulas == null || formulas.isEmpty() || values == null || values.isEmpty()) {
            return Result.empty(new Problem("Formulas or values is null or empty"));
        }

        if(formulas.size() != values.size()) {
            return Result.empty(new Problem("Size of formulas is unequal to size of values"));
        }

        var termList1 = new ArrayList<ITerm>();
        var termList2 = new ArrayList<ITerm>();
        var defaultValue = new Constant(0.0, Double.class);

        for(int i = 0; i < values.size(); i++) {
            if(values.get(i) instanceof Number) {
                termList1.add(new IfThenElse(formulas.get(i), new Constant(((Number) values.get(i)).doubleValue(), Double.class), defaultValue));
                termList2.add(new IfThenElse(formulas.get(i), new Constant(1.0, Double.class), defaultValue));
            } else {
                return Result.empty(new Problem("Unsupported type for attribute average"));
            }
        }

        return Result.ofNullable(new RealDivide(new RealAdd(termList1), new RealAdd(termList2)));
    }
}