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

import java.io.Serializable;

/**
 * Created by van on 10/06/20.
 */
public class GeneratedCode implements Serializable {

	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- AGENT PROPERTIES
	//--------------------------------------------------------------------------------------------------------------------------------
	private static final long serialVersionUID = 3L;
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- PROPERTIES
	//--------------------------------------------------------------------------------------------------------------------------------
	private int order;
	private String agentName;
	private String codegen;
	private long executionTime;
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- CONSTRUCTOR
	//--------------------------------------------------------------------------------------------------------------------------------
	public GeneratedCode(int order, String agentName, String codegen, long executionTime) {
		this.order = order;
		this.agentName = agentName;
		this.codegen = codegen;
		this.executionTime = executionTime;
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- ENCAPSULATION
	//--------------------------------------------------------------------------------------------------------------------------------
	public void setOrder(int order) { this.order = order; }
	public int getOrder() { return this.order; }
	
	public void setAgentName(String agentName) { this.agentName = agentName; }
	public String getAgentName() { return this.agentName; }
	
	public void setCodegen(String codegen) { this.codegen = codegen; }
	public String getCodegen() { return this.codegen; }
	
	public void setExecutionTime(long executionTime) { this.executionTime = executionTime; }
	public long getExecutionTime() { return this.executionTime; }
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- PRETTY PRINT
	//--------------------------------------------------------------------------------------------------------------------------------
	public String toString() {
		return "Order: " + this.order + 
				"\nAgentName: " + this.agentName + 
				"\nCodegen: " + this.codegen + 
				"\nExecutionTime: " + this.executionTime;
	}
}
