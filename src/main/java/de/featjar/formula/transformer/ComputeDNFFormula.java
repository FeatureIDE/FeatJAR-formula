/*
 * Copyright (C) 2023 Sebastian Krieter, Elias Kuiter
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
package de.featjar.formula.transformer;

import de.featjar.base.computation.*;
import de.featjar.base.computation.Progress;
import de.featjar.base.data.Result;
import de.featjar.base.tree.Trees;
import de.featjar.base.tree.structure.ITree;
import de.featjar.formula.structure.formula.IFormula;
import de.featjar.formula.structure.formula.FormulaNormalForm;
import de.featjar.formula.structure.formula.connective.Or;
import de.featjar.formula.tester.NormalForms;

/**
 * Transforms a formula into strict disjunctive normal form.
 *
 * @author Sebastian Krieter
 */
public class ComputeDNFFormula extends AComputation<IFormula> implements ITransformation<IFormula> {
    protected static final Dependency<IFormula> NNF_FORMULA = newRequiredDependency();

    public ComputeDNFFormula(IComputation<IFormula> nnfFormula) {
        dependOn(NNF_FORMULA);
        setInput(nnfFormula);
    }

    @Override
    public Dependency<IFormula> getInputDependency() {
        return NNF_FORMULA;
    }

    @Override
    public Result<IFormula> compute(DependencyList dependencyList, Progress progress) {
        IFormula formula = dependencyList.get(NNF_FORMULA);
        DistributiveTransformer formulaToDistributiveNFFormula = new DistributiveTransformer(false, null);
        return formulaToDistributiveNFFormula.apply(formula).map(f ->
                NormalForms.normalToStrictNormalForm(f, FormulaNormalForm.DNF));
    }

    @Override
    public ITree<IComputation<?>> cloneNode() {
        return new ComputeDNFFormula(getInput());
    }
}
