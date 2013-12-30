package com.androidhuman.ctsprepare.automator;

import org.json.JSONException;
import org.json.JSONObject;

public class GoogleAccount {
	public String email;
	public String password;
	
	public static GoogleAccount fromJson(String json) throws JSONException{
		JSONObject obj = new JSONObject(json);
		GoogleAccount account = new GoogleAccount();
		
		account.email = obj.getString("email");
		account.password = obj.getString("password");
		
		if(account.email==null || account.password==null){
			throw new IllegalStateException("Could not load the account informaion.");
		}
		
		return account;
	}
	
	public String toJson() throws JSONException{
		// Check all instance is not null
		if(this.email==null || this.password==null){
			throw new IllegalStateException("Invalid Account information");
		}
		JSONObject obj = new JSONObject();
		obj.put("email", this.email);
		obj.put("password", this.password);
		return obj.toString();
	}
}
