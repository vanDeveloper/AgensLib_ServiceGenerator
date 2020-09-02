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
package com.navi.agenslib.mas.utils.codegen;

import jade.util.leap.HashMap;
import jade.util.leap.List;
import jade.util.leap.Map;
import jade.util.leap.Serializable;

/**
 * Created by van on 10/06/20.
 */
public class GeneratedCodeFile implements Serializable {

	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- AGENT PROPERTIES
	//--------------------------------------------------------------------------------------------------------------------------------
	private static final long serialVersionUID = 4L;
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- PROPERTIES
	//--------------------------------------------------------------------------------------------------------------------------------
	private int part;
	private String filename;
	private String packageName;
	private List imports;
	private String codeClassname;
	private List generatedCodes;
	private Map executionTime;
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- CONSTRUCTOR
	//--------------------------------------------------------------------------------------------------------------------------------
	public GeneratedCodeFile() { 
		this.executionTime = new HashMap(); 
		this.part = -1;
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- ENCAPSULATION
	//--------------------------------------------------------------------------------------------------------------------------------
	public void setPart(int part) { this.part = part; }
	public int getPart() { return this.part; }
	
	public void setFilename(String filename) { this.filename = filename; }
	public String getFilename() { return this.filename; }
	
	public void setPackageName(String packageName) { this.packageName = packageName; }
	public String getPackageName() { return this.packageName; }
	
	public void setImports(List imports) { this.imports = imports; }
	public List getImports() { return this.imports; }
	
	public void setCodeClassname(String codeClassname) { this.codeClassname = codeClassname; }
	public String getCodeClassanme() { return this.codeClassname; }
	
	public void setGeneratedCodes(List generatedCodes) { this.generatedCodes = generatedCodes; }
	public List getGeneratedCodes() { return this.generatedCodes; }
	public void addGeneratedCodes(List generatedCodes) { 
		for(int i = 0; i < generatedCodes.size(); i++) {
			this.generatedCodes.add(generatedCodes.get(i));
		}
	}
	
	public void addEndClassMarker() { this.generatedCodes.add("}"); }
	public void removeEndClassMarker() {
		for(int i = 0; i < this.generatedCodes.size(); i++) {
			if(this.generatedCodes.get(i) instanceof String) {
				this.generatedCodes.remove(i);
				break;
			}
		}
	}
	public void addExecutionTime(Map map) {
		for(Object key : map.keySet().toArray()) {
			this.executionTime.put(key, map.get(key));
		}
	}
	public void addExecutionTime(String agentName, long executionTime) { this.executionTime.put(agentName, executionTime); }
	public Map getExecutionTime() { return this.executionTime; }
	
}
