package de.featjar.formula.structure.term.aggregate;

import de.featjar.base.tree.structure.ITree;
import de.featjar.formula.structure.ANonTerminalExpression;
import de.featjar.formula.structure.IExpression;
import de.featjar.formula.structure.term.ITerm;

import java.util.List;
import java.util.Optional;

public class Sum extends ANonTerminalExpression implements IAggregate {

    public Sum(ITerm iTerm) {
        super(iTerm);
    }

    @Override
    public String getName() {
        return "sum";
    }

    @Override
    public Class<Long> getType() {
        return Long.class;
    }

    @Override
    public Optional<?> evaluate(List<?> values) {
        int sum = 0;
        for (Object value : values) {
            if (value instanceof Long) {
                sum+=(Long) value;
            }
        }
        return Optional.ofNullable(sum);
    }

    @Override
    public ITree<IExpression> cloneNode() {
        return null;
    }
}
