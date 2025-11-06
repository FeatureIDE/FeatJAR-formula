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
package de.featjar.formula.io.textual;

import de.featjar.base.data.Result;
import de.featjar.base.io.format.IFormat;
import de.featjar.formula.assignment.BooleanAssignmentGroups;

public class BooleanAssignmentGroupsString implements IFormat<BooleanAssignmentGroups> {
    @Override
    public Result<String> serialize(BooleanAssignmentGroups booleanAssignmentGroups) {
        return Result.of(String.valueOf(booleanAssignmentGroups));
    }

    @Override
    public BooleanAssignmentGroupsString getInstance() {
        return this;
    }

    @Override
    public String getName() {
        return "BooleanAssignmentGroup";
    }

    @Override
    public boolean supportsWrite() {
        return true;
    }

    @Override
    public boolean supportsParse() {
        return false;
    }

    @Override
    public String getFileExtension() {
        return "text";
    }
}
