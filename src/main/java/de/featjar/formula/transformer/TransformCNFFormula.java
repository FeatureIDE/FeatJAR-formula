/*
 * Copyright (C) 2022 Sebastian Krieter, Elias Kuiter
 *
 * This file is part of formula.
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
package de.featjar.formula.transformer;

import de.featjar.base.computation.*;
import de.featjar.base.data.Result;
import de.featjar.base.tree.structure.ITree;
import de.featjar.formula.structure.IExpression;
import de.featjar.formula.structure.formula.IFormula;
import de.featjar.formula.structure.term.value.Variable;
import de.featjar.formula.visitor.ANormalFormTester;
import de.featjar.formula.structure.formula.connective.And;
import de.featjar.base.task.IMonitor;
import de.featjar.base.task.CancelableMonitor;
import de.featjar.base.tree.Trees;
import de.featjar.formula.visitor.NormalForms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Transforms a formula into clausal conjunctive normal form.
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public class TransformCNFFormula extends AComputation<IFormula> implements ITransformation<IFormula> {
    protected static final Dependency<IFormula> NNF_FORMULA = newRequiredDependency();

    protected int maximumNumberOfLiterals = Integer.MAX_VALUE; //todo: pass as dependent computation
    public final boolean useMultipleThreads = false; //todo: pass as dependent computation

    protected final List<IFormula> distributiveClauses;
    protected final List<ComputeTseitinCNFFormula.Substitute> tseitinClauses;
    protected boolean useDistributive;

    public TransformCNFFormula(IComputation<IFormula> nnfFormula) { // precondition: nnf must be given (TODO: validate)
        dependOn(NNF_FORMULA);
        setInput(nnfFormula);
        //this.maximumNumberOfLiterals = maximumNumberOfLiterals;
        if (useMultipleThreads) {
            distributiveClauses = Collections.synchronizedList(new ArrayList<>());
            tseitinClauses = Collections.synchronizedList(new ArrayList<>());
        } else {
            distributiveClauses = new ArrayList<>();
            tseitinClauses = new ArrayList<>();
        }
    }

    @Override
    public Dependency<IFormula> getInputDependency() {
        return NNF_FORMULA;
    }

    @Override
    public Result<IFormula> computeResult(List<?> results, IMonitor monitor) {
        IFormula formula = NNF_FORMULA.get(results);
        useDistributive = (maximumNumberOfLiterals > 0);
        final ANormalFormTester normalFormTester = NormalForms.getNormalFormTester(formula, IFormula.NormalForm.CNF);
        if (normalFormTester.isNormalForm()) {
            if (!normalFormTester.isClausalNormalForm()) {
                return Result.of(NormalForms.normalToClausalNormalForm((IFormula) Trees.clone(formula), IFormula.NormalForm.CNF));
            } else {
                return Result.of((IFormula) Trees.clone(formula)); // TODO: is it a computation's responsibility to clone its input or not? should the Store do this, or the caller, or thenComputeResult...?
            }
        }
        IFormula newFormula = (IFormula) formula.cloneTree();
        if (newFormula instanceof And) {
            final List<? extends IExpression> children = newFormula.getChildren();
            if (useMultipleThreads) {
                children.parallelStream().forEach(child -> transform((IFormula) child));
            } else {
                children.forEach(child -> transform((IFormula) child));
            }
        } else {
            transform(newFormula);
        }

        newFormula = new And(getTransformedClauses());
        newFormula = NormalForms.normalToClausalNormalForm(newFormula, IFormula.NormalForm.CNF);
        return Result.of(newFormula);
    }

    protected List<? extends IFormula> getTransformedClauses() {
        final List<IFormula> transformedClauses = new ArrayList<>(distributiveClauses);

        if (!tseitinClauses.isEmpty()) {
            final LinkedHashMap<ComputeTseitinCNFFormula.Substitute, ComputeTseitinCNFFormula.Substitute> combinedTseitinClauses = new LinkedHashMap<>();
            for (final ComputeTseitinCNFFormula.Substitute tseitinClause : tseitinClauses) {
                ComputeTseitinCNFFormula.Substitute substitute = combinedTseitinClauses.get(tseitinClause);
                if (substitute == null) {
                    combinedTseitinClauses.put(tseitinClause, tseitinClause);
                    final Variable variable = tseitinClause.getVariable();
                    if (variable != null) {
                        //TODO variable.setName(termMap.addBooleanVariable().getName());
                    }
                } else {
                    final Variable variable = substitute.getVariable();
                    if (variable != null) {
                        //TODO tseitinClause.getVariable().rename(variable.getName());
                    }
                }
            }
            for (final ComputeTseitinCNFFormula.Substitute tseitinClause : combinedTseitinClauses.keySet()) {
                for (final IExpression expression : tseitinClause.getClauses()) {
                    //TODO transformedClauses.add(Formulas.manipulate(expression, new VariableMapSetter(termMap)));
                }
            }
        }
        return transformedClauses;
    }

    private void transform(IFormula child) {
        final IFormula clonedChild = (IFormula) Trees.clone(child);
        if ((clonedChild).isCNF()) {
            if (clonedChild instanceof And) {
                distributiveClauses.addAll((List<? extends IFormula>) clonedChild.getChildren());
            } else {
                distributiveClauses.add(clonedChild);
            }
        } else {
            if (useDistributive) {
                try {
                    // todo: do not use .get
                    distributiveClauses.addAll(
                            (List<? extends IFormula>) distributive(clonedChild, new CancelableMonitor()).get().getChildren()); // TODO .get?
                    return;
                } catch (final ComputeNormalFormFormula.MaximumNumberOfLiteralsExceededException ignored) {
                }
            }
            // todo: do not use .get
            tseitinClauses.addAll(tseitin(clonedChild, new CancelableMonitor()).get()); // TODO: .get?
        }
    }

    protected Result<IFormula> distributive(IFormula child, IMonitor monitor)
            throws ComputeNormalFormFormula.MaximumNumberOfLiteralsExceededException {
        final ComputeNormalFormFormula cnfDistributiveLawTransformer =
                Computations.of(child, monitor)
                        .map(c -> new ComputeNormalFormFormula(c, IFormula.NormalForm.CNF)); // TODO: monitor subtask?
        cnfDistributiveLawTransformer.setMaximumNumberOfLiterals(maximumNumberOfLiterals);
        return cnfDistributiveLawTransformer.getResult();
    }

    protected Result<List<ComputeTseitinCNFFormula.Substitute>> tseitin(IExpression child, IMonitor monitor) {
        final ComputeTseitinCNFFormula toTseitinCNFFormula = new ComputeTseitinCNFFormula();
        return toTseitinCNFFormula.execute(child, monitor);
    }

    @Override
    public ITree<IComputation<?>> cloneNode() {
        return new TransformCNFFormula(getInput());
    }
}
