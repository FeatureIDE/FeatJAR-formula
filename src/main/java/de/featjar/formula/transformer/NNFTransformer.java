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

import de.featjar.base.data.Result;
import de.featjar.base.task.Monitor;
import de.featjar.base.tree.Trees;
import de.featjar.formula.structure.formula.Formula;
import de.featjar.formula.structure.formula.connective.Or;
import de.featjar.formula.visitor.NormalFormTester;
import de.featjar.formula.visitor.NormalForms;

/**
 * Transforms propositional formulas into negation normal form.
 *
 * @author Sebastian Krieter
 */
public class NNFTransformer implements Transformer {
    @Override
    public Result<Formula> execute(Formula formula, Monitor monitor) {
        final NormalFormTester normalFormTester = NormalForms.getNormalFormTester(expression, NormalForms.NormalForm.DNF);
        if (normalFormTester.isNormalForm) {
            if (!normalFormTester.isClausalNormalForm()) {
                return Result.of(NormalForms.toClausalNF(Trees.clone(expression), NormalForms.NormalForm.DNF));
            } else {
                return Result.of(Trees.clone(expression));
            }
        } else {
            expression = NormalForms.simplifyForNF(Trees.clone(expression));
            return distributiveLawTransformer.execute((expression instanceof Or) ? expression : new Or(expression), monitor)
                    .map(f -> NormalForms.toClausalNF(f, NormalForms.NormalForm.DNF));
        }
    }
}
