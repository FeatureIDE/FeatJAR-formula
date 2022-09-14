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
import de.featjar.formula.structure.Expression;
import de.featjar.formula.structure.formula.connective.And;
import de.featjar.formula.structure.formula.connective.Or;
import de.featjar.base.task.Monitor;
import de.featjar.base.tree.Trees;

/**
 * Transforms propositional formulas into DNF.
 *
 * @author Sebastian Krieter
 */
public class DNFTransformer implements Transformer {

    private final DistributiveLawTransformer distributiveLawTransformer;

    public DNFTransformer() {
        distributiveLawTransformer = new DistributiveLawTransformer(And.class, And::new);
    }

    public void setMaximumNumberOfLiterals(int maximumNumberOfLiterals) {
        distributiveLawTransformer.setMaximumNumberOfLiterals(maximumNumberOfLiterals);
    }

    @Override
    public Result<Expression> execute(Expression expression, Monitor monitor) {
        final NFTester nfTester = NormalForms.getNFTester(expression, NormalForms.NormalForm.DNF);
        if (nfTester.isNf) {
            if (!nfTester.isClausalNf()) {
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
