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
package com.navi.agenslib.mas.worker;

import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import com.navi.agenslib.mas.MasSetup;
import com.navi.agenslib.mas.utils.AgentUtils;
import com.navi.agenslib.mas.utils.blackboard.BlackboardTasks;
import com.navi.agenslib.mas.utils.codegen.GeneratedCode;
import com.navi.agenslib.mas.utils.codegen.GeneratedCodeFile;
import com.navi.agenslib.mas.utils.codegen.Packages;
import com.navi.agenslib.mas.utils.codegen.ReservedWord;
import com.navi.agenslib.mas.utils.settings.Configuration;
import com.navi.agenslib.mas.utils.settings.ServiceConfig;
import com.navi.agenslib.mas.utils.sorts.AgentType;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.util.leap.ArrayList;
import jade.util.leap.List;
import jade.util.leap.Map;

/**
 * Created by van on 10/06/20.
 */
public class WorkerAgent extends Agent {

	//--------------------------------------------------------------------------------------------------------------------------------
	// MARK:- AGENT PROPERTIES
	//--------------------------------------------------------------------------------------------------------------------------------
	private static final long serialVersionUID = 7L;
	public static final AID aid = new AID("WorkerAgent", AID.ISLOCALNAME);
	private static final String OWNER = "AgensLib";
	private static final AgentType MY_TYPE = AgentType.WORKER;
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- PROPERTIES
	//--------------------------------------------------------------------------------------------------------------------------------
	private Configuration config;
	private long executionTime;
	
	//--------------------------------------------------------------------------------------------------------------------------------
	// MARK:- OVERRIDE FROM JADE
	//--------------------------------------------------------------------------------------------------------------------------------
	@Override
	protected void setup() {
		AgentUtils.log(getLocalName(), " is ready.");
		this.setEnabledO2ACommunication(true, 1);
		this.config = (Configuration) this.getArguments()[0];

		AgentUtils.register(this, MY_TYPE, OWNER);

		this.addBehaviour(new WorkerBehaviour(this, 1000));
	}

	protected void takedown() {
		try { DFService.deregister(this); }
        catch (Exception e) { e.printStackTrace(); } 
		AgentUtils.log(getLocalName(), " terminating.");
	}

	//--------------------------------------------------------------------------------------------------------------------------------
	// MARK:- SETUP CALL FOR PROPOSE
	//--------------------------------------------------------------------------------------------------------------------------------
	public void setupProposal(AID writerAid, ServiceConfig service, BlackboardTasks tasks) {
		ACLMessage request = new ACLMessage(ACLMessage.CFP);
		request.clearAllReceiver();
		request.addReceiver(writerAid);
		request.setProtocol(FIPANames.InteractionProtocol.FIPA_PROPOSE);
		try {
			if(tasks != null) {
				request.setContentObject(tasks);
			} else {
				request.setContentObject(service);
			}
			this.send(request);
		} catch (Exception e) {
			AgentUtils.log(getLocalName(), "Error while sending serialized object: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
		AgentUtils.log(getLocalName(), "Sending proposals");
	}

	//--------------------------------------------------------------------------------------------------------------------------------
	// MARK:- INFORM THE RESULT OF THE TASK
	//--------------------------------------------------------------------------------------------------------------------------------
	public void taskDone(AID managerAid, GeneratedCodeFile codeFile) {
		ACLMessage request = new ACLMessage(ACLMessage.INFORM);
		request.clearAllReceiver();
		request.addReceiver(managerAid);
		request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
		AgentUtils.log(getLocalName(), "Inform task done");

		try {
			request.setContentObject(codeFile);
			this.send(request);
			this.vote();
		} catch (Exception e) {
			AgentUtils.logError(getLocalName(), "Failure to inform task result: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- VOITING PROTOCOL
	//--------------------------------------------------------------------------------------------------------------------------------
	private void vote() {
		Random random = new Random();
		int idxSelected = random.nextInt(MasSetup.managersNames.length);
		String managerVoted = MasSetup.managersNames[idxSelected];
			
		if(MasSetup.voting.containsKey(managerVoted)) {
			int votes = (Integer)MasSetup.voting.get(managerVoted);
			MasSetup.voting.put(managerVoted, ++votes);
		} else {
			MasSetup.voting.put(managerVoted, 1);
		}
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- FINISH CODE FILE 
	//--------------------------------------------------------------------------------------------------------------------------------
	private void finishCodeFile(AID managerAid, GeneratedCodeFile file, List codes, List imports, String filename, long startTime) {
		file.setImports(imports);
		file.setFilename(filename);
		file.addEndClassMarker();
		
		long endTime = System.nanoTime();
  		file.addExecutionTime(this.getLocalName(), endTime - startTime);
  		
  		for(int i = 0; i < codes.size(); i++){
  			if(i != codes.size() - 1) {
  				GeneratedCode order = ((GeneratedCode)codes.get(i));
  				file.addExecutionTime(order.getAgentName(), order.getExecutionTime());
  			} 
  		}
  		taskDone(managerAid, file);
  	}

	//--------------------------------------------------------------------------------------------------------------------------------
	// MARK:- CREATE ASSIGNED FILE WITH SORTED CODES GENERATED BY WRITERS
	//--------------------------------------------------------------------------------------------------------------------------------
	protected void createFile(AID managerAid, List codes, ServiceConfig service, long startTime) {
  		List sortedCodes = sortCodesList(codes);
  		List imports = new ArrayList();
  		String filename = AgentUtils.firstToUpperCase(service.getName());
  		GeneratedCodeFile file = new GeneratedCodeFile();
  		file.setGeneratedCodes(sortedCodes);
  	
  		file.setPackageName(this.config.getPackage_name() + 
  				Packages.APP_NET_SERVICES.getValue() + "." +
  				AgentUtils.firstToUpperCase(service.getName()) + ";");
  		
  		if(this.getLocalName().contains("INW")) {
  			filename = filename + "Interactor";
  			
  			imports.add(this.config.getPackage_name() + Packages.APP_NET_ERROR.getValue() + ".Error;\n");
  			file.setImports(imports);
  			
  			file.setCodeClassname(ReservedWord.PUBLIC.getValue() + " " + 
  								  ReservedWord.INTERFACE.getValue() + " " + filename + " {");
  		} else if(this.getLocalName().contains("DAW")) {
  			if(this.config.getContext_methods().contains(filename)){
  				imports.add(Packages.ANDROID_CONTEXT.getValue() + "\n");
  			}
  			filename = filename + "Dao";
  			file.setCodeClassname(ReservedWord.PUBLIC.getValue() + " " + 
  					  			  ReservedWord.INTERFACE.getValue() + " " + filename + " {");
  		} else if(this.getLocalName().contains("DIW")) {
  			
  			if(this.config.getContext_methods().contains(filename)){
  				imports.add(Packages.ANDROID_CONTEXT.getValue() + "\n");
  			} 
  			
  			imports.add(Packages.ANDROID_NONNULL.getValue() + "\n");
  			imports.add(Packages.ANDROID_NULLABLE.getValue() + "\n\n");
  			imports.add(this.config.getPackage_name() + Packages.APP_NET_ERROR.getValue() + ".Error;\n\n");
  			imports.add(Packages.RETROFIT2_CALL.getValue() + "\n");
  			imports.add(Packages.RETROFIT2_CALLBACK.getValue() + "\n");
  			imports.add(Packages.RETROFIT2_RESPONSE.getValue() + "\n");
  			
  			filename = filename + "DaoImpl";
  			String classname = ReservedWord.PUBLIC.getValue() + " " + 
  							   ReservedWord.CLASS.getValue() + " " + filename + " " + 
  							   ReservedWord.IMPLEMENTS.getValue() + " " + filename.replaceAll("Impl", "");
  			classname = classname + ", " +
  					ReservedWord.CALLBACK.getValue().replace("%s", filename.replaceAll("DaoImpl", "") + 
  					"Response") + " {";					
  			file.setCodeClassname(classname);
  		} else if(this.getLocalName().contains("RQW")) {
  			filename = filename + "Request";
  			
  			String tmpImport = config.getPackage_name() + Packages.APP_NET_SERVICES.getValue() + ".ServiceRequest;\n";
  			
  			if(service.getMode() != null && service.getMode().equalsIgnoreCase("raw")) {
  				imports.add(tmpImport + "\n");
  				imports.add(Packages.JSON_OBJECT.getValue() + "\n");
  			} else {
  				imports.add(tmpImport);
  			}
  			
  			file.setCodeClassname(ReservedWord.PUBLIC.getValue() + " " + 
  								  ReservedWord.CLASS.getValue() + " " + filename + " " + 
  								  ReservedWord.EXTENDS.getValue() + " ServiceRequest {");
  		} else if(this.getLocalName().contains("RPW")) {
  			filename = filename + "Response";
  			
  			if(hasArrayList(service.getResponseParams())) {
  				imports.add(Packages.JAVA_UTIL_ARRAYLIST.getValue());
  			}
  			imports.add(config.getPackage_name() + Packages.ENTITY_MODEL.getValue() + ".*;\n");
  			
  			file.setCodeClassname(ReservedWord.PUBLIC.getValue() + " " + 
  					  			  ReservedWord.CLASS.getValue() + " " + filename + " {");
  		}
  		
  		this.finishCodeFile(managerAid, file, sortedCodes, imports, filename, startTime);
	}

	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- CREATE SERVICE CONFIG FILE
	//--------------------------------------------------------------------------------------------------------------------------------
	protected void createServiceConfigFile(AID managerAid, List codes, long startTime) {
		List sortedCodes = sortCodesList(codes);
  		List imports = new ArrayList();
  		String filename = "ServiceConfig";
  		GeneratedCodeFile file = new GeneratedCodeFile();
  		file.setGeneratedCodes(sortedCodes);
  		
  		file.setPackageName(this.config.getPackage_name() + 
  				Packages.APP_NET_SERVICES.getValue() + ";");
  		
  		imports.add(this.config.getPackage_name() + Packages.APP_NET_BACKEND.getValue() + ".ServiceBackend;\n");
  		
  		file.setCodeClassname(ReservedWord.PUBLIC.getValue() + " " + 
	  			  ReservedWord.CLASS.getValue() + " " + filename + " {");
  		
  		this.finishCodeFile(managerAid, file, sortedCodes, imports, filename, startTime);
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- CREATE SERVICE ERROR FILE
	//--------------------------------------------------------------------------------------------------------------------------------
	protected void createServiceErrorFile(AID managerAid, List codes, long startTime) {
		List sortedCodes = sortCodesList(codes);
  		List imports = new ArrayList();
  		String filename = "Error";
  		GeneratedCodeFile file = new GeneratedCodeFile();
  		file.setGeneratedCodes(sortedCodes);
  		
  		file.setPackageName(this.config.getPackage_name() + 
  				Packages.APP_NET_ERROR.getValue() + ";");
  		
  		imports.add(Packages.ANDROID_NONNULL.getValue() + "\n");
  		
  		file.setCodeClassname(ReservedWord.PUBLIC.getValue() + " " + 
	  			  ReservedWord.CLASS.getValue() + " " + filename + " " + 
	  			  ReservedWord.EXTENDS.getValue() + " Exception {");
  	
  		this.finishCodeFile(managerAid, file, sortedCodes, imports, filename, startTime);
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- CREATE SERVICE BACKEND FILE
	//--------------------------------------------------------------------------------------------------------------------------------
	protected void createServiceBackendFile(AID managerAid, List codes, long startTime) {
		List sortedCodes = sortCodesList(codes);
		String filename = "ServiceBackend";
		GeneratedCodeFile file = new GeneratedCodeFile();
		file.setGeneratedCodes(sortedCodes);
		
		file.setPackageName(this.config.getPackage_name() + 
				Packages.APP_NET_BACKEND.getValue() + ";");
		
		file.setCodeClassname(ReservedWord.PUBLIC.getValue() + " " + 
					ReservedWord.CLASS.getValue() + " " + filename + " {");
		
		this.finishCodeFile(managerAid, file, sortedCodes, null, filename, startTime);
	}

	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- CREATE SERVICE API FILE
	//--------------------------------------------------------------------------------------------------------------------------------
	protected void createServiceApiFile(AID managerAid, List codes, long startTime) {
		List sortedCodes = sortCodesList(codes);
		List imports = new ArrayList();
		String filename = "Api";
		GeneratedCodeFile file = new GeneratedCodeFile();
		file.setGeneratedCodes(sortedCodes);
		
		file.setPackageName(this.config.getPackage_name() + Packages.APP_NET_SERVICES.getValue() + ";");
		
		for(int i = 0; i < MasSetup.services.size(); i++) {
			String serviceName = AgentUtils.firstToUpperCase(MasSetup.services.get(i).getName());
			String importValue = this.config.getPackage_name() + 
					Packages.APP_NET_SERVICES.getValue() + "." + 
					serviceName + "." + serviceName + "Response;\n";
			
			imports.add(importValue);
			
			importValue = this.config.getPackage_name() + 
					Packages.APP_NET_SERVICES.getValue() + "." + 
					serviceName + "." + serviceName + "Request;\n";
			
			if(i == MasSetup.services.size() - 1) {
				importValue = importValue + "\n";
			}
			imports.add(importValue);
		}
		imports.add(Packages.JSON_OBJECT.getValue() + "\n\n");
		
		imports.add(Packages.RETROFIT2_CALL.getValue() + "\n");
		imports.add(Packages.RETROFIT2_BODY.getValue() + "\n");
		imports.add(Packages.RETROFIT2_GET.getValue() + "\n");
		imports.add(Packages.RETROFIT2_POST.getValue() + "\n");
		imports.add(Packages.RETROFIT2_PATCH.getValue() + "\n");
		imports.add(Packages.RETROFIT2_DELETE.getValue() + "\n");
		imports.add(Packages.RETROFIT2_HEADER.getValue() + "\n");
		imports.add(Packages.RETROFIT2_HEADERS.getValue() + "\n");
		imports.add(Packages.RETROFIT2_QUERY.getValue() + "\n");
		imports.add(Packages.RETROFIT2_PATH.getValue() + "\n");
		imports.add(Packages.RETROFIT2_FIELD.getValue() + "\n");
		imports.add(Packages.RETROFIT2_FORM.getValue() + "\n");
		
		file.setCodeClassname(ReservedWord.PUBLIC.getValue() + " " + 
				ReservedWord.INTERFACE.getValue() + " " + filename + " {");
		
		this.finishCodeFile(managerAid, file, sortedCodes, imports, filename, startTime);
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- CREATE SERVICE REQUEST PART FILE
	//--------------------------------------------------------------------------------------------------------------------------------
	protected void createServiceRequestPartFile(AID managerAid, List codes, int part, long startTime) {
		List sortedCodes = sortCodesList(codes);
		List imports = new ArrayList();
		String filename = "ServiceRequest";
		GeneratedCodeFile file = new GeneratedCodeFile();
		file.setGeneratedCodes(sortedCodes);
		file.setPart(part);
		
		file.setPackageName(this.config.getPackage_name() + Packages.APP_NET_SERVICES.getValue() + ";");
		
		imports.add(Packages.GOOGLE_GSON.getValue() + "\n");
		imports.add(Packages.GOOGLE_GSON_BUILDER.getValue() + "\n\n");
		imports.add(Packages.RETROFIT2_GSON_FACTORY.getValue() + "\n");
		imports.add(Packages.RETROFIT2_BASE.getValue() + "\n\n");
		
		file.setCodeClassname(ReservedWord.PUBLIC.getValue() + " " + 
				ReservedWord.CLASS.getValue() + " " + filename + " {");
		
		this.finishCodeFile(managerAid, file, sortedCodes, imports, filename, startTime);
	}
	
	private boolean hasArrayList(Map responseParams) {
		for(Object key : responseParams.keySet().toArray()) {
			if( ((String)responseParams.get(key)).contains("List")) {
				return true;
			}
		}
		return false;
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	// MARK:- SORT LIST OF GENERATED CODES
	//--------------------------------------------------------------------------------------------------------------------------------
	private List sortCodesList(List codes) {
		java.util.List<GeneratedCode> orderedCodes = new java.util.ArrayList<>();

		for (Object code : codes.toArray()) {
			orderedCodes.add((GeneratedCode) code);
		}

		Collections.sort(orderedCodes, new Comparator<GeneratedCode>() {
			@Override
			public int compare(GeneratedCode o1, GeneratedCode o2) {
				return Integer.compare(o1.getOrder(), o2.getOrder());
			}
		});
		codes.clear();

		for (GeneratedCode code : orderedCodes) {
			codes.add(code);
		}
		return codes;
	}

	//--------------------------------------------------------------------------------------------------------------------------------
	// MARK:- ENCAPSULATION
	//--------------------------------------------------------------------------------------------------------------------------------
	public AgentType getType() { return MY_TYPE; }
	public Configuration getConfiguration() { return this.config; }
	public void setExecutionTime(long executionTime) { this.executionTime = executionTime; }
	public long getExecutionTime() { return this.executionTime; }
	
}
