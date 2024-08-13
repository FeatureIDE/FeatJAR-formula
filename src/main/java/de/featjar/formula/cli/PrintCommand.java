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
import de.featjar.base.cli.Flag;
import de.featjar.base.cli.ICommand;
import de.featjar.base.cli.Option;
import de.featjar.base.cli.OptionList;
import de.featjar.base.io.IO;
import de.featjar.base.tree.Trees;
import de.featjar.formula.io.FormulaFormats;
import de.featjar.formula.io.textual.ExpressionSerializer;
import de.featjar.formula.io.textual.Symbols;
import de.featjar.formula.structure.IFormula;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Prints the formula in a readable format.
 *
 * @author Andreas Gerasimow
 */
public class PrintCommand implements ICommand {

    public enum WhitespaceString {
        TAB(ExpressionSerializer.STANDARD_TAB_STRING), NEWLINE(ExpressionSerializer.STANDARD_NEW_LINE), SPACE(" ");

        private final String whitespaceValue;

        WhitespaceString(String value) {
            this.whitespaceValue = value;
        }

        public String getWhitespaceValue() {
            return whitespaceValue;
        }
    }

    public static final String CUSTOM_STRING_PREFIX = "CUSTOM:";

    /**
     * Defines the tab string.
     */
    public static final Option<String> TAB_OPTION = new Option<>("tab", Option.StringParser)
            .setDescription("Defines the tab value. Possible options: "
                    + Arrays.toString(WhitespaceString.values())
                    + ". For custom value, type " + CUSTOM_STRING_PREFIX + "<value>")
            .setDefaultValue(WhitespaceString.TAB.toString());

    /**
     * Defines the notation.
     */
    public static final Option<ExpressionSerializer.Notation> NOTATION_OPTION = new Option<>(
                    "notation", (arg) -> ExpressionSerializer.Notation.valueOf(arg.toUpperCase()))
            .setDescription("Defines the notation. Possible options: "
                    + Arrays.toString(ExpressionSerializer.Notation.values()))
            .setDefaultValue(ExpressionSerializer.STANDARD_NOTATION);

    /**
     * Defines the symbols.
     */
    public static final Option<Symbols> SYMBOLS_OPTION = new Option<>("format", (arg) -> {
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
    public static final Option<String> NEW_LINE_OPTION = new Option<>("newline", Option.StringParser)
            .setDescription("Defines the new line value. Possible options: "
                    + Arrays.toString(WhitespaceString.values())
                    + ". For custom value, type " + CUSTOM_STRING_PREFIX + "<value>")
            .setDefaultValue(WhitespaceString.NEWLINE.toString());

    /**
     * Enforces parentheses.
     */
    public static final Option<Boolean> ENFORCE_PARENTHESES_OPTION = new Flag("enforce-parentheses")
            .setDescription("Enforces parentheses.")
            .setDefaultValue(ExpressionSerializer.STANDARD_ENFORCE_PARENTHESES);

    /**
     * Enquotes whitespace.
     */
    public static final Option<Boolean> ENQUOTE_WHITESPACE_OPTION = new Flag("enquote-whitespace")
            .setDescription("Enquotes whitespace.")
            .setDefaultValue(ExpressionSerializer.STANDARD_ENQUOTE_WHITESPACE);

    @Override
    public List<Option<?>> getOptions() {
        return List.of(
                INPUT_OPTION,
                TAB_OPTION,
                NOTATION_OPTION,
                SYMBOLS_OPTION,
                NEW_LINE_OPTION,
                ENFORCE_PARENTHESES_OPTION,
                ENQUOTE_WHITESPACE_OPTION);
    }

    @Override
    public void run(OptionList optionParser) {
        String tab = optionParser.getResult(TAB_OPTION).get();
        ExpressionSerializer.Notation notation =
                optionParser.getResult(NOTATION_OPTION).get();
        Symbols symbols = optionParser.getResult(SYMBOLS_OPTION).get();
        String newLine = optionParser.getResult(NEW_LINE_OPTION).get();
        boolean ep = optionParser.getResult(ENFORCE_PARENTHESES_OPTION).get();
        boolean ew = optionParser.getResult(ENQUOTE_WHITESPACE_OPTION).get();

        IFormula formula = optionParser
                .getResult(INPUT_OPTION)
                .flatMap(p -> IO.load(p, FormulaFormats.getInstance()))
                .orElseThrow();

        ExpressionSerializer serializer = new ExpressionSerializer();

        tab = getWhitespaceString(tab);
        newLine = getWhitespaceString(newLine);

        if (tab == null || newLine == null) {
            return;
        }

        serializer.setTab(tab);
        serializer.setNotation(notation);
        serializer.setSymbols(symbols);
        serializer.setNewLine(newLine);
        serializer.setEnforceParentheses(ep);
        serializer.setEnquoteWhitespace(ew);

        if (formula != null) {
            String formulaString = Trees.traverse(formula, serializer).orElse("");
            FeatJAR.log().message(formulaString);
        }
    }

    public String getWhitespaceString(String input) {
        try {
            WhitespaceString ws = WhitespaceString.valueOf(input);
            return ws.getWhitespaceValue();
        } catch (IllegalArgumentException e) {
            if (!input.startsWith(CUSTOM_STRING_PREFIX)) {
                FeatJAR.log().error("Illegal string: " + input);
                return null;
            }
            return input.substring(CUSTOM_STRING_PREFIX.length());
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
