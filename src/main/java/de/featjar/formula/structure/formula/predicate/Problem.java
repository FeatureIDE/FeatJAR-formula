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
package de.featjar.formula.structure.formula.predicate;

import de.featjar.formula.structure.Expression;
import de.featjar.formula.structure.TerminalExpression;

import java.util.List;
import java.util.Objects;

/**
 * A placeholder for when an expression cannot be parsed.
 *
 * @author Sebastian Krieter
 */
public class Problem extends TerminalExpression implements Predicate {
    private final de.featjar.base.data.Problem problem;

    public Problem(de.featjar.base.data.Problem problem) {
        this.problem = problem;
    }

    public de.featjar.base.data.Problem getProblem() {
        return problem;
    }

    @Override
    public String getName() {
        return problem.toString();
    }

    @Override
    public Problem cloneNode() {
        return new Problem(problem);
    }

    @Override
    public boolean equalsNode(Expression other) {
        return super.equalsNode(other) && Objects.equals(problem, ((Problem) other).problem);
    }

    @Override
    public Object evaluate(List<?> values) {
        return null;
    }
}
