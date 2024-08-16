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
 * See <https://github.com/FeatureIDE/FeatJAR-formula> for further information.
 */
package de.featjar.formula.io;

import de.featjar.base.data.Problem;
import de.featjar.base.data.Result;
import de.featjar.base.io.format.IFormat;
import de.featjar.base.io.input.AInputMapper;
import de.featjar.formula.io.textual.ExpressionParser;
import de.featjar.formula.io.textual.PropositionalModelSymbols;
import de.featjar.formula.structure.IExpression;
import de.featjar.formula.structure.IFormula;
import de.featjar.formula.structure.connective.And;
import de.featjar.formula.structure.connective.Reference;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Parses feature-model formula files created by KConfigReader.
 * TODO: this is currently mostly a hack and should be parsed properly as first-order formulas
 *
 * @author Elias Kuiter
 * @author Andreas Gerasimow
 */
public class KConfigReaderFormat implements IFormat<IExpression> {

    @Override
    public Result<IExpression> parse(AInputMapper inputMapper) {
        final ArrayList<Problem> problems = new ArrayList<>();
        final ExpressionParser expressionParser = new ExpressionParser();
        expressionParser.setSymbols(PropositionalModelSymbols.INSTANCE);

        List<IFormula> subformulas = inputMapper
                .get()
                .getLineStream()
                .map(String::trim)
                .filter(l -> !l.isEmpty())
                .filter(l -> !l.startsWith("#"))
                .filter(l -> containsDef(l, problems))
                // "convert" non-boolean constraints into boolean constraints
                // TODO: parse as proper first-order formulas
                .map(l -> l.replace("=", "_"))
                .map(l -> l.replace(":", "_"))
                .map(l -> l.replace(".", "_"))
                .map(l -> l.replace(",", "_"))
                .map(l -> l.replace("/", "_"))
                .map(l -> l.replace("\\", "_"))
                .map(l -> l.replace(" ", "_"))
                .map(l -> l.replace("-", "_"))
                .map(l -> l.replaceAll("def\\((\\w+)\\)", "$1"))
                .map(expressionParser::parse)
                .peek(r -> problems.addAll(r.getProblems()))
                .filter(Result::isPresent)
                .map(expressionResult -> (IFormula) expressionResult.get())
                .collect(Collectors.toList());

        return Result.of(new Reference(subformulas.size() == 1 ? subformulas.get(0) : new And(subformulas)), problems);
    }

    public boolean containsDef(String line, ArrayList<Problem> problems) {
        Pattern pattern = Pattern.compile("def\\(.+\\)");
        Matcher matcher = pattern.matcher(line);
        boolean found = matcher.find();
        if (!found) {
            problems.add(new Problem("Line contains no def(...)"));
        }
        return found;
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
    public String getFileExtension() {
        return "model";
    }

    @Override
    public String getName() {
        return "KConfigReader";
    }
}
