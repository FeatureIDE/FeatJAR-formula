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
package de.featjar.formula.transformer;

import de.featjar.base.computation.*;
import de.featjar.base.data.Result;
import de.featjar.formula.structure.formula.FormulaNormalForm;
import de.featjar.formula.structure.formula.IFormula;
import de.featjar.formula.tester.NormalForms;
import java.util.List;

/**
 * Transforms a formula into strict disjunctive normal form.
 *
 * @author Sebastian Krieter
 */
public class ComputeDNFFormula extends AComputation<IFormula> {
    protected static final Dependency<IFormula> NNF_FORMULA = Dependency.newDependency(IFormula.class);

    public ComputeDNFFormula(IComputation<IFormula> nnfFormula) {
        super(nnfFormula);
    }

    protected ComputeDNFFormula(ComputeDNFFormula other) {
        super(other);
    }

    @Override
    public Result<IFormula> compute(List<Object> dependencyList, Progress progress) {
        IFormula formula = NNF_FORMULA.get(dependencyList);
        DistributiveTransformer formulaToDistributiveNFFormula = new DistributiveTransformer(false, null);
        return formulaToDistributiveNFFormula
                .apply(formula)
                .map(f -> NormalForms.normalToStrictNormalForm(f, FormulaNormalForm.DNF));
    }
}
