/*
 * Copyright (C) 2025 FeatJAR-Development-Team
 *
 * This file is part of FeatJAR-formula.
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
package de.featjar.formula.io;

import de.featjar.base.FeatJAR;
import de.featjar.base.io.format.AFormats;
import de.featjar.base.io.format.IFormat;
import de.featjar.formula.assignment.BooleanAssignmentGroups;
import java.util.Objects;
import java.util.Optional;

/**
 * Extension point for {@link AFormats formats} for {@link BooleanAssignmentGroups}.
 *
 * @author Sebastian Krieter
 */
public class BooleanAssignmentGroupsFormats extends AFormats<BooleanAssignmentGroups> {

    public static BooleanAssignmentGroupsFormats getInstance() {
        return FeatJAR.extensionPoint(BooleanAssignmentGroupsFormats.class);
    }

    /**
     * {@return an array of the names of all installed formats for BooleanAssignmentGroup}.
     */
    public static String[] getNames() {
        return getInstance().getExtensions().stream().map(IFormat::getName).toArray(String[]::new);
    }

    /**
     * {@return an array of the names of all installed formats for BooleanAssignmentGroup}.
     */
    public static Optional<IFormat<BooleanAssignmentGroups>> getGefFormatByName(String name) {
        return getInstance().getExtensions().stream()
                .filter(f -> Objects.equals(name, f.getName()))
                .findFirst();
    }
}
