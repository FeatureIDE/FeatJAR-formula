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
package de.featjar.formula.transform;

import de.featjar.base.data.Result;
import de.featjar.formula.structure.Formula;
import de.featjar.formula.structure.connective.And;
import de.featjar.formula.structure.connective.Connective;
import de.featjar.formula.structure.connective.Or;
import de.featjar.base.task.Monitor;

/**
 * Transforms propositional formulas into CNF.
 *
 * @author Sebastian Krieter
 */
public class CNFDistributiveLawTransformer extends DistributiveLawTransformer {

    public CNFDistributiveLawTransformer() {
        super(Or.class, Or::new);
    }

    @Override
    public Result<Connective> execute(Formula formula, Monitor monitor) {
        final Connective connective = (formula instanceof And) ? (And) formula : new And(formula);
        return super.execute(connective, monitor);
    }
}
