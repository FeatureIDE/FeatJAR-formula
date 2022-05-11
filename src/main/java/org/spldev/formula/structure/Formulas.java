/* -----------------------------------------------------------------------------
 * Formula Lib - Library to represent and edit propositional formulas.
 * Copyright (C) 2021-2022  Sebastian Krieter
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
package org.spldev.formula.structure;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import org.spldev.formula.io.textual.*;
import org.spldev.formula.structure.ValueVisitor.*;
import org.spldev.formula.structure.atomic.*;
import org.spldev.formula.structure.term.*;
import org.spldev.formula.structure.transform.*;
import org.spldev.formula.structure.transform.NormalForms.*;
import org.spldev.util.data.*;
import org.spldev.util.io.*;
import org.spldev.util.tree.*;
import org.spldev.util.tree.visitor.*;

public final class Formulas {

	private Formulas() {
	}

	public static String printTree(Formula formula) {
		final TreePrinter visitor = new TreePrinter();
		visitor.setFilter(n -> (!(n instanceof Variable<?>)));
		return Trees.traverse(formula, visitor).orElse("");
	}

	public static String printFormula(Formula formula) {
		try (final ByteArrayOutputStream s = new ByteArrayOutputStream()) {
			FileHandler.save(formula, s, new FormulaFormat());
			return s.toString();
		} catch (IOException e) {
			return "";
		}
	}

	public static Optional<Object> evaluate(Expression expression, Assignment assignment) {
		final ValueVisitor visitor = new ValueVisitor(assignment);
		visitor.setUnknown(UnknownVariableHandling.ERROR);
		return Trees.traverse(expression, visitor);
	}

	public static boolean isCNF(Formula formula) {
		return NormalForms.isNF(formula, NormalForm.CNF, false);
	}

	public static boolean isDNF(Formula formula) {
		return NormalForms.isNF(formula, NormalForm.DNF, false);
	}

	public static boolean isClausalCNF(Formula formula) {
		return NormalForms.isNF(formula, NormalForm.CNF, true);
	}

	public static Result<Formula> toCNF(Formula formula) {
		return NormalForms.toNF(formula, new CNFTransformer());
	}

	public static Result<Formula> toCNF(Formula formula, int maximumNumberOfLiterals) {
		final CNFTransformer transformer = new CNFTransformer();
		transformer.setMaximumNumberOfLiterals(maximumNumberOfLiterals);
		return NormalForms.toNF(formula, transformer);
	}

	public static Result<Formula> toDNF(Formula formula) {
		return NormalForms.toNF(formula, new DNFTransformer());
	}

	public static Expression manipulate(Expression node, TreeVisitor<Void, Expression> visitor) {
		final AuxiliaryRoot auxiliaryRoot = new AuxiliaryRoot(Trees.cloneTree(node));
		Trees.traverse(auxiliaryRoot, visitor);
		return auxiliaryRoot.getChild();
	}

	public static int getMaxDepth(Expression expression) {
		return Trees.traverse(expression, new TreeDepthCounter()).get();
	}

	public static Stream<Variable<?>> getVariableStream(Expression node) {
		final Stream<Variable<?>> stream = Trees.preOrderStream(node).filter(n -> n instanceof Variable)
			.map(n -> (Variable<?>) n);
		return stream.distinct();
	}

	public static List<Variable<?>> getVariables(Expression node) {
		return getVariableStream(node).collect(Collectors.toList());
	}

}
