/* -----------------------------------------------------------------------------
 * Formula-Lib - Library to represent and edit propositional formulas.
 * Copyright (C) 2021  Sebastian Krieter
 * 
 * This file is part of Formula-Lib.
 * 
 * Formula-Lib is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Formula-Lib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Formula-Lib.  If not, see <https://www.gnu.org/licenses/>.
 * 
 * See <https://github.com/skrieter/formula> for further information.
 * -----------------------------------------------------------------------------
 */
package org.spldev.formula.expression.io;

import java.io.*;
import java.util.function.*;

/**
 * Reads a source line by line, skipping empty lines.
 *
 * @author Sebastian Krieter
 */
public class LineIterator implements Supplier<String> {

	private final BufferedReader reader;
	private String line = null;
	private int lineCount = 0;

	public LineIterator(BufferedReader reader) {
		this.reader = reader;
	}

	@Override
	public String get() {
		try {
			do {
				line = reader.readLine();
				if (line == null) {
					return null;
				}
				lineCount++;
			} while (line.trim().isEmpty());
			return line;
		} catch (final IOException e) {
			return null;
		}
	}

	public String currentLine() {
		return line;
	}

	public void setCurrentLine(String line) {
		this.line = line;
	}

	public int getLineCount() {
		return lineCount;
	}

}
