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
package de.featjar.formula.transformer;

import de.featjar.base.computation.*;
import de.featjar.base.data.Result;
import de.featjar.base.tree.structure.ITree;
import de.featjar.formula.structure.ExpressionKind;
import de.featjar.formula.structure.formula.FormulaNormalForm;
import de.featjar.formula.structure.formula.IFormula;
import de.featjar.formula.structure.formula.connective.And;
import de.featjar.formula.tester.NormalForms;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * Transforms a formula into strict conjunctive normal form.
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public class ComputeCNFFormula extends AComputation<IFormula> {
    public static final Dependency<IFormula> NNF_FORMULA = Dependency.newDependency(IFormula.class);
    /**
     * Determines whether this computation uses the Plaisted-Greenbaum optimization.
     */
    public static final Dependency<Boolean> IS_PLAISTED_GREENBAUM = Dependency.newDependency(Boolean.class);
    /**
     * Determines the maximum number of literals available for distributive transformation.
     */
    public static final Dependency<Integer> MAXIMUM_NUMBER_OF_LITERALS = Dependency.newDependency(Integer.class);
    /**
     * Determines whether this computation is parallel.
     */
    public static final Dependency<Boolean> IS_PARALLEL =
            Dependency.newDependency(Boolean.class); // be careful, this does not guarantee determinism

    /**
     * Creates a new CNF formula computation.
     *
     * @param nnfFormula the input NNF formula computation
     */
    public ComputeCNFFormula(IComputation<IFormula> nnfFormula) {
        super(
                nnfFormula,
                Computations.of(Boolean.FALSE),
                Computations.of(Integer.MAX_VALUE),
                Computations.of(Boolean.FALSE));
    }

    protected ComputeCNFFormula(ComputeCNFFormula other) {
        super(other);
    }

    /**
     * Sets whether this computation introduces auxiliary variables.
     *
     * @param tseitin whether this computation introduces auxiliary variables
     */
    public void setTseitin(IComputation<Boolean> tseitin) {
        setDependencyComputation(
                MAXIMUM_NUMBER_OF_LITERALS,
                tseitin.mapResult(ComputeCNFFormula.class, "setTseitin", b -> b ? 0 : Integer.MAX_VALUE));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Result<IFormula> compute(List<Object> dependencyList, Progress progress) {
        IFormula nnfFormula = NNF_FORMULA.get(dependencyList);
        boolean isPlaistedGreenbaum = IS_PLAISTED_GREENBAUM.get(dependencyList);
        int maximumNumberOfLiterals = MAXIMUM_NUMBER_OF_LITERALS.get(dependencyList);
        boolean isParallel = IS_PARALLEL.get(dependencyList);
        assert ExpressionKind.NNF.test(nnfFormula);

        List<IFormula> clauseFormulas =
                isParallel ? Collections.synchronizedList(new ArrayList<>()) : new ArrayList<>();
        List<TseitinTransformer.Substitution> substitutions =
                isParallel ? Collections.synchronizedList(new ArrayList<>()) : new ArrayList<>();
        Consumer<IFormula> transformer = formula -> {
            transform(formula, clauseFormulas, substitutions, isPlaistedGreenbaum, maximumNumberOfLiterals);
            progress.incrementCurrentStep();
        };

        if (nnfFormula instanceof And) {
            List<IFormula> children = (List<IFormula>) nnfFormula.getChildren();
            progress.setTotalSteps(children.size());
            if (isParallel) {
                children.parallelStream().forEach(transformer);
            } else {
                children.forEach(transformer);
            }
        } else {
            progress.setTotalSteps(1);
            transformer.accept(nnfFormula);
        }

        TseitinTransformer.unify(substitutions);
        clauseFormulas.addAll(TseitinTransformer.getClauseFormulas(substitutions));
        return Result.of(NormalForms.normalToStrictNormalForm(new And(clauseFormulas), FormulaNormalForm.CNF));
    }

    @SuppressWarnings("unchecked")
    private void transform(
            IFormula formula,
            List<IFormula> clauseFormulas,
            List<TseitinTransformer.Substitution> substitutions,
            boolean isPlaistedGreenbaum,
            int maximumNumberOfLiterals) {
        if (formula.isStrictNormalForm(FormulaNormalForm.CNF)) {
            clauseFormulas.addAll((List<? extends IFormula>) formula.getChildren());
        } else if (formula.isNormalForm(FormulaNormalForm.CNF)) {
            clauseFormulas.addAll(
                    (List<? extends IFormula>) NormalForms.normalToStrictNormalForm(formula, FormulaNormalForm.CNF)
                            .getChildren());
        } else {
            Result<IFormula> transformationResult = distributiveTransform(
                    formula,
                    new DistributiveTransformer.MaximumNumberOfLiteralsCancelPredicate(maximumNumberOfLiterals));
            if (transformationResult.isPresent()) {
                clauseFormulas.addAll(
                        (List<? extends IFormula>) transformationResult.get().getChildren());
                return;
            }
            substitutions.addAll(tseitinTransform(formula, isPlaistedGreenbaum));
        }
    }

    protected Result<IFormula> distributiveTransform(
            IFormula formula, DistributiveTransformer.ICancelPredicate cancelPredicate) {
        return new DistributiveTransformer(true, cancelPredicate).apply(formula);
    }

    protected List<TseitinTransformer.Substitution> tseitinTransform(IFormula formula, boolean isPlaistedGreenbaum) {
        return new TseitinTransformer(isPlaistedGreenbaum).apply(formula);
    }

    @Override
    public ITree<IComputation<?>> cloneNode() {
        return new ComputeCNFFormula(this);
    }
}
