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
package de.featjar.formula.io.dimacs;

import de.featjar.formula.structure.Formula;
import de.featjar.base.data.Result;
import de.featjar.base.io.InputMapper;
import de.featjar.base.io.format.Format;
import de.featjar.base.io.format.ParseProblem;
import java.text.ParseException;
import java.util.Optional;

/**
 * Reads and writes feature models in the DIMACS CNF format.
 *
 * @author Sebastian Krieter
 * @author Timo G&uuml;nther
 */
public class DIMACSFormat implements Format<Formula> {

    public static final String ID = DIMACSFormat.class.getCanonicalName();

    @Override
    public String serialize(Formula formula) {
        final DimacsWriter w = new DimacsWriter(formula);
        w.setWritingVariableDirectory(true);
        return w.write();
    }

    @Override
    public Result<Formula> parse(InputMapper inputMapper) {
        final DimacsReader r = new DimacsReader();
        r.setReadingVariableDirectory(true);
        try {
            // TODO use getLines() instead
            return Result.of(r.read(inputMapper.get().read().get()));
        } catch (final ParseException e) {
            return Result.empty(new ParseProblem(e, e.getErrorOffset()));
        } catch (final Exception e) {
            return Result.empty(e);
        }
    }

    @Override
    public DIMACSFormat getInstance() {
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
        return "DIMACS";
    }

    @Override
    public Optional<String> getFileExtension() {
        return Optional.of("dimacs");
    }
}
