package com.androidhuman.ctsprepare.data;

import java.util.regex.Pattern;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class ProxySettings {
	public String ip;
	public String port;
	
	public static final String IPV4_REGEX = "\\A(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}\\z";
	
	public static ProxySettings fromJson(String json){
		JSONObject obj = (JSONObject)JSONValue.parse(json);
		ProxySettings proxy = new ProxySettings();
		
		proxy.ip = (String)obj.get("ip");
		proxy.port = (String)obj.get("port");
		
		if(proxy.ip==null || proxy.port==null){
			return null;
		}
		
		return proxy;
	}
	
	@SuppressWarnings("unchecked")
	public String toJson(){
		// Check all instance is not null
		if(this.ip==null || this.port==null){
			throw new IllegalStateException("Invalid Proxy information");
		}
		JSONObject obj = new JSONObject();
		obj.put("ip", this.ip);
		obj.put("port", this.port);
		return obj.toJSONString();
	}
	
	public boolean isValid(){
		if(!Pattern.matches(IPV4_REGEX, this.ip)){
			return false;
		}
		try{
			int port = Integer.parseInt(this.port);
			if(port> 65535 || port < 0){
				return false;
			}
		}catch(NumberFormatException e){
			return false;
		}
		return true;
	}
	
	

}
