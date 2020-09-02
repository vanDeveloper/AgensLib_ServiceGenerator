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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.navi.agenslib.mas.MasSetup;
import com.navi.agenslib.mas.manager.ManagerAgent.Paths;
import com.navi.agenslib.mas.utils.AgentUtils;
import com.navi.agenslib.mas.utils.blackboard.BlackboardTaskState;
import com.navi.agenslib.mas.utils.blackboard.BlackboardTasks;
import com.navi.agenslib.mas.utils.codegen.GeneratedCodeFile;
import com.navi.agenslib.mas.utils.sorts.AgentType;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

/**
* Created by van on 10/06/20.
*/
public class ManagerWinnerBehaviour extends TickerBehaviour {

	//------------------------------------------------------------------------------------------------------
  	//MARK:- PROPERTIES
	//------------------------------------------------------------------------------------------------------
  	private static final long serialVersionUID = 16L;
	private BlackboardTasks taskDelegated;
	private BlackboardTaskState taskState = BlackboardTaskState.PENDING;
	private ConcurrentLinkedQueue<GeneratedCodeFile> generatedFiles = new ConcurrentLinkedQueue<>();
	private boolean finished = false;
	
	//------------------------------------------------------------------------------------------------------
  	//MARK:- CONSTRUCTOR
	//------------------------------------------------------------------------------------------------------
  	public ManagerWinnerBehaviour(Agent a, long period) {
		super(a, period);
	}
			
  	//------------------------------------------------------------------------------------------------------
  	//MARK:- AUTO RUN BEHAVIOUR
  	//------------------------------------------------------------------------------------------------------
  	@Override
	protected void onTick() {
		
		ACLMessage msg = myAgent.receive(); 
		if (msg != null) {
			try {
				if(msg.getContentObject() != null && msg.getContentObject() instanceof BlackboardTasks) {
					// GETTING THE TASK
					this.taskDelegated = (BlackboardTasks)msg.getContentObject();
					AgentUtils.log(myAgent.getLocalName(), "Starting the next task: " + this.taskDelegated.toString());
					Thread.sleep(1000);
					
				} else if(msg.getContentObject() != null && msg.getContentObject() instanceof GeneratedCodeFile) {
					// RECOVER THE GENERATED FILE
					generatedFiles.add((GeneratedCodeFile)msg.getContentObject());
				
				} 
			} catch (InterruptedException | UnreadableException e) {
				e.printStackTrace();
				AgentUtils.log(myAgent.getLocalName(), 
						"Error while receiving the serialized object: " + e.getLocalizedMessage());
			}
		}
		
		// TAKING THE TASK AND SPLIT THEM INTO SUB TASKS THROUGTH THE WORKERS  (SECOND ROUND)
		if(taskDelegated != null && taskState == BlackboardTaskState.PENDING && 
				taskDelegated == BlackboardTasks.CONFIGURE_SERVICE_ARCHITECTURE) {
			taskState = BlackboardTaskState.IN_PROCESS;
			
			// SENDING PROPOSALS TO THEIR WORKERS TO WORK WITH TWO FILES
			Map<String, BlackboardTasks> workers = new HashMap<>();
			workers.put("INW", BlackboardTasks.CREATE_SERVICE_REQUEST_FILE);
			workers.put("RQW", BlackboardTasks.CREATE_SERVICE_REQUEST_FILE);
			workers.put("DIW", BlackboardTasks.CREATE_SERVICE_CONFIG_FILE);
			setupProposal(workers);
			
		} else if(taskDelegated != null && taskState == BlackboardTaskState.PENDING && 
				taskDelegated == BlackboardTasks.CONFIGURE_SERVICE_BACKEND){
			taskState = BlackboardTaskState.IN_PROCESS;
			
			// SENDING PROPOSALS TO THEIR WORKERS TO WORK WITH THREE FILES
			Map<String, BlackboardTasks> workers = new HashMap<>();
			workers.put("DAW", BlackboardTasks.CREATE_SERVICE_API_FILE);
			workers.put("DIW", BlackboardTasks.CREATE_SERVICE_ERROR_FILE);
			workers.put("RPW", BlackboardTasks.CREATE_SERVICE_BACKEND_FILE);
			setupProposal(workers);
			
		} else if(!finished && taskState == BlackboardTaskState.IN_PROCESS && 
				(taskDelegated == BlackboardTasks.CONFIGURE_SERVICE_ARCHITECTURE && generatedFiles.size() == 3) || 
				(taskDelegated == BlackboardTasks.CONFIGURE_SERVICE_BACKEND && generatedFiles.size() == 3)) {
			
			// ALL THE FILES SHOULD BE STORED & THE RETROFIT INTERFACE SHOULD BE BUILT
			List<String> mergedFiles = new ArrayList<>();
			
			for(GeneratedCodeFile file : this.generatedFiles) {
				if(file.getPart() == -1) {
					if(file.getFilename().contains("Error")) {
						((ManagerAgent)myAgent).buildFile(file, Paths.ERROR);
					} else if(file.getFilename().contains("ServiceBackend")) {
						((ManagerAgent)myAgent).buildFile(file, Paths.BACKEND);
					} else {
						((ManagerAgent)myAgent).buildFile(file, Paths.SERVICE_ESTRUCTURE);
					}
				} else {
					if(!mergedFiles.contains(file.getFilename())) {
						mergedFiles.add(file.getFilename());
						GeneratedCodeFile mergedFile = mergePartFile(file);
						((ManagerAgent)myAgent).buildFile(mergedFile, Paths.SERVICE_ESTRUCTURE);
					}
				}
			}
			AgentUtils.log(myAgent.getLocalName(),
					"Service: " + taskDelegated.toString() + " finished");
			
			// UPDATING THE TASK STATE TO FINISHED INCREMENTING THE COUNTERS TO PERFORM THE STOP CONDITION
			taskState = BlackboardTaskState.FINISHED;
			finished = true;
			MasSetup.winnersFinished++;
			
			// VERIFYING THE SECOND ROUND 
			if(MasSetup.winnersFinished == MasSetup.NUM_OF_WINNERS) {
				MasSetup.finished = true;
				
				// CALCULARTING TOTAL EXECUTION TIME
				Long end = System.nanoTime();
				Long executionTime = end - MasSetup.startTime;
				DecimalFormat df = new DecimalFormat("00.00000000");
				String seconds = df.format(executionTime.doubleValue() / 1_000_000_000);
				
				AgentUtils.logConsole("EXECUTION FINISHED: " + seconds + " secs");
				
				if(AgentUtils.logEnabled) {
					try {
						File filepath = new File(MasSetup.logFilePath);
						FileWriter file = new FileWriter(filepath, true);
					
						file.write("\n\n--------------------------------------------------------------------------------------\n");
						file.write("EXECUTION FINISHED: " + seconds + " secs\n");
						file.write("--------------------------------------------------------------------------------------\n");
						
						file.flush();
						file.close();
						
					} catch(IOException e) {
						AgentUtils.logError(myAgent.getLocalName(), e.getLocalizedMessage());
					}
				}
				
				// REALEASING AGENT CONTAINERS AND DF
				try {
					for(ContainerController container : AgentUtils.agentContainers) {
						for(String agentName: MasSetup.reports.keySet()){
							String containerName = container.getContainerName().split("_")[1];
							
							if(agentName.contains("_")) {
								String[] components = agentName.split("_");
								
								if(components[0].equals(containerName)) {
									AgentController agent = container.getAgent(agentName);	
									agent.kill();
								}
							} else {
								if(agentName.equals(containerName)) {
									AgentController agent = container.getAgent(agentName);
									agent.kill();
								}
							}
						}
						container.kill();
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
				// RELEASING STATIC VARIABLES
				MasSetup.setup();
			}
			this.stop();
			
		} else {
			block();
		}
	}
	
  	//------------------------------------------------------------------------------------------------------
  	//MARK:- SEARCH PART GENERATED FILE 
  	//------------------------------------------------------------------------------------------------------
  	private GeneratedCodeFile mergePartFile(GeneratedCodeFile file) {
  		int partToFind = (file.getPart() == 2)?1:2;
  		GeneratedCodeFile otherFile = null;
  		
  		for(GeneratedCodeFile partFile : this.generatedFiles) {
  			if(partFile.getPart() == partToFind && file.getFilename().equals(partFile.getFilename())) {
  				otherFile = partFile;
  				break;
			}
		}
  		if(partToFind == 2) {
  			file.removeEndClassMarker();
  	  		file.addGeneratedCodes(otherFile.getGeneratedCodes());
  	  		file.addExecutionTime(otherFile.getExecutionTime());
  	  		return file;
  		} else {
  			otherFile.removeEndClassMarker();
  			otherFile.addGeneratedCodes(file.getGeneratedCodes());
  			otherFile.addExecutionTime(file.getExecutionTime());
  			return otherFile;
  		}
  	}
  	
  	//------------------------------------------------------------------------------------------------------
  	//MARK:- SEND PROPOSAL TO WORKERS
  	//------------------------------------------------------------------------------------------------------
  	private void setupProposal(Map<String, BlackboardTasks> workers) {
		for(Map.Entry<String, BlackboardTasks> worker : workers.entrySet()) {
			AID aid = AgentUtils.search(myAgent, 
										myAgent.getLocalName() + "_" + worker.getKey(), 
										AgentType.WORKER);
			
			if(aid != null) {
				((ManagerAgent)myAgent).setupProposal(aid, false, worker.getValue());
			} else {
				AgentUtils.logError(myAgent.getLocalName(), "Agent AID not exists");
			}
		}
	}
}
