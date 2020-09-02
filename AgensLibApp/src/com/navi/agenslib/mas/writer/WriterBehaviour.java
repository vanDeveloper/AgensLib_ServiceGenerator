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
import com.navi.agenslib.mas.utils.blackboard.BlackboardTask;
import com.navi.agenslib.mas.utils.blackboard.BlackboardTaskState;
import com.navi.agenslib.mas.utils.blackboard.BlackboardTasks;
import com.navi.agenslib.mas.utils.settings.ServiceConfig;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

/**
 * Created by van on 10/06/20.
 */
public class WriterBehaviour extends TickerBehaviour {
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- AGENT PROPERTIES
	//--------------------------------------------------------------------------------------------------------------------------------
	private static final long serialVersionUID = 10L;
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- PROPERTIES
	//--------------------------------------------------------------------------------------------------------------------------------
	private ServiceConfig service; 
	private BlackboardTask taskDelegated;
	private AID workerAid;
	private BlackboardTasks winnerTask;
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- CONSTRUCTOR
	//--------------------------------------------------------------------------------------------------------------------------------
	public WriterBehaviour(Agent a, long period) {
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
				this.workerAid = msg.getSender();
				if(msg.getContentObject() != null && msg.getContentObject() instanceof ServiceConfig) {
					service = (ServiceConfig) msg.getContentObject();
				} else if(msg.getContentObject() != null && msg.getContentObject() instanceof BlackboardTasks) {
					winnerTask = (BlackboardTasks)msg.getContentObject();
				}
			} catch (UnreadableException e) {
				e.printStackTrace();
				AgentUtils.log(myAgent.getLocalName(), 
						"Error while receiving the serialized object: " + e.getLocalizedMessage());
			}
		}
		
		// CLEAN AGENT'S BEHAVIOUR THAT CORRESPONDS TO THE BRANCHES THAT ARE NOT WINNERS 
		/* if(MasSetup.managersFinished == MasSetup.managersNames.length &&
			 !AgentUtils.isAgentPartOfWinnerBranch(myAgent.getLocalName())){
			AgentUtils.log(myAgent.getLocalName(), "Ending Writer behaviour");
			this.stop();
		} else if(MasSetup.winnersFinished == MasSetup.NUM_OF_WINNERS) {
			AgentUtils.log(myAgent.getLocalName(), "Ending Writer behaviour");
			this.stop();
		}*/ 
		
		// IF A WINNER TASK WAS SENT THEN EXECUTE IT
		if(winnerTask != null) {
			switch(winnerTask) {
				case GENERATE_SERVICE_REQUEST_PARAMS:
					AgentUtils.log(myAgent.getLocalName(), "Generating Service Request Params...");
					((WriterInteractorAgent)myAgent).generateServiceRequestParams(workerAid);
					break;
				
				case GENERATE_SERVICE_REQUEST_CONSTRUCTOR:
					AgentUtils.log(myAgent.getLocalName(), "Generating Service Request Constructor...");
					((WriterInteractorAgent)myAgent).generateServiceRequestConstructor(workerAid);
					break;
				
				case GENERATE_SERVICE_REQUEST_SETUP:
					AgentUtils.log(myAgent.getLocalName(), "Generating Service Request Setup...");
					((WriterRequestAgent)myAgent).generateServiceRequestSetup(workerAid);
					break;
					
				case GENERATE_SERVICE_REQUEST_ENCAPSULATION:
					AgentUtils.log(myAgent.getLocalName(), "Generating Service Request Encapsulation...");
					((WriterRequestAgent)myAgent).generateServiceRequestEncapsulation(workerAid);
					break;
				
				case GENERATE_SERVICE_CONFIG_SINGLETON:
					AgentUtils.log(myAgent.getLocalName(), "Generating Service Config Singleton...");
					((WriterDaoImplAgent)myAgent).generateServiceConfigSingleton(workerAid);
					break;
					
				case GENERATE_SERVICE_CONFIG_PARAMS:
					AgentUtils.log(myAgent.getLocalName(), "Generating Service Config Params...");
					((WriterDaoImplAgent)myAgent).generateServiceConfigParams(workerAid);
					break;
					
				case GENERATE_SERVICE_CONFIG_SETUP:
					AgentUtils.log(myAgent.getLocalName(), "Generating Service Config Setup...");
					((WriterDaoImplAgent)myAgent).generateServiceConfigSetup(workerAid);
					break;
					
				case GENERATE_SERVICE_CONFIG_ENCAPSULATION:
					AgentUtils.log(myAgent.getLocalName(), "Generating Service Config Encapsulation...");
					((WriterDaoImplAgent)myAgent).generateServiceConfigEncapsulation(workerAid);
					break;
				
				case GENERATE_SERVICE_API_METHODS:
					AgentUtils.log(myAgent.getLocalName(), "Generating Service Api Methods...");
					((WriterDaoAgent)myAgent).generateServiceApi(workerAid);
					break;
				
				case GENERATE_SERVICE_ERROR_PARAMS:
					AgentUtils.log(myAgent.getLocalName(), "Generating Service Error Params...");
					((WriterDaoImplAgent)myAgent).generateServiceErrorParams(workerAid);
					break;
				
				case GENERATE_SERVICE_ERROR_CONSTRUCTOR:
					AgentUtils.log(myAgent.getLocalName(), "Generating Service Error Constructor...");
					((WriterDaoImplAgent)myAgent).generateServiceErrorConstructor(workerAid);
					break;
				
				case GENERATE_SERVICE_ERROR_ENCAPSULATION:
					AgentUtils.log(myAgent.getLocalName(), "Generating Service Error Encapsulation...");
					((WriterDaoImplAgent)myAgent).generateServiceErrorEncapsulation(workerAid);
					break;
					
				case GENERATE_SERVICE_ERROR_TOSTRING:
					AgentUtils.log(myAgent.getLocalName(), "Generating Service Error toString...");
					((WriterDaoImplAgent)myAgent).generateServiceErrorToString(workerAid);
					break;
				
				case GENERATE_SERVICE_BACKEND_PARAMS:
					AgentUtils.log(myAgent.getLocalName(), "Generating Service Backend Params...");
					((WriterResponseAgent)myAgent).generateServiceBackendParams(workerAid);
					break;
				
				case GENERATE_SERVICE_BACKEND_CONSTRUCTOR:
					AgentUtils.log(myAgent.getLocalName(), "Generating Service Backend Constructor...");
					((WriterResponseAgent)myAgent).generateServiceBackendConstructor(workerAid, service);
					break;
				
				case GENERATE_SERVICE_BACKEND_ENCAPSULATION:
					AgentUtils.log(myAgent.getLocalName(), "Generating Service Backend Encapsulation...");
					((WriterResponseAgent)myAgent).generateServiceBackendEncapsulation(workerAid);
					break;
				
				default: break;
			}
			this.stop();
		}
		
		// READING A TASK BY MY ROLE 
		int blackboardIdx = AgentUtils.getBlackboardIdx(myAgent.getLocalName());
		
		if(MasSetup.blackboards.length <= blackboardIdx) {
			this.stop();
		} else {
			taskDelegated = MasSetup.blackboards[blackboardIdx].getTask(((WriterAgent)myAgent).getRole());
			
			// ASKING FOR A SPECIFIC TASK TO ACCOMPLISH
			if(service != null && taskDelegated != null && 
					(taskDelegated.getState() != BlackboardTaskState.IN_PROCESS || 
					taskDelegated.getState() != BlackboardTaskState.FINISHED) ){
				taskDelegated.setState(BlackboardTaskState.IN_PROCESS);
				AgentUtils.log(myAgent.getLocalName(), "TaskDelegated: " + taskDelegated.toString());
				
				switch(taskDelegated.getType()) {
					case GENERATE_INTERACTOR_SUCCESS_METHOD:
						AgentUtils.log(myAgent.getLocalName(), "Generating Interactor success method...");
						((WriterInteractorAgent)myAgent).generateInteractorSuccess(workerAid, taskDelegated, service);
						break;
							
					case GENERATE_INTERACTOR_FAILURE_METHOD:
						AgentUtils.log(myAgent.getLocalName(), "Generating Interactor failure method...");
						((WriterInteractorAgent)myAgent).generateInteractorFailure(workerAid, taskDelegated, service);
						break;
					
					case GENERATE_DAO_METHOD:
						AgentUtils.log(myAgent.getLocalName(), "Generating Dao interface method...");
						((WriterDaoAgent)myAgent).generateDaoMethod(workerAid, taskDelegated, service);
						break;
							
					case GENERATE_DAOIMPL_PARAMS:
						AgentUtils.log(myAgent.getLocalName(), "Generating DaoImpl params...");
						((WriterDaoImplAgent)myAgent).generateDaoImplParams(workerAid, taskDelegated, service);
						break;
						
					case GENERATE_DAOIMPL_INTERFACE_METHOD:
						AgentUtils.log(myAgent.getLocalName(), "Generating DaoImpl interface method...");
						((WriterDaoImplAgent)myAgent).generateDaoMethod(workerAid, taskDelegated, service);
						break;
					
					case GENERATE_DAOIMPL_CALLBACK_RESPONSE_METHOD:
						AgentUtils.log(myAgent.getLocalName(), "Generating DaoImpl callback retrofit response method...");
						((WriterDaoImplAgent)myAgent).generateDaoImplResponse(workerAid, taskDelegated, service);
						break;
									
					case GENERATE_DAOIMPL_CALLBACK_FAILURE_METHOD:
						AgentUtils.log(myAgent.getLocalName(), "Generating DaoImpl callback retrofit failure method...");
						((WriterDaoImplAgent)myAgent).generateDaoImplFailure(workerAid, taskDelegated, service);
						break;
						
					case GENERATE_REQUEST_PARAMS:
						AgentUtils.log(myAgent.getLocalName(), "Generating request params...");
						((WriterRequestAgent)myAgent).generateParams(workerAid, taskDelegated, service);
						break;
					
					case GENERATE_REQUEST_CONSTRUCTOR:
						AgentUtils.log(myAgent.getLocalName(), "Generating request constructor...");
						((WriterRequestAgent)myAgent).generateConstructor(workerAid, taskDelegated, service);
						break;
					
					case GENERATE_RESPONSE_PARAMS:
						AgentUtils.log(myAgent.getLocalName(), "Generating response params...");
						((WriterResponseAgent)myAgent).generateParams(workerAid, taskDelegated, service);
						break;
					
					case GENERATE_RESPONSE_CONSTRUCTOR:
						AgentUtils.log(myAgent.getLocalName(), "Generating response constructor...");
						((WriterResponseAgent)myAgent).generateConstructor(workerAid, taskDelegated, service);
						break;
						
					case GENERATE_RESPONSE_ENCAPSULATION:
						AgentUtils.log(myAgent.getLocalName(), "Generating response encapsulation methods...");
						((WriterResponseAgent)myAgent).generateEncapsulation(workerAid, taskDelegated, service);
						break;
							
					default: break;
				}
				block();
			} else {
				block();
			}
		}
	}
}
