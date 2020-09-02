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

import java.util.Random;

import com.navi.agenslib.mas.MasSetup;
import com.navi.agenslib.mas.utils.AgentUtils;
import com.navi.agenslib.mas.utils.blackboard.BlackboardRole;
import com.navi.agenslib.mas.utils.codegen.GeneratedCode;
import com.navi.agenslib.mas.utils.settings.Configuration;
import com.navi.agenslib.mas.utils.sorts.AgentType;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;

/**
 * Created by van on 10/06/20.
 */
public class WriterAgent extends Agent {

	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- AGENT PROPERTIES
	//--------------------------------------------------------------------------------------------------------------------------------
	private static final long serialVersionUID = 9L;
	private static final String OWNER = "AgensLib";
	private static final AgentType MY_TYPE = AgentType.WRITER;
	protected AID aid = new AID("WriterAgent", AID.ISLOCALNAME);	
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- PROPERTIES
	//--------------------------------------------------------------------------------------------------------------------------------
	protected BlackboardRole role;
	private Configuration config;
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- OVERRIDE FROM JADE
	//--------------------------------------------------------------------------------------------------------------------------------
	@Override
	protected void setup() {
		AgentUtils.log(getLocalName(), " is ready.");
		this.setEnabledO2ACommunication(true, 1);	
		this.config = (Configuration)this.getArguments()[0];
		
		AgentUtils.register(this, MY_TYPE, OWNER);
			
		this.addBehaviour(new WriterBehaviour(this, 7000));		
	}
		
	protected void takedown() {
		try { DFService.deregister(this); }
        catch (Exception e) { e.printStackTrace(); } 
		AgentUtils.log(getLocalName(), " terminating.");
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- INFORM THE RESULT OF THE TASK
	//--------------------------------------------------------------------------------------------------------------------------------
	public void taskDone(AID workerAid, GeneratedCode codegen) {
		ACLMessage request = new ACLMessage(ACLMessage.INFORM);
    	request.clearAllReceiver();
    	request.addReceiver(workerAid);
    	request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
    	AgentUtils.log(getLocalName(), "Inform task done");
    	AgentUtils.log(getLocalName(), "Generated code: " + codegen.getCodegen());
    	
    	try {	
    		request.setContentObject(codegen);
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
	//MARK:- ENCAPSULATION
	//--------------------------------------------------------------------------------------------------------------------------------
	public AgentType getType() { return MY_TYPE; }
	public BlackboardRole getRole() { return this.role; }
	public Configuration getConfiguration() { return this.config; }
}
