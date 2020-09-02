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
package com.navi.agenslib.mas.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.navi.agenslib.mas.MasSetup;
import com.navi.agenslib.mas.utils.codegen.ReservedWord;
import com.navi.agenslib.mas.utils.sorts.AgentType;

import jade.Boot;
import jade.core.AID;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.util.leap.HashMap;
import jade.util.leap.Map;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

/**
 * Created by van on 10/06/20.
 */
public class AgentUtils {
	
	//------------------------------------------------------------------------------------------------------
  	//MARK:- PROPERTIES FOR CUSTOM CLASSES
	//------------------------------------------------------------------------------------------------------
  	public static ConcurrentMap<Object, Object> customClasses;
	public static ConcurrentMap<Object, String> classType;
	public static String logMessage; 
	public static boolean logEnabled = false;
	public static List<ContainerController> agentContainers;
	
	//------------------------------------------------------------------------------------------------------
  	//MARK:- INITIALIZE STATIC VARS
	//------------------------------------------------------------------------------------------------------
  	public static void setupVars() {
		customClasses = new ConcurrentHashMap<>();
		classType = new ConcurrentHashMap<>();
		agentContainers = new ArrayList<>();
	}
	
	//------------------------------------------------------------------------------------------------------
  	//MARK:- TO LOG CONSOLE & AGENTS'S ACTIVITY
	//------------------------------------------------------------------------------------------------------
  	public static void log(String agentName, String str) {
  		logMessage = "[" + agentName + "]: " + str;
  		if(logEnabled) {
  			logSeparator();
			System.out.println(logMessage);
			logSeparator();
  		}
  	}
	
	public static void logError(String agentName, String error) {
		logMessage = "[" + agentName + "]: " + error;
		if(logEnabled) {
  			logSeparator();
			System.out.println(logMessage);
			logSeparator();
  		}
	}
	
	public static void logConsole(String str) {
		if(logEnabled) {
			System.out.println(str);
		}
	}
	
	public static void logConsoleError(String error) {
		if(logEnabled) {
			System.err.println(error);
		}
	}
	
	public static void logSeparator() {
		System.out.println("--------------------------------------------------------------------------------------------------------------------");
	}
	
	public static void logInfoAPI(Object name, Object schema, int webservices) {
		logSeparator();
		logConsole("Information");
		logSeparator();
		logConsole("API: " + name);
		logConsole("Schema: " + schema);
		logConsole("Webservices: " + webservices);
		logSeparator();
	}
	
	//------------------------------------------------------------------------------------------------------
  	//MARK:- SETUP JADE FRAMEWORK
	//------------------------------------------------------------------------------------------------------
  	public static void setupJade() {
		String[] param = new String[]{"-gui"};
		Boot.main(param);
	}
	
	//------------------------------------------------------------------------------------------------------
  	//MARK:- REGISTER AGENTS INTO THE DF
	//------------------------------------------------------------------------------------------------------
  	public static void register(Agent agent, AgentType type, String owner){
        ServiceDescription sd1 = new ServiceDescription();
        sd1.setType(type.toString());
        sd1.setName(agent.getLocalName());
        sd1.setOwnership(owner);
        
        DFAgentDescription df = new DFAgentDescription();
        df.addServices(sd1);
        df.setName(agent.getAID());
        
        try {
        	DFService.register(agent, df);
            log(agent.getLocalName(), "Registered to the DF");
        } catch (FIPAException e) {
            logError(agent.getLocalName(), "Failed registration to DF: " + e.getMessage());
            agent.doDelete();
        }
    }
    
    //------------------------------------------------------------------------------------------------------
  	//MARK:- CREATE AN AGENT 
    //------------------------------------------------------------------------------------------------------
  	public static void createAgent(String containerName, String agentName, String className, Object[] arguments) {
  		Runtime runtime = Runtime.instance();
  		Profile profile = new ProfileImpl();
  		profile.setParameter(Profile.MAIN_HOST, "localhost");
  		profile.setParameter(Profile.MAIN_PORT, "1099");
  		profile.setParameter(Profile.CONTAINER_NAME, containerName);
  		
  		try {
  			ContainerController container = runtime.createAgentContainer(profile);
  			AgentController controller = container.createNewAgent(agentName, className, arguments);
  			agentContainers.add(container);
  			controller.start();
  			
  		} catch(StaleProxyException e) {
  			logConsoleError("ERROR: Cannot create agent " + agentName + " of class " + className);
        }
  	}
  	
  	//------------------------------------------------------------------------------------------------------
  	//MARK:- CREATE AN AGENT IN A GIVEN CONTAINER
  	//------------------------------------------------------------------------------------------------------
  	public static void createAgent(AgentContainer container, String agentName, String className, Object[] arguments) {
  		try {
  			AgentController controller = container.createNewAgent(agentName, className, arguments);
            controller.start();
  		} catch (StaleProxyException e) {
  			logConsoleError("ERROR: Cannot create agent " + agentName + " of class " + className);
  		}
    }
  	
  	//------------------------------------------------------------------------------------------------------
  	//MARK:- SEARCH AGENT BY AID
  	//------------------------------------------------------------------------------------------------------
  	public static AID search(Agent agent, String agentToSearch, AgentType type){
  		ServiceDescription searchCriterion = new ServiceDescription();
  		
  		if(type != null)
            searchCriterion.setType(type.toString());
        if(agent != null && agentToSearch != null)
            searchCriterion.setName(agentToSearch);
        
        AID searchedAgent = new AID();
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.addServices(searchCriterion);
        
        try {
            while (true) {
            	SearchConstraints c = new SearchConstraints();
                DFAgentDescription[] result = DFService.search(agent, dfd, c);
               
                if (result.length > 0) {
                    dfd = result[0];
                    searchedAgent = dfd.getName();
                    break;
                }
                Thread.sleep(1000);
            }
        } catch (Exception e) {
        	logConsoleError("ERROR: Cannot search the expected agent from parent " + agent.getLocalName());
        }
        return searchedAgent;
    }
  	
  	//------------------------------------------------------------------------------------------------------
  	//MARK:- COMPARE IF THE GIVEN AGENT IS PART OF THE WINNER BRANCH IN ORDER TO STOP IT 
    //------------------------------------------------------------------------------------------------------
  	public static boolean isAgentPartOfWinnerBranch(String agentName) {
  		String managerName = agentName;
  		
  		if(agentName.contains("_")) {
  			String[] components = agentName.split("_");
  			managerName = components[0];
  		}
  		
  		for(String winner : MasSetup.winners) {
  			if(managerName.equals(winner.split(" ")[0].strip())) {
  				return true;
  			}
  		}
  		return false;
  	}
  	
  	//------------------------------------------------------------------------------------------------------
  	//MARK:- GETTING BLACKBOARD INDEX BY AGENT NAME
  	//------------------------------------------------------------------------------------------------------
  	public static int getBlackboardIdx(String agentName) {
  		String[] components = agentName.split("_");
  		return Integer.parseInt(components[0].substring(1)) - 1;
  	}
  	
  	public static int getWaitingQueueTime() {
  		Random r = new Random();
  		int low = 1;
  		int high = 3;
  		return (r.nextInt(high - low) + low) * 1000;
  	}
  	
  	//------------------------------------------------------------------------------------------------------
  	//MARK:- CODE GENERATION UTILS
  	//------------------------------------------------------------------------------------------------------
  	public static String firstToLowerCase(String string) {
  		char characters[] = string.toCharArray();
  		characters[0] = Character.toLowerCase(characters[0]);
  		return new String(characters);
  	}
  	
  	public static String firstToUpperCase(String string) {
  		char characters[] = string.toCharArray();
  		characters[0] = Character.toUpperCase(characters[0]);
  		return new String(characters);
  	}
  	
  	public static boolean isIdLast(String path) {
  		char characters[] = path.toCharArray();
  		for(char c : characters) {
  			if(!Character.isDigit(c)){
  				return false;
  			}
  		}
  		return true;
  	}
  	
  	public static String getValidName(String string) {
  		char characters[] = string.toCharArray();
  		if(Character.isAlphabetic(characters[0])) {
  			characters[0] = Character.toUpperCase(characters[0]);
  			return new String(characters);
  		} else {
  			return getValidName(string.substring(1));
  		}
  	}
  	
  	public static String getCurrentDate() {
  		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");  
  		LocalDateTime now = LocalDateTime.now();  
  		return dtf.format(now);  
  	}
  	
  	public static String getError(String classname, int numOfTabs, String customError) {
  		
  		String errorInstance =  "Error error = " + ReservedWord.NEW.getValue() + " Error("; 
  		
  		if(customError == null) {
  			errorInstance = errorInstance + "e.getLocalizedMessage());\n";
  		} else {
  			errorInstance = errorInstance + "\"" + customError + "\");\n";
  		}

		for(int i = 0; i < numOfTabs; i++) {
			errorInstance = errorInstance + "\t";
		}
		errorInstance = errorInstance + ReservedWord.THIS.getValue() + "listener.on" + classname + "Failure(error);";
		return errorInstance;
  	}

	public static String getContentType(Map headers) {
		for(Object key : headers.keySet().toArray()) {
			if(((String)key).toLowerCase().contains("content-type")) {
				String k = (String)key;
				String value = (String)headers.get(key);
				headers.remove(key);
				return k + ": " + value;
			}
		}
		return null;
	}
	
	//------------------------------------------------------------------------------------------------------
  	//MARK:- MAP HEADERS 
  	//------------------------------------------------------------------------------------------------------
  	public static String mapAuthoriazation(JSONObject authJson) {
  		String auth = null;
  		String type = (String)authJson.get("type");
  		
  		if(authJson.get(type) instanceof JSONArray) {
  			JSONArray authArray = (JSONArray)authJson.get(type);
  			
  			for(Object obj : authArray) {
  				String value = (String)((JSONObject)obj).get("value");
  				auth = "Authorization: " + AgentUtils.firstToUpperCase(type) + " " + value;
  			}
  		}	
  		return auth;
  	}
  	
  	//------------------------------------------------------------------------------------------------------
  	//MARK:- MAP HEADERS 
  	//------------------------------------------------------------------------------------------------------
  	public static Map mapHeaders(JSONArray jsonHeaders) {
  		Map headers = new HashMap();
  		
  		for(Object obj : jsonHeaders) {
  			JSONObject header = (JSONObject)obj;
  			headers.put(header.get("key"), header.get("value"));
  		}
  		return headers;
  	}
  	
  	//------------------------------------------------------------------------------------------------------
  	//MARK:- MAP POST REQUEST 
  	//------------------------------------------------------------------------------------------------------
  	public static Map mapPostRequest(String agentName, JSONObject jsonBody) {
  		Map requestParams = new HashMap();
  		String mode = (String)jsonBody.get("mode");
  		
  		try {
  			if(mode.equals("urlencoded")) {
  				JSONArray urlencoded = (JSONArray)jsonBody.get("urlencoded");
  				 
  				for(Object param : urlencoded) {
  					requestParams.put(((JSONObject)param).get("key"), "String");
  				}
  			} else {
  				// RAW
  				JSONParser parser = new JSONParser();
  				JSONObject raw = (JSONObject) parser.parse((String)jsonBody.get("raw"));
  				
  				for(Object key : raw.keySet()) {
  					requestParams.put((String)key, "String");
  				}
  			}
  		} catch(ParseException e) {
  			logError(agentName, e.getLocalizedMessage());
  		}
  		return requestParams;
  	}
  
  	//------------------------------------------------------------------------------------------------------
  	//MARK:- MAP GET REQUEST
  	//------------------------------------------------------------------------------------------------------
  	public static Map mapGetRequest(Object url) {
  		Map requestParams = new HashMap();
  		
  		if(url instanceof JSONArray) {
  			JSONObject jsonUrl = (JSONObject)((JSONArray)url).get(0);
  			JSONArray params = (JSONArray)jsonUrl.get("query");
  			
  			for(Object query : params) {
  				mapDataTypeParams(requestParams, 
  								  ((JSONObject)query).get("key"), 
  								  ((JSONObject)query).get("value"));
  			}
  		} else {
  			if(((JSONObject)url).containsKey("query")) {
  				if(((JSONObject)url).get("query") instanceof JSONArray){
  					JSONArray params = (JSONArray)((JSONObject)url).get("query");
  					
  					for(Object query : params) {
  						mapDataTypeParams(requestParams, 
  								  		  ((JSONObject)query).get("key"), 
  								  		  ((JSONObject)query).get("value"));
  					}
  				} else {
  					logConsole("IS NEEDED TO HANDLE QUERY PARAMS IN A GET REQUEST AND NOT ARRAY");
  				}
  			}
  		}
  		return requestParams;
  	}

  	//------------------------------------------------------------------------------------------------------
  	//MARK:- MAP GET RESPONSE
  	//------------------------------------------------------------------------------------------------------
  	public static Map mapResponse(Object body) {
  		Map responseParams = new HashMap();
  		JSONObject jsonBody; 
  		
  		if(body instanceof JSONArray) {
  			jsonBody = (JSONObject)((JSONArray) body).get(0);
  		} else {
  			jsonBody = (JSONObject)body;
  		}
  		
  		for(Object key : jsonBody.keySet()) {
  			mapDataTypeParams(responseParams, key, jsonBody.get(key));
  		}
  		return responseParams;
  	}
  	
  	//------------------------------------------------------------------------------------------------------
  	//MARKK:- MAP DATA TYPES 
  	//------------------------------------------------------------------------------------------------------
  	public static void mapDataTypeParams(Map map, Object key, Object value) {
  		if(value == null) {
  			// SKIP IN THE PARAMS, SHOW IT IN THE FINAL REPORT
  		} else if(value instanceof ArrayList && ((ArrayList) value).size() > 0) { 
  			Object instance = ((ArrayList) value).get(0);
  			if(instance instanceof JSONObject) {
  				mapCustomClasses(key, value);
  				map.put(key, "List<" + firstToUpperCase((String)key) + ">");
	  		} else {
	  			map.put(key, "List<String>");
	  		}
  		} else if(value instanceof JSONObject) {
  			mapCustomClasses(key, value);
  			map.put(key, firstToUpperCase((String)key));
  		} else if(value instanceof String) {
  			map.put(key, "String");
  		} else if(value instanceof Double) {
  			map.put(key, "Double");
  		} else if(value instanceof Float) {
  			map.put(key, "Float");
  		} else if(value instanceof Long) {
  			map.put(key, "Long");
  		} else if(value instanceof Integer){
  			map.put(key, "Integer");
  		} else if(value instanceof Short){
  			map.put(key, "Short");
  		} else if(value instanceof Byte){
  			map.put(key, "Byte");
  		} else if(value instanceof Boolean) {
  			map.put(key, "Boolean");
  		} else {
  			// SKIP IN THE PARAMS, SHOW IT IN THE FINAL REPORT
  		}
  	}
  	
  	public static boolean isSimpleDataType(String type) {
  		String[] dataTypes = {"String", "Double", "Flaot", "Long", "Integer", "Short", "Byte", "Boolean", "List"};
  		
  		for(String t : dataTypes) {
  			if(type.contains(t)) {
  				return true;
  			}
  		}
  		return false;
  	}
  	
  	public static String getDataTypeParams(Object key, Object value) {
  		if(value instanceof String) {
  			return "String";
  		} else if(value instanceof Double) {
  			return "Double";
  		} else if(value instanceof Float) {
  			return "Float";
  		} else if(value instanceof Long) {
  			return "Long";
  		} else if(value instanceof Integer){
  			return "Integer";
  		} else if(value instanceof Short){
  			return "Short";
  		} else if(value instanceof Byte){
  			return "Byte";
  		} else if(value instanceof Boolean) {
  			return "Boolean";
  		} else {
  			for(Object k : classType.keySet().toArray()) {
  				if(((String)k).equals((String)key)) {
  					if(value instanceof JSONObject) {
  						return firstToUpperCase((String)k);
  					} else if(value instanceof ArrayList) {
  						return "List<" + firstToUpperCase((String)k) + ">";
  					} 
  				}
  			}
  			return "List<String>";
  		}
  	}
  	
  	//------------------------------------------------------------------------------------------------------
  	//MARK:- RECURSION MAP TO FIND CUSTOM CLASSES BY JSON STRUCTURE
  	//------------------------------------------------------------------------------------------------------
  	private static void mapCustomClasses(Object key, Object value) {
  		JSONObject root;
  		
  		if(value instanceof ArrayList) {
  			Object instance = ((ArrayList)value).get(0);
  			root = (JSONObject)instance;
  			classType.put(key, "List");
  		} else {
  			classType.put(key, "JSONObject");
  			root = (JSONObject)value;
  		}

  		findJsonObjects(key, root);
  	}
  	
  	private static void findJsonObjects(Object key, JSONObject json) {
  		if(!hasJson(json)) {
  			classType.put(key, "JSONObject");
  			customClasses.put(key, json);
  			return;
  		} else {
  			customClasses.put(key, json);
  			for(Object k : json.keySet()) {
  				if(json.get(k) instanceof JSONObject){
  					JSONObject obj = (JSONObject)json.get(k);
  					if(obj.size() > 0) {
	  					classType.put(k, "JSONObject");
	  					findJsonObjects(k, (JSONObject)json.get(k));
  					}
  				} else if(json.get(k) instanceof ArrayList) {
  					classType.put(k, "List");
  					ArrayList arrList = (ArrayList)json.get(k);
  					if(arrList.size() > 0) {
	  					Object arrObject = ((ArrayList)json.get(k)).get(0);
	  					if(arrObject instanceof JSONObject) {
	  						findJsonObjects(k, (JSONObject)arrObject);
	  					}
  					}
  				}
  			}
  		}
  		return;
  	}
  	
  	private static boolean hasJson(JSONObject json) {
  		for(Object key : json.keySet()) {
  			if(json.get(key) instanceof JSONObject) {
  				JSONObject obj = (JSONObject)json.get(key);
  				if(obj.size() > 0) {
  					return true;
  				}
  			} else if(json.get(key) instanceof ArrayList) {
  				ArrayList arrList = ((ArrayList)json.get(key));
  				if(arrList.size() > 0) {
	  				Object arrObject = arrList.get(0);
	  				if(arrObject instanceof JSONObject) {
	  					if(((JSONObject)arrObject).size() > 0) {
	  	  					return true;
	  	  				}
	  					return true;
	  				}
  				}
  			}
  		}
  		return false;
  	}
}
