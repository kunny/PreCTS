package com.androidhuman.ctsprepare.util;

import java.util.ArrayList;

import com.androidhuman.ctsprepare.data.BasicDeviceInfo;
import com.androidhuman.ctsprepare.data.Task;

public class Worker {
	private int currentTaskIdx = -1;
	private ArrayList<Task> tasksToRun = null;
	
	private BasicDeviceInfo deviceInfo = null;
	
	public Worker(){
		tasksToRun = new ArrayList<Task>();
	}

}
