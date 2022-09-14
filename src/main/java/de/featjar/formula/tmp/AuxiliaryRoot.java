/*
 * Copyright (C) 2022 Sebastian Krieter, Elias Kuiter
 *
 * This file is part of formula.
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
package de.featjar.formula.tmp;

import de.featjar.formula.structure.Expression;
import de.featjar.formula.structure.NonTerminalExpression;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A propositional node that can be transformed into conjunctive normal form
 * (cnf).
 *
 * @author Sebastian Krieter
 */
public class AuxiliaryRoot extends NonTerminalExpression {

    public AuxiliaryRoot(Expression expression) {
        super(expression);
    }

    private AuxiliaryRoot() {
    }

    @Override
    public String getName() {
        return "";
    }

    public Expression getChild() {
        return getChildren().iterator().next();
    }

    public void setChild(Expression expression) {
        Objects.requireNonNull(expression);
        setChildren(Arrays.asList(expression));
    }

    @Override
    public AuxiliaryRoot cloneNode() {
        return new AuxiliaryRoot();
    }

    @Override
    public Class<?> getType() {
        return getChild().getType();
    }

    @Override
    public Object evaluate(List<?> values) {
        return getChild().evaluate(values);
    }
}
