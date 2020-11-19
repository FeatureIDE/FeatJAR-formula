package org.spldev.formula.parse;

import org.spldev.formula.structure.*;
import org.spldev.util.io.*;
import org.spldev.util.io.format.*;

public class FormulaFormat implements Format<Formula> {

	public static final String ID = FormulaFormat.class.getCanonicalName();

	@Override
	public ParseResult<Formula> parse(CharSequence source) {
		return new NodeReader().read(source.toString());
	}

	@Override
	public String serialize(Formula object) {
		return new NodeWriter().write(object);
	}

	@Override
	public boolean supportsParse() {
		return true;
	}

	@Override
	public boolean supportsSerialize() {
		return true;
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public String getFileExtension() {
		return "formula";
	}

	@Override
	public String getName() {
		return "Formula";
	}

}
