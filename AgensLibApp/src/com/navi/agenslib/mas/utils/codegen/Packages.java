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

/**
 * Created by van on 10/06/20.
 */
public enum Packages {

	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- PACKAGES USED
	//--------------------------------------------------------------------------------------------------------------------------------
	APP_NET_SERVICES(".network.services"),
	APP_NET_ERROR(".network.error"),
	APP_NET_BACKEND(".network.backend"),
	JAVA_UTIL_ARRAYLIST("java.util.List;\n\n"),
	ENTITY_MODEL(".entities"),
	ANDROID_CONTEXT("android.content.Context;"),
	ANDROID_NONNULL("androidx.annotation.NonNull;"),
	ANDROID_NULLABLE("androidx.annotation.Nullable;"),
	GOOGLE_GSON("com.google.gson.Gson;"),
	GOOGLE_GSON_BUILDER("com.google.gson.GsonBuilder;"),
	JSON_OBJECT("org.json.JSONObject;"),
	RETROFIT2_BASE("retrofit2.Retrofit;"),
	RETROFIT2_GSON_FACTORY("retrofit2.converter.gson.GsonConverterFactory;"),
	RETROFIT2_CALL("retrofit2.Call;"),
	RETROFIT2_CALLBACK("retrofit2.Callback;"),
	RETROFIT2_RESPONSE("retrofit2.Response;"),
	RETROFIT2_BODY("retrofit2.http.Body;"),
	RETROFIT2_FIELD("retrofit2.http.Field;"),
	RETROFIT2_HEADER("retrofit2.http.Header;"),
	RETROFIT2_HEADERS("retrofit2.http.Headers;"),
	RETROFIT2_QUERY("retrofit2.http.Query;"),
	RETROFIT2_PATH("retrofit2.http.Path;"),
	RETROFIT2_FORM("retrofit2.http.FormUrlEncoded;"),
	RETROFIT2_GET("retrofit2.http.GET;"),
	RETROFIT2_POST("retrofit2.http.POST;"),
	RETROFIT2_PUT("retrofit2.http.PUT;"),
	RETROFIT2_PATCH("retrofit2.http.PATCH;"),
	RETROFIT2_DELETE("retrofit2.http.DELETE;");
	
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- PROPERTIES
	//--------------------------------------------------------------------------------------------------------------------------------
	private String packages;
			
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- CONSTRUCTOR
	//--------------------------------------------------------------------------------------------------------------------------------
	Packages(String packages) {
		this.packages = packages;
	}
			
	//--------------------------------------------------------------------------------------------------------------------------------
	//MARK:- ENCAPSULATION
	//--------------------------------------------------------------------------------------------------------------------------------
	public String getValue() {
		return packages;
	}
}
