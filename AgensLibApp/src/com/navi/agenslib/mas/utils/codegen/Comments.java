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
public enum Comments {
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- COMMENTS USED
	//--------------------------------------------------------------------------------------------------------------------------------
	VARIABLES_AND_COMPLEMENTS("Variables & Complements"),
	CONSTRUCTOR("Constructor"),
	ENCAPSULATION("Encapsulation"),
	SERVICE_IMPLEMENTATION("%s Implementation"),
	JSON_REPRESENTATION("Json representation"),
	CALLBACK_SERVICE("Callback Retrofit"),
	SERVICE_REQUEST_SETUP("Service Request Setup"),
	SERVICE_RESPONSE_OPSTATUS("Common Opstatus Code"),
	BACKEND_SETUP("Backend setup with X environment"),
	API_METHODS("Service Methods to be consumed by the user"),
	SINGLETON("Singleton"),	
	TODO_RESPONSE("//TODO: Handle the response here..."),
	TO_STRING("To String");	
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- PROPERTIES
	//--------------------------------------------------------------------------------------------------------------------------------
	private String comment;
				
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- CONSTRUCTOR
	//--------------------------------------------------------------------------------------------------------------------------------
	Comments(String comment) {
		this.comment = comment;
	}
				
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- ENCAPSULATION
	//--------------------------------------------------------------------------------------------------------------------------------
	public String getValue() {
		return comment;
	}
}
