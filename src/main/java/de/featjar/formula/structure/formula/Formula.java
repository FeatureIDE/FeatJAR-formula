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
        CNF,
        DNF
        // todo: NNF
    }

    default Class<?> getType() {
        return Boolean.class;
    }

    /**
     * {@return whether this formula is in conjunctive normal form}
     */
    default boolean isCNF() {
        return NormalForms.isNormalForm(this, NormalForm.CNF, false);
    }

    /**
     * {@return whether this formula is in disjunctive normal form}
     */
    default boolean isDNF() {
        return NormalForms.isNormalForm(this, NormalForm.DNF, false);
    }

    /**
     * {@return whether this formula is in clausal conjunctive normal form}
     */
    default boolean isClausalCNF() {
        return NormalForms.isNormalForm(this, NormalForm.CNF, true);
    }

    /**
     * {@return whether this formula is in clausal disjunctive normal form}
     */
    default boolean isClausalDNF() {
        return NormalForms.isNormalForm(this, NormalForm.DNF, true);
    }
    //todo: computation/store

    /**
     * {@return a formula in conjunctive normal form that is an equi-assignable to this formula}
     */
    default Result<Formula> toCNF() { // todo: CNF vs. IndexedCNF? InternalCNF?
        return NormalForms.toNormalForm(this, NormalForm.CNF, false);
    }

    /**
     * {@return a formula in disjunctive normal form that is an equi-assignable to this formula}
     */
    default Result<Formula> toDNF() {
        return NormalForms.toNormalForm(this, NormalForm.DNF, false);
    }

    /**
     * {@return a formula in clausal conjunctive normal form that is an equi-assignable to this formula}
     */
    default Result<Formula> toClausalCNF() {
        return NormalForms.toNormalForm(this, NormalForm.CNF, true);
    }

    /**
     * {@return a formula in clausal disjunctive normal form that is an equi-assignable to this formula}
     */
    default Result<Formula> toClausalDNF() {
        return NormalForms.toNormalForm(this, NormalForm.DNF, true);
    }

    // todo: mutate/analyze analogous to FeatureModel?
}
