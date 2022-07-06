/* -----------------------------------------------------------------------------
 * Formula Lib - Library to represent and edit propositional formulas.
 * Copyright (C) 2021-2022  Sebastian Krieter
 * 
 * This file is part of Formula Lib.
 * 
 * Formula Lib is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Formula Lib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Formula Lib.  If not, see <https://www.gnu.org/licenses/>.
 * 
 * See <https://github.com/skrieter/formula> for further information.
 * -----------------------------------------------------------------------------
 */
package org.spldev.clauses.solutions.io;

import java.io.*;
import java.util.*;

import org.spldev.clauses.*;
import org.spldev.clauses.LiteralList.*;
import org.spldev.clauses.solutions.*;
import org.spldev.formula.structure.atomic.literal.*;
import org.spldev.util.data.*;
import org.spldev.util.io.file.InputFileMapper;
import org.spldev.util.io.file.OutputFileMapper;

// TODO implement saving/loading constants
/**
 * Reads / Writes a list of configuration.
 *
 * @author Sebastian Krieter
 */
public class BinaryFormat extends org.spldev.util.io.binary.BinaryFormat<SolutionList> {

	public static final String ID = BinaryFormat.class.getCanonicalName();

	@Override
	public void write(SolutionList configurationList, OutputFileMapper outputFileMapper) throws IOException {
		final OutputStream outputStream = outputFileMapper.getMainFile().getOutputStream();
		final List<String> names = configurationList.getVariableMap().getVariableNames();
		writeInt(outputStream, names.size());
		for (final String name : names) {
			writeString(outputStream, name);
		}
		final BitSet bs = new BitSet(names.size());
		final List<LiteralList> solutions = configurationList.getSolutions();
		writeInt(outputStream, solutions.size());
		for (final LiteralList configuration : solutions) {
			final int[] literals = configuration.getLiterals();
			for (int i = 0; i < literals.length; i++) {
				bs.set(i, literals[i] > 0);
			}
			final byte[] byteArray = bs.toByteArray();
			writeInt(outputStream, byteArray.length);
			writeBytes(outputStream, byteArray);
			bs.clear();
		}
		outputStream.flush();
	}

	@Override
	public Result<SolutionList> parse(InputFileMapper inputFileMapper) {
		final InputStream inputStream = inputFileMapper.getMainFile().getInputStream();
		try {
			final int numberOfVariables = readInt(inputStream);
			final List<String> variableNames = new ArrayList<>(numberOfVariables);
			for (int i = 0; i < numberOfVariables; i++) {
				variableNames.add(readString(inputStream));
			}
			final VariableMap variableMap = new VariableMap();
			variableNames.forEach(variableMap::addBooleanVariable);
			final int numberOfSolutions = readInt(inputStream);
			final List<LiteralList> solutionList = new ArrayList<>(numberOfSolutions);
			for (int i = 0; i < numberOfSolutions; i++) {
				final BitSet bs = BitSet.valueOf(readBytes(inputStream, readInt(inputStream)));
				final int[] literals = new int[numberOfVariables];
				for (int j = 0; j < numberOfVariables; j++) {
					literals[j] = bs.get(j) ? (j + 1) : -(j + 1);
				}
				solutionList.add(new LiteralList(literals, Order.INDEX, false));
			}
			return Result.of(new SolutionList(variableMap, solutionList));
		} catch (final IOException e) {
			return Result.empty(e);
		}
	}

	@Override
	public String getFileExtension() {
		return "sample";
	}

	@Override
	public BinaryFormat getInstance() {
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
		return "BinarySample";
	}

}
