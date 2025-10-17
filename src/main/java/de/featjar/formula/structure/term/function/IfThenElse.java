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
package de.featjar.formula.structure.term.function;

import de.featjar.base.tree.structure.ITree;
import de.featjar.formula.structure.ANonTerminalExpression;
import de.featjar.formula.structure.IExpression;
import de.featjar.formula.structure.IFormula;
import de.featjar.formula.structure.term.ITerm;

import java.util.List;
import java.util.Optional;

public class IfThenElse extends ANonTerminalExpression implements IFunction {

    private final Class<?> type;

    protected IfThenElse() {
        type = Object.class;
    }

    public IfThenElse(IFormula formula, ITerm term1, ITerm term2) {
        super(formula, term1, term2);

        if(term1.getType() != term2.getType()) {
            throw new IllegalArgumentException("Terms don't match");
        }

        this.type = term1.getType();
    }

    @Override
    public String getName() {
        return "IfThenElse";
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public Optional<?> evaluate(List<?> values) {
        if(!values.isEmpty()) {
            Object condition = values.get(0);

            if (condition instanceof Boolean) {
                return Optional.ofNullable((Boolean) condition ? values.get(1) : values.get(2));
            }
        }

        return Optional.empty();
    }

    @Override
    public ITree<IExpression> cloneNode() {
        return new IfThenElse();
    }
}