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
package de.featjar.clauses.solutions.io;

import de.featjar.clauses.LiteralList;
import de.featjar.clauses.solutions.SolutionList;
import de.featjar.formula.structure.atomic.literal.VariableMap;
import de.featjar.util.data.Result;
import de.featjar.util.io.InputMapper;
import de.featjar.util.io.OutputMapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Optional;

// TODO implement saving/loading constants
/**
 * Reads / Writes a list of configuration.
 *
 * @author Sebastian Krieter
 */
public class BinaryFormat extends de.featjar.util.io.binary.BinaryFormat<SolutionList> {
    @Override
    public void write(SolutionList configurationList, OutputMapper outputMapper) throws IOException {
        final OutputStream outputStream = outputMapper.get().getOutputStream();
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
    public Result<SolutionList> parse(InputMapper inputMapper) {
        final InputStream inputStream = inputMapper.get().getInputStream();
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
                solutionList.add(new LiteralList(literals, LiteralList.Order.INDEX, false));
            }
            return Result.of(new SolutionList(variableMap, solutionList));
        } catch (final IOException e) {
            return Result.empty(e);
        }
    }

    @Override
    public Optional<String> getFileExtension() {
        return Optional.empty();
    }

    @Override
    public BinaryFormat getInstance() {
        return this;
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
