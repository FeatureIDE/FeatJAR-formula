/*
 * Copyright (C) 2025 FeatJAR-Development-Team
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
package de.featjar.formula.io.dimacs;

import de.featjar.base.data.Pair;
import de.featjar.base.io.input.AInputMapper;
import de.featjar.formula.VariableMap;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Generic parser for multiple problems in DIMACS format.
 *
 * @author Sebastian Krieter
 */
public class DimacsListParser extends DimacsParser {

    /**
     * Parses the input.
     *
     * @param inputMapper The source to read from.
     * @return a pair containing the variable map and a list of lists of clauses (one list per problem in the file).
     * @throws IOException    if the reader encounters a problem.
     * @throws ParseException if the input does not conform to the DIMACS CNF file format.
     */
    public Pair<VariableMap, List<List<int[]>>> parseList(AInputMapper inputMapper) throws ParseException, IOException {
        init(inputMapper);
        List<List<int[]>> groups = new ArrayList<>();
        while (nonEmptyLineIterator.currentLine() != null) {
            groups.add(readLines());
        }
        return new Pair<>(indexVariables, groups);
    }
}
