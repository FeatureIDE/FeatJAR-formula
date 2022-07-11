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
package org.spldev.formula.structure.transform;

import org.spldev.formula.structure.*;
import org.spldev.formula.structure.atomic.literal.*;
import org.spldev.formula.structure.compound.*;
import org.spldev.util.data.*;
import org.spldev.util.job.*;
import org.spldev.util.tree.*;

/**
 * Transforms propositional formulas into (clausal) CNF or DNF.
 *
 * @author Sebastian Krieter
 */
public final class NormalForms {

	private NormalForms() {
	}

	public enum NormalForm {
		CNF, DNF
	}

	public static Formula simplifyForNF(Formula formula) {
		final AuxiliaryRoot auxiliaryRoot = new AuxiliaryRoot(formula);
		Trees.traverse(auxiliaryRoot, new EquivalenceVisitor());
		Trees.traverse(auxiliaryRoot, new DeMorganVisitor());
		Trees.traverse(auxiliaryRoot, new TreeSimplifier());
		return (Formula) auxiliaryRoot.getChild();
	}

	public static Result<Formula> toNF(Formula root, Transformer transformer) {
		return Executor.run(transformer, root);
	}

	public static boolean isNF(Formula formula, NormalForm normalForm, boolean clausal) {
		final NFTester tester = getNFTester(formula, normalForm);
		return clausal ? tester.isClausalNf() : tester.isNf();
	}

	static NFTester getNFTester(Formula formula, NormalForm normalForm) {
		NFTester tester;
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

	public static Formula toClausalNF(Formula formula, NormalForm normalForm) {
		switch (normalForm) {
		case CNF:
			if (formula instanceof Literal) {
				formula = new And(new Or(formula));
			} else if (formula instanceof Or) {
				formula = new And(formula);
			} else {
				formula.mapChildren(child -> (child instanceof Literal ? new Or((Literal) child) : child));
			}
			break;
		case DNF:
			if (formula instanceof Literal) {
				formula = new Or(new And(formula));
			} else if (formula instanceof And) {
				formula = new Or(new And(formula));
			} else {
				formula.mapChildren(child -> (child instanceof Literal ? new And((Literal) child) : child));
			}
			break;
		default:
		}
		return formula;
	}

}
