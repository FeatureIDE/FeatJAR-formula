package de.featjar.formula.structure;

import de.featjar.base.data.Problem;
import de.featjar.formula.structure.formula.Formula;
import de.featjar.formula.structure.formula.connective.*;
import de.featjar.formula.structure.formula.predicate.*;
import de.featjar.formula.structure.term.Term;
import de.featjar.formula.structure.term.function.*;
import de.featjar.formula.structure.term.value.Constant;
import de.featjar.formula.structure.term.value.Value;
import de.featjar.formula.structure.term.value.Variable;

/**
 * Creates expressions in propositional or first-order logic.
 * Wraps constructors conveniently to allow for a static import using
 * {@code import static de.featjar.formula.structure.Expressions.*;}.
 *
 * @author Elias Kuiter
 */
public class Expressions {
    /**
     * A tautology.
     */
    public static final True True = de.featjar.formula.structure.formula.predicate.True.getInstance();
    /**
     * A contradiction.
     */
    public static final False False = de.featjar.formula.structure.formula.predicate.False.getInstance();

    /**
     * {@return a formula that evaluates to {@code true} iff all of the given formulas evaluate to {@code true}}
     * @param formulas the formulas
     */
    public static And and(Formula... formulas) {
        return new And(formulas);
    }

    /**
     * {@return a formula that evaluates to {@code true} iff at least a given number of the given formulas evaluate to {@code true}}
     * @param minimum the minimum
     * @param formulas the formulas
     */
    public static AtLeast atLeast(int minimum, Formula... formulas) {
        return new AtLeast(minimum, formulas);
    }

    /**
     * {@return a formula that evaluates to {@code true} iff at most a given number of the given formulas evaluate to {@code true}}
     * @param maximum the maximum
     * @param formulas the formulas
     */
    public static AtMost atMost(int maximum, Formula... formulas) {
        return new AtMost(maximum, formulas);
    }

    /**
     * {@return a formula that evaluates to {@code true} iff the number of the given formulas that evaluate to {@code true} is in a given range}
     * @param minimum the minimum
     * @param formulas the formulas
     */
    public static Between between(int minimum, int maximum, Formula... formulas) {
        return new Between(minimum, maximum, formulas);
    }

    /**
     * {@return a formula that evaluates to {@code true} iff both given formulas evaluate to the same value}
     * @param leftFormula the left formula
     * @param rightFormula the right formula
     */
    public static BiImplies biImplies(Formula leftFormula, Formula rightFormula) {
        return new BiImplies(leftFormula, rightFormula);
    }

    /**
     * {@return a formula that evaluates to {@code true} iff the number of the given formulas that evaluate to {@code true} is equal to a given number}
     * @param bound the bound
     * @param formulas the formulas
     */
    public static Choose choose(int bound, Formula... formulas) {
        return new Choose(bound, formulas);
    }

    /**
     * {@return a formula that evaluates to {@code true} iff there is a value of the given bound variable such that the given formula evaluates to {@code true}}
     * @param boundVariable the bound variable
     * @param formula the formula
     */
    public static Exists exists(Variable boundVariable, Formula formula) {
        return new Exists(boundVariable, formula);
    }

    /**
     * {@return a formula that evaluates to {@code true} iff for all values of the given bound variable, the given formula evaluates to {@code true}}
     * @param boundVariable the bound variable
     * @param formula the formula
     */
    public static ForAll forAll(Variable boundVariable, Formula formula) {
        return new ForAll(boundVariable, formula);
    }

    /**
     * {@return a formula that evaluates to {@code true} iff the given left formula evaluates to {@code false} or
     * the given right formula evaluates to {@code true}}
     * @param leftFormula the left formula
     * @param rightFormula the right formula
     */
    public static Implies implies(Formula leftFormula, Formula rightFormula) {
        return new Implies(leftFormula, rightFormula);
    }

    /**
     * {@return a formula that evaluates to {@code true} iff the given formula evaluates to {@code false}}
     * @param formula the formula
     */
    public static Not not(Formula formula) {
        return new Not(formula);
    }

    /**
     * {@return a formula that evaluates to {@code true} iff at least one of the given formulas evaluate to {@code true}}
     * @param formulas the formulas
     */
    public static Or or(Formula... formulas) {
        return new Or(formulas);
    }

    /**
     * {@return a formula that evaluates to {@code true} iff both given terms evaluate to the same value}
     * @param leftTerm the left term
     * @param rightTerm the right term
     */
    public static Equals equals(Term leftTerm, Term rightTerm) {
        return new Equals(leftTerm, rightTerm);
    }

    /**
     * {@return a formula that evaluates to {@code true} iff the given left term evaluates to a larger or the same value as the given right term}
     * @param leftTerm the left term
     * @param rightTerm the right term
     */
    public static GreaterEqual greaterEqual(Term leftTerm, Term rightTerm) {
        return new GreaterEqual(leftTerm, rightTerm);
    }

    /**
     * {@return a formula that evaluates to {@code true} iff the given left term evaluates to a larger value as the given right term}
     * @param leftTerm the left term
     * @param rightTerm the right term
     */
    public static GreaterThan greaterThan(Term leftTerm, Term rightTerm) {
        return new GreaterThan(leftTerm, rightTerm);
    }

    /**
     * {@return a formula that evaluates to {@code true} iff the given left term evaluates to a smaller or the same value as the given right term}
     * @param leftTerm the left term
     * @param rightTerm the right term
     */
    public static LessEqual lessEqual(Term leftTerm, Term rightTerm) {
        return new LessEqual(leftTerm, rightTerm);
    }

    /**
     * {@return a formula that evaluates to {@code true} iff the given left term evaluates to a smaller value as the given right term}
     * @param leftTerm the left term
     * @param rightTerm the right term
     */
    public static LessThan lessThan(Term leftTerm, Term rightTerm) {
        return new LessThan(leftTerm, rightTerm);
    }

    /**
     * {@return a formula that evaluates to {@code true} iff the given term evaluates to the given polarity}
     * @param isPositive the polarity
     * @param value the term
     */
    public static Literal literal(boolean isPositive, Value value) {
        return new Literal(isPositive, value);
    }

    /**
     * {@return a formula that evaluates to {@code true} iff the given term evaluates to {@code true}}
     * @param value the term
     */
    public static Literal literal(Value value) {
        return new Literal(value);
    }

    /**
     * {@return a formula that evaluates to {@code true} iff the Boolean variable with the given name evaluates to the given polarity}
     * @param isPositive the polarity
     * @param variableName the variable name
     */
    public static Literal literal(boolean isPositive, String variableName) {
        return new Literal(isPositive, variableName);
    }

    /**
     * {@return a formula that evaluates to {@code true} iff the Boolean variable with the given name evaluates to {@code true}}
     * @param variableName the variable name
     */
    public static Literal literal(String variableName) {
        return new Literal(variableName);
    }

    /**
     * {@return a formula that evaluates to {@code true} iff both given terms evaluate to different values}
     * @param leftTerm the left term
     * @param rightTerm the right term
     */
    public static NotEquals notEquals(Term leftTerm, Term rightTerm) {
        return new NotEquals(leftTerm, rightTerm);
    }

    /**
     * {@return a placeholder for when an expression cannot be parsed due to some given problem}
     * @param problem the problem
     */
    public static ProblemFormula problemFormula(Problem problem) {
        return new ProblemFormula(problem);
    }

    /**
     * {@return a term that adds the values of two integer terms}
     * @param leftTerm the left term
     * @param rightTerm the right term
     */
    public static IntegerAdd integerAdd(Term leftTerm, Term rightTerm) {
        return new IntegerAdd(leftTerm, rightTerm);
    }

    /**
     * {@return a term that divides the values of two integer terms}
     * @param leftTerm the left term
     * @param rightTerm the right term
     */
    public static IntegerDivide integerDivide(Term leftTerm, Term rightTerm) {
        return new IntegerDivide(leftTerm, rightTerm);
    }

    /**
     * {@return a term that multiplies the values of two integer terms}
     * @param leftTerm the left term
     * @param rightTerm the right term
     */
    public static IntegerMultiply integerMultiply(Term leftTerm, Term rightTerm) {
        return new IntegerMultiply(leftTerm, rightTerm);
    }

    /**
     * {@return a term that adds the values of two real terms}
     * @param leftTerm the left term
     * @param rightTerm the right term
     */
    public static RealAdd realAdd(Term leftTerm, Term rightTerm) {
        return new RealAdd(leftTerm, rightTerm);
    }

    /**
     * {@return a term that divides the values of two real terms}
     * @param leftTerm the left term
     * @param rightTerm the right term
     */
    public static RealDivide realDivide(Term leftTerm, Term rightTerm) {
        return new RealDivide(leftTerm, rightTerm);
    }

    /**
     * {@return a term that multiplies the values of two real terms}
     * @param leftTerm the left term
     * @param rightTerm the right term
     */
    public static RealMultiply realMultiply(Term leftTerm, Term rightTerm) {
        return new RealMultiply(leftTerm, rightTerm);
    }

    /**
     * {@return a constant term that evaluates to a given value, the term having the given type}
     * @param value the value
     * @param type the type
     */
    public static Constant constant(Object value, Class<?> type) {
        return new Constant(value, type);
    }

    /**
     * {@return a constant term that evaluates to a given value, the term having the type of the given value}
     * @param value the value
     */
    public static Constant constant(Object value) {
        return new Constant(value);
    }

    /**
     * {@return a variable term for a given name, the term having the given type}
     * @param name the name
     * @param type the type
     */
    public static Variable variable(String name, Class<?> type) {
        return new Variable(name, type);
    }

    /**
     * {@return a Boolean variable term for a given name}
     * @param name the name
     */
    public static Variable variable(String name) {
        return new Variable(name);
    }
}
