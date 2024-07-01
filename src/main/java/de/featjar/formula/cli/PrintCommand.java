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
import de.featjar.base.io.IO;
import de.featjar.base.tree.Trees;
import de.featjar.formula.io.FormulaFormats;
import de.featjar.formula.io.textual.ExpressionSerializer;
import de.featjar.formula.io.textual.Symbols;
import de.featjar.formula.structure.IFormula;
import java.util.Arrays;
import java.util.Optional;

/**
 * Prints the formula in a readable format.
 *
 * @author Andreas Gerasimow
 */
public class PrintCommand extends ACommand {

    /**
     * Defines the tab string.
     */
    public static final Option<String> TAB_OPTION = Option.newOption("tab", Option.StringParser)
            .setDescription("Defines the tab string.")
            .setDefaultValue(ExpressionSerializer.STANDARD_TAB_STRING);

    /**
     * Defines the notation.
     */
    public static final Option<ExpressionSerializer.Notation> NOTATION_OPTION = Option.newOption(
                    "notation", (arg) -> ExpressionSerializer.Notation.valueOf(arg.toUpperCase()))
            .setDescription("Defines the notation. Possible options: "
                    + Arrays.toString(ExpressionSerializer.Notation.values()))
            .setDefaultValue(ExpressionSerializer.STANDARD_NOTATION);

    /**
     * Defines the separator string.
     */
    public static final Option<String> SEPARATOR_OPTION = Option.newOption("separator", Option.StringParser)
            .setDescription("Defines the separator string.")
            .setDefaultValue(ExpressionSerializer.STANDARD_SEPARATOR);

    /**
     * Defines the symbols.
     */
    public static final Option<Symbols> SYMBOLS_OPTION = Option.newOption("format", (arg) -> {
                try {
                    return (Symbols) Class.forName(arg).getField("INSTANCE").get(null);
                } catch (IllegalAccessException | NoSuchFieldException | ClassNotFoundException e) {
                    FeatJAR.log().error(e);
                    return ExpressionSerializer.STANDARD_SYMBOLS;
                }
            })
            .setDescription("Defines the symbols.")
            .setDefaultValue(ExpressionSerializer.STANDARD_SYMBOLS);

    /**
     * Defines the new line string.
     */
    public static final Option<String> NEW_LINE_OPTION = Option.newOption("newline", Option.StringParser)
            .setDescription("Defines the new line string.")
            .setDefaultValue(ExpressionSerializer.STANDARD_NEW_LINE);

    /**
     * Enforces parentheses.
     */
    public static final Option<Boolean> ENFORCE_PARENTHESES_OPTION = Option.newFlag("enforce-parentheses")
            .setDescription("Enforces parentheses.")
            .setDefaultValue(ExpressionSerializer.STANDARD_ENFORCE_PARENTHESES);

    /**
     * Enquotes whitespace.
     */
    public static final Option<Boolean> ENQUOTE_WHITESPACE_OPTION = Option.newFlag("enquote-whitespace")
            .setDescription("Enquotes whitespace.")
            .setDefaultValue(ExpressionSerializer.STANDARD_ENQUOTE_WHITESPACE);

    @Override
    public void run(OptionList optionParser) {
        String tab = optionParser.getResult(TAB_OPTION).get();
        ExpressionSerializer.Notation notation =
                optionParser.getResult(NOTATION_OPTION).get();
        String separator = optionParser.getResult(SEPARATOR_OPTION).get();
        Symbols symbols = optionParser.getResult(SYMBOLS_OPTION).get();
        String newLine = optionParser.getResult(NEW_LINE_OPTION).get();
        boolean ep = optionParser.getResult(ENFORCE_PARENTHESES_OPTION).get();
        boolean ew = optionParser.getResult(ENQUOTE_WHITESPACE_OPTION).get();

        IFormula formula = optionParser
                .getResult(INPUT_OPTION)
                .flatMap(p -> IO.load(p, FormulaFormats.getInstance()))
                .orElseThrow();

        ExpressionSerializer serializer = new ExpressionSerializer();

        serializer.setTab(tab);
        serializer.setNotation(notation);
        serializer.setSeparator(separator);
        serializer.setSymbols(symbols);
        serializer.setNewLine(newLine);
        serializer.setEnforceParentheses(ep);
        serializer.setEnquoteWhitespace(ew);

        if (formula != null) {
            String formulaString = Trees.traverse(formula, serializer).orElse("");
            FeatJAR.log().message(formulaString);
        }
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Prints the formula in a readable format.");
    }

    @Override
    public Optional<String> getShortName() {
        return Optional.of("print");
    }
}
