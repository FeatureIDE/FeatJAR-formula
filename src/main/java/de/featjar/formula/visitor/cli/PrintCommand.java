package de.featjar.formula.visitor.cli;

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
import de.featjar.formula.structure.formula.IFormula;

import java.util.Arrays;
import java.util.List;

/**
 * Prints the formula in a readable format.
 *
 * @author Andreas Gerasimow
 */
public class PrintCommand implements ICommand {

    /**
     * Defines the tab string.
     */
    public static final Option<String> TAB_OPTION = new Option<>(
            "tab", Option.StringParser)
            .setDescription("Defines the tab string.");

    /**
     * Defines the notation.
     */
    public static final Option<ExpressionSerializer.Notation> NOTATION_OPTION = new Option<>(
            "notation", (arg) -> ExpressionSerializer.Notation.valueOf(arg.toUpperCase()))
            .setDescription("Defines the notation. Possible options: " + Arrays.toString(ExpressionSerializer.Notation.values()));

    /**
     * Defines the separator string.
     */
    public static final Option<String> SEPARATOR_OPTION = new Option<>(
            "separator", Option.StringParser)
            .setDescription("Defines the separator string.");

    /**
     * Defines the symbols.
     */
    public static final Option<String> SYMBOLS_OPTION = new Option<>(
            "format", Option.StringParser)
            .setDescription("Defines the symbols.");

    /**
     * Defines the new line string.
     */
    public static final Option<String> NEW_LINE_OPTION = new Option<>(
            "newline", Option.StringParser)
            .setDescription("Defines the new line string.");

    /**
     * Enforces parentheses.
     */
    public static final Option<Boolean> ENFORCE_PARENTHESES_OPTION = new Flag(
            "enforce-parentheses")
            .setDescription("Enforces parentheses.");

    /**
     * Enquotes whitespace.
     */
    public static final Option<Boolean> ENQUOTE_WHITESPACE_OPTION = new Flag(
            "enquote-whitespace")
            .setDescription("Enquotes whitespace.");

    @Override
    public List<Option<?>> getOptions() {
        return  List.of(
                INPUT_OPTION,
                TAB_OPTION,
                NOTATION_OPTION,
                SEPARATOR_OPTION,
                SYMBOLS_OPTION,
                NEW_LINE_OPTION,
                ENFORCE_PARENTHESES_OPTION,
                ENQUOTE_WHITESPACE_OPTION);
    }


    @Override
    public void run(OptionList optionParser) {
        String tab = optionParser.getResult(TAB_OPTION).orElse(ExpressionSerializer.STANDARD_TAB_STRING);
        ExpressionSerializer.Notation notation = optionParser.getResult(NOTATION_OPTION).orElse(ExpressionSerializer.STANDARD_NOTATION);
        String separator = optionParser.getResult(SEPARATOR_OPTION).orElse(ExpressionSerializer.STANDARD_SEPARATOR);
        String symbolsString = optionParser.getResult(SYMBOLS_OPTION).orElse(null);
        String newLine = optionParser.getResult(NEW_LINE_OPTION).orElse(ExpressionSerializer.STANDARD_NEW_LINE);
        boolean ep = optionParser.getResult(ENFORCE_PARENTHESES_OPTION).orElse(ExpressionSerializer.STANDARD_ENFORCE_PARENTHESES);
        boolean ew = optionParser.getResult(ENQUOTE_WHITESPACE_OPTION).orElse(ExpressionSerializer.STANDARD_ENQUOTE_WHITESPACE);

        try {
            Symbols symbols = symbolsString == null ?
                    ExpressionSerializer.STANDARD_SYMBOLS :
                    (Symbols) Class.forName(symbolsString).getField("INSTANCE").get(null);

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
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            FeatJAR.log().error(e.getMessage());
        }
    }

    @Override
    public String getDescription() {
        return "Prints the formula in a readable format.";
    }

    @Override
    public String getShortName() {
        return "print";
    }
}
