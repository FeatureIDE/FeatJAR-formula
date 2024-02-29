/*
 * Copyright (C) 2024 FeatJAR-Development-Team
 *
 * This file is part of FeatJAR-formula-analysis-sat4j.
 *
 * formula-analysis-sat4j is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3.0 of the License,
 * or (at your option) any later version.
 *
 * formula-analysis-sat4j is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with formula-analysis-sat4j. If not, see <https://www.gnu.org/licenses/>.
 *
 * See <https://github.com/FeatureIDE/FeatJAR-formula-analysis-sat4j> for further information.
 */
package de.featjar;

import static de.featjar.formula.structure.Expressions.and;
import static de.featjar.formula.structure.Expressions.literal;
import static de.featjar.formula.structure.Expressions.not;
import static de.featjar.formula.structure.Expressions.or;
import static de.featjar.formula.structure.Expressions.reference;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import de.featjar.base.FeatJAR;
import de.featjar.base.computation.Cache;
import de.featjar.base.data.Result;
import de.featjar.base.io.IO;
import de.featjar.base.io.format.IFormat;
import de.featjar.base.io.format.IFormatSupplier;
import de.featjar.base.log.CallerFormatter;
import de.featjar.base.log.Log;
import de.featjar.base.log.TimeStampFormatter;
import de.featjar.formula.io.FormulaFormats;
import de.featjar.formula.structure.formula.IFormula;
import java.net.URL;

public class Common {

    static {
        FeatJAR.configure()
                .log(c -> c.logToSystemOut(Log.Verbosity.MESSAGE, Log.Verbosity.INFO, Log.Verbosity.DEBUG))
                .log(c -> c.logToSystemErr(Log.Verbosity.ERROR, Log.Verbosity.WARNING))
                .log(c -> c.addFormatter(new TimeStampFormatter()))
                .log(c -> c.addFormatter(new CallerFormatter()))
                .cache(c -> c.setCachePolicy(Cache.CachePolicy.CACHE_NONE))
                .initialize();
    }

    public static <T> T load(String modelPath, IFormatSupplier<T> formatSupplier) {
        URL systemResource = ClassLoader.getSystemResource(modelPath);
        if (systemResource == null) {
            fail(modelPath);
        }
        Result<T> load = IO.load(systemResource, formatSupplier);
        assertTrue(load.isPresent(), load::printProblems);
        return load.get();
    }

    public static <T> T load(String modelPath, IFormat<T> format) {
        URL systemResource = ClassLoader.getSystemResource(modelPath);
        if (systemResource == null) {
            fail(modelPath);
        }
        return IO.load(systemResource, format).orElseThrow();
    }

    public static IFormula loadFormula(String modelPath) {
        return load(modelPath, FormulaFormats.getInstance());
    }

    public static IFormula getFormula(String name) {
        switch (name) {
            case "faulty": {
                return null;
            }
            case "empty": {
                return reference(and());
            }
            case "void": {
                return reference(and(or()));
            }
            case "123-n1n2n3": {
                return reference(and(
                        or(literal("1"), literal("2"), literal("3")),
                        or(literal(false, "1"), literal(false, "2"), literal(false, "3"))));
            }
            case "ABC-nAnBnC": {
                return reference(and(
                        or(literal("A"), literal("B"), literal("C")),
                        or(not(literal("A")), or(not(literal("B")), not(literal("C"))))));
            }
            case "nA": {
                return reference(not(literal("A")));
            }
            case "nAB": {
                return reference(or(not(literal("A")), literal("B")));
            }
            default:
                fail(name);
                return null;
        }
    }
}
