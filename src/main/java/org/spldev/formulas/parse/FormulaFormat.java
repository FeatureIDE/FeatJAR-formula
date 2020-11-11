package org.spldev.formulas.parse;

import java.util.*;

import org.spldev.formulas.structure.*;
import org.spldev.utils.io.formats.*;

public class FormulaFormat implements Format<Formula> {

	public static final String ID = FormulaFormat.class.getCanonicalName();

	@Override
	public Optional<Formula> parse(CharSequence source) {
		return new NodeReader().read(source.toString());
	}

	@Override
	public Optional<Formula> parse(CharSequence source, List<ParseProblem> problems) {
		return new NodeReader().read(source.toString(), problems);
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
