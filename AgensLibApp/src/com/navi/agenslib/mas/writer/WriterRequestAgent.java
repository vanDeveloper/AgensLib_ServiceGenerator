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
public class WriterRequestAgent extends WriterAgent {

	//--------------------------------------------------------------------------------------------------------------------------------
	// MARK:- PROPERTIES
	//--------------------------------------------------------------------------------------------------------------------------------
	private static final long serialVersionUID = 14L;

	//--------------------------------------------------------------------------------------------------------------------------------
	// MARK:- OVERRIDE FROM JADE
	//--------------------------------------------------------------------------------------------------------------------------------
	@Override
	protected void setup() {
		super.setup();
		super.aid = new AID("WriterRequestAgent", AID.ISLOCALNAME);
		super.role = BlackboardRole.WRITER_REQUEST_AGENT;
	}

	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- REQUEST PARAMS
	//--------------------------------------------------------------------------------------------------------------------------------
	protected void generateParams(AID workerAid, BlackboardTask taskDelegated, ServiceConfig service) {
		long startTime = System.nanoTime();
		String codeGenerated = ""; 
		
		if (service.getMode() != null && service.getMode().equalsIgnoreCase("raw")) {
			codeGenerated = Annotation.MARK.getValue()
					.replace("%s", Comments.VARIABLES_AND_COMPLEMENTS.getValue());
			Object[] keys = service.getRequestParams().keySet().toArray();
			Map values = service.getRequestParams();
			int i = 0;
			
			for (Object key : keys) {
				codeGenerated = codeGenerated + "\t" + ReservedWord.PUBLIC.getValue() + " " + values.get(key) + " "
						+ AgentUtils.firstToLowerCase((String) key) + ";";
				if(i != keys.length - 1) {	
					codeGenerated = codeGenerated + "\n";
				} 
				i++;
			}
		}
		
		taskDelegated.setState(BlackboardTaskState.FINISHED);
		long end = System.nanoTime();
		long executionTime = end - startTime;
		super.taskDone(workerAid, new GeneratedCode(1, this.getLocalName(),  codeGenerated, executionTime));
	}

	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- REQUEST CONSTRUCTOR
	//--------------------------------------------------------------------------------------------------------------------------------
	protected void generateConstructor(AID workerAid, BlackboardTask taskDelegated, ServiceConfig service) {
		long startTime = System.nanoTime();
		String codeGenerated = Annotation.MARK.getValue().replace("%s", Comments.CONSTRUCTOR.getValue());
		Object[] keys = service.getRequestParams().keySet().toArray();
		Map values = service.getRequestParams();
		int acc = 0;
		
		codeGenerated = codeGenerated + "\t" + ReservedWord.PUBLIC.getValue() + " " +
				AgentUtils.firstToUpperCase(service.getName()) + "Request (";
		
		if(service.getMode() != null && service.getMode().equalsIgnoreCase("raw")) {
			
			for (Object key : keys) {
				if (acc == keys.length - 1) {
					codeGenerated = codeGenerated + values.get(key) + " " + AgentUtils.firstToLowerCase((String) key);
				} else {
					codeGenerated = codeGenerated + values.get(key) + " " + AgentUtils.firstToLowerCase((String) key)
							+ ", \n\t\t\t\t";
				}
				acc++;
			}
			codeGenerated = codeGenerated + ") {\n\t\t" + ReservedWord.SUPER.getValue() + ";\n";
		
			for (Object key : keys) {
				String param = AgentUtils.firstToLowerCase((String) key);
				codeGenerated = codeGenerated + "\t\t" + ReservedWord.THIS.getValue() + param + " = " + param + ";\n";
			}
			codeGenerated = codeGenerated + "\t}\n";
		
			/*codeGenerated = codeGenerated + Annotation.MARK.getValue().replace("%s", Comments.JSON_REPRESENTATION.getValue()) + "\t" +  
					ReservedWord.PUBLIC.getValue() + " JSONObject toJSON() " + 
					ReservedWord.THROWS.getValue() + " Exception {\n\t\t" + 
					"JSONObject json = " + ReservedWord.NEW.getValue() + " JSONObject();\n\t\t";
					
			for (Object key : keys) {
				String field = AgentUtils.firstToLowerCase((String) key);
				codeGenerated = codeGenerated + "json.put(\"" + field + "\", " + 
						ReservedWord.THIS.getValue() + field + ");\n\t\t";
			}
			codeGenerated = codeGenerated + ReservedWord.RETURN.getValue() + " json;\n\t}";
			*/
		} else {
			codeGenerated = codeGenerated + ") {}";
		}
		
		taskDelegated.setState(BlackboardTaskState.FINISHED);
		long end = System.nanoTime();
		long executionTime = end - startTime;
		super.taskDone(workerAid, new GeneratedCode(2, this.getLocalName(), codeGenerated, executionTime));
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- SERVICE REQUEST SETUP
	//--------------------------------------------------------------------------------------------------------------------------------
	protected void generateServiceRequestSetup(AID workerAid) {
		long startTime = System.nanoTime();
		String codeGenerated = Annotation.MARK.getValue()
				.replace("%s", Comments.SERVICE_REQUEST_SETUP.getValue()) + "\t" +
				ReservedWord.PRIVATE.getValue() + " " + 
				ReservedWord.VOID.getValue() + " setup() {\n\t\t" + 
				"ServiceConfig.sharedInstance().setup();\n\n\t\t" + 
				"Gson gson = new GsonBuilder()\n" + "\t\t\t" + 
				".setPrettyPrinting()\n\t\t\t" + 
				".setLenient()\n\t\t\t" +
				".create();\n\n\t\t" + 
				"Retrofit retrofit = " + ReservedWord.NEW.getValue() + " Retrofit.Builder()\n\t\t\t"  +
				".baseUrl(ServiceConfig.sharedInstance().getBackend().getUrl())\n\t\t\t" + 
				".addConverterFactory(GsonConverterFactory.create(gson))\n\t\t\t" +
				".build();\n\n\t\t" +
				"api = retrofit.create(Api.class);\n\t}";
		long end = System.nanoTime();
		long executionTime = end - startTime;
		super.taskDone(workerAid, new GeneratedCode(1, this.getLocalName(), codeGenerated, executionTime));
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- SERVICE REQUEST ENCAPSULATION
	//--------------------------------------------------------------------------------------------------------------------------------
	protected void generateServiceRequestEncapsulation(AID workerAid) {
		long startTime = System.nanoTime();
		String codeGenerated = Annotation.MARK.getValue()
				.replace("%s", Comments.ENCAPSULATION.getValue()) + "\t" + 
				ReservedWord.PUBLIC.getValue() + " Api getApi() { " + 
				ReservedWord.RETURN.getValue() + " " + ReservedWord.THIS.getValue() + "api; }";
		long end = System.nanoTime();
		long executionTime = end - startTime;
		super.taskDone(workerAid, new GeneratedCode(2, this.getLocalName(), codeGenerated, executionTime));
	}
	
}
