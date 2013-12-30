package com.androidhuman.ctsprepare.data;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class GoogleAccount {
	public String email;
	public String password;
	
	public static GoogleAccount fromJson(String json){
		JSONObject obj = (JSONObject)JSONValue.parse(json);
		GoogleAccount account = new GoogleAccount();
		
		account.email = (String)obj.get("email");
		account.password = (String)obj.get("password");
		
		if(account.email==null || account.password==null){
			throw new IllegalStateException("Could not load the account informaion.");
		}
		
		return account;
	}
	
	@SuppressWarnings("unchecked")
	public String toJson(){
		// Check all instance is not null
		if(this.email==null || this.password==null){
			throw new IllegalStateException("Invalid Account information");
		}
		JSONObject obj = new JSONObject();
		obj.put("email", this.email);
		obj.put("password", this.password);
		return obj.toJSONString();
	}
}
