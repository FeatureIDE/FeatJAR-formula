package org.sk.prop4j.structure;

import java.util.*;

import org.sk.trees.structure.*;

/**
 * A constraint that is true iff all child nodes are true.
 *
 * @author Sebastian Krieter
 */
public interface Compound extends Formula, NonTerminalNode<Formula> {

	@Override
	List<Formula> getChildren();

}
