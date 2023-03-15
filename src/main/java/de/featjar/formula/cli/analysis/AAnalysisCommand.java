/*
 * Copyright (C) 2023 Elias Kuiter
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
package de.featjar.formula.cli.analysis;

import static de.featjar.base.computation.Computations.*;

import de.featjar.base.FeatJAR;
import de.featjar.base.cli.*;
import de.featjar.base.computation.*;
import de.featjar.base.data.Problem;
import de.featjar.base.data.Result;
import de.featjar.base.io.IO;
import de.featjar.base.io.graphviz.GraphVizComputationTreeFormat;
import de.featjar.formula.analysis.value.ValueAssignment;
import de.featjar.formula.analysis.value.ValueClause;
import de.featjar.formula.analysis.value.ValueClauseList;
import de.featjar.formula.io.FormulaFormats;
import de.featjar.formula.io.value.ValueAssignmentFormat;
import de.featjar.formula.io.value.ValueAssignmentListFormat;
import de.featjar.formula.structure.formula.IFormula;
import de.featjar.formula.transformer.ComputeCNFFormula;
import de.featjar.formula.transformer.ComputeDNFFormula;
import de.featjar.formula.transformer.ComputeNNFFormula;

import java.time.Duration;
import java.util.List;

/**
 * Computes an analysis result for a formula.
 *
 * @param <T> the type of the analysis result
 */
public abstract class AAnalysisCommand<T> implements ICommand {
    public static final Option<ValueAssignment> ASSIGNMENT_OPTION = new Option<>(
                    "assignment", s -> IO.load(s, new ValueAssignmentFormat<>(ValueAssignment::new)))
            .setDescription("An additional assignment to assume")
            .setDefaultValue(new ValueAssignment());

    public static final Option<ValueClauseList> CLAUSES_OPTION = new Option<>(
                    "clauses", s -> IO.load(s, new ValueAssignmentListFormat<>(ValueClauseList::new, ValueClause::new)))
            .setDescription("An additional clause list to assume")
            .setDefaultValue(new ValueClauseList());

    public static final Option<Boolean> BROWSE_CACHE_OPTION =
            new Flag("browse-cache").setDescription("Show cache contents in default browser");

    protected IComputation<IFormula> formula;
    protected IOptionInput optionParser;

    //todo: output option
    @Override
    public List<Option<?>> getOptions() {
        return List.of(INPUT_OPTION, BROWSE_CACHE_OPTION);
    }

    @Override
    public void run(IOptionInput optionParser) {
        this.optionParser = optionParser;
        String input = optionParser.get(INPUT_OPTION).get();
        Boolean browseCache = optionParser.get(BROWSE_CACHE_OPTION).get();
        this.formula = async(Commands.loadFile(input, FeatJAR.extensionPoint(FormulaFormats.class)));

        IComputation<T> computation = newComputation();
        FeatJAR.log().info("running computation");
        FeatJAR.log().debug(computation.print());
        final long localTime = System.nanoTime();
        final Result<T> result = computation.parallelComputeResult();
        final long timeNeeded = System.nanoTime() - localTime;
        if (result.isPresent()) {
            FeatJAR.log().info("time needed for computation: " + ((timeNeeded / 1_000_000) / 1000.0) + "s");
            System.out.println(serializeResult(result.get()));
        } else {
            System.err.println("Could not compute result.");
            // System.exit(1); // todo: only do this at the very end of running all commands to signal an error
        }
        if (result.hasProblems()) {
            System.err.println("The following problem(s) occurred:");
            for (Problem problem : result.getProblems()) {
                System.out.print(problem);
                problem.getException().printStackTrace();
            }
        }
        if (browseCache) FeatJAR.cache().browse(new GraphVizComputationTreeFormat());
        this.optionParser = null;
    }

    public abstract IComputation<T> newComputation();

    public String serializeResult(T result) {
        return result.toString();
    }
}
