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

import org.spldev.formula.expression.*;
import org.spldev.formula.expression.compound.*;
import org.spldev.formula.expression.transform.DistributiveLawTransformer.*;
import org.spldev.formula.expression.transform.NormalForms.*;
import org.spldev.util.job.*;
import org.spldev.util.tree.*;

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
	public Formula execute(Formula formula, InternalMonitor monitor) throws MaximumNumberOfLiteralsExceededException {
		final NFTester nfTester = NormalForms.getNFTester(formula, NormalForm.DNF);
		if (nfTester.isNf) {
			if (!nfTester.isClausalNf()) {
				return NormalForms.toClausalNF(Trees.cloneTree(formula), NormalForm.DNF);
			} else {
				return Trees.cloneTree(formula);
			}
		} else {
			formula = NormalForms.simplifyForNF(Trees.cloneTree(formula));
			formula = distributiveLawTransformer.execute((formula instanceof Or) ? (Or) formula : new Or(formula),
				monitor);
			formula = NormalForms.toClausalNF(formula, NormalForm.DNF);
			return formula;
		}
	}

}
