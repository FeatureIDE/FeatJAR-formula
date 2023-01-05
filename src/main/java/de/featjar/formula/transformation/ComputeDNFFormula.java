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
package de.featjar.formula.transformation;

import de.featjar.base.computation.*;
import de.featjar.base.data.Result;
import de.featjar.base.task.IMonitor;
import de.featjar.base.tree.structure.ITree;
import de.featjar.formula.structure.formula.IFormula;
import de.featjar.formula.structure.formula.connective.Or;
import de.featjar.base.tree.Trees;
import de.featjar.formula.visitor.ANormalFormTester;
import de.featjar.formula.visitor.NormalForms;

import java.util.List;

/**
 * Transforms a formula into clausal disjunctive normal form.
 *
 * @author Sebastian Krieter
 * @deprecated does not currently work
 */
@Deprecated
public class ComputeDNFFormula extends AComputation<IFormula> implements ITransformation<IFormula> {
    protected static final Dependency<IFormula> NNF_FORMULA = newRequiredDependency();
    protected int maximumNumberOfLiterals;

    public ComputeDNFFormula(IComputation<IFormula> nnfFormula) {
        dependOn(NNF_FORMULA);
        setInput(nnfFormula);
    }

    @Override
    public Dependency<IFormula> getInputDependency() {
        return NNF_FORMULA;
    }

    public void setMaximumNumberOfLiterals(int maximumNumberOfLiterals) {
        this.maximumNumberOfLiterals = maximumNumberOfLiterals;
    }

    @Override
    public Result<IFormula> computeResult(List<?> results, IMonitor monitor) {
        IFormula formula = NNF_FORMULA.get(results);
        final ANormalFormTester normalFormTester = NormalForms.getNormalFormTester(formula, IFormula.NormalForm.DNF);
        if (normalFormTester.isNormalForm()) {
            if (!normalFormTester.isClausalNormalForm()) {
                return NormalForms.toNormalForm((IFormula) Trees.clone(formula), IFormula.NormalForm.DNF, true);
            } else {
                return Result.of((IFormula) Trees.clone(formula));
            }
        } else {
            formula = (IFormula) Trees.clone(formula);
            ComputeNormalFormFormula formulaToDistributiveNFFormula =
                    Computations.of((formula instanceof Or) ? formula : new Or(formula), monitor)
                            .map(c -> new ComputeNormalFormFormula(c, IFormula.NormalForm.DNF));
            formulaToDistributiveNFFormula.setMaximumNumberOfLiterals(maximumNumberOfLiterals);
            return formulaToDistributiveNFFormula.getResult()
                    .map(f -> NormalForms.normalToClausalNormalForm(f, IFormula.NormalForm.DNF));
        }
    }

    @Override
    public ITree<IComputation<?>> cloneNode() {
        return new ComputeDNFFormula(getInput());
    }
}
