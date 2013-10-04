package com.androidhuman.ctsprepare.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class Model {
	
	private String modelName;
	private Map<Task, ArrayList<Event>> tasks;
	
	public Model(){
		tasks = new LinkedHashMap<Task, ArrayList<Event>>();
		
		// Default options
		tasks.put(new Task(Task.ACTIVATE_DEV_ADMIN), null);
		tasks.put(new Task(Task.CONFIGURE_SCR_TIMEOUT), null);
		tasks.put(new Task(Task.CONFIGURE_WIFI), null);
	}
	
	public Model(String model){
		this();
		setModel(model);
	}
	
	public void setModel(String model){
		this.modelName = model;
		File file = new File(String.format("models/%s.cat", modelName));
		if(file.exists()){
			loadModel(modelName);
		}else{
			saveAsFile();
		}
	}
	
	public void setTask(Task key, ArrayList<Event> events){
		tasks.remove(key);
		tasks.put(key, events);
	}
	
	public void loadModel(String model){
		// Load task data for model
		try{
			tasks.clear();
			File file = new File(String.format("models/%s.cat", model));
			InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
			
			Object ob = JSONValue.parse(reader);
			if(ob!=null){
				// tasks arr
				JSONObject jsonObj = (JSONObject)ob;
				JSONArray  arr = (JSONArray)jsonObj.get("tasks");
				int taskCount = arr.size();
				for(int i=0; i<taskCount; i++){
					// type, events
					JSONObject obj = (JSONObject)arr.get(i);
					
					Task task = new Task(((Long)obj.get("task")).intValue());
					
					JSONArray eventArray = (JSONArray)obj.get("events");
					int eventCnt = eventArray.size();
					ArrayList<Event> eventList = new ArrayList<Event>();
					
					// Fetch event list from JSON and add it to event list
					for(int j=0; j<eventCnt; j++){
						Event event = Event.fromJson((JSONObject)eventArray.get(j));
						eventList.add(event);
					}
					
					// Put data into map
					tasks.put(task, eventList);
				}
			}
			
			reader.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void saveAsFile(){
		try{
			File file = new File(String.format("models/%s.cat", modelName));
			file.createNewFile();
			
			Set<Entry<Task, ArrayList<Event>>> entries = tasks.entrySet();
			Iterator<Entry<Task, ArrayList<Event>>> it = entries.iterator();
			Entry<Task, ArrayList<Event>> entry = null;
			
			JSONObject to = new JSONObject();
			JSONArray tarr = new JSONArray();
			while(it.hasNext()){
				entry = it.next();
				JSONObject obj = new JSONObject();
				obj.put("task", entry.getKey().type);
				
				ArrayList<Event> list = entry.getValue();
				
				JSONArray arr = new JSONArray();
				if(list!=null){
					for(Event event : list){
						JSONObject o = new JSONObject();
						o.put("type", event.getType());
						o.put("value", event.getText());
						arr.add(o);
					}
				}
				obj.put("events", arr);
				
				tarr.add(obj);
			}
			to.put("tasks", tarr);
			
			FileWriter writer = new FileWriter(file);
			writer.write(to.toJSONString());
			writer.flush();
			writer.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public Map<Task, ArrayList<Event>> getTasks(){
		return tasks;
	}

}
