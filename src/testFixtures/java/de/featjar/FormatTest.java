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
 * See <https://github.com/FeatJAR> for further information.
 */
package de.featjar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import de.featjar.base.data.Result;
import de.featjar.base.io.IO;
import de.featjar.base.io.format.IFormat;
import de.featjar.base.io.format.IFormatSupplier;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests formats.
 *
 * @author Sebastian Krieter
 */
public class FormatTest {

    public static <T> void testLoad(T expression1, String name, int count, IFormat<T> format) {
        assertEquals(format.getClass().getCanonicalName(), format.getIdentifier());
        assertTrue(format.supportsParse());
        assertFalse(format.supportsSerialize());

        for (final T expression2 : getFileList(name, format, count)) {
            compare(expression1, expression2);
        }
    }

    public static <T> void testSaveAndLoad(T expression1, String name, IFormat<T> format) {
        assertEquals(format.getClass().getCanonicalName(), format.getIdentifier());
        assertTrue(format.supportsParse());
        assertTrue(format.supportsSerialize());
        final T expression3 = saveAndLoad(expression1, format);
        compare(expression1, expression3);
    }

    public static <T> void testLoadAndSave(T expression1, String name, int count, IFormat<T> format) {
        assertEquals(format.getClass().getCanonicalName(), format.getIdentifier());
        assertTrue(format.supportsParse());
        assertTrue(format.supportsSerialize());
        final T expression3 = saveAndLoad(expression1, format);
        for (final T expression2 : getFileList(name, format, count)) {
            final T expression4 = saveAndLoad(expression2, format);
            compare(expression1, expression2);
            compare(expression1, expression3);
            compare(expression1, expression4);
            compare(expression2, expression3);
            compare(expression2, expression4);
            compare(expression3, expression4);
        }
    }

    private static <T> List<T> getFileList(String name, IFormat<T> format, int count) {
        ArrayList<T> list = new ArrayList<>(count);
        for (int i = 1; i <= count; i++) {
            list.add(Common.load(String.format("formats/%s_%02d", name, i), IFormatSupplier.of(format)));
        }
        assertFalse(list.isEmpty());
        return list;
    }

    private static void compare(final Object expression1, final Object expression2) {
        assertEquals(expression1, expression2, "Objects are different");
    }

    private static <T> T saveAndLoad(T object, IFormat<T> format) {
        if (object == null) {
            return null;
        }
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            IO.save(object, out, format);
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
