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
package com.navi.agenslib.mas.utils.blackboard;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by van on 10/06/20.
 */
public class Blackboard implements BlackboardInterface {

	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- PROPERTIES
	//--------------------------------------------------------------------------------------------------------------------------------
	private ConcurrentHashMap<Integer, BlackboardTask> tasks;
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- CONSTRUCTOR
	//--------------------------------------------------------------------------------------------------------------------------------
	public Blackboard() {
		this.tasks = new ConcurrentHashMap<>();
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- BLACKBOARD INTERFACE PROTOCOLS
	//--------------------------------------------------------------------------------------------------------------------------------
	@Override
	public void writeTask(BlackboardTask task) {
		tasks.put(task.getId(), task);
	}
	
	@Override
	public boolean removeTask(int id) {
		for (Map.Entry<Integer, BlackboardTask> entry : tasks.entrySet()) {
			if(entry.getValue().getId() == id) {
				return tasks.remove(entry.getKey(), entry.getValue());
			}
		}
		return false;
	}
	
	@Override
	public BlackboardTask getTask(BlackboardRole role) {
		for (Map.Entry<Integer, BlackboardTask> entry : tasks.entrySet()) {
			if(entry.getValue().getRole().equals(role) && 
					!entry.getValue().getState().equals(BlackboardTaskState.IN_PROCESS) && 
					!entry.getValue().getState().equals(BlackboardTaskState.FINISHED)) {
				return entry.getValue();
			}
		}
		return null;
	}
	
	@Override
	public BlackboardTask getTaskInProcess(BlackboardRole role) {
		for (Map.Entry<Integer, BlackboardTask> entry : tasks.entrySet()) {
			if(entry.getValue().getRole().equals(role) && 
					entry.getValue().getState().equals(BlackboardTaskState.IN_PROCESS)) {
				return entry.getValue();
			}
		}
		return null;
	}

	@Override
	public BlackboardTaskState getTaskState(BlackboardTask task) {
		for (Map.Entry<Integer, BlackboardTask> entry : tasks.entrySet()) {
			if(task.equals(entry.getValue())) {
				return entry.getValue().getState();
			}
		}
		return null;
	}	
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- GETTING NEXT TASK ID
	//--------------------------------------------------------------------------------------------------------------------------------
	public int getId() { 
		if(this.tasks.size() == 0) {
			return this.tasks.size(); 
		} else {
			int id = 0;
			for (Map.Entry<Integer, BlackboardTask> entry : tasks.entrySet()) {
				if(entry.getValue().getId() > id) {
					id = entry.getValue().getId();
				}
			}
			return id + 1;
		}
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- PRETTY PRINT
	//--------------------------------------------------------------------------------------------------------------------------------
	public String toString() { 
		String str = "\n";
		for (Map.Entry<Integer, BlackboardTask> entry : tasks.entrySet()) {
			str = str + entry.getValue().toString();
		}
		return str;
	}

}
