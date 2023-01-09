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
package de.featjar.formula.visitor;

import de.featjar.base.computation.IComputation;
import de.featjar.formula.structure.formula.IFormula;
import de.featjar.formula.structure.formula.predicate.Literal;
import de.featjar.formula.structure.formula.connective.And;
import de.featjar.formula.structure.formula.connective.Or;
import de.featjar.base.data.Result;
import de.featjar.base.tree.Trees;
import de.featjar.formula.transformation.ComputeCNFFormula;
import de.featjar.formula.transformation.ComputeDNFFormula;
import de.featjar.formula.transformation.ComputeNNFFormula;

import static de.featjar.base.computation.Computations.async;

/**
 * Tests and transforms formulas for and into normal forms.
 *
 * @author Sebastian Krieter
 */
public class NormalForms {

    public static ANormalFormTester getNormalFormTester(IFormula formula, IFormula.NormalForm normalForm) {
        ANormalFormTester normalFormTester = normalForm == IFormula.NormalForm.NNF
                ? new NNFTester()
                : normalForm == IFormula.NormalForm.CNF
                ? new CNFTester()
                : new DNFTester();
        Trees.traverse(formula, normalFormTester);
        return normalFormTester;
    }

    public static boolean isNormalForm(IFormula formula, IFormula.NormalForm normalForm) {
        return getNormalFormTester(formula, normalForm).isNormalForm();
    }

    public static boolean isClausalNormalForm(IFormula formula, IFormula.NormalForm normalForm) {
        return getNormalFormTester(formula, normalForm).isClausalNormalForm();
    }

    public static Result<IFormula> toNormalForm(IFormula formula, IFormula.NormalForm normalForm, boolean isClausal) {
        IComputation<IFormula> normalFormFormula = async(formula)
                .map(normalForm == IFormula.NormalForm.NNF
                        ? ComputeNNFFormula::new
                        : normalForm == IFormula.NormalForm.CNF
                        ? ComputeCNFFormula::new
                        : ComputeDNFFormula::new);
        Result<IFormula> res = normalFormFormula.get();
        return res.map(f -> isClausal ? normalToClausalNormalForm(formula, normalForm) : f);
    }

    public static IFormula normalToClausalNormalForm(IFormula formula, IFormula.NormalForm normalForm) {
        switch (normalForm) {
            case NNF:
                // TODO: currently not implemented
                throw new UnsupportedOperationException();
            case CNF:
                if (formula instanceof Literal) {
                    formula = new And(new Or(formula));
                } else if (formula instanceof Or) {
                    formula = new And(formula);
                } else {
                    formula.replaceChildren(child -> (child instanceof Literal ? new Or((IFormula) child) : child));
                }
                break;
            case DNF:
                if (formula instanceof Literal) {
                    formula = new Or(new And(formula));
                } else if (formula instanceof And) {
                    formula = new Or(new And(formula));
                } else {
                    formula.replaceChildren(child -> (child instanceof Literal ? new And((IFormula) child) : child));
                }
                break;
        }
        return formula;
    }
}
