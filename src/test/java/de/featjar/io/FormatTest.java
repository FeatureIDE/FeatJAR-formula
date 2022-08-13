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
 * See <https://github.com/FeatJAR/formula> for further information.
 */
package de.featjar.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import de.featjar.formula.structure.Formula;
import de.featjar.util.data.Result;
import de.featjar.util.io.IO;
import de.featjar.util.io.format.Format;
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

    private static final Path rootDirectory = Paths.get("src/test/resources");
    private static final Path formatsDirectory = rootDirectory.resolve("formats");

    public static void testLoad(Formula formula1, String name, Format<Formula> format) {
        assertEquals(format.getClass().getCanonicalName(), format.getIdentifier());
        assertTrue(format.supportsParse());
        assertFalse(format.supportsSerialize());

        for (final Path file : getFileList(name, format)) {
            final Formula formula2 = load(format, file);
            compareFormulas(formula1, formula2);
        }
    }

    public static void testLoadAndSave(Formula formula1, String name, Format<Formula> format) {
        assertEquals(format.getClass().getCanonicalName(), format.getIdentifier());
        assertTrue(format.supportsParse());
        assertTrue(format.supportsSerialize());
        final Formula formula3 = saveAndLoad(formula1, format);
        for (final Path file : getFileList(name, format)) {
            final Formula formula2 = load(format, file);
            final Formula formula4 = saveAndLoad(formula2, format);
            compareFormulas(formula1, formula2);
            compareFormulas(formula1, formula3);
            compareFormulas(formula1, formula4);
            compareFormulas(formula2, formula3);
            compareFormulas(formula2, formula4);
            compareFormulas(formula3, formula4);
        }
    }

    private static <T> T load(Format<T> format, Path path) {
        return IO.load(path, format).get();
    }

    private static List<Path> getFileList(String name, Format<Formula> format) {
        final String namePattern = Pattern.quote(name) + "_\\d\\d[.]" + format.getFileExtension();
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
            return null;
        }
    }

    private static void compareFormulas(final Formula formula1, final Formula formula2) {
        assertEquals(formula1, formula2, "Formulas are different");
        if (formula1 != null) {
            assertEquals(formula1.getVariableMap(), formula2.getVariableMap(), "Variables are different");
        }
    }

    private static <T> T saveAndLoad(T object, Format<T> format) {
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
