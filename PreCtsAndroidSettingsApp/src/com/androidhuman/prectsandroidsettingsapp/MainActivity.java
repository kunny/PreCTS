package com.androidhuman.prectsandroidsettingsapp;

import android.app.Activity;
import android.content.ContentResolver;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {

	TextView tvMsg;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		tvMsg = (TextView)findViewById(R.id.tv_message);
		
		try{
			ContentResolver.setMasterSyncAutomatically(false);
			tvMsg.setText(R.string.msg_disabled_sync_settings);
		}catch(Exception e){
			tvMsg.setText(e.getMessage());
		}
		
		new FinishHandler().setActivity(this).sendEmptyMessageDelayed(0, 3000);
	}


}
