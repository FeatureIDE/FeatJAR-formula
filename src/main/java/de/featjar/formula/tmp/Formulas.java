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
 * See <https://github.com/FeatureIDE/FeatJAR-formula> for further information.
 */
package de.featjar.formula.tmp;

import de.featjar.base.data.Result;
import de.featjar.base.io.IO;
import de.featjar.base.tree.Trees;
import de.featjar.base.tree.visitor.TreeDepthCounter;
import de.featjar.base.tree.visitor.TreePrinter;
import de.featjar.base.tree.visitor.TreeVisitor;
import de.featjar.formula.io.textual.FormulaFormat;
import de.featjar.formula.structure.Expression;
import de.featjar.formula.transformer.CNFTransformer;
import de.featjar.formula.transformer.DNFTransformer;
import de.featjar.formula.visitor.NormalForms;
import de.featjar.formula.visitor.NormalForms.NormalForm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Formulas {

    public static String printTree(Expression expression) {
        final TreePrinter visitor = new TreePrinter();
        visitor.setFilter(n -> (!(n instanceof Variable)));
        return Trees.traverse(expression, visitor).orElse("");
    }

    public static String printFormula(Expression expression) {
        try (final ByteArrayOutputStream s = new ByteArrayOutputStream()) {
            IO.save(expression, s, new FormulaFormat());
            return s.toString();
        } catch (IOException e) {
            return "";
        }
    }

    public static boolean isCNF(Expression expression) {
        return NormalForms.isNormalForm(expression, NormalForm.CNF, false);
    }

    public static boolean isDNF(Expression expression) {
        return NormalForms.isNormalForm(expression, NormalForm.DNF, false);
    }

    public static boolean isClausalCNF(Expression expression) {
        return NormalForms.isNormalForm(expression, NormalForm.CNF, true);
    }

    public static Result<Expression> toCNF(Expression expression) {
        return NormalForms.toNF(expression, new CNFTransformer());
    }

    public static Result<Expression> toCNF(Expression expression, int maximumNumberOfLiterals) {
        final CNFTransformer transformer = new CNFTransformer();
        transformer.setMaximumNumberOfLiterals(maximumNumberOfLiterals);
        return NormalForms.toNF(expression, transformer);
    }

    public static Result<Expression> toDNF(Expression expression) {
        return NormalForms.toNF(expression, new DNFTransformer());
    }

    public static Expression manipulate(Expression expression, TreeVisitor<Expression, Void> visitor) {
        final AuxiliaryRoot auxiliaryRoot = new AuxiliaryRoot(Trees.clone(expression));
        Trees.traverse(auxiliaryRoot, visitor);
        return auxiliaryRoot.getChild();
    }

    public static int getMaxDepth(Expression expression) {
        return Trees.traverse(expression, new TreeDepthCounter()).get();
    }

    public static Stream<Variable> getVariableStream(Expression expression) {
        final Stream<Variable> stream =
                Trees.preOrderStream(expression).filter(n -> n instanceof Variable).map(n -> (Variable) n);
        return stream.distinct();
    }

    public static List<Variable> getVariables(Expression expression) {
        return getVariableStream(expression).collect(Collectors.toList());
    }

    public static List<String> getVariableNames(Expression expression) {
        return getVariableStream(expression).map(Variable::getName).collect(Collectors.toList());
    }

    public static <T extends Expression> T create(Function<TermMap, T> fn) {
        return fn.apply(new TermMap());
    }

    /**
     * {@return a list of values reduced to a single value}
     *
     * @param values the values
     * @param binaryOperator the binary operator
     * @param <T> the type of the value
     */
    @SuppressWarnings("unchecked")
    public static <T> T reduce(List<?> values, final BinaryOperator<T> binaryOperator) {
        if (values.stream().anyMatch(Objects::isNull)) {
            return null;
        }
        return values.stream().map(l -> (T) l).reduce(binaryOperator).orElse(null);
    }

    // visitor for replacing all subformulas
}
