package de.featjar.formula.structure;


public class ExpressionKindNotSupportedException extends RuntimeException {
    public ExpressionKindNotSupportedException() {
    }

    public ExpressionKindNotSupportedException(ExpressionKind expressionKind) {
        super("expression kind not supported, expected " + expressionKind.getName());
    }
}
