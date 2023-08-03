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
import de.featjar.base.tree.Trees;
import de.featjar.formula.structure.ExpressionKind;
import de.featjar.formula.structure.formula.IFormula;
import de.featjar.formula.structure.formula.connective.Reference;
import de.featjar.formula.structure.term.value.Variable;
import de.featjar.formula.visitor.*;
import java.util.List;

/**
 * Transforms a formula into strict negation normal form.
 *
 * @author Elias Kuiter
 */
public class ComputeNNFFormula extends AComputation<IFormula> {
    protected static final Dependency<IFormula> FORMULA = Dependency.newDependency(IFormula.class);

    public ComputeNNFFormula(IComputation<IFormula> formula) {
        super(formula);
    }

    protected ComputeNNFFormula(ComputeNNFFormula other) {
        super(other);
    }

    @Override
    public Result<IFormula> compute(List<Object> dependencyList, Progress progress) {
        IFormula formula = FORMULA.get(dependencyList);
        ExpressionKind.BOOLEAN.assertFor(formula);
        if (formula.getVariables().isEmpty()) throw new IllegalArgumentException("requires at least one variable");
        Variable variable = formula.getVariables().get(0);
        return Reference.mutateClone(formula, reference -> Trees.traverse(reference, new ConnectiveSimplifier())
                .flatMap(_void -> Trees.traverse(reference, new DeMorganApplier()))
                .flatMap(_void -> Trees.traverse(reference, new TrueFalseSimplifier()))
                .flatMap(_void -> Trees.traverse(reference, new TrueFalseRemover(variable)))
                .flatMap(_void -> Trees.traverse(reference, new AndOrSimplifier())));
    }
}
