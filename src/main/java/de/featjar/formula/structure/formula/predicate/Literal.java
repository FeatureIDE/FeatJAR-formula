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

import de.featjar.formula.structure.*;
import de.featjar.formula.structure.term.Term;
import de.featjar.formula.structure.term.value.Variable;

import java.util.List;
import java.util.Objects;

/**
 * Expresses "A == true" (or A) and "A == false" (or !A) constraints.
 * Evaluates to {@code true} iff its child evaluates to {@link #isPositive}.
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public class Literal extends NonTerminalExpression implements UnaryExpression, PolarPredicate {

    private boolean isPositive;

    public Literal(Literal literal) {
        this.isPositive = literal.isPositive;
    }

    public Literal(boolean isPositive, Term term) {
        super(term);
        this.isPositive = isPositive;
        if (!Objects.equals(term.getType(), Boolean.class))
            throw new IllegalArgumentException("Boolean literals only accept Boolean terms");
    }

    public Literal(Term term) {
        this(true, term);
    }

    public Literal(String variableName) {
        this(true, new Variable(variableName));
    }

    public Literal(boolean isPositive, String variableName) {
        this(isPositive, new Variable(variableName));
    }

    @Override
    public boolean isPositive() {
        return isPositive;
    }

    public void setPositive(boolean isPositive) {
        this.isPositive = isPositive;
    }

    @Override
    public Literal invert() {
        return new Literal(!isPositive, (Term) getExpression());
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<? extends Term> getChildren() {
        return (List<? extends Term>) super.getChildren();
    }

    @Override
    public String getName() {
        return isPositive ? "+" : "-";
    }

    @Override
    public Class<Boolean> getChildrenType() {
        return Boolean.class;
    }

    @Override
    public Boolean evaluate(List<?> values) {
        final Boolean b = (Boolean) values.get(0);
        return b != null ? isPositive == b : null;
    }

    @Override
    public Literal cloneNode() {
        return new Literal(this);
    }

    @Override
    public boolean equalsNode(Expression other) {
        return super.equalsNode(other) && isPositive == ((Literal) other).isPositive;
    }
}
