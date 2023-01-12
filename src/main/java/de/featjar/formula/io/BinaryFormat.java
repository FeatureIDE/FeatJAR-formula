/*
 * Copyright (C) 2023 Sebastian Krieter, Elias Kuiter
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
package de.featjar.formula.io;

import de.featjar.base.io.binary.ABinaryFormat;
import de.featjar.formula.analysis.bool.*;

// TODO implement saving/loading constants
/**
 * Reads / Writes a list of configuration.
 *
 * @author Sebastian Krieter
 * @deprecated
 */
@Deprecated
public class BinaryFormat extends ABinaryFormat<ABooleanAssignmentList<?>> { // DNFBinaryFormat?
    //    @Override
    //    public void write(BooleanAssignmentList<?, ?> solutions, OutputMapper outputMapper) throws IOException {
    //        final OutputStream outputStream = outputMapper.get().getOutputStream();
    //        final List<String> names = solutions.getVariableMap().getVariableNames();
    //        writeInt(outputStream, names.size());
    //        for (final String name : names) {
    //            writeString(outputStream, name);
    //        }
    //        final BitSet bs = new BitSet(names.size());
    //        writeInt(outputStream, solutions.size());
    //        for (final BooleanAssignment configuration : solutions.getAll()) {
    //            final int[] literals = configuration.getIntegers();
    //            for (int i = 0; i < literals.length; i++) {
    //                bs.set(i, literals[i] > 0);
    //            }
    //            final byte[] byteArray = bs.toByteArray();
    //            writeInt(outputStream, byteArray.length);
    //            writeBytes(outputStream, byteArray);
    //            bs.clear();
    //        }
    //        outputStream.flush();
    //    }
    //
    //    @Override
    //    public Result<BooleanAssignmentList<?, ?>> parse(InputMapper inputMapper) {
    //        final InputStream inputStream = inputMapper.get().getInputStream();
    //        try {
    //            final int numberOfVariables = readInt(inputStream);
    //            final List<String> variableNames = new ArrayList<>(numberOfVariables);
    //            for (int i = 0; i < numberOfVariables; i++) {
    //                variableNames.add(readString(inputStream));
    //            }
    //            final VariableMap termMap = VariableMap.empty();
    //            variableNames.forEach(termMap::add);
    //            final int numberOfSolutions = readInt(inputStream);
    //            final BooleanSolutionList LiteralSolutionList = new BooleanSolutionList(numberOfSolutions);
    //            for (int i = 0; i < numberOfSolutions; i++) {
    //                final BitSet bs = BitSet.valueOf(readBytes(inputStream, readInt(inputStream)));
    //                final int[] literals = new int[numberOfVariables];
    //                for (int j = 0; j < numberOfVariables; j++) {
    //                    literals[j] = bs.get(j) ? (j + 1) : -(j + 1);
    //                }
    //                LiteralSolutionList.add(new BooleanSolution(literals, false));
    //            }
    //            BooleanSolutionList solutionList = new BooleanSolutionList(LiteralSolutionList);
    //            solutionList.setVariableMap(termMap);
    //            return Result.of(solutionList);
    //        } catch (final IOException e) {
    //            return Result.empty(e);
    //        }
    //    }

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
