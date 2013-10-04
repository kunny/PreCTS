package com.androidhuman.ctsprepare.data;

import org.json.simple.JSONObject;


public class Event {
	
	public static class Type{
		public static final int KEYSTROKE = 0;
		public static final int TEXT = 1;
		public static final int DELAY = 2;
		public static final int LAUNCH = 3;
	}
	
	@Override
	public String toString(){
		switch(type){
		case Type.KEYSTROKE:
			return "Key";
		case Type.TEXT:
			return "Text";
		case Type.DELAY:
			return "Delay";
		case Type.LAUNCH:
			return "Launch";
		default:
			throw new IllegalArgumentException();
		}
	}
	
	private int type = -1;
	private String data;
	
	private Event(){
		
	}
	
	public static Event fromJson(JSONObject obj){
		Event event = new Event();
		event.type = Long.valueOf((Long)obj.get("type")).intValue();
		event.data = (String)obj.get("value");
		return event;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject toJson(){
		JSONObject obj = new JSONObject();
		obj.put("type", type);
		obj.put("value", data);
		return obj;
	}
	
	public static Event newTextEvent(String text){
		Event event = new Event();
		event.type = Type.TEXT;
		event.data = text;
		return event;
	}
	
	public static Event newKeyStrokeEvent(int keyCode){
		Event event = new Event();
		event.type = Type.KEYSTROKE;
		event.data = Integer.toString(keyCode);
		return event;
	}
	
	public static Event newHoldEvent(int msToDelay){
		Event event = new Event();
		event.type = Type.DELAY;
		event.data = Integer.toString(msToDelay);
		return event;
	}
	
	public static Event newLaunchEvent(String adbCommand){
		Event event = new Event();
		event.type = Type.LAUNCH;
		event.data = String.format("am start %s", adbCommand);
		return event;
	}
	
	public int getType(){
		return type;
	}
	
	public String getText(){
		return data;
	}
	
	public int getKeyCode(){
		return Integer.parseInt(data);
	}

}
