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
package de.featjar.formula.io.dimacs;

import de.featjar.base.computation.Computations;
import de.featjar.base.data.Result;
import de.featjar.base.io.format.IFormat;
import de.featjar.formula.VariableMap;
import de.featjar.formula.computation.ComputeCNFFormula;
import de.featjar.formula.computation.ComputeNNFFormula;
import de.featjar.formula.structure.IExpression;
import de.featjar.formula.structure.IFormula;
import de.featjar.formula.structure.connective.Reference;
import de.featjar.formula.structure.predicate.Literal;

/**
 * Reads and writes feature models in the DIMACS CNF format.
 *
 * @author Sebastian Krieter
 */
public class FormulaCNFDimacsFormat implements IFormat<IFormula> {

    @Override
    public Result<String> serialize(IFormula formula) {
        Reference cnfFormula = Computations.of(formula)
                .map(ComputeNNFFormula::new)
                .map(ComputeCNFFormula::new)
                .set(ComputeCNFFormula.IS_STRICT, true)
                .compute();
        VariableMap variableMap = new VariableMap(formula.getVariableMap().keySet());
        return Result.of(DimacsSerializer.serialize(
                variableMap, cnfFormula.getExpression().getChildren(), c -> writeClause(c, variableMap)));
    }

    private static int[] writeClause(IExpression clause, VariableMap variableMap) {
        int[] literals = new int[clause.getChildrenCount()];
        int i = 0;
        for (final IExpression child : clause.getChildren()) {
            final Literal l = (Literal) child;
            final int index = variableMap.get(l.getExpression().getName()).orElseThrow();
            literals[i++] = l.isPositive() ? index : -index;
        }
        return literals;
    }

    @Override
    public boolean supportsWrite() {
        return true;
    }

    @Override
    public String getName() {
        return "CNF-DIMACS";
    }

    @Override
    public String getFileExtension() {
        return "dimacs";
    }
}
