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

import de.featjar.base.data.Computation;
import de.featjar.formula.structure.formula.Formula;
import de.featjar.formula.structure.formula.predicate.Literal;
import de.featjar.formula.structure.formula.connective.And;
import de.featjar.formula.structure.formula.connective.Or;
import de.featjar.base.data.Result;
import de.featjar.base.tree.Trees;
import de.featjar.formula.transformer.ToCNF;
import de.featjar.formula.transformer.ToDNF;
import de.featjar.formula.transformer.ToNNF;

/**
 * Tests and transforms formulas for and into normal forms.
 *
 * @author Sebastian Krieter
 */
public class NormalForms {

    public static NormalFormTester getNormalFormTester(Formula formula, Formula.NormalForm normalForm) {
        NormalFormTester normalFormTester = normalForm == Formula.NormalForm.NNF
                ? new NormalFormTester.NNF()
                : normalForm == Formula.NormalForm.CNF
                ? new NormalFormTester.CNF()
                : new NormalFormTester.DNF();
        Trees.traverse(formula, normalFormTester);
        return normalFormTester;
    }

    public static boolean isNormalForm(Formula formula, Formula.NormalForm normalForm) {
        return getNormalFormTester(formula, normalForm).isNormalForm();
    }

    public static boolean isClausalNormalForm(Formula formula, Formula.NormalForm normalForm) {
        return getNormalFormTester(formula, normalForm).isClausalNormalForm();
    }

    public static Result<Formula> toNormalForm(Formula formula, Formula.NormalForm normalForm, boolean isClausal) {
        Computation<Formula> normalFormFormulaComputation = Computation.of(formula)
                .map(normalForm == Formula.NormalForm.NNF
                        ? ToNNF::new
                        : normalForm == Formula.NormalForm.CNF
                        ? ToCNF::new
                        : ToDNF::new);
        Result<Formula> res = normalFormFormulaComputation.getResult();
        return res.map(f -> isClausal ? normalToClausalNormalForm(formula, normalForm) : f);
    }

    public static Formula normalToClausalNormalForm(Formula formula, Formula.NormalForm normalForm) {
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
                    formula.replaceChildren(child -> (child instanceof Literal ? new Or((Formula) child) : child));
                }
                break;
            case DNF:
                if (formula instanceof Literal) {
                    formula = new Or(new And(formula));
                } else if (formula instanceof And) {
                    formula = new Or(new And(formula));
                } else {
                    formula.replaceChildren(child -> (child instanceof Literal ? new And((Formula) child) : child));
                }
                break;
        }
        return formula;
    }
}
