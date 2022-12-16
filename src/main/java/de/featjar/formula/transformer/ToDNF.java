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
package de.featjar.formula.transformer;

import de.featjar.base.data.Computation;
import de.featjar.base.data.FutureResult;
import de.featjar.base.data.Result;
import de.featjar.formula.structure.formula.Formula;
import de.featjar.formula.structure.formula.connective.Or;
import de.featjar.base.tree.Trees;
import de.featjar.formula.visitor.NormalFormTester;
import de.featjar.formula.visitor.NormalForms;

/**
 * Transforms a formula into clausal disjunctive normal form.
 *
 * @author Sebastian Krieter
 * @deprecated does not currently work
 */
@Deprecated
public class ToDNF implements Computation<Formula> {
    protected final Computation<Formula> nnfFormulaComputation;
    protected int maximumNumberOfLiterals;

    public ToDNF(Computation<Formula> nnfFormulaComputation) {
        this.nnfFormulaComputation = nnfFormulaComputation;
    }

    public void setMaximumNumberOfLiterals(int maximumNumberOfLiterals) {
        this.maximumNumberOfLiterals = maximumNumberOfLiterals;
    }

    @Override
    public FutureResult<Formula> compute() {
        return nnfFormulaComputation.get().thenComputeResult(((formula, monitor) -> {
            final NormalFormTester normalFormTester = NormalForms.getNormalFormTester(formula, Formula.NormalForm.DNF);
            if (normalFormTester.isNormalForm()) {
                if (!normalFormTester.isClausalNormalForm()) {
                    return NormalForms.toNormalForm((Formula) Trees.clone(formula), Formula.NormalForm.DNF, true);
                } else {
                    return Result.of((Formula) Trees.clone(formula));
                }
            } else {
                formula = (Formula) Trees.clone(formula);
                ToNormalForm formulaToDistributiveNFFormula =
                        Computation.of((formula instanceof Or) ? formula : new Or(formula), monitor)
                                .map(c -> new ToNormalForm(c, Formula.NormalForm.DNF));
                formulaToDistributiveNFFormula.setMaximumNumberOfLiterals(maximumNumberOfLiterals);
                return formulaToDistributiveNFFormula.getResult()
                        .map(f -> NormalForms.normalToClausalNormalForm(f, Formula.NormalForm.DNF));
            }
        }));
    }
}
