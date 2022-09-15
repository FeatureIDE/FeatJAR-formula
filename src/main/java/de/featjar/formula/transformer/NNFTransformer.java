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
import de.featjar.formula.visitor.*;

/**
 * Transforms a formula into negation normal form.
 *
 * @author Elias Kuiter
 */
public class NNFTransformer implements FormulaTransformer {
    @Override
    public Result<Formula> execute(Formula formula, Monitor monitor) {
        //todo
        //final NormalFormTester normalFormTester = NormalForms.getNormalFormTester(formula, NormalForms.NormalForm.NNF);
        if (false) {//normalFormTester.isNormalForm) {
//            if (!normalFormTester.isClausalNormalForm()) {
//                return Result.of(NormalForms.toClausalNF(Trees.clone(expression), NormalForms.NormalForm.DNF));
//            } else {
//                return Result.of(Trees.clone(expression));
//            }
            return Result.empty();
        } else {
            formula = (Formula) formula.cloneTree();
            Trees.traverse(formula, new ConnectiveSimplifier());
            Trees.traverse(formula, new DeMorganApplier());
            Trees.traverse(formula, new AndOrSimplifier());
            return Result.of(formula);
        }
    }
}
