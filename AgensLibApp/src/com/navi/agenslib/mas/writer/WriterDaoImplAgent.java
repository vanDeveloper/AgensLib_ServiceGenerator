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
import com.navi.agenslib.mas.utils.settings.Configuration;
import com.navi.agenslib.mas.utils.settings.ServiceConfig;

import jade.core.AID;

/**
 * Created by van on 10/06/20.
 */
public class WriterDaoImplAgent extends WriterAgent {

	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- PROPERTIES
	//--------------------------------------------------------------------------------------------------------------------------------
	private static final long serialVersionUID = 12L;
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- OVERRIDE FROM JADE
	//--------------------------------------------------------------------------------------------------------------------------------
	@Override
	protected void setup() {
		super.setup();
		super.aid = new AID("WriterDaoImplAgent", AID.ISLOCALNAME);
		super.role = BlackboardRole.WRITER_DAOIMPL_AGENT;
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- DAOIMPL PARAMS
	//--------------------------------------------------------------------------------------------------------------------------------
	protected void generateDaoImplParams(AID workerAid, BlackboardTask taskDelegated, ServiceConfig service) {
		long startTime = System.nanoTime();
		String dataType = AgentUtils.firstToUpperCase(service.getName());
		String codeGenerated = Annotation.MARK.getValue()
					.replace("%s", Comments.VARIABLES_AND_COMPLEMENTS.getValue()) +
					"\t" + ReservedWord.PRIVATE.getValue() + " " + dataType + "Interactor listener;\n" + 
					"\t" + ReservedWord.PRIVATE.getValue() + " " + dataType + "Request request;";
		taskDelegated.setState(BlackboardTaskState.FINISHED);
		long end = System.nanoTime();
		long executionTime = end - startTime;
		super.taskDone(workerAid, new GeneratedCode(1, this.getLocalName(), codeGenerated, executionTime));
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- DAOIMPL INTERFACE IMPLEMENTATION METHOD
	//--------------------------------------------------------------------------------------------------------------------------------
	protected void generateDaoMethod(AID workerAid, BlackboardTask taskDelegated, ServiceConfig service) { 
		long startTime = System.nanoTime();
		String classname = AgentUtils.firstToUpperCase(service.getName());
		String codeGenerated = Annotation.MARK.getValue()
				.replace("%s", Comments.SERVICE_IMPLEMENTATION.getValue()
				.replace("%s", service.getName())) +
				Annotation.OVERRIDE.getValue() + "\t" + 
				ReservedWord.PUBLIC.getValue() + " " + 
				ReservedWord.VOID.getValue() + " " + 
				AgentUtils.firstToLowerCase(service.getName()) + " (";
				
		if(super.getConfiguration().getContext_methods().contains(classname) ) {
			codeGenerated = codeGenerated + ReservedWord.CONTEXT.getValue() + ",\n\t\t\t";
		}
		
		boolean needsIdPath = false;
		if(service.getPath().contains("{idPath}")) {
			codeGenerated = codeGenerated + "String idPath,\n\t\t\t";
			needsIdPath = true;
		}
		
		Object[] keys = service.getRequestParams().keySet().toArray();
		int acc = 0;
		
		for(Object key : keys) {
			String param = AgentUtils.firstToLowerCase((String)key);
			if(acc == 0) {
				codeGenerated = codeGenerated + service.getRequestParams().get(key) + " " + param + ",\n";
			} else {
				codeGenerated = codeGenerated + "\t\t\t" + service.getRequestParams().get(key) + " " + param + ",\n";
			}
			acc++;
		}
		
		if(keys.length == 0) {
			codeGenerated = codeGenerated + classname + "Interactor listener) {\n";
		} else {
			codeGenerated = codeGenerated + "\t\t\t" + classname + "Interactor listener) {\n";
		}
			
		// INITIALIZATION 
		codeGenerated = codeGenerated + "\t\t" + 
				ReservedWord.THIS.getValue() + "listener = listener;\n";
		codeGenerated = codeGenerated + "\t\t" + 
				ReservedWord.THIS.getValue() + "request = " + ReservedWord.NEW.getValue() + " " + classname + "Request(";
		
		String contentType = AgentUtils.getContentType(service.getHeaders());
		String params = "";
		acc = 0;
			
		for(Object key :keys) {
			if(acc == keys.length - 1) {
				params = params + AgentUtils.firstToLowerCase((String)key);
			} else {
				params = params + AgentUtils.firstToLowerCase((String)key) + ",\n\t\t\t\t\t";
			}
			acc++;
		}
		boolean needsJson = false;
		
		if((service.getMode() != null && service.getMode().equals("raw")) || 
				(contentType != null && contentType.contains("application/json")) ) {
			codeGenerated = codeGenerated + params;
			needsJson = true;
		}
		
		codeGenerated = codeGenerated + ");\n\n\t\t" +
				ReservedWord.TRY.getValue() + " {\n\t\t\t" + 
				ReservedWord.CALL.getValue().replace("%s", classname + "Response") + 
				" call = request.getApi()\n\t\t\t\t" +
				"." + AgentUtils.firstToLowerCase(service.getName()) + "(";
			
		int i = 0; 
		
		for(Object key : service.getHeaders().keySet().toArray()) {
			if(params.equals("") && i == service.getHeaders().size() - 1) {
				codeGenerated = codeGenerated + "\"" + service.getHeaders().get(key) + "\"";
			} else {
				codeGenerated = codeGenerated + "\"" + service.getHeaders().get(key) + "\",\n\t\t\t\t\t";
			}
			i++;
		}
		
		if(needsJson) {
			if(needsIdPath) {
				codeGenerated = codeGenerated + "idPath, ";
			}
			codeGenerated = codeGenerated + "request";
		} else if(needsIdPath){
			codeGenerated = codeGenerated + "idPath";
		} else {
			codeGenerated = codeGenerated + params;
		}
		
		codeGenerated = codeGenerated + ");\n\t\t\tcall.enqueue(" + 
				ReservedWord.THIS.getValue().replace(".", "") + ");\n\n\t\t} " + 
				ReservedWord.CATCH.getValue() + " (Exception e) {\n\t\t\t" + 
				AgentUtils.getError(classname, 3, null) + "\n\t\t}\n\t}";
		
		taskDelegated.setState(BlackboardTaskState.FINISHED);
		long end = System.nanoTime();
		long executionTime = end - startTime;
		super.taskDone(workerAid, new GeneratedCode(2, this.getLocalName(),  codeGenerated, executionTime));
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- DAOIMPL RETROFIT RESPONSE METHOD
	//--------------------------------------------------------------------------------------------------------------------------------		
	protected void generateDaoImplResponse(AID workerAid, BlackboardTask taskDelegated, ServiceConfig service) {
		long startTime = System.nanoTime();
		String classname = AgentUtils.firstToUpperCase(service.getName());
		Configuration config = ((WriterAgent)this).getConfiguration();
		String successMethod = "Success" + "(response.body());";
		String configError = config.getResponse_generic_error();
		String genericError = AgentUtils.getError(classname, 4, configError);
		String opstatusValidation = "";
		
		String codeGenerated = Annotation.MARK.getValue()
				.replace("%s", Comments.CALLBACK_SERVICE.getValue()) +
				Annotation.OVERRIDE.getValue() + "\t" + 
				ReservedWord.PUBLIC.getValue() + " " + 
				ReservedWord.VOID.getValue() + " onResponse(" +
				Annotation.NULLABLE.getValue() + " " + 
				ReservedWord.CALL.getValue().replace("%s", classname + "Response") + " call,\n\t\t\t" + 
				Annotation.NULLABLE.getValue() + " " +
				ReservedWord.RESPONSE.getValue().replace("%s", classname + "Response") + " response) {\n\t\t" + 
				ReservedWord.TRY.getValue() + " {\n\t\t\t" + 
				ReservedWord.IF.getValue() + "(response != " + ReservedWord.NULL.getValue() + 
				" && response.body() != " + ReservedWord.NULL.getValue() + " &&\n\t\t\t\t\t" + 
				"response.isSuccessful()"; 
		
		codeGenerated = codeGenerated + opstatusValidation + "){\n\n\t\t\t\t" + 
				ReservedWord.THIS.getValue() + "listener.on" + classname + successMethod + "\n\t\t\t} " +
				ReservedWord.ELSE.getValue() + " {\n\t\t\t\t" + genericError + "\n\t\t\t}\n\t\t} " +
				ReservedWord.CATCH.getValue() + " (Exception e){\n\t\t\t" + AgentUtils.getError(classname, 3, null) + "\n\t\t}\n\t}\n";
		taskDelegated.setState(BlackboardTaskState.FINISHED);
		long end = System.nanoTime();
		long executionTime = end - startTime;
		super.taskDone(workerAid, new GeneratedCode(3, this.getLocalName(), codeGenerated, executionTime));
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- DAOIMPL RETROFIT FAILURE METHOD
	//--------------------------------------------------------------------------------------------------------------------------------		
	protected void generateDaoImplFailure(AID workerAid, BlackboardTask taskDelegated, ServiceConfig service) {
		long startTime = System.nanoTime();
		String classname = AgentUtils.firstToUpperCase(service.getName());
		String configError = ((WriterAgent)this).getConfiguration().getResponse_generic_error();
		String genericError = AgentUtils.getError(classname, 2, configError);
		String codeGenerated = Annotation.OVERRIDE.getValue() + "\t" + 
				ReservedWord.PUBLIC.getValue() + " " + ReservedWord.VOID.getValue() + " onFailure(" + 
				Annotation.NULLABLE.getValue() + " " + 
				ReservedWord.CALL.getValue().replace("%s", classname + "Response") + " call,\n\t\t\t" +
				Annotation.NONNULL.getValue() + " " + ReservedWord.THROWABLE.getValue() + " t) {\n\t\t" + 
				genericError + "\n\t}\n";
		taskDelegated.setState(BlackboardTaskState.FINISHED);
		long end = System.nanoTime();
		long executionTime = end - startTime;
		super.taskDone(workerAid, new GeneratedCode(4, this.getLocalName(), codeGenerated, executionTime));
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- SERVICE CONFIG SINGLETON
	//--------------------------------------------------------------------------------------------------------------------------------		
	protected void generateServiceConfigSingleton(AID workerAid) {
		long startTime = System.nanoTime();
		String codeGenerated = Annotation.MARK.getValue()
				.replace("%s", Comments.SINGLETON.getValue()) + "\t" + 
				ReservedWord.PUBLIC.getValue() + " " + 
				ReservedWord.STATIC.getValue() + " ServiceConfig configInstance;\n\n\t" +
				ReservedWord.PUBLIC.getValue() + " " + 
				ReservedWord.STATIC.getValue() + " ServiceConfig sharedInstance() {\n\t\t" + 
				ReservedWord.IF.getValue() + "(configInstance == null){\n\t\t\t" + 
				"configInstance = " + ReservedWord.NEW.getValue() + " ServiceConfig();\n\t\t}\n\t\t" + 
				ReservedWord.RETURN.getValue() + " configInstance;\n\t}";
		long end = System.nanoTime();
		long executionTime = end - startTime;
		super.taskDone(workerAid, new GeneratedCode(1, this.getLocalName(), codeGenerated, executionTime));
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- SERVICE CONFIG PARAMS
	//--------------------------------------------------------------------------------------------------------------------------------		
	protected void generateServiceConfigParams(AID workerAid) {
		long startTime = System.nanoTime();
		String codeGenerated = Annotation.MARK.getValue()
				.replace("%s", Comments.VARIABLES_AND_COMPLEMENTS.getValue()) + "\t" + 
				ReservedWord.PRIVATE.getValue() + " ServiceBackend backend;";
		long end = System.nanoTime();
		long executionTime = end - startTime;
		super.taskDone(workerAid, new GeneratedCode(2, this.getLocalName(), codeGenerated, executionTime));
	}
		
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- SERVICE CONFIG SETUP
	//--------------------------------------------------------------------------------------------------------------------------------		
	protected void generateServiceConfigSetup(AID workerAid) {
		long startTime = System.nanoTime();
		String codeGenerated = Annotation.MARK.getValue()
				.replace("%s", Comments.BACKEND_SETUP.getValue()) + "\t" + 
				ReservedWord.PUBLIC.getValue() + " " + 
				ReservedWord.VOID.getValue() + " setup() {\n\t\t" + 
				"backend = " + ReservedWord.NEW.getValue() + " ServiceBackend(ServiceBackend.Environment.DEV);\n\t}";
		long end = System.nanoTime();
		long executionTime = end - startTime;
		super.taskDone(workerAid, new GeneratedCode(3, this.getLocalName(), codeGenerated, executionTime));
	}
		
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- SERVICE CONFIG ENCAPSULATION 
	//--------------------------------------------------------------------------------------------------------------------------------		
	protected void generateServiceConfigEncapsulation(AID workerAid) {
		long startTime = System.nanoTime();
		String codeGenerated = Annotation.MARK.getValue()
				.replace("%s", Comments.ENCAPSULATION.getValue()) + "\t" + 
				ReservedWord.PUBLIC.getValue() + " ServiceBackend getBackend() { " +
				ReservedWord.RETURN.getValue() + " " + 
				ReservedWord.THIS.getValue() + "backend; }";
		long end = System.nanoTime();
		long executionTime = end - startTime;
		super.taskDone(workerAid, new GeneratedCode(4, this.getLocalName(), codeGenerated, executionTime));
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- SERVICE ERROR PARAMS 
	//--------------------------------------------------------------------------------------------------------------------------------		
	protected void generateServiceErrorParams(AID workerAid) {
		long startTime = System.nanoTime();
		String codeGenerated = Annotation.MARK.getValue()
				.replace("%s", Comments.VARIABLES_AND_COMPLEMENTS.getValue()) + "\t" + 
				ReservedWord.PRIVATE.getValue() + " int code;\n\t" + 
				ReservedWord.PRIVATE.getValue() + " String msg;";
		long end = System.nanoTime();
		long executionTime = end - startTime;
		super.taskDone(workerAid, new GeneratedCode(1, this.getLocalName(), codeGenerated, executionTime));
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- SERVICE CONFIG CONSTRUCTOR 
	//--------------------------------------------------------------------------------------------------------------------------------		
	protected void generateServiceErrorConstructor(AID workerAid) {
		long startTime = System.nanoTime();
		String codeGenerated = Annotation.MARK.getValue()
				.replace("%s", Comments.CONSTRUCTOR.getValue()) + "\t" + 
				ReservedWord.PUBLIC.getValue() + " Error(String msg) {\n\t\t" +
				ReservedWord.THIS.getValue() + "code = 1003;\n\t\t";
		codeGenerated = codeGenerated + 
				ReservedWord.THIS.getValue() + "msg = msg;\n\t}";
		long end = System.nanoTime();
		long executionTime = end - startTime;
		super.taskDone(workerAid, new GeneratedCode(2, this.getLocalName(), codeGenerated, executionTime));
	}
		
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- SERVICE CONFIG ENCAPSULATION 
	//--------------------------------------------------------------------------------------------------------------------------------		
	protected void generateServiceErrorEncapsulation(AID workerAid) {
		long startTime = System.nanoTime();
		String codeGenerated = Annotation.MARK.getValue()
				.replace("%s", Comments.ENCAPSULATION.getValue()) + "\t" + 
				ReservedWord.PUBLIC.getValue() + " int getCode() { " +
				ReservedWord.RETURN.getValue() + " " + 
				ReservedWord.THIS.getValue() + "code; }\n\t";
		codeGenerated = codeGenerated + 
				ReservedWord.PUBLIC.getValue() + " String getMsg() { " + 
				ReservedWord.RETURN.getValue() + " " + 
				ReservedWord.THIS.getValue() + "msg; }";
 		long end = System.nanoTime();
		long executionTime = end - startTime;
		super.taskDone(workerAid, new GeneratedCode(3, this.getLocalName(), codeGenerated, executionTime));
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- SERVICE CONFIG TOSTRING 
	//--------------------------------------------------------------------------------------------------------------------------------		
	protected void generateServiceErrorToString(AID workerAid) {
		long startTime = System.nanoTime();
		String codeGenerated = Annotation.MARK.getValue()
				.replace("%s", Comments.TO_STRING.getValue()) + "\t" + 
				Annotation.NONNULL.getValue() + "\n" + 
				Annotation.OVERRIDE.getValue() + "\t" + 
				ReservedWord.PUBLIC.getValue() + " String toString() {\n\t\t" +
				ReservedWord.RETURN.getValue() + " \"Code: \" + " + 
				ReservedWord.THIS.getValue() + "code + \n\t\t\t\"Msg: \" + " + 
				ReservedWord.THIS.getValue() + "msg;\n\t}";
		long end = System.nanoTime();
		long executionTime = end - startTime;
		super.taskDone(workerAid, new GeneratedCode(4, this.getLocalName(), codeGenerated, executionTime));
	}
}
