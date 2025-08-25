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
package de.featjar.composition.cli;

import de.featjar.base.FeatJAR;
import de.featjar.base.cli.ACommand;
import de.featjar.base.cli.Option;
import de.featjar.base.cli.OptionList;
import de.featjar.base.data.Result;
import de.featjar.base.io.IO;
import de.featjar.composition.Preprocessor;
import de.featjar.formula.assignment.Assignment;
import de.featjar.formula.io.textual.CPPAssignmentFormat;
import de.featjar.formula.io.textual.JavaSymbols;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.stream.Stream;

public class PreprocessorCommand extends ACommand {

    public static final Option<Path> CONFIGURATION_OPTION = Option.newOption("configuration", Option.PathParser)
            .setDescription("Path to configuration file")
            .setValidator(Option.PathValidator);

    public static final Option<String> MODE_OPTION = Option.newEnumOption(
                    "mode", "process", "print-variables", "print-annotations")
            .setDefaultValue("process")
            .setDescription("Mode of operation");

    public static final Option<Boolean> ANNOTATIONS_OPTION =
            Option.newFlag("annotations").setDescription("Prints all annotations in the input file");

    public static final Option<String> PREFIX_OPTION = Option.newOption("annotation-prefix", Option.StringParser)
            .setDefaultValue("")
            .setDescription("The prefix that preceeds each annotation");

    public static final Option<String> START_OPTION = Option.newOption("annotation-start", Option.StringParser)
            .setDefaultValue("if")
            .setDescription("The prefix of an annotation opening an if block");

    public static final Option<String> END_OPTION = Option.newOption("annotation-end", Option.StringParser)
            .setDefaultValue("endif")
            .setDescription("The prefix of an annotation closing an if block");

    @Override
    public int run(OptionList optionParser) {
        Path in = optionParser.getResult(INPUT_OPTION).orElseThrow();
        Path out = optionParser.getResult(OUTPUT_OPTION).orElse(null);
        Path assignmentPath = optionParser.getResult(CONFIGURATION_OPTION).orElseThrow();

        Charset charset = StandardCharsets.UTF_8;
        String annotationPrefix = optionParser.getResult(PREFIX_OPTION).orElseThrow();
        String annotationStart = optionParser.getResult(START_OPTION).orElseThrow();
        String annotationEnd = optionParser.getResult(END_OPTION).orElseThrow();

        Preprocessor preprocessor =
                new Preprocessor(annotationPrefix, annotationStart, annotationEnd, JavaSymbols.INSTANCE);

        String mode = optionParser.getResult(MODE_OPTION).orElseThrow();

        Stream<String> stream = null;
        try {
            switch (mode) {
                case "process":
                    stream = preprocess(in, out, assignmentPath, charset, preprocessor);
                    break;
                case "print-variables":
                    stream = printVariableNames(in, charset, preprocessor);
                    break;
                case "print-annotations":
                    stream = printAnnotations(in, charset, preprocessor);
                    break;
                default:
                    return 1;
            }
        } catch (IOException e) {
            FeatJAR.log().error(e);
            return 1;
        }

        if (out != null) {
            try (BufferedWriter writer = Files.newBufferedWriter(
                    out, charset, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                stream.forEach(line -> {
                    try {
                        writer.write(line);
                        writer.newLine();
                        writer.flush();
                    } catch (IOException e) {
                        FeatJAR.log().error(e);
                    }
                });
            } catch (IOException e) {
                FeatJAR.log().error(e);
                return 1;
            }
        } else {
            stream.forEach(FeatJAR.log()::plainMessage);
        }
        return 0;
    }

    private Stream<String> preprocess(
            Path in, Path out, Path assignmentPath, Charset charset, Preprocessor preprocessor) throws IOException {
        Result<Assignment> parsedAssignment = IO.load(assignmentPath, new CPPAssignmentFormat());
        return preprocessor.preprocess(Files.lines(in, charset), parsedAssignment.get());
    }

    private Stream<String> printVariableNames(Path in, Charset charset, Preprocessor preprocessor) throws IOException {
        return preprocessor.extractVariableNames(Files.lines(in, charset)).stream();
    }

    private Stream<String> printAnnotations(Path in, Charset charset, Preprocessor preprocessor) throws IOException {
        return preprocessor.extractAnnotations(Files.lines(in, charset)).stream();
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Preprocesses files with annotations");
    }

    @Override
    public Optional<String> getShortName() {
        return Optional.of("preprocessor");
    }
}
