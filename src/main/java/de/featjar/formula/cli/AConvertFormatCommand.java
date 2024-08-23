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
package de.featjar.formula.cli;

import de.featjar.base.FeatJAR;
import de.featjar.base.cli.ACommand;
import de.featjar.base.cli.Option;
import de.featjar.base.cli.OptionList;
import de.featjar.base.data.Result;
import de.featjar.base.io.IO;
import de.featjar.base.io.format.IFormat;
import de.featjar.formula.io.FormulaFormats;
import de.featjar.formula.structure.IFormula;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Converts the format of a given formula.
 *
 * @author Andreas Gerasimow
 */
public abstract class AConvertFormatCommand extends ACommand {

    /**
     * Specifies output format.
     */
    public static final Option<String> FORMAT_OPTION = Option.newOption("format", Option.StringParser)
            .setDescription("Specifies output format.")
            .setRequired(true);

    @Override
    public void run(OptionList optionParser) {
        Path outputPath = optionParser.getResult(OUTPUT_OPTION).orElse(null);
        String formatString = optionParser.getResult(FORMAT_OPTION).orElseThrow();

        try {
            @SuppressWarnings("unchecked")
            IFormat<IFormula> format = FeatJAR.extension((Class<IFormat<IFormula>>) Class.forName(formatString));

            IFormula formula = optionParser
                    .getResult(INPUT_OPTION)
                    .flatMap(p -> IO.load(p, FormulaFormats.getInstance()))
                    .orElseThrow();

            IFormula modifiedFormula = modifyFormula(formula);

            if (outputPath == null || Files.isDirectory(outputPath)) {
                Result<String> string = format.serialize(modifiedFormula);
                if (string.isPresent()) {
                    FeatJAR.log().message(string.get());
                } else {
                    FeatJAR.log().problems(string.getProblems());
                }
            } else {
                IO.save(modifiedFormula, outputPath, format);
            }
        } catch (ClassNotFoundException | IOException e) {
            FeatJAR.log().error(e);
        }
    }

    /**
     * Modifies the formula before output
     * @param formula Formula to modify
     * @return The modified formula
     */
    protected abstract IFormula modifyFormula(IFormula formula);
}
