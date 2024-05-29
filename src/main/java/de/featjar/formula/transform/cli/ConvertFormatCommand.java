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
package de.featjar.formula.transform.cli;

import de.featjar.base.FeatJAR;
import de.featjar.base.cli.ICommand;
import de.featjar.base.cli.Option;
import de.featjar.base.cli.OptionList;
import de.featjar.base.data.Result;
import de.featjar.base.io.IO;
import de.featjar.base.io.format.IFormat;
import de.featjar.formula.io.FormulaFormats;
import de.featjar.formula.structure.formula.IFormula;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Converts the format of a given formula.
 *
 * @author Andreas Gerasimow
 */
public class ConvertFormatCommand implements ICommand {

    /**
     * Specifies output format.
     */
    public static final Option<String> FORMAT_OPTION = new Option<>("format", Option.StringParser)
            .setDescription("Specifies output format.")
            .setRequired(true);

    @Override
    public List<Option<?>> getOptions() {
        return List.of(FORMAT_OPTION, INPUT_OPTION, OUTPUT_OPTION);
    }

    @Override
    public void run(OptionList optionParser) {
        Path outputPath = optionParser.getResult(OUTPUT_OPTION).orElse(null);
        String formatString = optionParser.getResult(FORMAT_OPTION).orElse(null);

        try {
            Class<IFormat<IFormula>> classObj = (Class<IFormat<IFormula>>) Class.forName(formatString);
            IFormat<IFormula> format = FeatJAR.extension(classObj);

            IFormula formula = optionParser
                    .getResult(INPUT_OPTION)
                    .flatMap(p -> IO.load(p, FormulaFormats.getInstance()))
                    .orElseThrow();

            if (outputPath == null || outputPath.toString().equals("results")) {
                Result<String> string = format.serialize(formula);
                if (string.isPresent()) {
                    FeatJAR.log().message(string.get());
                } else {
                    string.getProblems().forEach(FeatJAR.log()::message);
                }
            } else {
                IO.save(formula, outputPath, format);
            }

        } catch (ClassNotFoundException | IOException e) {
            FeatJAR.log().error(e);
        }
    }

    @Override
    public String getDescription() {
        return "Converts the format of a given formula.";
    }

    @Override
    public String getShortName() {
        return "convert-format";
    }
}
