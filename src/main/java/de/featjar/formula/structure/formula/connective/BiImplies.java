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
 * Expresses "A iff B" constraints (i.e., bi-implication).
 * Evaluates to {@code true} iff both children evaluate to the same value.
 *
 * @author Sebastian Krieter
 */
public class BiImplies extends ANonTerminalExpression implements IConnective, IBinaryExpression {

    protected BiImplies() {}

    public BiImplies(IFormula leftFormula, IFormula rightFormula) {
        super(leftFormula, rightFormula);
    }

    public BiImplies(List<? extends IFormula> formulas) {
        super(formulas);
    }

    @Override
    public String getName() {
        return "biimplies";
    }

    @Override
    public Object evaluate(List<?> values) {
        return (boolean) values.get(0) == (boolean) values.get(1);
    }

    @Override
    public BiImplies cloneNode() {
        return new BiImplies();
    }
}
