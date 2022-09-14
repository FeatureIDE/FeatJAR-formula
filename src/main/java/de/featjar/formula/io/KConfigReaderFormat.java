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

import de.featjar.formula.io.textual.NodeReader;
import de.featjar.formula.io.textual.PropositionalModelSymbols;
import de.featjar.formula.structure.Formula;
import de.featjar.formula.structure.formula.connective.And;
import de.featjar.base.data.Problem;
import de.featjar.base.data.Result;
import de.featjar.base.io.InputMapper;
import de.featjar.base.io.format.Format;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

public class KConfigReaderFormat implements Format<Formula> {

    public static final String ID = KConfigReaderFormat.class.getCanonicalName();

    @Override
    public Result<Formula> parse(InputMapper inputMapper) {
        final ArrayList<Problem> problems = new ArrayList<>();
        final NodeReader nodeReader = new NodeReader();
        nodeReader.setSymbols(PropositionalModelSymbols.INSTANCE);
        return Result.of(
                new And(inputMapper
                        .get()
                        .getLineStream() //
                        .map(String::trim) //
                        .filter(l -> !l.isEmpty()) //
                        .filter(l -> !l.startsWith("#")) //
                        // fix non-boolean constraints
                        .map(l -> l.replace("=", "_"))
                        .map(l -> l.replace(":", "_"))
                        .map(l -> l.replace(".", "_"))
                        .map(l -> l.replace(",", "_"))
                        .map(l -> l.replace("/", "_"))
                        .map(l -> l.replace("\\", "_"))
                        .map(l -> l.replace(" ", "_"))
                        .map(l -> l.replace("-", "_"))
                        .map(l -> l.replaceAll("def\\((\\w+)\\)", "$1"))
                        .map(nodeReader::read) //
                        .peek(r -> problems.addAll(r.getProblems()))
                        .filter(Result::isPresent)
                        .map(Result::get) //
                        .collect(Collectors.toList())),
                problems);
    }

    @Override
    public boolean supportsParse() {
        return true;
    }

    @Override
    public boolean supportsSerialize() {
        return false;
    }

    @Override
    public String getIdentifier() {
        return ID;
    }

    @Override
    public Optional<String> getFileExtension() {
        return Optional.of("model");
    }

    @Override
    public String getName() {
        return "KConfigReader";
    }
}
