package de.featjar.formula.io;

import de.featjar.base.FeatJAR;
import de.featjar.base.io.format.AFormats;
import de.featjar.formula.assignment.BooleanAssignment;

public class BooleanAssignmentFormats extends AFormats<BooleanAssignment> {

	public static BooleanAssignmentFormats getInstance() {
		return FeatJAR.extensionPoint(BooleanAssignmentFormats.class);
	}
}
