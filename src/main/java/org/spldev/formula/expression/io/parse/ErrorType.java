/* -----------------------------------------------------------------------------
 * Formula Lib - Library to represent and edit propositional formulas.
 * Copyright (C) 2021  Sebastian Krieter
 * 
 * This file is part of Formula Lib.
 * 
 * Formula Lib is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Formula Lib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Formula Lib.  If not, see <https://www.gnu.org/licenses/>.
 * 
 * See <https://github.com/skrieter/formula> for further information.
 * -----------------------------------------------------------------------------
 */
package org.spldev.formula.expression.io.parse;

public class ErrorType {

	public enum ErrorEnum {
		None, InvalidFeatureName, InvalidExpressionLeft, InvalidExpressionRight, Default
	}

	private ErrorEnum error;
	private int startErrorIndex;
	private int endErrorIndex;
	private String keyword;

	public ErrorType(ErrorEnum error, int startErrorIndex, int endErrorIndex) {
		setError(error);
		setStartErrorIndex(startErrorIndex);
		setEndErrorIndex(endErrorIndex);
	}

	public ErrorType(ErrorEnum error) {
		setError(error);
	}

	public ErrorType(ErrorEnum error, String keyword) {
		setError(error);
		setKeyword(keyword);
	}

	public ErrorEnum getError() {
		return error;
	}

	public void setError(ErrorEnum error) {
		this.error = error;
	}

	public int getStartErrorIndex() {
		return startErrorIndex;
	}

	public void setStartErrorIndex(int startErrorIndex) {
		this.startErrorIndex = startErrorIndex;
	}

	public int getEndErrorIndex() {
		return endErrorIndex;
	}

	public void setEndErrorIndex(int endErrorIndex) {
		this.endErrorIndex = endErrorIndex;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

}
