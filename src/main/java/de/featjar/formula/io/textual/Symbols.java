/*
 * Copyright (C) 2023 Sebastian Krieter, Elias Kuiter
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

import de.featjar.formula.structure.Formula;
import de.featjar.formula.structure.compound.And;
import de.featjar.formula.structure.compound.AtLeast;
import de.featjar.formula.structure.compound.AtMost;
import de.featjar.formula.structure.compound.Between;
import de.featjar.formula.structure.compound.Biimplies;
import de.featjar.formula.structure.compound.Choose;
import de.featjar.formula.structure.compound.Compound;
import de.featjar.formula.structure.compound.Exists;
import de.featjar.formula.structure.compound.ForAll;
import de.featjar.formula.structure.compound.Implies;
import de.featjar.formula.structure.compound.Not;
import de.featjar.formula.structure.compound.Or;
import de.featjar.util.data.Pair;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Symbols {

    public enum Operator {
        NOT("not", 0),
        AND("and", 6),
        OR("or", 5),
        IMPLIES("implies", 4),
        EQUALS("equals", 3),
        CHOOSE("choose", 2),
        ATLEAST("atleast", 2),
        BETWEEN("between", 2),
        ATMOST("atmost", 2),
        EXISTS("exists", 1),
        FORALL("forall", 1),
        UNKNOWN("?", -1);

        private String defaultName;
        private int priority;

        Operator(String defaultName, int priority) {
            this.defaultName = defaultName;
            this.priority = priority;
        }

        public int getPriority() {
            return priority;
        }
    }

    private final Map<String, Operator> symbolToOperator = new HashMap<>();
    private final Map<Operator, String> operatorToSymbol = new HashMap<>();

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

    private final void setSymbol(Operator operator, String name) {
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

    public static Operator getOperator(Formula node) throws IllegalArgumentException {
        if (node instanceof Compound) {
            if (node instanceof Not) {
                return Operator.NOT;
            }
            if (node instanceof And) {
                return Operator.AND;
            }
            if (node instanceof Or) {
                return Operator.OR;
            }
            if (node instanceof Implies) {
                return Operator.IMPLIES;
            }
            if (node instanceof Biimplies) {
                return Operator.EQUALS;
            }
            if (node instanceof AtLeast) {
                return Operator.ATLEAST;
            }
            if (node instanceof AtMost) {
                return Operator.ATMOST;
            }
            if (node instanceof Choose) {
                return Operator.CHOOSE;
            }
            if (node instanceof Between) {
                return Operator.BETWEEN;
            }
            if (node instanceof ForAll) {
                return Operator.FORALL;
            }
            if (node instanceof Exists) {
                return Operator.EXISTS;
            }
            return Operator.UNKNOWN;
        }
        throw new IllegalArgumentException("Unrecognized node type: " + node.getClass());
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
