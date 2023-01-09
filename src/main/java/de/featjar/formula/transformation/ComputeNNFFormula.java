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
import de.featjar.base.computation.Progress;
import de.featjar.base.data.Result;
import de.featjar.base.tree.Trees;
import de.featjar.base.tree.structure.ITree;
import de.featjar.formula.structure.formula.IFormula;
import de.featjar.formula.structure.formula.connective.Reference;
import de.featjar.formula.visitor.*;

/**
 * Transforms a formula into clausal negation normal form.
 *
 * @author Elias Kuiter
 */
public class ComputeNNFFormula extends AComputation<IFormula> implements ITransformation<IFormula> {
    protected static final Dependency<IFormula> FORMULA = newRequiredDependency();

    public ComputeNNFFormula(IComputation<IFormula> formula) {
        dependOn(FORMULA);
        setInput(formula);
    }

    @Override
    public Dependency<IFormula> getInputDependency() {
        return FORMULA;
    }

    @Override
    public Result<IFormula> compute(DependencyList dependencyList, Progress progress) {
        IFormula formula = dependencyList.get(FORMULA);
        return Reference.mutateClone(formula,
                reference -> Trees.traverse(reference, new ConnectiveSimplifier())
                        .flatMap(_void -> Trees.traverse(reference, new DeMorganApplier()))
                        .flatMap(_void -> Trees.traverse(reference, new TrueFalseSimplifier()))
                        .flatMap(_void -> Trees.traverse(reference, new AndOrSimplifier())));
    }

    @Override
    public ITree<IComputation<?>> cloneNode() {
        return new ComputeNNFFormula(getInput());
    }
}
