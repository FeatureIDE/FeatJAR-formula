///* -----------------------------------------------------------------------------
// * Formula Lib - Library to represent and edit propositional formulas.
// * Copyright (C) 2021-2022  Sebastian Krieter
// * 
// * This file is part of Formula Lib.
// * 
// * Formula Lib is free software: you can redistribute it and/or modify it
// * under the terms of the GNU Lesser General Public License as published by
// * the Free Software Foundation, either version 3 of the License,
// * or (at your option) any later version.
// * 
// * Formula Lib is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
// * See the GNU Lesser General Public License for more details.
// * 
// * You should have received a copy of the GNU Lesser General Public License
// * along with Formula Lib.  If not, see <https://www.gnu.org/licenses/>.
// * 
// * See <https://github.com/skrieter/formula> for further information.
// * -----------------------------------------------------------------------------
// */
//package org.spldev.formula.structure.term;
//
//import java.util.*;
//
//import org.spldev.formula.structure.*;
//import org.spldev.formula.structure.atomic.literal.*;
//import org.spldev.formula.structure.atomic.literal.NamedTermMap.*;
//import org.spldev.formula.structure.atomic.literal.VariableMap.*;
//
//public class Constant<T> extends ValueTerm<T> implements Term<T> {
//
//	public Constant(int index, VariableMap map) {
//		super(index, map);
//	}
//
//	public Constant(Constant<T> oldConstant) {
//		super(oldConstant);
//	}
//
//	public Constant<T> cloneNode() {
//		return new Constant<>(this);
//	}
//
//	@Override
//	public String getName() {
//		return map.getConstant(index).map(Signature::getName).orElseThrow(
//			() -> new NoSuchElementException());
//	}
//
//	@SuppressWarnings("unchecked")
//	@Override
//	public Class<T> getType() {
//		return (Class<T>) map.getConstant(index).map(Signature::getType).orElseThrow(
//			() -> new NoSuchElementException());
//	}
//
//	@SuppressWarnings("unchecked")
//	public T getValue() {
//		return (T) map.getConstant(index).map(ConstantSignature::getValue).orElseThrow(
//			() -> new NoSuchElementException());
//	}
//
//	@Override
//	public void setVariableMap(VariableMap newMap) {
//		index = Objects.requireNonNull(newMap).getConstantSignature(getName()).map(Signature::getIndex).orElse(0);
//		this.map = newMap;
//	}
//
//	@Override
//	public T eval(List<?> values) {
//		assert Expression.checkValues(0, values);
//		return getValue();
//	}
//
//}
