package de.featjar.formula.structure.term.aggregate;

import de.featjar.base.tree.structure.ITree;
import de.featjar.formula.structure.ATerminalExpression;
import de.featjar.formula.structure.IExpression;

import java.util.List;
import java.util.Optional;

/**
 * The average aggregate placeholder sums attribute values from attributes with a specific attribute name and divides
 * the sum by the number of attributes.
 * Only boolean features which are selected ({@link de.featjar.formula.structure.term.value.Variable} with
 * type {@link Boolean} and value true) will be considered.
 *
 * @author Lara Merza
 * @author Felix
 * @author Jonas Hanke
 */
public class AttributeAverage extends ATerminalExpression implements IAttributeAggregate {

    private final String nameOfAttribute;

    public AttributeAverage(String nameOfAttribute) {
        this.nameOfAttribute = nameOfAttribute;
    }

    @Override
    public String getName() {
        return "avg";
    }

    @Override
    public Class<?> getType() {
        return Double.class;
    }

    @Override
    public Optional<?> evaluate(List<?> values) {
        // TODO
        return Optional.empty();
    }

    @Override
    public ITree<IExpression> cloneNode() {
        return new AttributeAverage(nameOfAttribute);
    }
}