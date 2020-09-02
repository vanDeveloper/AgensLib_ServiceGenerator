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
public enum Annotation {

	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- ANNOTATION TYPES
	//--------------------------------------------------------------------------------------------------------------------------------
	OVERRIDE("\t@Override\n"),
	NULLABLE("@Nullable"),
	NONNULL("@NonNull"),
	POST("\t@POST"),
	GET("\t@GET"),
	PUT("\t@PUT"),
	PATCH("\t@PATCH"),
	DELETE("\t@DELETE"),
	HEADERS("\t@Headers"),
	HEADER("@Header"),
	BODY("@Body"),
	QUERY("@Query"),
	FIELD("@Field"),
	PATH("@Path"),
	FORM_URL_ENCODED("\t@FormUrlEncoded"),
	MARK("\n\t//------------------------------------------------------------------------------------------------------\n" + 
		 "\t//MARK:- %s \n" + 
		 "\t//------------------------------------------------------------------------------------------------------\n");
		    
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- PROPERTIES
	//--------------------------------------------------------------------------------------------------------------------------------
	private String annotation;
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- CONSTRUCTOR
	//--------------------------------------------------------------------------------------------------------------------------------
	Annotation(String annotation) {
		this.annotation = annotation;
   	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- ENCAPSULATION
	//--------------------------------------------------------------------------------------------------------------------------------
	public String getValue() {
		return annotation;
	}
}
