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

import jade.util.leap.Serializable;

/**
 * Created by van on 10/06/20.
 */
public enum BlackboardTasks implements Serializable {
	GENERATE_INTERACTOR_SUCCESS_METHOD,
	GENERATE_INTERACTOR_FAILURE_METHOD,
	
	GENERATE_DAO_METHOD,
	
	GENERATE_DAOIMPL_PARAMS,
	GENERATE_DAOIMPL_INTERFACE_METHOD,
	GENERATE_DAOIMPL_CALLBACK_RESPONSE_METHOD,
	GENERATE_DAOIMPL_CALLBACK_FAILURE_METHOD,
	
	GENERATE_REQUEST_PARAMS,
	GENERATE_REQUEST_CONSTRUCTOR,
	
	GENERATE_RESPONSE_PARAMS,
	GENERATE_RESPONSE_CONSTRUCTOR,
	GENERATE_RESPONSE_ENCAPSULATION,
	
	CONFIGURE_SERVICE_ARCHITECTURE,
	CONFIGURE_SERVICE_BACKEND,
	
	CREATE_SERVICE_REQUEST_FILE,
	CREATE_SERVICE_CONFIG_FILE,
	
	GENERATE_SERVICE_REQUEST_PARAMS,
	GENERATE_SERVICE_REQUEST_CONSTRUCTOR,
	GENERATE_SERVICE_REQUEST_SETUP,
	GENERATE_SERVICE_REQUEST_ENCAPSULATION,
	
	GENERATE_SERVICE_CONFIG_SINGLETON,
	GENERATE_SERVICE_CONFIG_PARAMS,
	GENERATE_SERVICE_CONFIG_SETUP,
	GENERATE_SERVICE_CONFIG_ENCAPSULATION, 
	
	CREATE_SERVICE_API_FILE,
	CREATE_SERVICE_ERROR_FILE,
	CREATE_SERVICE_BACKEND_FILE,
	
	GENERATE_SERVICE_API_METHODS,
	
	GENERATE_SERVICE_ERROR_PARAMS, 
	GENERATE_SERVICE_ERROR_CONSTRUCTOR,
	GENERATE_SERVICE_ERROR_ENCAPSULATION,
	GENERATE_SERVICE_ERROR_TOSTRING,
	
	GENERATE_SERVICE_BACKEND_PARAMS,
	GENERATE_SERVICE_BACKEND_CONSTRUCTOR,
	GENERATE_SERVICE_BACKEND_ENCAPSULATION
}
