/************************************************************************************************************************************
 * LICENSE
 ************************************************************************************************************************************
 * 	This file is part of AgensLib.
 * 
 *  AgensLib is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  AgensLib is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.

 *  You should have received a copy of the GNU General Public License
 *  along with AgensLib.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.navi.agenslib.mas.utils.codegen;

/**
 * Created by van on 10/06/20.
 */
public enum ReservedWord {
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- RESERVED WORDS TYPES
	//--------------------------------------------------------------------------------------------------------------------------------
	PUBLIC("public"),
	PRIVATE("private"),
	PACKAGE("package"),
	INTERFACE("interface"),
	IMPLEMENTS("implements"),
	EXTENDS("extends"),
	IMPORT("import"),
	CLASS("class"),
	RETURN("return"),
	SUPER("super()"),
	THIS("this."),
	THROWS("throws"),
	THROWABLE("Throwable"),
	CALL("Call<%s>"),
	CALLBACK("Callback<%s>"),
	RESPONSE("Response<%s>"),
	STATIC("static"),
	FINAL("final"),
	VOID("void"),
	ENUM("enum"),
	IF("if"),
	ELSE("else"),
	TRY("try"),
	CATCH("catch"),
	SWITCH("switch"),
	CASE("case"),
	DEFAULT("default:"),
	BREAK("break"),
	NEW("new"),
	NULL("null"),
	CONTEXT("Context context");
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- PROPERTIES
	//--------------------------------------------------------------------------------------------------------------------------------
	private String reservedWord;
		
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- CONSTRUCTOR
	//--------------------------------------------------------------------------------------------------------------------------------
	ReservedWord(String reservedWord) {
		this.reservedWord = reservedWord;
	}
		
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- ENCAPSULATION
	//--------------------------------------------------------------------------------------------------------------------------------
	public String getValue() {
		return reservedWord;
	}
}
