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
import de.featjar.base.io.input.AInputMapper;
import de.featjar.formula.assignment.BooleanAssignmentValueMap;

public class BooleanAssignmentValueMapFormat implements IFormat<BooleanAssignmentValueMap> {

    @Override
    public boolean supportsParse() {
        return true;
    }

    @Override
    public boolean supportsSerialize() {
        return true;
    }

    @Override
    public Result<BooleanAssignmentValueMap> parse(AInputMapper inputMapper) {
        return new BooleanAssignmentValueMapParser().parse(inputMapper);
    }

    @Override
    public Result<String> serialize(BooleanAssignmentValueMap object) {
        return new BooleanAssignmentValueMapParser().serialize(object);
    }

    @Override
    public String getName() {
        return "BooleanAssignmentValueMap";
    }
}
