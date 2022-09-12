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
package de.featjar.formula.transform;

import de.featjar.formula.structure.Formula;
import de.featjar.formula.structure.VariableMap;
import de.featjar.formula.structure.VariableMap.Constant;
import de.featjar.formula.structure.VariableMap.Variable;
import de.featjar.base.tree.visitor.TreeVisitor;
import java.util.List;

public class VariableMapSetter implements TreeVisitor<Void, Formula> {

    private final VariableMap variableMap;

    public VariableMapSetter(VariableMap variableMap) {
        this.variableMap = variableMap;
    }

    private Formula replaceValueTerms(Formula node) {
        if (node instanceof Variable) {
            final Variable replacement = variableMap
                    .getVariable(node.getName())
                    .orElseThrow(() ->
                            new IllegalArgumentException("Map does not contain variable with name " + node.getName()));
            return replacement;
        } else if (node instanceof Constant) {
            final Constant replacement = variableMap
                    .getConstant(node.getName())
                    .orElseThrow(() ->
                            new IllegalArgumentException("Map does not contain constant with name " + node.getName()));
            return replacement;
        }
        return node;
    }

    @Override
    public TraversalAction lastVisit(List<Formula> path) {
        final Formula node = getCurrentNode(path);
        node.replaceChildren(this::replaceValueTerms);
        return TreeVisitor.super.lastVisit(path);
    }
}
