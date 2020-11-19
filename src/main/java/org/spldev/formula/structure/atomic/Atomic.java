package org.spldev.formula.structure.atomic;

import java.util.*;

import org.spldev.formula.structure.*;
import org.spldev.formula.structure.term.*;

/**
 * An atomic formula.
 *
 * @author Sebastian Krieter
 */
public interface Atomic extends Formula {

	@Override
	List<? extends Term<?>> getChildren();

	Atomic flip();

}
