package de.featjar.formula.structure;

import de.featjar.base.data.Sets;
import de.featjar.formula.structure.formula.connective.*;
import de.featjar.formula.structure.formula.predicate.*;
import de.featjar.formula.structure.term.function.*;
import de.featjar.formula.structure.term.value.Constant;
import de.featjar.formula.structure.term.value.Variable;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Tests whether a given expression only contains certain kinds of elements.
 * Can be used to test whether an expression is (non-)Boolean.
 *
 * @author Elias Kuiter
 */
public interface ExpressionKind extends Predicate<IExpression> {
    /**
     * {@return the name of this expression kind}
     */
    String getName();

    /**
     * Represents Boolean expressions in strict negation normal form.
     */
    ExpressionKind NNF = of("NNF", Variable.class, Literal.class, And.class, Or.class);

    /**
     * Represents arbitrary Boolean expressions.
     * These can usually be simplified to strict negation normal form.
     * Simplification of {@link True} and {@link False} is only possible if the formula has at least one {@link Variable}.
     */
    ExpressionKind BOOLEAN =
            extend("Boolean", NNF,
                    Not.class,
                    Implies.class, BiImplies.class,
                    True.class, False.class,
                    AtLeast.class, AtMost.class, Between.class, Choose.class);

    /**
     * Represents quantified Boolean expressions.
     */
    ExpressionKind QBF = extend("QBF", BOOLEAN, Exists.class, ForAll.class);

    /**
     Represents first-order expressions.
     */
    ExpressionKind FIRST_ORDER =
            extend("first order", BOOLEAN,
                    Constant.class,
                    Equals.class, NotEquals.class,
                    GreaterEqual.class, GreaterThan.class, LessEqual.class, LessThan.class,
                    IntegerAdd.class, IntegerDivide.class, IntegerMultiply.class,
                    RealAdd.class, RealDivide.class, RealMultiply.class);

    /**
     * Represents all expressions.
     */
    ExpressionKind ARBITRARY =
            extend("arbitrary", FIRST_ORDER,
                    ProblemFormula.class, Reference.class);


    Collection<Class<? extends IExpression>> getAllowedClasses();

    @Override
    default boolean test(IExpression expression) {
        return expression.getDescendantsAsPreOrder().stream()
                .allMatch(e -> getAllowedClasses().stream()
                        .anyMatch(c -> c.isAssignableFrom(e.getClass())));
    }

    default void assertFor(IExpression expression) {
        if (!test(expression))
            throw new ExpressionKindNotSupportedException(this);
    }

    @SafeVarargs
    static ExpressionKind of(String name, Class<? extends IExpression>... allowedClasses) {
        LinkedHashSet<Class<? extends IExpression>> allowedClassesSet = Sets.of(allowedClasses);
        return new ExpressionKind() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public Collection<Class<? extends IExpression>> getAllowedClasses() {
                return allowedClassesSet;
            }
        };
    }

    @SafeVarargs
    static ExpressionKind extend(String name, ExpressionKind expressionKind, Class<? extends IExpression>... allowedClasses) {
        LinkedHashSet<Class<? extends IExpression>> allowedClassesSet =
                Sets.union(expressionKind.getAllowedClasses(), List.of(allowedClasses));
        return new ExpressionKind() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public Collection<Class<? extends IExpression>> getAllowedClasses() {
                return allowedClassesSet;
            }
        };
    }

    static ExpressionKind getExpressionKind(IExpression expression) {
        return Stream.of(NNF, BOOLEAN, QBF, FIRST_ORDER, ARBITRARY)
                .filter(expressionKind -> expressionKind.test(expression)).findFirst()
                .orElse(null);
    }
}
