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
package com.navi.agenslib.mas;

import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.navi.agenslib.mas.utils.AgentUtils;
import com.navi.agenslib.mas.utils.blackboard.Blackboard;
import com.navi.agenslib.mas.utils.blackboard.BlackboardTasks;
import com.navi.agenslib.mas.utils.settings.Configuration;
import com.navi.agenslib.mas.utils.settings.ServiceConfig;
import com.navi.agenslib.mas.utils.sorts.AgentClassname;

/**
 * Created by van on 10/06/20.
 */
public class MasSetup {
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- STATIC BLACKBOARD
	//--------------------------------------------------------------------------------------------------------------------------------
	public static Blackboard[] blackboards;
	public static ConcurrentMap<String, Object> voting;
	public static ConcurrentMap<String, Long> reports;
	public static LinkedList<BlackboardTasks> winnerTasks;
	public static List<ServiceConfig> services;
		
	public static int NUM_OF_WINNERS;
	public static String[] winners;
	public static String[] managersNames; 
	public static int managersFinished;
	public static int winnersFinished;
	public static Long startTime;
	public static String logFilePath;
	public static boolean analyzingWinners;
	public static boolean finished;
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- SINGLETON
	//--------------------------------------------------------------------------------------------------------------------------------
	private static MasSetup sharedInstance; 
	
	public static MasSetup sharedInstance() {
		if(sharedInstance == null) {
			sharedInstance = new MasSetup();
		}
		return sharedInstance;
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- CONSTRUCTOR
	//--------------------------------------------------------------------------------------------------------------------------------
	public MasSetup(){
		setup();
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- SETUP
	//--------------------------------------------------------------------------------------------------------------------------------
	public void run(Configuration simulation, JSONObject info, JSONArray webservices) throws Exception {
		// STARTING THE HOLE EXECUTION TIME
		startTime = System.nanoTime();
			
		// INITIALIZATION 
		managersNames = new String[webservices.size()];
		winners[0] = "";
		winners[1] = "";
		winnerTasks.add(BlackboardTasks.CONFIGURE_SERVICE_ARCHITECTURE);
		winnerTasks.add(BlackboardTasks.CONFIGURE_SERVICE_BACKEND);
			
		// CREATING BLACKBOARD 
		MasSetup.setupBlackboards(webservices.size());
			
		// LOGGING API SCHEMA INFORMATION
		AgentUtils.logInfoAPI(info.get("name"), info.get("schema"), webservices.size());
		
		// CREATING N MANAGERS TO HANDLE EACH WEB SERVICE
		for(int i = 0; i < webservices.size(); i++) {
			JSONObject service = (JSONObject)webservices.get(i);
			managersNames[i] = "A" + (i + 1) + " (" + (String)service.get("name") + ")";
			
			Object[] params = new Object[2];
			params[0] = service.toString();
			params[1] = simulation;
				
			AgentUtils.createAgent("WS_A" + (i + 1),
								   "A" + (i + 1), 
								   AgentClassname.MANAGER.getClassname(), 
								   params);
		}
	}
	 
	//MARK:- RESET BLACKBOARD MEMORY FOR WINNERS
	public static void setupBlackboards(int size) {
		MasSetup.blackboards = null;
		MasSetup.blackboards = new Blackboard[size];
		
		for(int i = 0; i < size; i++) {
			blackboards[i] = new Blackboard();
		}
	}
	
	//MARK:- SETUP FUNCTION USED TO INITIALIZE ALL THE VARIABLES
	public static void setup() {
		voting = new ConcurrentHashMap<>();
		reports = new ConcurrentHashMap<>();
		winnerTasks = new LinkedList<>();
		services = new ArrayList<>();
		
		NUM_OF_WINNERS = 2;
		winners = new String[NUM_OF_WINNERS];
		managersFinished = 0;
		winnersFinished = 0;
		startTime = 0L;
		analyzingWinners = false;
		finished = false;
		
		AgentUtils.setupVars();
	}
}

