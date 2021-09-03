/* -----------------------------------------------------------------------------
 * Formula Lib - Library to represent and edit propositional formulas.
 * Copyright (C) 2021  Sebastian Krieter
 * 
 * This file is part of Formula Lib.
 * 
 * Formula Lib is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Formula Lib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Formula Lib.  If not, see <https://www.gnu.org/licenses/>.
 * 
 * See <https://github.com/skrieter/formula> for further information.
 * -----------------------------------------------------------------------------
 */
package org.spldev.formula.expression.transform;

import org.spldev.formula.expression.Formula;
import org.spldev.formula.expression.compound.And;
import org.spldev.formula.expression.compound.Or;
import org.spldev.formula.expression.transform.NormalForms.NormalForm;
import org.spldev.util.job.InternalMonitor;
import org.spldev.util.tree.Trees;

/**
 * Transforms propositional formulas into CNF.
 *
 * @author Sebastian Krieter
 */
public class CNFDistributiveLawTransformer extends DistributiveLawTransformer {

	public Formula execute(Formula formula, InternalMonitor monitor) {
		final NFTester nfTester = NormalForms.isNF(formula, NormalForm.CNF);
		if (nfTester.isNf) {
			formula = Trees.cloneTree(formula);
			if (!nfTester.isClausalNf()) {
				formula = NormalForms.toClausalNF(formula, NormalForm.CNF);
			}
		} else {
			formula = NormalForms.simplifyForNF(formula);
			formula = (formula instanceof And) ? formula : new And(formula);
			transfrom(formula, Or.class, Or::new);
			formula = NormalForms.toClausalNF(formula, NormalForm.CNF);
		}
		return formula;
	}

}
