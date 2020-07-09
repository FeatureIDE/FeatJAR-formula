package org.sk.prop4j.structure.compound;

import java.util.List;

import org.sk.prop4j.structure.Formula;
import org.sk.trees.structure.NonTerminalNode;

/**
 * A constraint that is true iff all child nodes are true.
 *
 * @author Sebastian Krieter
 */
public interface Compound extends Formula, NonTerminalNode<Formula> {

	@Override
	List<Formula> getChildren();

}
