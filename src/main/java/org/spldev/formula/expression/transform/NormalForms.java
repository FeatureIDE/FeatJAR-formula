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

import org.spldev.formula.expression.AuxiliaryRoot;
import org.spldev.formula.expression.Formula;
import org.spldev.formula.expression.atomic.literal.Literal;
import org.spldev.formula.expression.compound.And;
import org.spldev.formula.expression.compound.Or;
import org.spldev.util.Result;
import org.spldev.util.job.Executor;
import org.spldev.util.tree.Trees;

/**
 * Transforms propositional formulas into (clausal) CNF or DNF.
 *
 * @author Sebastian Krieter
 */
public final class NormalForms {

	private NormalForms() {
	}

	public enum NormalForm {
		TsyetinCNF, CNF, DNF
	}

	public static Formula simplifyForNF(Formula formula) {
		final AuxiliaryRoot auxiliaryRoot = new AuxiliaryRoot(formula);
		Trees.traverse(auxiliaryRoot, new EquivalenceVisitor());
		Trees.traverse(auxiliaryRoot, new DeMorganVisitor());
		Trees.traverse(auxiliaryRoot, new TreeSimplifier());
		return (Formula) auxiliaryRoot.getChild();
	}

	public static Result<Formula> toNF(Formula root, NormalForm normalForm) {
		final Transformer transformer;
		switch (normalForm) {
		case TsyetinCNF:
			transformer = new CNFTseytinTransformer();
			break;
		case CNF:
			transformer = new CNFDistributiveLawTransformer();
			break;
		case DNF:
			transformer = new DNFDistributiveLawTransformer();
			break;
		default:
			throw new IllegalStateException(String.valueOf(normalForm));
		}
		return Executor.run(transformer, root);
	}

	public static boolean isNF(Formula formula, NormalForm normalForm, boolean clausal) {
		final NFTester tester = isNF(formula, normalForm);
		return clausal ? tester.isClausalNf() : tester.isNf;
	}

	static NFTester isNF(Formula formula, NormalForm normalForm) {
		NFTester tester;
		switch (normalForm) {
		case TsyetinCNF:
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

	static Formula toClausalNF(Formula formula, NormalForm normalForm) {
		switch (normalForm) {
		case TsyetinCNF:
		case CNF:
			if (formula instanceof Literal) {
				formula = new And(new Or(formula));
			} else if (formula instanceof Or) {
				formula = new And(new Or(formula));
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
