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
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.navi.agenslib.mas.MasSetup;
import com.navi.agenslib.mas.manager.ManagerAgent.Paths;
import com.navi.agenslib.mas.utils.AgentUtils;
import com.navi.agenslib.mas.utils.codegen.GeneratedCodeFile;
import com.navi.agenslib.mas.utils.sorts.AgentClassname;
import com.navi.agenslib.mas.utils.sorts.AgentType;
import com.navi.agenslib.mas.utils.sorts.WorkerType;

/**
 * Created by van on 10/06/20.
 */
public class ManagerBehaviour extends TickerBehaviour {
	
	//------------------------------------------------------------------------------------------------------
  	//MARK:- AGENT PROPERTIES
	//------------------------------------------------------------------------------------------------------
  	private static final long serialVersionUID = 6L;
  	
  	//------------------------------------------------------------------------------------------------------
  	//MARK:- PROPERTIES
  	//------------------------------------------------------------------------------------------------------
  	private static final int AGENT_BY_BRANCH = 18;
	private List<GeneratedCodeFile> generatedFiles = new ArrayList<>();
	private boolean serviceTaken = false;
	private boolean finished = false;
	
	//------------------------------------------------------------------------------------------------------
  	//MARK:- CONSTRUCTOR
	//------------------------------------------------------------------------------------------------------
  	public ManagerBehaviour(Agent a, long period) {
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
				if(msg.getContentObject() != null && msg.getContentObject() instanceof GeneratedCodeFile) {
					// RECOVER THE GENERATED FILE
					generatedFiles.add((GeneratedCodeFile)msg.getContentObject());
				} 
			} catch (UnreadableException e) {
				e.printStackTrace();
				AgentUtils.log(myAgent.getLocalName(), 
						"Error while receiving the serialized object: " + e.getLocalizedMessage());
			}
		}
		
		// TAKE THE SERVICE AND SPLIT IT
		if(!serviceTaken) {
			serviceTaken = true;
			((ManagerAgent)myAgent).splitAssignedService();
			this.createWorkers();
			block();
		}
			
		// CHECK UNTIL ALL THE FILES ARE FINISHED
		if(!finished && generatedFiles.size() == ManagerAgent.NUM_WORKERS){
			MasSetup.managersFinished++;
			finished = true;
			
			// ALL THE FILES SHOULD BE STORED & THE RETROFIT INTERFACE SHOULD BE BUILT
			for(GeneratedCodeFile file : this.generatedFiles) {
				((ManagerAgent)myAgent).buildFile(file, Paths.SERVICE_DAO);
			}
			AgentUtils.log(myAgent.getLocalName(),
					"Service: " + ((ManagerAgent)myAgent).getServiceConfig().getName() + " finished");
			
			// GETTING THE OVERALL EXECUTION TIME TAKEN THE WORKERS AND THE WRITERS
			long overallTime = 0;
			
			for(GeneratedCodeFile file : this.generatedFiles) {
				for(Object key : file.getExecutionTime().keySet().toArray()) {
					overallTime += (Long)file.getExecutionTime().get(key);
					MasSetup.reports.put((String)key, (Long)file.getExecutionTime().get(key));
				}
			}
			MasSetup.reports.put(myAgent.getLocalName(), overallTime);
			
			Set<Entry<String, Long>> entrySet = MasSetup.reports.entrySet();
		    List<Entry<String, Long>> sortedEntrySet = new ArrayList<Entry<String, Long>>(entrySet);
		    Collections.sort(sortedEntrySet, new Comparator<Map.Entry<String, Long>>(){
		    	@Override 
		    	public int compare(Map.Entry<String, Long> entry1, Map.Entry<String, Long> entry2){
		    		String agentName1 = entry1.getKey().substring(1);
		    		String agentName2 = entry2.getKey().substring(1);
		    		
		    		if(agentName1.contains("_")) {
		    			String[] agentNames = agentName1.split("_");
		    			agentName1 = agentNames[0];
			        } 
		    		if(agentName2.contains("_")) {
		    			String[] agentNames = agentName2.split("_");
		    			agentName2 = agentNames[0];
		    		}
		    		Integer e1 = Integer.parseInt(agentName1);
		    		Integer e2 = Integer.parseInt(agentName2);
		            
		    		return e1.compareTo(e2);
		        }
		    });
		    
			if(MasSetup.managersFinished == MasSetup.managersNames.length && !MasSetup.analyzingWinners) {
				MasSetup.analyzingWinners = true;
				
				// REPORTING LOG
				AgentUtils.logSeparator();
				AgentUtils.logConsole("Analyzing winners...");
				//if(AgentUtils.logEnabled) {
					this.writeLog("LogServices", sortedEntrySet);
				//}
				AgentUtils.logConsole("JSON Collection finished.. (Log saved)...");
				AgentUtils.logConsole("Faster Branch winner: " + MasSetup.winners[0] + "");
				AgentUtils.logConsole("Voting Branch winner: " + MasSetup.winners[1] + "");
				AgentUtils.logSeparator();
				
				// RELEASE STATIC DATA THAT IS NOT LONGER NEEDED
				AgentUtils.customClasses = null;
				AgentUtils.classType = null; 
			}
		} else {
			if(MasSetup.managersFinished == MasSetup.managersNames.length && 
					AgentUtils.isAgentPartOfWinnerBranch(myAgent.getLocalName())) {
				myAgent.addBehaviour(new ManagerWinnerBehaviour(myAgent, 1000));
				((ManagerAgent)myAgent).setupProposal(myAgent.getAID(), false, MasSetup.winnerTasks.removeFirst());
				this.stop();
				
			} else if(MasSetup.managersFinished == MasSetup.managersNames.length) {
				this.stop();
			}
			block(1000);
		}
	}	
	
  	//------------------------------------------------------------------------------------------------------
  	//MARK:- CREATE WORKERS
  	//------------------------------------------------------------------------------------------------------
  	private void createWorkers() {
  		Map<WorkerType, String> workers = new HashMap<>();
  		workers.put(WorkerType.CREATE_INTERACTOR_FILE, "INW");
  		workers.put(WorkerType.CREATE_DAO_FILE, "DAW");
  		workers.put(WorkerType.CREATE_DAOIMPL_FILE, "DIW");
  		workers.put(WorkerType.CREATE_REQUEST_FILE, "RQW");
  		workers.put(WorkerType.CREATE_RESPONSE_FILE, "RPW");
			
  		for(Map.Entry<WorkerType, String> worker : workers.entrySet()) {
  			Object[] args = new Object[1];
  			args[0] = ((ManagerAgent)myAgent).getConfiguration();
  			
  			AgentUtils.createAgent(myAgent.getContainerController(),
  					myAgent.getLocalName() + "_" + worker.getValue(), 
  					AgentClassname.WORKER.getClassname(), 
  					args);
				
  			AID aid = AgentUtils.search(myAgent, 
  					myAgent.getLocalName() + "_" + worker.getValue(), 
  					AgentType.WORKER);
  			if(aid != null) {
  				((ManagerAgent)myAgent).setupProposal(aid, true, null);
			} else {
				AgentUtils.logError(myAgent.getLocalName(), "Agent AID not exists");
			}
  		}
	}
	
  	//------------------------------------------------------------------------------------------------------
  	//MARK:- CREATING THE LOG FILE 
  	//------------------------------------------------------------------------------------------------------
  	private void writeLog(String filename, List<Entry<String, Long>> sortedEntrySet) {
		try {
			MasSetup.logFilePath = ((ManagerAgent)myAgent).getConfiguration().getApi() + "/" + filename + ".txt";
			File filepath = new File(MasSetup.logFilePath);
			
			if(!filepath.getParentFile().exists()) {
				filepath.getParentFile().mkdirs();
			} 
			
			FileWriter file = new FileWriter(filepath, false);
			
			file.write("--------------------------------------------------------------------------------------\n");
			file.write("INTERMEDIATE LOG REPORT OF THE SERVICES \n");
			file.write("--------------------------------------------------------------------------------------\n");
			DecimalFormat df = new DecimalFormat("00.00000000");
			double secs = 100;
			int count = 0; 
			int branch = 1;
			
			for(Map.Entry<String, Long> set : sortedEntrySet) {
				String tabs = "\t\t";
				String agentName = set.getKey();
				double time = set.getValue().doubleValue();
				String seconds = df.format(time / 1_000_000_000);
				
				if(agentName.length() <= 3) {
					tabs = tabs + "\t";
				} 
				
				if(count % AGENT_BY_BRANCH == 0) {
					file.write("--------------------------------------------------------------------------------------\n");
					file.write("BRANCH " + branch + "\n");
					file.write("--------------------------------------------------------------------------------------\n");
					branch++;
				}
				file.write("AgentName: " + agentName + tabs + "Time: " + seconds + " secs\n");
				count++;
				
				if(Double.parseDouble(seconds) < secs && !agentName.contains("_")){
					secs = Double.parseDouble(seconds);
					MasSetup.winners[0] = agentName;
				}
			}
			
			try {
				// REPLACING AGENT FULL NAME WITH SERVICE ASSOCIATED
				for(Object agentName : MasSetup.voting.keySet().toArray()) {
					if(((String)agentName).contains(MasSetup.winners[0])) {
						MasSetup.winners[0] = (String)agentName;
						break;
					}
				}
			} catch(Exception e) {
				AgentUtils.logConsoleError(e.getLocalizedMessage());
			}
			
			file.write("\nFASTER WINNER BRANCH: " + MasSetup.winners[0] + "\n");
			
			int votes = 0;
			
			file.write("TOTAL AGENTS: " + MasSetup.reports.size() + "\n");
			file.write("--------------------------------------------------------------------------------------\n");
			file.write("VOTES \n");
			file.write("--------------------------------------------------------------------------------------\n");
			
			for(Object agentName : MasSetup.voting.keySet().toArray()) {
				String[] components = ((String)agentName).split(" ");
				file.write("Manager " + components[0] + ": \t" + MasSetup.voting.get(agentName) + "\t" + components[1] + "\n");
				
				if(((Integer)MasSetup.voting.get(agentName)) > votes && !MasSetup.winners[0].equals((String)agentName)){
					votes = ((Integer)MasSetup.voting.get(agentName));
					MasSetup.winners[1] = (String)agentName;
				}
			}
			file.write("\nVOTING WINNER BRANCH: " + MasSetup.winners[1] + "\n");
			file.write("--------------------------------------------------------------------------------------\n");
			
			file.flush();
			file.close();
			
		} catch(IOException e) {
			AgentUtils.logError(myAgent.getLocalName(), e.getLocalizedMessage());
		}
	}
}
