package de.featjar.formula.structure.term.function;

import de.featjar.base.tree.structure.ITree;
import de.featjar.formula.structure.ANonTerminalExpression;
import de.featjar.formula.structure.IExpression;
import de.featjar.formula.structure.term.ITerm;
import de.featjar.formula.structure.term.value.Variable;

import java.util.List;
import java.util.Optional;

public class IfThenElse extends ANonTerminalExpression implements IFunction {

    private final Class<?> type;

    public IfThenElse(Variable variable, ITerm term1, ITerm term2, final Class<?> type) {
        super(variable, term1, term2);

        this.type = type;
    }

    @Override
    public String getName() {
        return "IfThenElse";
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public Optional<?> evaluate(List<?> values) {
        Object condition = values.get(0);
        if(condition instanceof Boolean) {
            return Optional.ofNullable((Boolean) condition ? values.get(1) : values.get(2));
        }
        return Optional.empty();
    }

    @Override
    public ITree<IExpression> cloneNode() {
        return null;
    }
}