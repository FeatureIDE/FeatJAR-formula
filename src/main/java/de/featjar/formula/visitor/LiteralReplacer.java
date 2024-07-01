package de.featjar.formula.visitor;

import de.featjar.base.data.Result;
import de.featjar.base.data.Void;
import de.featjar.base.tree.visitor.ITreeVisitor;
import de.featjar.formula.assignment.Assignment;
import de.featjar.formula.structure.IExpression;
import de.featjar.formula.structure.IFormula;
import de.featjar.formula.structure.connective.IConnective;
import de.featjar.formula.structure.predicate.IPolarPredicate;
import de.featjar.formula.structure.predicate.IPredicate;
import de.featjar.formula.structure.predicate.Literal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Replaces literals with other literals.
 *
 * @author Andreas Gerasimow
 */
public class LiteralReplacer implements ITreeVisitor<IFormula, Void> {
    Map<IPolarPredicate, IExpression> literalMap;

    public LiteralReplacer(Map<IPolarPredicate, IExpression> literalMap) {
        this.literalMap = literalMap;
    }

    public LiteralReplacer(Assignment assignment) {
        this.literalMap = new HashMap<>();
        assignment.getAll().forEach((key, value) -> {
            if (value instanceof IExpression) {
                this.literalMap.put(new Literal(key), (IExpression) value);
            } else {
                throw new IllegalArgumentException("Value " + value + " is not an IExpression.");
            }
        });
    }

    @Override
    public TraversalAction firstVisit(List<IFormula> path) {
        final IFormula formula = ITreeVisitor.getCurrentNode(path);
        if (formula instanceof IPredicate) {
            return TraversalAction.SKIP_CHILDREN;
        } else if (formula instanceof IConnective) {
            return TraversalAction.CONTINUE;
        } else {
            return TraversalAction.FAIL;
        }
    }

    @Override
    public TraversalAction lastVisit(List<IFormula> path) {
        final IFormula formula = ITreeVisitor.getCurrentNode(path);
        formula.replaceChildren(c -> {
            if (c instanceof Literal && literalMap.containsKey(c)) {
                return literalMap.get(c);
            }
            return c;
        });
        return TraversalAction.CONTINUE;
    }

    @Override
    public Result<Void> getResult() {
        return Result.ofVoid();
    }
}
