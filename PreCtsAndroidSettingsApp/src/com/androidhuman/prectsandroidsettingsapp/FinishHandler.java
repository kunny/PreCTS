package com.androidhuman.prectsandroidsettingsapp;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

public class FinishHandler extends Handler {
	private Activity activity;
	
	public FinishHandler setActivity(Activity activity){
		this.activity = activity;
		return this;
	}

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		activity.finish();
	}
	
	
}
