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
import jade.util.leap.Map;

/**
 * Created by van on 10/06/20.
 */
public class WriterResponseAgent extends WriterAgent {

	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- PROPERTIES
	//--------------------------------------------------------------------------------------------------------------------------------
	private static final long serialVersionUID = 15L;
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- OVERRIDE FROM JADE
	//--------------------------------------------------------------------------------------------------------------------------------
	@Override
	protected void setup() {
		super.setup();
		super.aid = new AID("WriterResponseAgent", AID.ISLOCALNAME);
		super.role = BlackboardRole.WRITER_RESPONSE_AGENT;
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:-  RESPONSE PARAMS
	//--------------------------------------------------------------------------------------------------------------------------------	
	protected void generateParams(AID workerAid, BlackboardTask taskDelegated, ServiceConfig service) {
		long startTime = System.nanoTime();
		String codeGenerated = Annotation.MARK.getValue()
					.replace("%s", Comments.VARIABLES_AND_COMPLEMENTS.getValue());
		Object[] keys = service.getResponseParams().keySet().toArray();
		Map values = service.getResponseParams();
		int i = 0; 
		
		for(Object key : keys) {
			String type = (String)values.get(key);
			String correctType = AgentUtils.getValidName(type);
			
			codeGenerated = codeGenerated + "\t" + 
					ReservedWord.PRIVATE.getValue() + " " + 
					correctType + " " + 
					AgentUtils.firstToLowerCase((String)key) + ";";
			if(i != keys.length - 1) {	
				codeGenerated = codeGenerated + "\n";
			} 
			i++;
		}
		taskDelegated.setState(BlackboardTaskState.FINISHED);
		long end = System.nanoTime();
		long executionTime = end - startTime;
		super.taskDone(workerAid, new GeneratedCode(1, this.getLocalName(), codeGenerated, executionTime));
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:-  RESPONSE CONSTRUCTOR
	//--------------------------------------------------------------------------------------------------------------------------------	
	protected void generateConstructor(AID workerAid, BlackboardTask taskDelegated, ServiceConfig service) {
		long startTime = System.nanoTime();
		String codeGenerated = Annotation.MARK.getValue()
					.replace("%s", Comments.CONSTRUCTOR.getValue());
		Object[] keys = service.getResponseParams().keySet().toArray();
		Map values = service.getResponseParams();
		int acc = 0;
			
		codeGenerated = codeGenerated + "\t" + ReservedWord.PUBLIC.getValue() + " " + 
				AgentUtils.firstToUpperCase(service.getName()) + "Response ("; 
			
		for(Object key : keys) {
			String type = (String)values.get(key);
			String correctType = AgentUtils.getValidName(type);
			
			if(acc == keys.length - 1) {
				codeGenerated = codeGenerated + correctType + " " + 
						AgentUtils.firstToLowerCase((String)key);
			} else {
				codeGenerated = codeGenerated + correctType + " " + 
						AgentUtils.firstToLowerCase((String)key) + ", \n\t\t\t\t";
			}
			acc++;
		}
		codeGenerated = codeGenerated + ") {\n\t\t" + ReservedWord.SUPER.getValue() + ";\n";
			
		for(Object key : keys) {
			String param = AgentUtils.firstToLowerCase((String)key);
			codeGenerated = codeGenerated + "\t\t" + ReservedWord.THIS.getValue() + param + " = " + param + ";\n";
		}
		codeGenerated = codeGenerated + "\t}";
		taskDelegated.setState(BlackboardTaskState.FINISHED);
		long end = System.nanoTime();
		long executionTime = end - startTime;
		super.taskDone(workerAid, new GeneratedCode(2, this.getLocalName(), codeGenerated, executionTime));
	}
		
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:-  RESPONSE ENCAPSULATION
	//--------------------------------------------------------------------------------------------------------------------------------	
	protected void generateEncapsulation(AID workerAid, BlackboardTask taskDelegated, ServiceConfig service) {
		long startTime = System.nanoTime();
		Object[] keys = service.getResponseParams().keySet().toArray();
		String codeGenerated = Annotation.MARK.getValue()
				.replace("%s", Comments.ENCAPSULATION.getValue());
			
		for(Object key : keys) {
			String param = AgentUtils.firstToLowerCase((String)key);
			String dataType = (String)service.getResponseParams().get(key);
			String method = AgentUtils.getValidName(dataType);
			String correctType = AgentUtils.getValidName(dataType);
			
			if(AgentUtils.isSimpleDataType(dataType)) {
				method = AgentUtils.firstToUpperCase((String)key);
			}
			
			// GENERATING SET METHOD
			codeGenerated = codeGenerated + "\t" + ReservedWord.PUBLIC.getValue() + " " +
					ReservedWord.VOID.getValue() + " set" + method + "(" + 
					correctType + " " + param + ") {";
			codeGenerated = codeGenerated + " " + ReservedWord.THIS.getValue() + param + " = " + param + "; }\n";
			
			// GENERATING GET METHOD
			codeGenerated = codeGenerated + "\t" + ReservedWord.PUBLIC.getValue() + " " + correctType + 
					" get" + method + "() { " + ReservedWord.RETURN.getValue() + " " +
					ReservedWord.THIS.getValue() + param + "; }\n\n";
		}
		taskDelegated.setState(BlackboardTaskState.FINISHED);
		long end = System.nanoTime();
		long executionTime = end - startTime;
		super.taskDone(workerAid, new GeneratedCode(3, this.getLocalName(), codeGenerated, executionTime));
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- SERVICE BACKEND PARAMS
	//--------------------------------------------------------------------------------------------------------------------------------	
	protected void generateServiceBackendParams(AID workerAid) {
		long startTime = System.nanoTime();
		String codeGenerated = Annotation.MARK.getValue()
				.replace("%s", Comments.VARIABLES_AND_COMPLEMENTS.getValue()) + "\t" + 
				ReservedWord.PRIVATE.getValue() + " String protocol;\n\t" +
				ReservedWord.PRIVATE.getValue() + " String domain;\n\t" +
				ReservedWord.PRIVATE.getValue() + " String port;\n\t" +
				ReservedWord.PRIVATE.getValue() + " String context;\n\n\t" + 
				ReservedWord.PUBLIC.getValue() + " " + ReservedWord.ENUM.getValue() + " Environment { DEV };"; 
		long end = System.nanoTime();
		long executionTime = end - startTime;
		super.taskDone(workerAid, new GeneratedCode(1, this.getLocalName(), codeGenerated, executionTime));
	}
		
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- SERVICE BACKEND CONSTRUCTOR
	//--------------------------------------------------------------------------------------------------------------------------------	
	protected void generateServiceBackendConstructor(AID workerAid, ServiceConfig service) {
		long startTime = System.nanoTime();
		String port = (service.getPort() == null)?"":service.getPort();
		String codeGenerated = Annotation.MARK.getValue()
				.replace("%s", Comments.CONSTRUCTOR.getValue()) + "\t" + 
				ReservedWord.PUBLIC.getValue() + " ServiceBackend(Environment env){\n\t\t" + 
				ReservedWord.SWITCH.getValue() + "(env){\n\t\t\t" +
				ReservedWord.CASE.getValue() + " DEV:\n\t\t\t\t" + 
				ReservedWord.THIS.getValue() + "protocol = \"" + service.getProtocol() + "\";\n\t\t\t\t" + 
				ReservedWord.THIS.getValue() + "domain = \"" + service.getHost() + "\";\n\t\t\t\t" + 
				ReservedWord.THIS.getValue() + "port = \"" + port + "\";\n\t\t\t\t" + 
				ReservedWord.BREAK.getValue() + ";\n\t\t\t" +
				ReservedWord.DEFAULT.getValue() + "\n\t\t\t" + 
				ReservedWord.BREAK.getValue() + ";\n\t\t}\n\t}";
		long end = System.nanoTime();
		long executionTime = end - startTime;
		super.taskDone(workerAid, new GeneratedCode(2, this.getLocalName(), codeGenerated, executionTime));
	}
			
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- SERVICE BACKEND ENCAPSULATION
	//--------------------------------------------------------------------------------------------------------------------------------	
	protected void generateServiceBackendEncapsulation(AID workerAid) {
		long startTime = System.nanoTime();
		String codeGenerated = Annotation.MARK.getValue()
				.replace("%s", Comments.ENCAPSULATION.getValue()) + "\t" +
				ReservedWord.PUBLIC.getValue() + " String getUrl(){\n\t\t" + 
				ReservedWord.IF.getValue() + "(" + ReservedWord.THIS.getValue() + "port == " + 
				ReservedWord.NULL.getValue() + " || " + ReservedWord.THIS.getValue() + "port.equals(\"\")){\n\t\t\t" + 
				ReservedWord.RETURN.getValue() + " " + ReservedWord.THIS.getValue() + "protocol + \"://\" + " + 
				ReservedWord.THIS.getValue() + "domain;\n\t\t} " + ReservedWord.ELSE.getValue() + " {\n\t\t\t" + 
				ReservedWord.RETURN.getValue() + " " + ReservedWord.THIS.getValue() + "protocol + \"://\" + " + 
				ReservedWord.THIS.getValue() + "domain + \":\" + " + ReservedWord.THIS.getValue() + "port + \"/\";\n\t\t}\n\t}"; 
		long end = System.nanoTime();
		long executionTime = end - startTime;
		super.taskDone(workerAid, new GeneratedCode(3, this.getLocalName(), codeGenerated, executionTime));
	}
}
