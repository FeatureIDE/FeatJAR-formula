/*
 * Copyright (C) 2023 FeatJAR-Development-Team
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
 * See <https://github.com/FeatJAR> for further information.
 */
package de.featjar.formula.io.csv;

import de.featjar.base.data.Result;
import de.featjar.base.io.format.IFormat;
import de.featjar.formula.analysis.bool.ABooleanAssignment;
import de.featjar.formula.analysis.bool.BooleanAssignmentSpace;
import java.util.List;

/**
 * Writes a list of configuration.
 *
 * @author Sebastian Krieter
 */
public class BooleanSolutionListCSVFormat implements IFormat<BooleanAssignmentSpace> {

    public static final String ID = BooleanSolutionListCSVFormat.class.getCanonicalName();

    @Override
    public Result<String> serialize(BooleanAssignmentSpace configurationList) {
        final StringBuilder csv = new StringBuilder();
        csv.append("Configuration");
        final List<String> names = configurationList.getVariableMap().getVariableNames();
        for (final String name : names) {
            csv.append(';');
            csv.append(name);
        }
        csv.append('\n');
        int configurationIndex = 0;
        for (final ABooleanAssignment configuration :
                configurationList.getGroups().get(0)) {
            csv.append(configurationIndex++);
            final int[] literals = configuration.get();
            for (int i = 0; i < literals.length; i++) {
                csv.append(';');
                final int l = literals[i];
                csv.append(l == 0 ? '0' : l > 0 ? '+' : '-');
            }
            csv.append('\n');
        }
        return Result.of(csv.toString());
    }

    @Override
    public String getFileExtension() {
        return "csv";
    }

    @Override
    public BooleanSolutionListCSVFormat getInstance() {
        return this;
    }

    @Override
    public String getIdentifier() {
        return ID;
    }

    @Override
    public boolean supportsSerialize() {
        return true;
    }

    @Override
    public boolean supportsParse() {
        return true;
    }

    @Override
    public String getName() {
        return "ConfigurationList";
    }
}
