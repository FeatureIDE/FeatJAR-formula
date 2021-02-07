package org.spldev.formula.expression.io;

import java.io.*;
import java.text.*;

import org.spldev.formula.expression.*;
import org.spldev.util.*;
import org.spldev.util.io.format.*;
import org.spldev.util.io.format.Format;

/**
 * Reads and writes feature models in the DIMACS CNF format.
 *
 * @author Sebastian Krieter
 * @author Timo G&uuml;nther
 */
public class DIMACSFormat implements Format<Formula> {

	public static final String ID = ".format.cnf." + DIMACSFormat.class.getSimpleName();

	@Override
	public Result<Formula> parse(CharSequence source) {
		final DimacsReader r = new DimacsReader();
		r.setReadingVariableDirectory(true);
		try {
			return Result.of(r.read(source.toString()));
		} catch (final ParseException e) {
			return Result.empty(new ParseProblem(e, e.getErrorOffset()));
		} catch (final IOException e) {
			return Result.empty(e);
		}
	}

	@Override
	public DIMACSFormat getInstance() {
		return this;
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public boolean supportsSerialize() {
		return false;
	}

	@Override
	public boolean supportsParse() {
		return true;
	}

	@Override
	public String getName() {
		return "DIMACS";
	}

	@Override
	public String getFileExtension() {
		return "dimacs";
	}

}
