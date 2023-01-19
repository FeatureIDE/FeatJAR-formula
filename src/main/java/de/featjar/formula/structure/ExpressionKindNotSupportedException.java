package de.featjar.formula.structure;


public class ExpressionKindNotSupportedException extends RuntimeException {
    public ExpressionKindNotSupportedException() {
    }

    public ExpressionKindNotSupportedException(String expectedExpressionKind) {
        super("expression kind not supported, expected " + expectedExpressionKind);
    }
}
