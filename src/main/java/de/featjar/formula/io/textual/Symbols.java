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
package de.featjar.formula.io.textual;

import de.featjar.base.data.Maps;
import de.featjar.base.data.Pair;
import de.featjar.formula.structure.IExpression;
import de.featjar.formula.structure.formula.connective.*;
import java.util.Collection;
import java.util.LinkedHashMap;

public class Symbols {

    public enum Operator {
        NOT("not", 0),
        AND("and", 6),
        OR("or", 5),
        IMPLIES("implies", 4),
        BIIMPLIES("biimplies", 3),
        CHOOSE("choose", 2),
        ATLEAST("atleast", 2),
        BETWEEN("between", 2),
        ATMOST("atmost", 2),
        EXISTS("exists", 1),
        FORALL("forall", 1),
        UNKNOWN("?", -1);

        private final String defaultName;
        private final int priority;

        Operator(String defaultName, int priority) {
            this.defaultName = defaultName;
            this.priority = priority;
        }

        public int getPriority() {
            return priority;
        }
    }

    private final LinkedHashMap<String, Operator> symbolToOperator = Maps.empty();
    private final LinkedHashMap<Operator, String> operatorToSymbol = Maps.empty();

    private final boolean textual;

    public Symbols(Collection<Pair<Operator, String>> symbols, boolean textual) {
        this(symbols, textual, true);
    }

    public Symbols(Collection<Pair<Operator, String>> symbols, boolean textual, boolean addTextualSymbols) {
        this.textual = textual;
        if (addTextualSymbols) {
            for (final Operator operator : Operator.values()) {
                setSymbol(operator, operator.defaultName);
            }
        }
        for (final Pair<Operator, String> pair : symbols) {
            setSymbol(pair.getKey(), pair.getValue());
        }
    }

    private void setSymbol(Operator operator, String name) {
        symbolToOperator.put(name, operator);
        operatorToSymbol.put(operator, name);
    }

    public Operator parseSymbol(String symbol) {
        final Operator operator = symbolToOperator.get(symbol);
        return operator != null ? operator : Operator.UNKNOWN;
    }

    public String getSymbol(Operator operator) {
        final String symbol = operatorToSymbol.get(operator);
        return symbol != null ? symbol : operator.defaultName;
    }

    public static Operator getOperator(IExpression expression) throws IllegalArgumentException {
        if (expression instanceof IConnective) {
            if (expression instanceof Not) {
                return Operator.NOT;
            }
            if (expression instanceof And) {
                return Operator.AND;
            }
            if (expression instanceof Or) {
                return Operator.OR;
            }
            if (expression instanceof Implies) {
                return Operator.IMPLIES;
            }
            if (expression instanceof BiImplies) {
                return Operator.BIIMPLIES;
            }
            if (expression instanceof AtLeast) {
                return Operator.ATLEAST;
            }
            if (expression instanceof AtMost) {
                return Operator.ATMOST;
            }
            if (expression instanceof Choose) {
                return Operator.CHOOSE;
            }
            if (expression instanceof Between) {
                return Operator.BETWEEN;
            }
            if (expression instanceof ForAll) {
                return Operator.FORALL;
            }
            if (expression instanceof Exists) {
                return Operator.EXISTS;
            }
            return Operator.UNKNOWN;
        }
        throw new IllegalArgumentException("Unrecognized node type: " + expression.getClass());
    }

    public boolean isTextual() {
        return textual;
    }

    /**
     * Assigns a number to every operator. For instance, that {@link And} has a
     * higher order than {@link Or} means that <em>(A and B or C)</em> is equal to
     * <em>((A and B) or C)</em>.
     *
     * @param operator operator type
     * @return the order assigned to the type of node
     */
    protected int getOrder(Operator operator) {
        return operator != null ? operator.getPriority() : Operator.UNKNOWN.getPriority();
    }
}
