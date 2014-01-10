package com.androidhuman.ctsprepare.data;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class WifiAp {
	public String apName;
	public String password;
	
	public static WifiAp fromJson(String json){
		JSONObject obj = (JSONObject)JSONValue.parse(json);
		WifiAp account = new WifiAp();
		
		account.apName = (String)obj.get("apName");
		account.password = (String)obj.get("password");
		
		if(account.apName==null || account.password==null){
			throw new IllegalStateException("Could not load WiFi AP informaion.");
		}
		
		return account;
	}
	
	@SuppressWarnings("unchecked")
	public String toJson(){
		// Check all instance is not null
		if(this.apName==null || this.password==null){
			throw new IllegalStateException("Invalid AP information");
		}
		JSONObject obj = new JSONObject();
		obj.put("apName", this.apName);
		obj.put("password", this.password);
		return obj.toJSONString();
	}
}
