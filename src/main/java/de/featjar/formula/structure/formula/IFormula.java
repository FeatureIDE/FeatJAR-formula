/*
 * Copyright (C) 2023 Sebastian Krieter, Elias Kuiter
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
package de.featjar.formula.structure.formula;

import de.featjar.base.computation.IComputation;
import de.featjar.base.data.Result;
import de.featjar.formula.analysis.VariableMap;
import de.featjar.formula.analysis.bool.BooleanClauseList;
import de.featjar.formula.analysis.bool.ComputeBooleanRepresentationOfCNFFormula;
import de.featjar.formula.analysis.value.IValueRepresentation;
import de.featjar.formula.structure.IExpression;
import de.featjar.formula.structure.formula.connective.AQuantifier;
import de.featjar.formula.structure.formula.connective.IConnective;
import de.featjar.formula.structure.formula.predicate.IPredicate;
import de.featjar.formula.structure.term.value.Variable;
import de.featjar.formula.visitor.NormalForms;
import java.util.LinkedHashSet;

/**
 * A well-formed formula.
 * Evaluates to either {@code true} or {@code false}.
 * In a formula, each {@link Variable} can, but does not have to be bound by a
 * {@link AQuantifier}.
 * A formula is either a {@link IConnective}
 * or a {@link IPredicate}.
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public interface IFormula extends IExpression, IValueRepresentation {
    /**
     * Normal form of a formula.
     */
    enum NormalForm {
        NNF,
        CNF,
        DNF
        // TODO: add other normal forms (e.g., d-dNNF)
    }

    default Class<?> getType() {
        return Boolean.class;
    }

    /**
     * {@return whether this formula is in the given normal form}
     *
     * @param normalForm the normal form
     */
    default boolean isNormalForm(NormalForm normalForm) {
        return NormalForms.isNormalForm(this, normalForm);
    }

    /**
     * {@return whether this formula is in the given clausal normal form}
     *
     * @param normalForm the clausal normal form
     */
    default boolean isClausalNormalForm(NormalForm normalForm) {
        return NormalForms.isClausalNormalForm(this, normalForm);
    }

    /**
     * {@return whether this formula is in negation normal form}
     */
    default boolean isNNF() {
        return isNormalForm(NormalForm.NNF);
    }

    /**
     * {@return whether this formula is in conjunctive normal form}
     */
    default boolean isCNF() {
        return isNormalForm(NormalForm.CNF);
    }

    /**
     * {@return whether this formula is in disjunctive normal form}
     */
    default boolean isDNF() {
        return isNormalForm(NormalForm.DNF);
    }

    /**
     * {@return a formula in the given normal form that is equivalent to this formula}
     */
    default Result<IFormula> toNormalForm(NormalForm normalForm) {
        return NormalForms.toNormalForm(this, normalForm, false);
    }

    /**
     * {@return a formula in the given clausal normal form that is equivalent to this formula}
     */
    default Result<IFormula> toClausalNormalForm(NormalForm normalForm) {
        return NormalForms.toNormalForm(this, normalForm, true);
    }

    /**
     * {@return a formula in clausal negation normal form that is equivalent to this formula}
     */
    default Result<IFormula> toNNF() {
        return toClausalNormalForm(NormalForm.NNF);
    }

    /**
     * {@return a formula in clausal conjunctive normal form that is equivalent to this formula}
     */
    default Result<IFormula> toCNF() {
        return toClausalNormalForm(NormalForm.CNF);
    }

    /**
     * {@return a formula in clausal disjunctive normal form that is equivalent to this formula}
     */
    default Result<IFormula> toDNF() {
        return toClausalNormalForm(NormalForm.DNF);
    }

    @Override
    default LinkedHashSet<String> getVariableNames() {
        return IExpression.super.getVariableNames();
    }

    @Override
    default Result<BooleanClauseList> toBoolean(VariableMap variableMap) {
        return ComputeBooleanRepresentationOfCNFFormula.toBooleanClauseList(this, variableMap);
    }

    @SuppressWarnings("unchecked")
    @Override
    default IComputation<BooleanClauseList> toBoolean(IComputation<VariableMap> variableMap) {
        return (IComputation<BooleanClauseList>) IValueRepresentation.super.toBoolean(variableMap);
    }

    // TODO: mutate/analyze analogous to FeatureModel?
}
