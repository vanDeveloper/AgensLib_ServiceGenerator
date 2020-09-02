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

import com.navi.agenslib.mas.MasSetup;
import com.navi.agenslib.mas.utils.AgentUtils;
import com.navi.agenslib.mas.utils.blackboard.BlackboardRole;
import com.navi.agenslib.mas.utils.blackboard.BlackboardTask;
import com.navi.agenslib.mas.utils.blackboard.BlackboardTaskState;
import com.navi.agenslib.mas.utils.blackboard.BlackboardTasks;
import com.navi.agenslib.mas.utils.codegen.GeneratedCode;
import com.navi.agenslib.mas.utils.settings.ServiceConfig;
import com.navi.agenslib.mas.utils.sorts.AgentClassname;
import com.navi.agenslib.mas.utils.sorts.AgentType;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.util.leap.ArrayList;
import jade.util.leap.List;

/**
 * Created by van on 10/06/20.
 */
public class WorkerBehaviour extends TickerBehaviour {
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- AGENT PROPERTIES
	//--------------------------------------------------------------------------------------------------------------------------------
	private static final long serialVersionUID = 8L;
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK: PROPERTIES
	//--------------------------------------------------------------------------------------------------------------------------------
	private AID managerAid;
	private ServiceConfig service; 
	private boolean tasksFinished = false;
	private List myTasks = new ArrayList();
	private List generatedCodes = new ArrayList();
	private long startTime; 
	private BlackboardTasks taskDelegated; 
	private boolean winnerBehaviour = false;
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- CONSTRUCTOR
	//--------------------------------------------------------------------------------------------------------------------------------
	public WorkerBehaviour(Agent a, long period) {
		super(a, period);
	}
		
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- AUTO RUN BEHAVIOUR
	//--------------------------------------------------------------------------------------------------------------------------------
	@Override
	protected void onTick() {
		
		ACLMessage msg = myAgent.receive(); 
		if (msg != null) {
			try {
				if(msg.getContentObject() != null && msg.getContentObject() instanceof ServiceConfig) {
					
					// RECOVERING THE CONTENT AND CREATE THE WRITERS BY EACH FILE
					this.startTime = System.nanoTime();
					this.managerAid = msg.getSender();
					service = (ServiceConfig) msg.getContentObject();
					this.createWriters(service);
				
				} else if(msg.getContentObject() != null && msg.getContentObject() instanceof GeneratedCode) {
					
					// RECOVERING THE GENERATED CODES OF THE WRITERS 
					GeneratedCode codegen = (GeneratedCode)msg.getContentObject();
					generatedCodes.add(codegen);
					
				} else if(msg.getContentObject() != null && msg.getContentObject() instanceof BlackboardTasks) {
					
					// SETTING UP ENV VARS
					this.startTime = System.nanoTime();
					winnerBehaviour = true;
					generatedCodes.clear();
					
					// GETTING THE TASK
					taskDelegated = (BlackboardTasks)msg.getContentObject();
					AgentUtils.log(myAgent.getLocalName(), taskDelegated.toString());
					this.createServiceArch();
				}
			} catch (UnreadableException e) {
				e.printStackTrace();
				AgentUtils.log(myAgent.getLocalName(), 
						"Error while receiving the serialized object: " + e.getLocalizedMessage());
			}
		}
		
		// CLEAN AGENT'S BEHAVIOUR THAT CORRESPONDS TO THE BRANCHES THAT ARE NOT WINNERS 
		/*
		if(MasSetup.managersFinished == MasSetup.managersNames.length &&
				!AgentUtils.isAgentPartOfWinnerBranch(myAgent.getLocalName())){
			AgentUtils.log(myAgent.getLocalName(), "Ending Worker behaviour");
			this.stop();
		} else if(MasSetup.winnersFinished == MasSetup.NUM_OF_WINNERS) {
			AgentUtils.log(myAgent.getLocalName(), "Ending Worker behaviour");
			this.stop();
		}
		*/
		
		// ASKING IF ALL THE TASK WERE FINISHED
		if(this.myTasks.size() > 0 && !tasksFinished && !winnerBehaviour) {
			// VERIFYING IF ALL THE WORKERS HAS DONE THEIR TASKS
			int taskDone = 0;
			int blackboardIdx = AgentUtils.getBlackboardIdx(myAgent.getLocalName());
			
			for (int i = 0; i < this.myTasks.size(); i++) {
				if(((BlackboardTask)this.myTasks.get(i)).getState().equals(BlackboardTaskState.FINISHED)) {
					taskDone++;
					MasSetup.blackboards[blackboardIdx].removeTask(((BlackboardTask)this.myTasks.get(i)).getId());
				}
			}
			
			if(generatedCodes != null && 
					taskDone == generatedCodes.size() && 
					taskDone == myTasks.size()) {
				// ALL THE WRITERS FINISHED BUILD THE FILE
				tasksFinished = true;
				((WorkerAgent)myAgent).createFile(managerAid, generatedCodes, service, startTime);
				block();
			}
		} 
		// ASKING IF ALL THE TASK WERE FINISHED FOR THE WINNER STEP
		else if(winnerBehaviour && generatedCodes.size() > 0) {
			if(generatedCodes.size() == 2 && myAgent.getLocalName().contains("INW") && 
					taskDelegated == BlackboardTasks.CREATE_SERVICE_REQUEST_FILE) {
				((WorkerAgent)myAgent).createServiceRequestPartFile(managerAid, generatedCodes, 1, startTime);
				this.stop();
			} else if(generatedCodes.size() == 2 && myAgent.getLocalName().contains("RQW") && 
					taskDelegated == BlackboardTasks.CREATE_SERVICE_REQUEST_FILE) {
				((WorkerAgent)myAgent).createServiceRequestPartFile(managerAid, generatedCodes, 2, startTime);
				this.stop();
			} else if(generatedCodes.size() == 4 && myAgent.getLocalName().contains("DIW") && 
					taskDelegated == BlackboardTasks.CREATE_SERVICE_CONFIG_FILE) {
				((WorkerAgent)myAgent).createServiceConfigFile(managerAid, generatedCodes, startTime);
				this.stop();
			} else if(generatedCodes.size() == 4 && myAgent.getLocalName().contains("DIW") && 
					taskDelegated == BlackboardTasks.CREATE_SERVICE_ERROR_FILE){
				((WorkerAgent)myAgent).createServiceErrorFile(managerAid, generatedCodes, startTime);
				this.stop();
			} else if(generatedCodes.size() == 3 && myAgent.getLocalName().contains("RPW") && 
					taskDelegated == BlackboardTasks.CREATE_SERVICE_BACKEND_FILE){
				((WorkerAgent)myAgent).createServiceBackendFile(managerAid, generatedCodes, startTime);
				this.stop();
			} else if(generatedCodes.size() == 1 && myAgent.getLocalName().contains("DAW") && 
					taskDelegated == BlackboardTasks.CREATE_SERVICE_API_FILE) {
				((WorkerAgent)myAgent).createServiceApiFile(managerAid, generatedCodes, startTime);
				this.stop();
			}
		} else {
			block(1000);
		}
	}	
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- CREATE WRITERS
	//--------------------------------------------------------------------------------------------------------------------------------
	private void createWriters(ServiceConfig config) {
		try {
			if(myAgent.getLocalName().contains("INW")) {
				this.createInteractorInterface();	
			} else if(myAgent.getLocalName().contains("DAW")) {
				this.createDaoInterface();
			} else if(myAgent.getLocalName().contains("DIW")) {
				this.createDaoImpl();
			} else if(myAgent.getLocalName().contains("RQW")) {
				this.createRequest();
			} else if(myAgent.getLocalName().contains("RPW")) {
				this.createResponse();
			}		
		} catch(InterruptedException e) {
			AgentUtils.logError(myAgent.getLocalName(), e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- CREATE WRITERS AGENTS 
	//--------------------------------------------------------------------------------------------------------------------------------
	private void createWriter(int writers, String agentclass) throws InterruptedException {
		for(int i = 0; i < writers; i++) {
			Object[] args = new Object[1];
			args[0] = ((WorkerAgent)myAgent).getConfiguration();
			
			AgentUtils.createAgent(myAgent.getContainerController(),
								   myAgent.getLocalName() + "_" + "W" + (i + 1),
								   agentclass, 
								   args);
			
			AID aid = AgentUtils.search(myAgent, 
										myAgent.getLocalName() + "_" + "W" + (i + 1), 
										AgentType.WRITER);
			if(aid != null) {
				((WorkerAgent)myAgent).setupProposal(aid, service, null);
			} else {
				AgentUtils.logError(myAgent.getLocalName(), "Agent AID not exists");
			}
			
			Thread.sleep(1000);
		}
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- CREATE TASK 
	//--------------------------------------------------------------------------------------------------------------------------------
	private void createTask(BlackboardTasks[] tasks, BlackboardRole role) {
		int blackboardIdx = AgentUtils.getBlackboardIdx(myAgent.getLocalName());
		for(BlackboardTasks taskType : tasks) {
			this.myTasks.add(new BlackboardTask(MasSetup.blackboards[blackboardIdx].getId(), taskType, role));
			MasSetup.blackboards[blackboardIdx].writeTask((BlackboardTask)this.myTasks.get(myTasks.size() - 1));
		}
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- CREATE INTERACTOR INTERFACE WRITERS & TASKS WITH THE PROPOSALS 
	//--------------------------------------------------------------------------------------------------------------------------------
	private void createInteractorInterface() throws InterruptedException {
		
		BlackboardTasks[] tasks = { BlackboardTasks.GENERATE_INTERACTOR_SUCCESS_METHOD, 
									BlackboardTasks.GENERATE_INTERACTOR_FAILURE_METHOD };
		this.createTask(tasks, BlackboardRole.WRITER_INTERACTOR_AGENT);
		this.createWriter(tasks.length, AgentClassname.INTERACTOR.getClassname());
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- CREATE DAO INTERFACE WRITERS & TASKS WITH THE PROPOSALS 
	//--------------------------------------------------------------------------------------------------------------------------------
	private void createDaoInterface() throws InterruptedException {
		
		BlackboardTasks[] tasks = { BlackboardTasks.GENERATE_DAO_METHOD };
		this.createTask(tasks, BlackboardRole.WRITER_DAO_AGENT);
		this.createWriter(tasks.length, AgentClassname.DAO.getClassname());
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- CREATE DAOIMPL WRITERS & TASKS WITH THE PROPOSALS 
	//--------------------------------------------------------------------------------------------------------------------------------
	private void createDaoImpl() throws InterruptedException {
		
		BlackboardTasks[] tasks = { BlackboardTasks.GENERATE_DAOIMPL_PARAMS,
								    BlackboardTasks.GENERATE_DAOIMPL_INTERFACE_METHOD,
								    BlackboardTasks.GENERATE_DAOIMPL_CALLBACK_RESPONSE_METHOD,
								    BlackboardTasks.GENERATE_DAOIMPL_CALLBACK_FAILURE_METHOD };
		this.createTask(tasks, BlackboardRole.WRITER_DAOIMPL_AGENT);
		this.createWriter(tasks.length, AgentClassname.DAO_IMPL.getClassname());
	}	
		
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- CREATE REQUEST WRITERS & TASKS WITH THE PROPOSALS 
	//--------------------------------------------------------------------------------------------------------------------------------
	private void createRequest() throws InterruptedException {
		
		BlackboardTasks[] tasks = { BlackboardTasks.GENERATE_REQUEST_PARAMS, 
									BlackboardTasks.GENERATE_REQUEST_CONSTRUCTOR };
		this.createTask(tasks, BlackboardRole.WRITER_REQUEST_AGENT);
		this.createWriter(tasks.length, AgentClassname.REQUEST.getClassname());
	}
		
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- CREATE RESPONSE WRITERS & TASKS WITH THE PROPOSALS 
	//--------------------------------------------------------------------------------------------------------------------------------
	private void createResponse() throws InterruptedException {
		
		BlackboardTasks[] tasks = { BlackboardTasks.GENERATE_RESPONSE_PARAMS, 
									BlackboardTasks.GENERATE_RESPONSE_CONSTRUCTOR,
									BlackboardTasks.GENERATE_RESPONSE_ENCAPSULATION };
		this.createTask(tasks, BlackboardRole.WRITER_RESPONSE_AGENT);
		this.createWriter(tasks.length, AgentClassname.RESPONSE.getClassname());
	}

	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- CREATE SERVICE ARCHITECTURE OR SERVICE BACKEND FILES
	//--------------------------------------------------------------------------------------------------------------------------------
	private void createServiceArch() {
		
		if(taskDelegated == BlackboardTasks.CREATE_SERVICE_REQUEST_FILE ) {
			if(myAgent.getLocalName().contains("INW")) {
				this.createServiceRequestInw();
			} else if(myAgent.getLocalName().contains("RQW")) {
				this.createServiceRequestRqw();
			}
		} else if(taskDelegated == BlackboardTasks.CREATE_SERVICE_CONFIG_FILE) {
			if(myAgent.getLocalName().contains("DIW")) {
				this.createServiceConfigDiw();
			} 	
		} else if(taskDelegated == BlackboardTasks.CREATE_SERVICE_API_FILE){
			if(myAgent.getLocalName().contains("DAW")) {
				this.createServiceApiDao();
			}
		} else if(taskDelegated == BlackboardTasks.CREATE_SERVICE_BACKEND_FILE) {
			if(myAgent.getLocalName().contains("RPW")) {
				this.createServiceBackendRpw();
			}
		} else if(taskDelegated == BlackboardTasks.CREATE_SERVICE_ERROR_FILE) {
			if(myAgent.getLocalName().contains("DIW")) {
				this.createServiceErrorDiw();
			}
		}
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- SENDING WINNER PROPOSALS 
	//--------------------------------------------------------------------------------------------------------------------------------
	private void setupWinnerProposal(BlackboardTasks[] tasks) {
		for(int i = 0; i < tasks.length; i++) {
			AID aid = AgentUtils.search(myAgent, 
										myAgent.getLocalName() + "_" + "W" + (i + 1), 
					 					AgentType.WRITER);
			if(aid != null) {
				((WorkerAgent)myAgent).setupProposal(aid, service, tasks[i]);
			} else {
				AgentUtils.logError(myAgent.getLocalName(), "Agent AID not exists");
			}
		}
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- CREATE SERVICE RESPONSE
	//--------------------------------------------------------------------------------------------------------------------------------
	private void createServiceRequestInw() {
		BlackboardTasks[] tasks = { BlackboardTasks.GENERATE_SERVICE_REQUEST_PARAMS, 
									BlackboardTasks.GENERATE_SERVICE_REQUEST_CONSTRUCTOR };
		this.setupWinnerProposal(tasks);
	}
	private void createServiceRequestRqw() {
		BlackboardTasks[] tasks = { BlackboardTasks.GENERATE_SERVICE_REQUEST_SETUP, 
									BlackboardTasks.GENERATE_SERVICE_REQUEST_ENCAPSULATION };
		this.setupWinnerProposal(tasks);
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- CREATE SERVICE CONFIG 
	//--------------------------------------------------------------------------------------------------------------------------------
	private void createServiceConfigDiw() {
		BlackboardTasks[] tasks = { BlackboardTasks.GENERATE_SERVICE_CONFIG_SINGLETON, 
									BlackboardTasks.GENERATE_SERVICE_CONFIG_PARAMS,
									BlackboardTasks.GENERATE_SERVICE_CONFIG_SETUP, 
									BlackboardTasks.GENERATE_SERVICE_CONFIG_ENCAPSULATION };
		this.setupWinnerProposal(tasks);
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- CREATE SERVICE API 
	//--------------------------------------------------------------------------------------------------------------------------------
	private void createServiceApiDao() {
		BlackboardTasks[] tasks = { BlackboardTasks.GENERATE_SERVICE_API_METHODS };
		this.setupWinnerProposal(tasks);
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- CREATE SERVICE ERROR 
	//--------------------------------------------------------------------------------------------------------------------------------
	private void createServiceErrorDiw() {
		BlackboardTasks[] tasks = { BlackboardTasks.GENERATE_SERVICE_ERROR_PARAMS, 
									BlackboardTasks.GENERATE_SERVICE_ERROR_CONSTRUCTOR, 
									BlackboardTasks.GENERATE_SERVICE_ERROR_ENCAPSULATION, 
									BlackboardTasks.GENERATE_SERVICE_ERROR_TOSTRING };
		this.setupWinnerProposal(tasks);
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- CREATE SERVICE BACKEND 
	//--------------------------------------------------------------------------------------------------------------------------------
	private void createServiceBackendRpw() {
		BlackboardTasks[] tasks = { BlackboardTasks.GENERATE_SERVICE_BACKEND_PARAMS,
									BlackboardTasks.GENERATE_SERVICE_BACKEND_CONSTRUCTOR, 
									BlackboardTasks.GENERATE_SERVICE_BACKEND_ENCAPSULATION };
		this.setupWinnerProposal(tasks);
	}
}
