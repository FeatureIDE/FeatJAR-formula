/* -----------------------------------------------------------------------------
 * formula - Propositional and first-order formulas
 * Copyright (C) 2020 Sebastian Krieter
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
 * See <https://github.com/FeatJAR/formula> for further information.
 * -----------------------------------------------------------------------------
 */
package de.featjar.formula.structure.transform;

import de.featjar.formula.structure.Formula;
import de.featjar.formula.structure.compound.And;
import de.featjar.formula.structure.compound.Or;
import de.featjar.util.job.InternalMonitor;
import de.featjar.util.tree.Trees;
import de.featjar.formula.structure.*;
import de.featjar.formula.structure.compound.*;
import de.featjar.formula.structure.transform.NormalForms.*;
import de.featjar.util.job.*;
import de.featjar.util.tree.*;

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
	public Formula execute(Formula formula, InternalMonitor monitor)
		throws DistributiveLawTransformer.MaximumNumberOfLiteralsExceededException {
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
