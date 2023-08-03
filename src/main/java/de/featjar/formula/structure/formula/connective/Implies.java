/*
 * Copyright (C) 2023 FeatJAR-Development-Team
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
 * See <https://github.com/FeatJAR> for further information.
 */
package de.featjar.formula.structure.formula.connective;

import de.featjar.formula.structure.ANonTerminalExpression;
import de.featjar.formula.structure.IBinaryExpression;
import de.featjar.formula.structure.formula.IFormula;
import java.util.List;

/**
 * Expresses "if A, then B" constraints (i.e., implication).
 * Evaluates to {@code true} iff the left child evaluates to {@code false} or
 * the right child evaluates to {@code true}.
 *
 * @author Sebastian Krieter
 */
public class Implies extends ANonTerminalExpression implements IConnective, IBinaryExpression {

    protected Implies() {}

    public Implies(IFormula leftFormula, IFormula rightFormula) {
        super(leftFormula, rightFormula);
    }

    public Implies(List<? extends IFormula> formulas) {
        super(formulas);
    }

    @Override
    public String getName() {
        return "implies";
    }

    @Override
    public Object evaluate(List<?> values) {
        return !(boolean) values.get(0) || (boolean) values.get(1);
    }

    @Override
    public Implies cloneNode() {
        return new Implies();
    }
}
