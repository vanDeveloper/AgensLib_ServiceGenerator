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
package com.navi.agenslib.mas.writer;

import com.navi.agenslib.mas.utils.AgentUtils;
import com.navi.agenslib.mas.utils.blackboard.BlackboardRole;
import com.navi.agenslib.mas.utils.blackboard.BlackboardTask;
import com.navi.agenslib.mas.utils.blackboard.BlackboardTaskState;
import com.navi.agenslib.mas.utils.codegen.Annotation;
import com.navi.agenslib.mas.utils.codegen.Comments;
import com.navi.agenslib.mas.utils.codegen.GeneratedCode;
import com.navi.agenslib.mas.utils.codegen.ReservedWord;
import com.navi.agenslib.mas.utils.settings.ServiceConfig;

import jade.core.AID;

/**
 * Created by van on 10/06/20.
 */
public class WriterInteractorAgent extends WriterAgent {

	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- PROPERTIES
	//--------------------------------------------------------------------------------------------------------------------------------
	private static final long serialVersionUID = 13L;
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- OVERRIDE FROM JADE
	//--------------------------------------------------------------------------------------------------------------------------------
	@Override
	protected void setup() {
		super.setup();
		super.aid = new AID("WriterInteractorAgent", AID.ISLOCALNAME);
		super.role = BlackboardRole.WRITER_INTERACTOR_AGENT;
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- INTERACTOR (INTERFACE) SUCCESS METHOD
	//--------------------------------------------------------------------------------------------------------------------------------
	protected void generateInteractorSuccess(AID workerAid, BlackboardTask taskDelegated, ServiceConfig service) {
		long startTime = System.nanoTime();
		String successMethod = "Success" + "(" + service.getName() + "Response response);";
		String codeGenerated = "\t" + ReservedWord.VOID.getValue() + 
				" on" + AgentUtils.firstToUpperCase(service.getName()) + successMethod; 
		taskDelegated.setState(BlackboardTaskState.FINISHED);	
		long end = System.nanoTime();
		long executionTime = end - startTime;
		super.taskDone(workerAid, new GeneratedCode(1, this.getLocalName(), codeGenerated, executionTime));
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- INTERACTOR (INTERFACE) FAILURE METHOD
	//--------------------------------------------------------------------------------------------------------------------------------
	protected void generateInteractorFailure(AID workerAid, BlackboardTask taskDelegated, ServiceConfig service) {
		long startTime = System.nanoTime();
		String codeGenerated = "\t" + ReservedWord.VOID.getValue()+ 
				" on" + AgentUtils.firstToUpperCase(service.getName()) + "Failure(Error error);";
		taskDelegated.setState(BlackboardTaskState.FINISHED);
		long end = System.nanoTime();
		long executionTime = end - startTime;
		super.taskDone(workerAid, new GeneratedCode(2, this.getLocalName(), codeGenerated, executionTime));
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- SERVICE REQUEST (PARAMS)
	//--------------------------------------------------------------------------------------------------------------------------------
	protected void generateServiceRequestParams(AID workerAid) {
		long startTime = System.nanoTime();
		String codeGenerated = Annotation.MARK.getValue()
				.replace("%s", Comments.VARIABLES_AND_COMPLEMENTS.getValue()) + 
				"\t" + ReservedWord.PRIVATE.getValue() + " Api api;"; 
		long end = System.nanoTime();
		long executionTime = end - startTime;
		super.taskDone(workerAid, new GeneratedCode(1, this.getLocalName(), codeGenerated, executionTime));
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- SERVICE REQUEST (CONSTRUCTOR)
	//--------------------------------------------------------------------------------------------------------------------------------
	protected void generateServiceRequestConstructor(AID workerAid) {
		long startTime = System.nanoTime();
		String codeGenerated = Annotation.MARK.getValue()
				.replace("%s", Comments.CONSTRUCTOR.getValue()) + 
				"\t" + ReservedWord.PUBLIC.getValue() + " ServiceRequest(){\n\t\t" + 
				ReservedWord.THIS.getValue() + "setup();\n\t}"; 
		long end = System.nanoTime();
		long executionTime = end - startTime;
		super.taskDone(workerAid, new GeneratedCode(2, this.getLocalName(), codeGenerated, executionTime));
	}
}
