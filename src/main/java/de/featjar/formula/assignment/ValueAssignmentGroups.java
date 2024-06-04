/*
 * Copyright (C) 2024 FeatJAR-Development-Team
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
package de.featjar.formula.assignment;

import de.featjar.formula.VariableMap;
import java.util.List;

/**
 * {@link AAssignmentGroups} implementation for {@link AValueAssignment}.
 *
 * @author Sebastian Krieter
 */
public class ValueAssignmentGroups extends AAssignmentGroups<AValueAssignment> {

    public ValueAssignmentGroups(VariableMap variableMap, List<? extends List<? extends AValueAssignment>> assignment) {
        super(variableMap, assignment);
    }
}
