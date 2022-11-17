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
        NormalFormTester tester;
        switch (normalForm) {
            case CNF:
                tester = new CNFTester();
                break;
            case DNF:
                tester = new DNFTester();
                break;
            default:
                throw new IllegalStateException(String.valueOf(normalForm));
        }
        Trees.traverse(formula, tester);
        return tester;
    }

    public static boolean isNormalForm(Formula formula, Formula.NormalForm normalForm, boolean clausal) {
        final NormalFormTester tester = getNormalFormTester(formula, normalForm);
        return clausal ? tester.isClausalNormalForm() : tester.isNormalForm();
    }

    // todo: use computation and store
    public static Result<Formula> toNormalForm(Formula formula, Formula.NormalForm normalForm, boolean clausal) {
        Computation<Formula> formulaTransformer;
        ToNNF nnfFormulaComputation = new ToNNF(Computation.of(formula));
        switch (normalForm) {
            case CNF:
                formulaTransformer = new ToCNF(nnfFormulaComputation); // todo who decides what should get cached?
                break;
            case DNF:
                formulaTransformer = new ToDNF(nnfFormulaComputation);
                break;
            default:
                throw new IllegalStateException(String.valueOf(normalForm));
        }
        Result<Formula> res = formulaTransformer.getResult();
        return res.map(f -> clausal ? toClausalNormalForm(formula, normalForm) : f);
    }

    public static Formula toClausalNormalForm(Formula formula, Formula.NormalForm normalForm) {
        switch (normalForm) {
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
