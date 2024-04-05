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
public abstract class AConvertFormatCommand implements ICommand {

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

            IFormula modifiedFormula = modifyFormula(formula);

            if (outputPath == null || outputPath.toString().equals("results")) {
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
