/*
 * Copyright (C) 2023 FeatJAR-Development-Team
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
 * See <https://github.com/FeatJAR> for further information.
 */
package de.featjar.formula.analysis;

import de.featjar.base.FeatJAR;
import de.featjar.base.cli.Flag;
import de.featjar.base.cli.ICommand;
import de.featjar.base.cli.Option;
import de.featjar.base.cli.OptionList;
import de.featjar.base.computation.IComputation;
import de.featjar.base.data.Result;
import de.featjar.base.io.graphviz.GraphVizComputationTreeFormat;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.List;

/**
 * Computes an analysis result for a formula.
 *
 * @param <T> the type of the analysis result
 */
public abstract class AAnalysisCommand<T> implements ICommand {

    public static final Option<Boolean> BROWSE_CACHE_OPTION =
            new Flag("browse-cache").setDescription("Show cache contents in default browser");

    public static final Option<Boolean> NON_PARALLEL = new Flag("non-parallel") //
            .setDescription("Disable parallel computation. Is overridden by timeout option");

    public static final Option<Duration> TIMEOUT_OPTION = new Option<>(
                    "timeout", s -> Duration.ofSeconds(Long.parseLong(s)))
            .setDescription("Timeout in seconds")
            .setValidator(timeout -> !timeout.isNegative())
            .setDefaultValue(Duration.ZERO);

    protected OptionList optionParser;

    @Override
    public List<Option<?>> getOptions() {
        return List.of(INPUT_OPTION, BROWSE_CACHE_OPTION, NON_PARALLEL, TIMEOUT_OPTION, OUTPUT_OPTION);
    }

    @Override
    public void run(OptionList optionParser) {
        this.optionParser = optionParser;
        boolean browseCache = optionParser.getResult(BROWSE_CACHE_OPTION).get();
        boolean parallel = !optionParser.getResult(NON_PARALLEL).get();
        Duration timeout = optionParser.getResult(TIMEOUT_OPTION).get();
        Path outputPath = optionParser.getResult(OUTPUT_OPTION).orElse(null);

        IComputation<T> computation;
        try {
            computation = newComputation();
        } catch (Exception e) {
            FeatJAR.log().error("ERROR: %s", e.getMessage());
            return;
        }
        FeatJAR.log().debug("running computation %s", computation.print());

        final Result<T> result;
        final long timeNeeded;
        if (!timeout.isZero()) {
            final long localTime = System.nanoTime();
            result = computation.computeResult(true, true, timeout);
            timeNeeded = System.nanoTime() - localTime;
        } else if (parallel) {
            final long localTime = System.nanoTime();
            result = computation.computeFutureResult(true, true).get();
            timeNeeded = System.nanoTime() - localTime;
        } else {
            final long localTime = System.nanoTime();
            result = computation.computeResult(true, true);
            timeNeeded = System.nanoTime() - localTime;
        }

        if (result.isPresent()) {
            FeatJAR.log().info("time needed for computation: " + ((timeNeeded / 1_000_000) / 1000.0) + "s");
            if (outputPath == null) {
                FeatJAR.log().message(serializeResult(result.get()));
            } else {
                try {
                    if (!writeToOutputFile(result.get(), outputPath)) {
                        Files.write(
                                outputPath,
                                serializeResult(result.get()).getBytes(),
                                StandardOpenOption.TRUNCATE_EXISTING);
                    }
                } catch (IOException e) {
                    FeatJAR.log().error(e);
                }
            }
        } else {
            FeatJAR.log().error("Could not compute result.");
        }
        if (result.hasProblems()) {
            FeatJAR.log().error("The following problem(s) occurred:");
            FeatJAR.log().problems(result.getProblems());
        }
        if (browseCache) {
            FeatJAR.cache().browse(new GraphVizComputationTreeFormat());
        }
        this.optionParser = null;
    }

    protected abstract IComputation<T> newComputation();

    protected boolean writeToOutputFile(T result, Path outputPath) {
        return false;
    }

    protected String serializeResult(T result) {
        return result.toString();
    }
}
