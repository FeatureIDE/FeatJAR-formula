package de.featjar.formula.structure.term.aggregate;

import de.featjar.base.tree.structure.ITree;
import de.featjar.formula.structure.ANonTerminalExpression;
import de.featjar.formula.structure.IExpression;
import de.featjar.formula.structure.IFormula;
import de.featjar.formula.structure.term.ITerm;

import java.beans.Expression;
import java.util.List;
import java.util.Optional;

public class Avarage extends ANonTerminalExpression implements IAggregate {

    public Avarage(ITerm iTerm) {
        super(iTerm);
    }

    @Override
    public String getName() {
        return "avg";
    }

    @Override
    public Class<Double> getType() {
        return Double.class;
    }

    @Override
    public Optional<?> evaluate(List<?> values) {
        double size = values.size();
        double sum = 0;
        for (Object value : values) {
            if (value instanceof Double) {
                sum+=(Double) value;
            }
        }
        return Optional.ofNullable(sum / size);
    }

    @Override
    public ITree<IExpression> cloneNode() {
        return null;
    }
}
