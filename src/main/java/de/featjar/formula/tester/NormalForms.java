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
package de.featjar.formula.tester;

import static de.featjar.base.computation.Computations.async;

import de.featjar.base.data.Result;
import de.featjar.formula.structure.formula.FormulaNormalForm;
import de.featjar.formula.structure.formula.IFormula;
import de.featjar.formula.structure.formula.connective.And;
import de.featjar.formula.structure.formula.connective.Or;
import de.featjar.formula.structure.formula.predicate.Literal;
import de.featjar.formula.transformer.ComputeCNFFormula;
import de.featjar.formula.transformer.ComputeDNFFormula;
import de.featjar.formula.transformer.ComputeNNFFormula;
import java.util.stream.Collectors;

/**
 * Tests and transforms formulas for and into normal forms.
 *
 * @author Sebastian Krieter
 */
public class NormalForms {
    // todo: use computation framework, always return strict normal form
    public static Result<IFormula> toNormalForm(
            IFormula formula, FormulaNormalForm formulaNormalForm, boolean isStrict) {
        return async(formula)
                .map(
                        formulaNormalForm == FormulaNormalForm.NNF
                                ? ComputeNNFFormula::new
                                : formulaNormalForm == FormulaNormalForm.CNF
                                        ? ComputeCNFFormula::new
                                        : ComputeDNFFormula::new)
                .get()
                .map(f -> isStrict ? normalToStrictNormalForm(formula, formulaNormalForm) : f);
    }

    // todo: make this a computation? assumes that normal form is already there
    public static IFormula normalToStrictNormalForm(IFormula formula, FormulaNormalForm formulaNormalForm) {
        switch (formulaNormalForm) {
            case NNF:
                // TODO: currently not implemented
                throw new UnsupportedOperationException();
            case CNF:
                if (formula instanceof Literal) {
                    formula = new And(new Or(formula));
                } else if (formula instanceof Or) {
                    formula = new And(formula);
                } else {
                    formula = new And(formula.getChildren().stream()
                            .map(child -> (IFormula) (child instanceof Literal ? new Or((IFormula) child) : child))
                            .collect(Collectors.toList()));
                }
                break;
            case DNF:
                if (formula instanceof Literal) {
                    formula = new Or(new And(formula));
                } else if (formula instanceof And) {
                    formula = new Or(new And(formula));
                } else {
                    formula = new And(formula.getChildren().stream()
                            .map(child -> (IFormula) (child instanceof Literal ? new And((IFormula) child) : child))
                            .collect(Collectors.toList()));
                }
                break;
        }
        return formula;
    }
}
