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
package com.navi.agenslib.mas.utils.settings;

import java.io.Serializable;

/**
 * Created by van on 10/06/20.
 */
public class Configuration implements Serializable {

	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- PROPERTIES
	//--------------------------------------------------------------------------------------------------------------------------------
	private static final long serialVersionUID = 2L;
	private String api;
	private String package_name;
	private String context_methods;
	private String response_generic_error;
	private String outputPath;
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- CONSTRUCTOR
	//--------------------------------------------------------------------------------------------------------------------------------
	public Configuration(String api, String response_generic_error, String outputPath) {
		this.api = api;
		this.package_name = "com.navi.agenslib";
		this.context_methods = "";
		this.response_generic_error = response_generic_error;
		this.outputPath = outputPath;
	}
		
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- ENCAPSULATION
	//--------------------------------------------------------------------------------------------------------------------------------
	public void setApi(String api) { this.api = api; }
	public String getApi() { return this.api; }
	
	public void setPackage_name(String package_name) { this.package_name = package_name; }
	public String getPackage_name() { return this.package_name; }
	
	public void setContext_methods(String context_methods) { this.context_methods = context_methods; }
	public String getContext_methods() { return this.context_methods; }
	
	public void setResponse_generic_error(String response_generic_error) { this.response_generic_error = response_generic_error; }
	public String getResponse_generic_error() { return this.response_generic_error; }
	
	public void setOutputPath(String outputPath) { this.outputPath = outputPath; }
	public String getOutputPath() { return this.outputPath; }
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- PRETTY PRINT
	//--------------------------------------------------------------------------------------------------------------------------------
	@Override
	public String toString() {
		return "PackageName: " + this.package_name + 
				"\nApi: " + this.api + 
				"\nContextMethods: " + this.context_methods + 
				"\nResponseGenericError: " + this.response_generic_error +
				"\nOutputPath: " + this.outputPath; 
	}
}
