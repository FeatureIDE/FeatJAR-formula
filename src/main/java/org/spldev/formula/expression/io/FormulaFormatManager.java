package org.spldev.formula.expression.io;

import org.spldev.formula.expression.*;
import org.spldev.formula.expression.io.parse.*;
import org.spldev.util.io.format.*;

public final class FormulaFormatManager extends FormatManager<Formula> {

	private static FormulaFormatManager INSTANCE = new FormulaFormatManager();

	static {
		INSTANCE.addExtension(new DIMACSFormat());
		INSTANCE.addExtension(new XmlFeatureModelCNFFormat());
		INSTANCE.addExtension(new XmlFeatureModelFormat());
		INSTANCE.addExtension(new FormulaFormat());
	}

	public static FormulaFormatManager getInstance() {
		return INSTANCE;
	}

	private FormulaFormatManager() {
	}

}
