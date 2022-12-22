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
package de.featjar.formula.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import de.featjar.base.data.Result;
import de.featjar.base.io.IO;
import de.featjar.base.io.format.IFormat;
import de.featjar.formula.structure.formula.IFormula;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Tests formats.
 *
 * @author Sebastian Krieter
 */
public class FormatTest {

    public static final Path rootDirectory = Paths.get("src/test/resources");
    public static final Path formatsDirectory = rootDirectory.resolve("formats");

    public static void testLoad(IFormula expression1, String name, IFormat<IFormula> format) {
        assertEquals(format.getClass().getCanonicalName(), format.getIdentifier());
        assertTrue(format.supportsParse());
        assertFalse(format.supportsSerialize());

        for (final Path file : getFileList(name, format)) {
            final IFormula expression2 = load(format, file);
            compareFormulas(expression1, expression2);
        }
    }

    public static void testLoadAndSave(IFormula expression1, String name, IFormat<IFormula> format) {
        assertEquals(format.getClass().getCanonicalName(), format.getIdentifier());
        assertTrue(format.supportsParse());
        assertTrue(format.supportsSerialize());
        final IFormula expression3 = saveAndLoad(expression1, format);
        for (final Path file : getFileList(name, format)) {
            final IFormula expression2 = load(format, file);
            final IFormula expression4 = saveAndLoad(expression2, format);
            compareFormulas(expression1, expression2);
            compareFormulas(expression1, expression3);
            compareFormulas(expression1, expression4);
            compareFormulas(expression2, expression3);
            compareFormulas(expression2, expression4);
            compareFormulas(expression3, expression4);
        }
    }

    private static <T> T load(IFormat<T> format, Path path) {
        return IO.load(path, format).get();
    }

    @SuppressWarnings("resource")
    private static List<Path> getFileList(String name, IFormat<IFormula> format) {
        final String namePattern = Pattern.quote(name) + "_\\d\\d";
        try {
            final List<Path> fileList = Files.walk(formatsDirectory.resolve(format.getName()))
                    .filter(Files::isRegularFile)
                    .filter(f -> f.getFileName().toString().matches(namePattern))
                    .sorted()
                    .collect(Collectors.toList());
            assertNotNull(fileList);
            assertFalse(fileList.isEmpty());
            return fileList;
        } catch (final IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
            throw new RuntimeException();
        }
    }

    private static void compareFormulas(final IFormula expression1, final IFormula expression2) {
        assertEquals(expression1, expression2, "Formulas are different");
    }

    private static <T> T saveAndLoad(T object, IFormat<T> format) {
        if (object == null) {
            return null;
        }
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            IO.save(object, out, format);
            out.flush();
        } catch (final IOException e) {
            e.printStackTrace();
            fail("Failed saving object: " + e.getMessage());
        }
        final byte[] memory = out.toByteArray();
        assertNotNull(memory);
        assertTrue(memory.length > 0, "Saved object is empty");

        final Result<T> result = IO.load(new ByteArrayInputStream(memory), format);
        assertTrue(result.isPresent(), "Failed loading saved object");
        return result.get();
    }
}
