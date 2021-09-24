/* -----------------------------------------------------------------------------
 * Formula Lib - Library to represent and edit propositional formulas.
 * Copyright (C) 2021  Sebastian Krieter
 * 
 * This file is part of Formula Lib.
 * 
 * Formula Lib is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Formula Lib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Formula Lib.  If not, see <https://www.gnu.org/licenses/>.
 * 
 * See <https://github.com/skrieter/formula> for further information.
 * -----------------------------------------------------------------------------
 */
package org.spldev.formula.cli;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.regex.*;
import java.util.stream.*;

import org.spldev.formula.expression.*;
import org.spldev.formula.expression.io.*;
import org.spldev.util.*;
import org.spldev.util.cli.*;
import org.spldev.util.extension.ExtensionPoint.*;
import org.spldev.util.io.*;
import org.spldev.util.io.format.*;
import org.spldev.util.logging.*;

/**
 * Command line interface for sampling algorithms.
 *
 * @author Sebastian Krieter
 */
public class ConverterCLI implements CLIFunction {

	@Override
	public String getId() {
		return "convert";
	}

	@Override
	public String getDescription() {
		return "Converts feature models between various formats";
	}

	@Override
	public void run(List<String> args) {
		Path input = null;
		Path output = null;
		Format<Formula> outFormat = null;
		boolean recursive = false;
		boolean overwrite = false;
		boolean dryRun = false;
		String fileNameFilter = null;

		final ListIterator<String> iterator = args.listIterator();
		if (iterator.hasNext()) {
			input = Paths.get(iterator.next());
		}
		if (iterator.hasNext()) {
			final String name = iterator.next();
			try {
				outFormat = FormulaFormatManager.getInstance().getFormatById(name).orElse(Logger::logProblems);
			} catch (final NoSuchExtensionException e) {
				throw new RuntimeException(e);
			}
		}

		while (iterator.hasNext()) {
			final String arg = iterator.next();
			switch (arg) {
			case "-out": {
				output = Paths.get(CLI.getArgValue(iterator, arg));
				break;
			}
			case "-r": {
				recursive = true;
				break;
			}
			case "-f": {
				overwrite = true;
				break;
			}
			case "-name": {
				fileNameFilter = CLI.getArgValue(iterator, arg);
				break;
			}
			case "-dry": {
				dryRun = true;
				break;
			}
			}
		}
		if (outFormat == null) {
			throw new IllegalArgumentException("No output format specified!");
		}
		if (input == null) {
			throw new IllegalArgumentException("No input directory or file defined!");
		} else if (!Files.exists(input)) {
			throw new IllegalArgumentException("No input directory or file does not exist!");
		}
		final boolean directory = Files.isDirectory(input);

		if (output == null) {
			output = Paths.get(directory ? "out" : "out." + outFormat.getFileExtension());
		}
		if (overwrite) {
			if (directory) {
				if (Files.isRegularFile(output)) {
					try {
						Files.delete(output);
					} catch (final IOException e) {
						throw new IllegalArgumentException("Existing output file could not be deleted!");
					}
				}
			} else {
				if (Files.isDirectory(output)) {
					try {
						Files.delete(output);
					} catch (final IOException e) {
						throw new IllegalArgumentException("Existing output file could not be deleted!");
					}
				}
			}
		} else {
			if (Files.exists(output)) {
				if (Files.isDirectory(output)) {
					try {
						if (Files.list(output).findAny().isPresent()) {
							throw new IllegalArgumentException(
								"Output directory is not empty!\n\tUse -f to force overwrite existing files");
						}
					} catch (final IOException e) {
						throw new IllegalArgumentException("Output directory is not accessible!");
					}
				} else {
					throw new IllegalArgumentException("Output file already exists!\n\tUse -f to force overwrite");
				}
			}
		}

		if (directory && !Files.exists(output)) {
			try {
				Files.createDirectory(output);
			} catch (final IOException e) {
				throw new IllegalArgumentException("Output directory could not be created!");
			}
		}

		final boolean convert = !dryRun;
		if (directory) {
			final Format<Formula> format = outFormat;
			final Path rootIn = input;
			final Path rootOut = output;
			final Predicate<String> fileNamePredicate = fileNameFilter == null ? (s -> true)
				: Pattern.compile(fileNameFilter).asMatchPredicate();
			try {
				final Stream<Path> fileStream = recursive ? Files.walk(input) : Files.list(input);
				fileStream //
					.filter(Files::isRegularFile) //
					.filter(f -> fileNamePredicate.test(f.getFileName().toString())) //
					.forEach(inputFile -> {
						final Path outputDirectory = rootOut.resolve(rootIn.relativize(inputFile.getParent()));
						final Path outputFile = outputDirectory
							.resolve(FileHandler.getFileNameWithoutExtension(inputFile.getFileName()) + "."
								+ format.getFileExtension());
						Logger.logInfo(inputFile + " -> " + outputFile);
						if (convert) {
							try {
								Files.createDirectories(outputDirectory);
							} catch (final IOException e) {
								throw new RuntimeException(e);
							}
							convert(inputFile, outputFile, format);
						}
					});
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
		} else {
			Logger.logInfo(input + " -> " + output);
			if (convert) {
				convert(input, output, outFormat);
			}
		}
	}

	private void convert(Path inputFile, Path outputFile, Format<Formula> outFormat) {
		try {
			final Result<Formula> parse = FileHandler.load(inputFile, FormulaFormatManager.getInstance(),
				StandardCharsets.UTF_8);
			if (parse.isPresent()) {
				FileHandler.save(parse.get(), outputFile, outFormat, StandardCharsets.UTF_8);
			} else {
				Logger.logProblems(parse.getProblems());
			}
		} catch (final IOException e) {
			throw new RuntimeException(e);
		} catch (final Exception e) {
			Logger.logError(e);
		}
	}
}
