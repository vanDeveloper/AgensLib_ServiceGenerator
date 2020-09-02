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

import com.navi.agenslib.mas.MasSetup;
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
public class WriterDaoAgent extends WriterAgent {

	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- PROPERTIES
	//--------------------------------------------------------------------------------------------------------------------------------
	private static final long serialVersionUID = 11L;
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- OVERRIDE FROM JADE
	//--------------------------------------------------------------------------------------------------------------------------------
	@Override
	protected void setup() {
		super.setup();
		super.aid = new AID("WriterDaoAgent", AID.ISLOCALNAME);
		super.role = BlackboardRole.WRITER_DAO_AGENT;
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- DAO (INTERFACE) METHOD
	//--------------------------------------------------------------------------------------------------------------------------------	
	protected void generateDaoMethod(AID workerAid, BlackboardTask taskDelegated, ServiceConfig service) { 
		long startTime = System.nanoTime();
		String classname = AgentUtils.firstToUpperCase(service.getName());
		
		String codeGenerated = "\t" + ReservedWord.VOID.getValue() + " " + 
						AgentUtils.firstToLowerCase(service.getName()) + " (";
				
		if(super.getConfiguration().getContext_methods().contains(classname) ) {
			codeGenerated = codeGenerated + ReservedWord.CONTEXT.getValue() + ",\n\t\t\t";
		}
		
		if(service.getPath().contains("{idPath}")) {
			codeGenerated = codeGenerated + "String idPath,\n\t\t\t";
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
			codeGenerated = codeGenerated + classname + "Interactor listener);\n";
		} else {
			codeGenerated = codeGenerated + "\t\t\t" + classname + "Interactor listener);\n";
		}
		
		taskDelegated.setState(BlackboardTaskState.FINISHED);
		long end = System.nanoTime();
		long executionTime = end - startTime;
		super.taskDone(workerAid, new GeneratedCode(1, this.getLocalName(), codeGenerated, executionTime));
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- SERVICE API METHODS
	//--------------------------------------------------------------------------------------------------------------------------------	
	protected void generateServiceApi(AID workerAid) {
		long startTime = System.nanoTime();
		String codeGenerated = Annotation.MARK.getValue().replace("%s", Comments.API_METHODS.getValue());
		int count = 0; 
		
		AgentUtils.logConsole("Services: " + MasSetup.services.size());
		for(int i = 0; i < MasSetup.services.size(); i++) {
			ServiceConfig service = MasSetup.services.get(i);
			String filename = AgentUtils.firstToLowerCase(service.getName());
			boolean hasParams = false;
			String contentType = null;
			boolean justHeaders = false;
			
			if(service.getRequestType().equals("GET")){
				codeGenerated = codeGenerated + Annotation.GET.getValue();
			} else if(service.getRequestType().equals("PUT")) {
				codeGenerated = codeGenerated + Annotation.PUT.getValue();
			} else if(service.getRequestType().equals("PATCH")){
				codeGenerated = codeGenerated + Annotation.PATCH.getValue();
			} else if(service.getRequestType().equals("DELETE")){
				codeGenerated = codeGenerated + Annotation.DELETE.getValue();
			} else {
				codeGenerated = codeGenerated + Annotation.POST.getValue();
	 		}
			codeGenerated = codeGenerated + "(\"" + service.getPath() + "\")\n";
			
			if(service.getAuth() != null) {
				codeGenerated = codeGenerated + 
						Annotation.HEADERS.getValue() + 
						"(\"" + service.getAuth() + "\")\n";
			}
			
			if(service.getHeaders().size() > 0) {
				contentType = AgentUtils.getContentType(service.getHeaders());
				if(contentType != null && contentType.contains("x-www-form-urlencoded")) {
					codeGenerated = codeGenerated + Annotation.FORM_URL_ENCODED.getValue() + "\n";
				} else if(contentType != null){
					codeGenerated = codeGenerated + 
							Annotation.HEADERS.getValue() + 
							"(\"" + contentType + "\")\n";
				}
			}
			codeGenerated = codeGenerated + "\t" + ReservedWord.CALL.getValue()
					.replace("%s", service.getName() + "Response") + " " + 
					filename + "(";
			
			if(service.getRequestParams().size() > 0) {
				hasParams = true;
			}
			
			if(service.getHeaders().size() > 0) {
				count = 0; 
				for(Object key : service.getHeaders().keySet().toArray()) {
					if(!hasParams && !service.hasUrlQuery()) {
						if(count == service.getHeaders().size() - 1) {
							codeGenerated = codeGenerated + Annotation.HEADER.getValue() + 
									"(\"" + key + "\") String " + ((String)key).replace("-", "_") + ");\n\n"; 
							justHeaders = true;
						} else {
							codeGenerated = codeGenerated + Annotation.HEADER.getValue() + 
									"(\"" + key + "\") String " + ((String)key).replace("-", "_") + ",\n\t\t\t"; 
						}
					} else {
						codeGenerated = codeGenerated + Annotation.HEADER.getValue() + 
								"(\"" + key + "\") String " + ((String)key).replace("-", "_") + ",\n\t\t\t"; 
					}
					count++;
				}
			}
			
			if(hasParams) {
				count = 0;
				if(service.hasUrlQuery()) {
					codeGenerated = codeGenerated + Annotation.PATH.getValue() + 
							"(\"idPath\") String idPath,\n\t\t\t";
				}
				
				for(Object key : service.getRequestParams().keySet().toArray()) {
					String annotation = Annotation.QUERY.getValue();
					
					if((service.getMode() != null && service.getMode().equals("raw")) ||  
							(contentType != null &&
							contentType.contains("application/json"))) {
						codeGenerated = codeGenerated + Annotation.BODY.getValue() + " " + 
							service.getName() + "Request body);\n\n";
						break;
					} else if(service.getRequestType().equals("POST")) {
						annotation = Annotation.FIELD.getValue();
					}
					
					if(count == service.getRequestParams().size() - 1) {
						codeGenerated = codeGenerated + annotation + 
								"(\"" + key + "\") String " + 
								AgentUtils.firstToLowerCase((String)key) + ");\n\n"; 
					} else {
						codeGenerated = codeGenerated + annotation + 
								"(\"" + key + "\") String " + 
								AgentUtils.firstToLowerCase((String)key) + ",\n\t\t\t";
					}
					count++;
				}
			} else if(service.hasUrlQuery()){
				codeGenerated = codeGenerated + Annotation.PATH.getValue() + 
						"(\"idPath\") String idPath);\n\n";
			} else if(!justHeaders){
				codeGenerated = codeGenerated + ");\n\n";
			}
		} 
		long end = System.nanoTime();
		long executionTime = end - startTime;
		super.taskDone(workerAid, new GeneratedCode(1, this.getLocalName(), codeGenerated, executionTime));
	}
}
