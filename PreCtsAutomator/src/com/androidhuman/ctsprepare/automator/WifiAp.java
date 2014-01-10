package com.androidhuman.ctsprepare.automator;

import org.json.JSONException;
import org.json.JSONObject;

public class WifiAp {
	public String apName;
	public String password;
	
	public static WifiAp fromJson(String json) throws JSONException{
		JSONObject obj = new JSONObject(json);
		WifiAp account = new WifiAp();
		
		account.apName = obj.getString("apName");
		account.password = obj.getString("password");
		
		if(account.apName==null || account.password==null){
			throw new IllegalStateException("Could not load WiFi AP informaion.");
		}
		
		return account;
	}
	
	public String toJson() throws JSONException{
		// Check all instance is not null
		if(this.apName==null || this.password==null){
			throw new IllegalStateException("Invalid AP information");
		}
		JSONObject obj = new JSONObject();
		obj.put("apName", this.apName);
		obj.put("password", this.password);
		return obj.toString();
	}
}
