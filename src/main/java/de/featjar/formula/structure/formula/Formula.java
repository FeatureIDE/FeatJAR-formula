package de.featjar.formula.structure.formula;

import de.featjar.base.data.Result;
import de.featjar.formula.structure.Expression;
import de.featjar.formula.structure.formula.predicate.Predicate;
import de.featjar.formula.structure.term.value.Variable;
import de.featjar.formula.visitor.NormalForms;

/**
 * A well-formed formula.
 * Evaluates to either {@code true} or {@code false}.
 * In a formula, each {@link Variable} can, but does not have to be bound by a
 * {@link de.featjar.formula.structure.formula.connective.Quantifier}.
 * A formula is either a {@link de.featjar.formula.structure.formula.connective.Connective}
 * or a {@link Predicate}.
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public interface Formula extends Expression {
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
    default Result<Formula> toNormalForm(NormalForm normalForm) {
        return NormalForms.toNormalForm(this, normalForm, false);
    }

    /**
     * {@return a formula in the given clausal normal form that is equivalent to this formula}
     */
    default Result<Formula> toClausalNormalForm(NormalForm normalForm) {
        return NormalForms.toNormalForm(this, normalForm, true);
    }

    /**
     * {@return a formula in clausal negation normal form that is equivalent to this formula}
     */
    default Result<Formula> toNNF() {
        return toClausalNormalForm(NormalForm.NNF);
    }

    /**
     * {@return a formula in clausal conjunctive normal form that is equivalent to this formula}
     */
    default Result<Formula> toCNF() {
        return toClausalNormalForm(NormalForm.CNF);
    }

    /**
     * {@return a formula in clausal disjunctive normal form that is equivalent to this formula}
     */
    default Result<Formula> toDNF() {
        return toClausalNormalForm(NormalForm.DNF);
    }

    // TODO: mutate/analyze analogous to FeatureModel?
}
