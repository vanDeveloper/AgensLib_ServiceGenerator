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

/**
 * Created by van on 10/06/20.
 */
public class BlackboardTask {

	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- PROPERTIES
	//--------------------------------------------------------------------------------------------------------------------------------
	private int id;
	private BlackboardTasks task;
	private BlackboardTaskState state;
	private BlackboardRole role;
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- CONSTRUCTOR
	//--------------------------------------------------------------------------------------------------------------------------------
	public BlackboardTask(int id, BlackboardTasks task, BlackboardRole role) {
		this.id = id;
		this.task = task;
		this.role = role;
		this.state = BlackboardTaskState.PENDING;
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- ENCAPSULATION
	//--------------------------------------------------------------------------------------------------------------------------------
	public int getId() { return this.id; }
	
	public BlackboardTasks getType() { return this.task; }
	
	public void setState(BlackboardTaskState state) { this.state = state; }
	public BlackboardTaskState getState() { return this.state; }

	public BlackboardRole getRole() { return this.role; }
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- PRETTY PRINT
	//--------------------------------------------------------------------------------------------------------------------------------
	@Override 
	public String toString() {
		return "Id: " + this.id + 
				", Task: " + this.task.toString() + 
				", Role: " + this.role.toString() + 
				", State: " + this.state.toString() + "\n";
	}
	
}

