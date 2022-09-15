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

import de.featjar.base.io.IO;
import de.featjar.base.tree.Trees;
import de.featjar.base.tree.visitor.TreePrinter;
import de.featjar.formula.io.textual.FormulaFormat;
import de.featjar.formula.structure.Expression;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.BinaryOperator;

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
