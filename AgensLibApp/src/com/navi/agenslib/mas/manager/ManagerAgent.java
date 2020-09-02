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
package com.navi.agenslib.mas.manager;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.navi.agenslib.mas.MasSetup;
import com.navi.agenslib.mas.utils.AgentUtils;
import com.navi.agenslib.mas.utils.blackboard.BlackboardRole;
import com.navi.agenslib.mas.utils.blackboard.BlackboardTasks;
import com.navi.agenslib.mas.utils.codegen.Annotation;
import com.navi.agenslib.mas.utils.codegen.Comments;
import com.navi.agenslib.mas.utils.codegen.GeneratedCode;
import com.navi.agenslib.mas.utils.codegen.GeneratedCodeFile;
import com.navi.agenslib.mas.utils.codegen.Packages;
import com.navi.agenslib.mas.utils.codegen.ReservedWord;
import com.navi.agenslib.mas.utils.settings.Configuration;
import com.navi.agenslib.mas.utils.settings.ServiceConfig;
import com.navi.agenslib.mas.utils.sorts.AgentType;

/**
 * Created by van on 10/06/20.
 */
public class ManagerAgent extends Agent {

	//------------------------------------------------------------------------------------------------------
  	//MARK:- AGENT PROPERTIES
	//------------------------------------------------------------------------------------------------------
  	private static final long serialVersionUID = 5L;
	protected static final int NUM_WORKERS = 5;
	protected static final int HALF_WORKERS = 3;
	public static final AID aid = new AID("ManagerAgent", AID.ISLOCALNAME);	
	private static final String OWNER = "AgensLib";
	private static final AgentType MY_TYPE = AgentType.MANAGER;
	private static final BlackboardRole MY_ROLE = BlackboardRole.MANAGER_AGENT;
	
	//------------------------------------------------------------------------------------------------------
  	//MARK:- PROPERTIES
	//------------------------------------------------------------------------------------------------------
  	private JSONObject jsonService;
	private ServiceConfig service;
	private Configuration config;
	public enum Paths {
		SERVICE_DAO,
		SERVICE_ESTRUCTURE,
		ERROR,
		BACKEND
	}
	
	//------------------------------------------------------------------------------------------------------
  	//MARK:- OVERRIDE FROM JADE
	//------------------------------------------------------------------------------------------------------
  	@Override
	protected void setup() {
		AgentUtils.log(getLocalName(), " is ready.");
		this.setEnabledO2ACommunication(true, 1);
		AgentUtils.register(this, MY_TYPE, OWNER);
		this.addBehaviour(new ManagerBehaviour(this, AgentUtils.getWaitingQueueTime()));
	}
	
	protected void takedown() {
		try { DFService.deregister(this); }
        catch (Exception e) { e.printStackTrace(); } 
		AgentUtils.log(getLocalName(), " terminating.");
	}
	
	//------------------------------------------------------------------------------------------------------
  	//MARK:- PARSERING THE ASSGINED JSON SERVICE
	//------------------------------------------------------------------------------------------------------
  	protected void splitAssignedService() {
		try {
			JSONParser parser = new JSONParser();
			this.jsonService = (JSONObject) parser.parse((String)this.getArguments()[0]);
			this.config = (Configuration)this.getArguments()[1];
			
			this.service = new ServiceConfig((String)jsonService.get("name"));
			JSONObject request = (JSONObject)jsonService.get("request");
			
			if(request.containsKey("auth")) {
				this.service.setAuth(AgentUtils.mapAuthoriazation((JSONObject)request.get("auth")));
			}
			
			this.service.setRequestType((String)request.get("method"));
			this.service.setHeaders(AgentUtils.mapHeaders((JSONArray)request.get("header")));
			
			if(this.service.getRequestType().equals("POST") ||
					this.service.getRequestType().equals("PUT") || 
					this.service.getRequestType().equals("PATCH")) {
				JSONObject body = (JSONObject)request.get("body");
				this.service.setMode((String)body.get("mode"));
				this.service.setRequestParams(AgentUtils.mapPostRequest(this.getLocalName(), body));
			} else {
				this.service.setRequestParams(AgentUtils.mapGetRequest(request.get("url")));
			}
			
			JSONObject url = (JSONObject)request.get("url");
			
			this.service.setProtocol((String)url.get("protocol"));
			
			JSONArray host = (JSONArray)url.get("host");
			this.service.setHost(getAllElements(host, true));
			
			JSONArray path = (JSONArray)url.get("path");
			this.service.setPath(getAllElements(path, false));
			
			if(url.containsKey("port")) {
				this.service.setPort((String)url.get("port"));
			}
			
			JSONArray response = (JSONArray)jsonService.get("response");
			if(response.size() > 0) {
				Object body = parser.parse((String)((JSONObject)response.get(0)).get("body"));
				this.service.setResponseParams(AgentUtils.mapResponse(body));
			} else {
				AgentUtils.logConsole("");
			}
			MasSetup.services.add(this.service);
			
			for(Object key : AgentUtils.customClasses.keySet().toArray()) {
				String filename = AgentUtils.getValidName((String)key);
				this.buildEntityFile(filename, (JSONObject)AgentUtils.customClasses.get(key));
			}
			
		} catch(ParseException e) {
			AgentUtils.logError(this.getLocalName(), e.getLocalizedMessage());
		}
	}
	
  	//------------------------------------------------------------------------------------------------------
  	//MARK:- USED TO RETURN THE ELEMENTS OF A HOST ARRAY OR THE PATH QUERY PARAMS
  	//------------------------------------------------------------------------------------------------------
  	private String getAllElements(JSONArray array, boolean isHost) {
  		String fullString = "";
		String delimiter = "/";
		
		if(isHost) delimiter = ".";
  		
		for(int i = 0; i < array.size(); i++) {
			
			if(i == array.size() - 1 && AgentUtils.isIdLast((String)array.get(i)) ) {
				fullString = fullString + "{idPath}";
				service.hasUrlQuery(true);
			} else {
				fullString = fullString + array.get(i);
			}
			
			if(i != array.size() - 1) {
				fullString = fullString + delimiter;
			}
		}
		return fullString;
  	}
  	
  	//------------------------------------------------------------------------------------------------------
  	//MARK:- SETUP CALL FOR PROPOSE
  	//------------------------------------------------------------------------------------------------------
  	public void setupProposal(AID agentAid, boolean isWorker, BlackboardTasks task){
    	ACLMessage request = new ACLMessage(ACLMessage.CFP);
    	request.clearAllReceiver();
    	request.addReceiver(agentAid);
    	request.setProtocol(FIPANames.InteractionProtocol.FIPA_PROPOSE);
    	try {
    		if(isWorker) {
    			request.setContentObject(this.service);
    		} else {
    			request.setContentObject(task);
    		}
    		this.send(request);
    	} catch(Exception e) {
    		AgentUtils.log(getLocalName(), "Error while sending serialized object: " + e.getLocalizedMessage());
    		e.printStackTrace();
    	}
    	AgentUtils.log(getLocalName(), "Sending proposals");
    }
	
  	//------------------------------------------------------------------------------------------------------
  	//MARK:- BUILDING THE MODEL FILE NEEDED IN THE RESPONSE 
  	//------------------------------------------------------------------------------------------------------
  	private void buildEntityFile(String filename, JSONObject properties) {
    	try {
    		String path = this.getApiEntityPath() + "/entities/" + filename + ".java";
			File filepath = new File(path);
			
			if(!filepath.getParentFile().exists()) {
				filepath.getParentFile().mkdirs();
			} 
			
			FileWriter file = new FileWriter(filepath, false);
			
			file.write(ReservedWord.PACKAGE.getValue() + " " + 
						this.getConfiguration().getPackage_name() + Packages.ENTITY_MODEL.getValue() + ";\n\n");
			
			if(hasArrayList(properties)) {
				file.write(ReservedWord.IMPORT.getValue() + " " + Packages.JAVA_UTIL_ARRAYLIST.getValue());
			}
			
			file.write("\n/**\n" + 
					   " * Created by " + OWNER + " on 10/06/20.\n" + 
					   " */\n");
			
			// CLASS NAME
			file.write(ReservedWord.PUBLIC.getValue() + " " + 
					   ReservedWord.CLASS.getValue() + " " + filename + " {\n");
			
			// PROPERTIES
			file.write(Annotation.MARK.getValue()
							.replace("%s", Comments.VARIABLES_AND_COMPLEMENTS.getValue()));
			
			for(Object key : properties.keySet()) {
				if(properties.get(key) != null) {
					String type = AgentUtils.getDataTypeParams(key, properties.get(key));
					String correctType = AgentUtils.getValidName(type);
					String name = (String)key;
					
					file.write("\t" + ReservedWord.PRIVATE.getValue() + " " + correctType + " " + name + ";\n");
				}
			}
			
			// CONSTRUCTOR
			file.write(Annotation.MARK.getValue()
							.replace("%s", Comments.CONSTRUCTOR.getValue()));
			
			file.write("\t" + ReservedWord.PUBLIC.getValue() + " " + filename + " () {\n\t\n\t}\n");
			
			// ENCAPSULATION
			file.write(Annotation.MARK.getValue()
						.replace("%s", Comments.ENCAPSULATION.getValue()));
			
			for(Object key : properties.keySet()) {
				if(properties.get(key) != null) {
					String param = (String)key;
					String type = AgentUtils.getDataTypeParams(key, properties.get(key));
					String method = AgentUtils.getValidName(type);
					String correctType = AgentUtils.getValidName(type);
					
					if(AgentUtils.isSimpleDataType(type)) {
						method = AgentUtils.firstToUpperCase((String)key);
					}
					
					// GENERATING SET METHO
					file.write("\t" + ReservedWord.PUBLIC.getValue() + " " + 
									  ReservedWord.VOID.getValue() + " set" + method + 
									  "(" + correctType + " " + param + ") { " +
									  ReservedWord.THIS.getValue() + param + " = " + param + "; }\n");
					
					// GENERATING GET METHO
					file.write("\t" + ReservedWord.PUBLIC.getValue() + " " + 
							correctType + " get" + method  + "() { " + ReservedWord.RETURN.getValue() + " " + 
							ReservedWord.THIS.getValue() + param + "; }\n\n");
				}
			}
			
			file.write("}");
			
			file.flush();
			file.close();
    	} catch(IOException e) {
    		AgentUtils.logError(getLocalName(), e.getLocalizedMessage());
    	}
    }
  	
  	private boolean hasArrayList(JSONObject properties) {
  		for(Object key : properties.keySet()) {
			if(properties.get(key) != null) {
				if(AgentUtils.getDataTypeParams(key, properties.get(key)).contains("ArrayList")) {
					return true;
				}
			}
		}
  		return false;
  	}
    
  	//------------------------------------------------------------------------------------------------------
  	//MARK:- BUILDING THE FILE
  	//------------------------------------------------------------------------------------------------------
  	protected void buildFile(GeneratedCodeFile codefile, Paths paths) {
		try {
			String path = ""; 
			
			switch(paths) {
				case SERVICE_DAO:
					path = this.getApiPath() + "/" + codefile.getFilename() + ".java";
					break;
				
				case SERVICE_ESTRUCTURE:
					path = this.getServicePath() + codefile.getFilename() + ".java";
					break;
					
				case ERROR:
					path = this.getErrorPath() + codefile.getFilename() + ".java";
					break;
				
				case BACKEND: 
					path = this.getBackendPath() + codefile.getFilename() + ".java";
					break;
					
				default: break;
				
			}
			File filepath = new File(path);
			
			if(!filepath.getParentFile().exists()) {
				filepath.getParentFile().mkdirs();
			} 
			
			FileWriter file = new FileWriter(filepath, false);
			
			file.write(ReservedWord.PACKAGE.getValue() + " " + codefile.getPackageName() + "\n\n");
			
			if(codefile.getImports() != null) {
				for(Object imports : codefile.getImports().toArray()) {
					file.write(ReservedWord.IMPORT.getValue() + " " + (String)imports);
				}
			}
			
			file.write("\n/**\n" + 
					   " * Created by " + OWNER + " project on " + AgentUtils.getCurrentDate() + ".\n" + 
					   " */\n");
			file.write(codefile.getCodeClassanme() + "\n");
			
			for(int i = 0; i < codefile.getGeneratedCodes().size(); i++) {
				if(codefile.getGeneratedCodes().get(i) instanceof String) {
					file.write(((String)codefile.getGeneratedCodes().get(i)) + "\n");
				} else {
					file.write(((GeneratedCode)codefile.getGeneratedCodes().get(i)).getCodegen() + "\n");
				}
			}
			file.flush();
			file.close();
			
		} catch(IOException e) {
			AgentUtils.logError(this.getLocalName(), e.getLocalizedMessage());
		}
	}
  	
  	//------------------------------------------------------------------------------------------------------
  	//MARK:- ENCAPSULATION
  	//------------------------------------------------------------------------------------------------------
  	public ServiceConfig getServiceConfig() { return this.service; }
	public Configuration getConfiguration() { return this.config; }
	private String getApiEntityPath() { return this.config.getOutputPath(); }
	private String getApiPath() { return this.config.getOutputPath() + "/network/services/"+ this.service.getName(); }
	private String getServicePath() { return this.config.getOutputPath() + "/network/services/"; }
	private String getErrorPath() { return  this.config.getOutputPath() + "/network/error/"; }
	private String getBackendPath() { return this.config.getOutputPath() + "/network/backend/"; }
	public BlackboardRole getRole() { return MY_ROLE; }
    public AgentType getType() { return MY_TYPE; }
	
}
