package de.featjar.formula.structure;

import de.featjar.formula.structure.formula.Formula;
import de.featjar.formula.structure.formula.connective.*;
import de.featjar.formula.structure.formula.predicate.*;
import de.featjar.formula.structure.term.Term;
import de.featjar.formula.structure.term.function.*;
import de.featjar.formula.structure.term.value.Constant;
import de.featjar.formula.structure.term.value.Variable;

/**
 * Creates expressions in propositional or first-order logic.
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

    public static And and(Formula... formulas) {
        return new And(formulas);
    }

    public static AtLeast atLeast(int minimum, Formula... formulas) {
        return new AtLeast(minimum, formulas);
    }

    public static AtMost atMost(int maximum, Formula... formulas) {
        return new AtMost(maximum, formulas);
    }

    public static Between between(int minimum, int maximum, Formula... formulas) {
        return new Between(minimum, maximum, formulas);
    }

    public static BiImplies biImplies(Formula leftFormula, Formula rightFormula) {
        return new BiImplies(leftFormula, rightFormula);
    }

    public static Choose choose(int bound, Formula... formulas) {
        return new Choose(bound, formulas);
    }

    public static Exists exists(Variable boundVariable, Formula formula) {
        return new Exists(boundVariable, formula);
    }

    public static ForAll forAll(Variable boundVariable, Formula formula) {
        return new ForAll(boundVariable, formula);
    }

    public static Implies implies(Formula leftFormula, Formula rightFormula) {
        return new Implies(leftFormula, rightFormula);
    }

    public static Not not(Formula formula) {
        return new Not(formula);
    }

    public static Or or(Formula... formulas) {
        return new Or(formulas);
    }

    public static Equals equals(Term leftTerm, Term rightTerm) {
        return new Equals(leftTerm, rightTerm);
    }

    public static GreaterEqual greaterEqual(Term leftTerm, Term rightTerm) {
        return new GreaterEqual(leftTerm, rightTerm);
    }

    public static GreaterThan greaterThan(Term leftTerm, Term rightTerm) {
        return new GreaterThan(leftTerm, rightTerm);
    }

    public static LessEqual lessEqual(Term leftTerm, Term rightTerm) {
        return new LessEqual(leftTerm, rightTerm);
    }

    public static LessThan lessThan(Term leftTerm, Term rightTerm) {
        return new LessThan(leftTerm, rightTerm);
    }

    public static Literal literal(boolean isPositive, Term term) {
        return new Literal(isPositive, term);
    }

    public static Literal literal(Term term) {
        return new Literal(term);
    }

    public static Literal literal(boolean argument1, String argument2) {
        return new Literal(argument1, argument2);
    }

    public static Literal literal(String literal) {
        return new Literal(literal);
    }

    public static NotEquals notEquals(Term leftTerm, Term rightTerm) {
        return new NotEquals(leftTerm, rightTerm);
    }

    public static Problem problem(de.featjar.base.data.Problem problem) {
        return new Problem(problem);
    }

    public static IntegerAdd integerAdd(Term leftTerm, Term rightTerm) {
        return new IntegerAdd(leftTerm, rightTerm);
    }

    public static IntegerDivide integerDivide(Term leftTerm, Term rightTerm) {
        return new IntegerDivide(leftTerm, rightTerm);
    }

    public static IntegerMultiply integerMultiply(Term leftTerm, Term rightTerm) {
        return new IntegerMultiply(leftTerm, rightTerm);
    }

    public static RealAdd realAdd(Term leftTerm, Term rightTerm) {
        return new RealAdd(leftTerm, rightTerm);
    }

    public static RealDivide realDivide(Term leftTerm, Term rightTerm) {
        return new RealDivide(leftTerm, rightTerm);
    }

    public static RealMultiply realMultiply(Term leftTerm, Term rightTerm) {
        return new RealMultiply(leftTerm, rightTerm);
    }

    public static Constant constant(Object value, Class<?> type) {
        return new Constant(value, type);
    }

    public static Constant constant(Object value) {
        return new Constant(value);
    }

    public static Variable variable(String name, Class<?> type) {
        return new Variable(name, type);
    }

    public static Variable variable(String name) {
        return new Variable(name);
    }
}
