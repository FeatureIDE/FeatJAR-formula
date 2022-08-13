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
 * See <https://github.com/FeatJAR/formula> for further information.
 */
package de.featjar.formula.structure;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.featjar.formula.io.textual.FormulaFormat;
import de.featjar.formula.structure.ValueVisitor.UnknownVariableHandling;
import de.featjar.formula.structure.atomic.Assignment;
import de.featjar.formula.structure.atomic.literal.VariableMap;
import de.featjar.formula.structure.atomic.literal.VariableMap.Variable;
import de.featjar.formula.structure.transform.CNFTransformer;
import de.featjar.formula.structure.transform.DNFTransformer;
import de.featjar.formula.structure.transform.NormalForms;
import de.featjar.formula.structure.transform.NormalForms.NormalForm;
import de.featjar.formula.structure.transform.VariableMapSetter;
import de.featjar.util.data.Result;
import de.featjar.util.io.IO;
import de.featjar.util.tree.Trees;
import de.featjar.util.tree.visitor.TreeDepthCounter;
import de.featjar.util.tree.visitor.TreePrinter;
import de.featjar.util.tree.visitor.TreeVisitor;

public final class Formulas {

	private Formulas() {
	}

	public static String printTree(Formula formula) {
		final TreePrinter visitor = new TreePrinter();
		visitor.setFilter(n -> (!(n instanceof Variable)));
		return Trees.traverse(formula, visitor).orElse("");
	}

	public static String printFormula(Formula formula) {
		try (final ByteArrayOutputStream s = new ByteArrayOutputStream()) {
			IO.save(formula, s, new FormulaFormat());
			return s.toString();
		} catch (IOException e) {
			return "";
		}
	}

	public static Optional<Object> evaluate(Formula expression, Assignment assignment) {
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

	public static Formula manipulate(Formula node, TreeVisitor<Void, Formula> visitor) {
		final AuxiliaryRoot auxiliaryRoot = new AuxiliaryRoot(Trees.cloneTree(node));
		Trees.traverse(auxiliaryRoot, visitor);
		return auxiliaryRoot.getChild();
	}

	public static int getMaxDepth(Formula expression) {
		return Trees.traverse(expression, new TreeDepthCounter()).get();
	}

	public static Stream<Variable> getVariableStream(Formula node) {
		final Stream<Variable> stream = Trees.preOrderStream(node).filter(n -> n instanceof Variable)
			.map(n -> (Variable) n);
		return stream.distinct();
	}

	public static List<Variable> getVariables(Formula node) {
		return getVariableStream(node).collect(Collectors.toList());
	}

	public static List<String> getVariableNames(Formula node) {
		return getVariableStream(node).map(Variable::getName).collect(Collectors.toList());
	}

	public static <T extends Formula> T create(Function<VariableMap, T> fn) {
		return fn.apply(new VariableMap());
	}

	/**
	 * Child formulas are cloned and their variable maps merged. That is, the
	 * composed formula exists independently of its children. This is useful e.g.
	 * for composing several feature model (interface) formulas.
	 */
	public static <T, U extends Formula> T compose(Function<List<U>, T> fn, List<U> expressions) {
		return fn.apply(cloneWithSharedVariableMap(expressions));
	}

	@SafeVarargs
	public static <T, U extends Formula> T compose(Function<List<U>, T> fn, U... expressions) {
		return compose(fn, Arrays.asList(expressions));
	}

	/**
	 * Composes formulas (e.g., for feature model fragments and interfaces) by
	 * cloning and variable map merging. Assumes that the supplied formulas are
	 * partly independent, partly dependent (on common variables). Leaves the input
	 * formulas and their variable maps untouched by returning copies.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Formula> List<T> cloneWithSharedVariableMap(List<T> children) {
		final List<VariableMap> maps = children.stream().map(f -> f.getVariableMap().orElseGet(VariableMap::new))
			.collect(Collectors.toList());
		VariableMap composedMap = VariableMap.merge(maps);
		final List<T> collect = children.stream()
			.map(f -> (T) Formulas.manipulate(f, new VariableMapSetter(composedMap)))
			.collect(Collectors.toList());
		return collect;
	}
}
