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
package com.navi.agenslib.mas.utils.settings;

import jade.util.leap.Map;
import jade.util.leap.Serializable;

/**
 * Created by van on 10/06/20.
 */
public class ServiceConfig implements Serializable {
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- PROPERTIES 
	//--------------------------------------------------------------------------------------------------------------------------------
	private static final long serialVersionUID = 1L;
	private String name;
	private String protocol;
	private String host;
	private String path;
	private boolean hasUrlQuery;
	private String port;
	private String mode;
	private String auth;
	private Map headers;
	private String requestType;
	private Map requestParams;
	private Map responseParams;
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- CONSTRUCTOR
	//--------------------------------------------------------------------------------------------------------------------------------
	public ServiceConfig(String name) { 
		this.name = name;
		this.hasUrlQuery = false;
	}

	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- ENCAPSULATION
	//--------------------------------------------------------------------------------------------------------------------------------
	public void setName(String name) { this.name = name; }
	public String getName() { return this.name; }
	
	public void setProtocol(String protocol) { this.protocol = protocol; }
	public String getProtocol() { return this.protocol; }
	
	public void setHost(String host) { this.host = host; }
	public String getHost() { return this.host; }
	
	public void setPath(String path) { this.path = path; }
	public String getPath() { return this.path; }
	
	public void hasUrlQuery(boolean hasUrlQuery) { this.hasUrlQuery = hasUrlQuery; }
	public boolean hasUrlQuery() { return this.hasUrlQuery; }
	
	public void setPort(String port) { this.port = port; }
	public String getPort() { return this.port; }
	
	public void setMode(String mode) { this.mode = mode; }
	public String getMode() { return this.mode; }

	public void setAuth(String auth) { this.auth = auth; }
	public String getAuth() { return this.auth; }
	
	public void setHeaders(Map headers) { this.headers = headers; }
	public Map getHeaders() { return this.headers; }
	
	public void setRequestType(String requestType) { this.requestType = requestType; }
	public String getRequestType() { return this.requestType; }
	
	public void setRequestParams(Map requestParams) { this.requestParams = requestParams; }
	public Map getRequestParams() { return this.requestParams; }
	
	public void setResponseParams(Map responseParams) { this.responseParams = responseParams; }
	public Map getResponseParams() { return this.responseParams; }
}
