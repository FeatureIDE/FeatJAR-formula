package de.featjar.formula.structure.term.aggregate;

import de.featjar.formula.structure.term.ITerm;

/**
 * Interface for modelling attribute aggregate functionality. Currently, attribute aggregates are placeholders which
 * will be substituted with the correct first order logic formula.
 *
 * @author Lara Merza
 * @author Felix Behme
 * @author Jonas Hanke
 */
public interface IAttributeAggregate extends ITerm {

    String getAttributeName();
}
