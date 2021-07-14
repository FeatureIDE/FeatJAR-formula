/* -----------------------------------------------------------------------------
 * Formula-Lib - Library to represent and edit propositional formulas.
 * Copyright (C) 2021  Sebastian Krieter
 * 
 * This file is part of Formula-Lib.
 * 
 * Formula-Lib is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Formula-Lib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Formula-Lib.  If not, see <https://www.gnu.org/licenses/>.
 * 
 * See <https://github.com/skrieter/formula> for further information.
 * -----------------------------------------------------------------------------
 */
package org.spldev.formula.expression;

import java.util.*;
import java.util.stream.*;

import org.spldev.formula.expression.ValueVisitor.*;
import org.spldev.formula.expression.atomic.*;
import org.spldev.formula.expression.atomic.literal.*;
import org.spldev.formula.expression.transform.*;
import org.spldev.util.tree.*;
import org.spldev.util.tree.visitor.*;

public class Formulas {

	public enum NormalForm {
		CNF, DNF, ClausalCNF, ClausalDNF
	}

	public static Optional<Object> evaluate(Expression expression, Assignment assignment) {
		final ValueVisitor visitor = new ValueVisitor(assignment);
		visitor.setUnkown(UnkownVariableHandling.ERROR);
		return Trees.traverse(expression, visitor);
	}

	public static boolean isCNF(Formula formula) {
		return isNF(formula, NormalForm.CNF);
	}

	public static boolean isDNF(Formula formula) {
		return isNF(formula, NormalForm.DNF);
	}

	public static boolean isClausalCNF(Formula formula) {
		return isNF(formula, NormalForm.ClausalCNF);
	}

	public static boolean isClausalDNF(Formula formula) {
		return isNF(formula, NormalForm.ClausalDNF);
	}

	public static boolean isNF(Formula formula, NormalForm normalForm) {
		switch (normalForm) {
		case CNF:
			return Trees.traverse(formula, new CNFVisitor()).get();
		case DNF:
			return Trees.traverse(formula, new DNFVisitor()).get();
		case ClausalCNF: {
			final CNFVisitor visitor = new CNFVisitor();
			Trees.traverse(formula, visitor);
			return visitor.isClausalNf();
		}
		case ClausalDNF: {
			final DNFVisitor visitor = new DNFVisitor();
			Trees.traverse(formula, visitor);
			return visitor.isClausalNf();
		}
		default:
			throw new IllegalStateException(String.valueOf(normalForm));
		}
	}

	public static Formula toCNF(Formula formula) {
		return toNF(formula, NormalForm.CNF);
	}

	public static Formula toDNF(Formula formula) {
		return toNF(formula, NormalForm.DNF);
	}

	public static Formula toClausalCNF(Formula formula) {
		return toNF(formula, NormalForm.ClausalCNF);
	}

	public static Formula toClausalDNF(Formula formula) {
		return toNF(formula, NormalForm.ClausalDNF);
	}

	private static Formula toNF(Formula formula, NormalForm normalForm) {
		if (isNF(formula, normalForm)) {
			return Trees.cloneTree(formula);
		}
		return distributiveLawTransform(simplifyForNF(formula), normalForm);
	}

	public static Formula simplifyForNF(Formula formula) {
		return NFTransformer.simplifyForNF(formula);
	}

	public static Formula distributiveLawTransform(Formula root, NormalForm normalForm) {
		return NFTransformer.distributiveLawTransform(root, normalForm);
	}

	public static Expression manipulate(Expression node, TreeVisitor<Void, Expression> visitor) {
		final AuxiliaryRoot auxiliaryRoot = new AuxiliaryRoot(Trees.cloneTree(node));
		Trees.traverse(auxiliaryRoot, visitor);
		return auxiliaryRoot.getChild();
	}

	public static int getMaxDepth(Expression expression) {
		return Trees.traverse(expression, new TreeDepthCounter()).get();
	}

	public static Stream<String> getVariableStream(Expression node) {
		return Trees.preOrderStream(node).filter(n -> n instanceof Terminal).map(n -> ((Terminal) n).getName())
			.distinct();
	}

	public static List<String> getVariables(Expression node) {
		return getVariableStream(node).collect(Collectors.toList());
	}

	public static VariableMap createVariableMapping(Expression node) {
		return new VariableMap(getVariables(node));
	}

}
