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
package org.spldev.formula.expression;

import java.util.*;
import java.util.stream.*;

import org.spldev.formula.expression.ValueVisitor.*;
import org.spldev.formula.expression.atomic.*;
import org.spldev.formula.expression.atomic.literal.*;
import org.spldev.formula.expression.term.*;
import org.spldev.formula.expression.transform.*;
import org.spldev.formula.expression.transform.NormalForms.*;
import org.spldev.util.*;
import org.spldev.util.tree.*;
import org.spldev.util.tree.visitor.*;

public final class Formulas {

	private Formulas() {
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
		return NormalForms.toNF(formula, NormalForm.CNF);
	}

	public static Result<Formula> toTseytinCNF(Formula formula) {
		return NormalForms.toNF(formula, NormalForm.TSEYTIN_CNF);
	}

	public static Result<Formula> toDNF(Formula formula) {
		return NormalForms.toNF(formula, NormalForm.DNF);
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

	public static Expression mergeVariableMaps(Expression expression) {
		final List<VariableMap> maps = Formulas.getVariableStream(expression).map(Variable::getVariableMap).distinct()
			.collect(Collectors.toList());
		if (maps.size() > 1) {
			final VariableMap newMap = VariableMap.fromNames(
				maps.stream().flatMap(v -> v.getNames().stream()).distinct().collect(Collectors.toList()));
			Trees.postOrderStream(expression) //
				.forEach(e -> e.mapChildren(v -> (v instanceof Variable) ? ((Variable<?>) v).adapt(newMap) : v));
		}
		return expression;
	}

}
