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

import de.featjar.formula.io.textual.NodeWriter.Notation;
import de.featjar.formula.structure.Formula;
import de.featjar.formula.structure.compound.And;
import de.featjar.util.io.format.Format;

public class ACTSFormat implements Format<Formula> {

    public static final String ID = ACTSFormat.class.getCanonicalName();

    @Override
    public String serialize(Formula object) {
        final StringBuilder sb = new StringBuilder();
        sb.append("[System]\nname: system\n[Parameter]\n");

        for (String name : object.getVariableMap().get().getVariableNames()) {
            sb.append(name);
            sb.append(" (boolean) : true, false\n");
        }
        sb.append("[Constraint]\n");

        final NodeWriter nodeWriter = new NodeWriter();
        nodeWriter.setEnforceBrackets(false);
        nodeWriter.setNotation(Notation.INFIX);
        nodeWriter.setSymbols(JavaSymbols.INSTANCE);
        if (object instanceof And) {
            for (Formula formula : object.getChildren()) {
                sb.append(nodeWriter.write(formula));
                sb.append("\n");
            }
        } else {
            sb.append(nodeWriter.write(object));
        }

        return sb.toString();
    }

    @Override
    public boolean supportsSerialize() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return ID;
    }

    @Override
    public String getFileExtension() {
        return "txt";
    }

    @Override
    public String getName() {
        return "ACTS";
    }
}
