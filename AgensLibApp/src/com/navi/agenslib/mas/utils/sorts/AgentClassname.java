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
package com.navi.agenslib.mas.utils.sorts;

/**
 * Created by van on 10/06/20.
 */
public enum AgentClassname {

	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- AGENTS CLASSNAME'S TYPES
	//--------------------------------------------------------------------------------------------------------------------------------
	MANAGER("com.navi.agenslib.mas.manager.ManagerAgent"),
	WORKER("com.navi.agenslib.mas.worker.WorkerAgent"),
	
	INTERACTOR("com.navi.agenslib.mas.writer.WriterInteractorAgent"),
	DAO("com.navi.agenslib.mas.writer.WriterDaoAgent"),
	DAO_IMPL("com.navi.agenslib.mas.writer.WriterDaoImplAgent"),
	REQUEST("com.navi.agenslib.mas.writer.WriterRequestAgent"),
	RESPONSE("com.navi.agenslib.mas.writer.WriterResponseAgent");
		
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- PROPERTIES
	//--------------------------------------------------------------------------------------------------------------------------------
	private String classname;
		 
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- CONSTRUCTOR
	//--------------------------------------------------------------------------------------------------------------------------------
	AgentClassname(String classname) {
		this.classname = classname;
	}
		
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- ENCAPSULATION
	//--------------------------------------------------------------------------------------------------------------------------------
	public String getClassname() {
	   	return classname;
	}
		
}
