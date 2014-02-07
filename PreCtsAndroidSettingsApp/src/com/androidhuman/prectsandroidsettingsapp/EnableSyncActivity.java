package com.androidhuman.prectsandroidsettingsapp;

import android.app.Activity;
import android.content.ContentResolver;
import android.os.Bundle;
import android.widget.TextView;

public class EnableSyncActivity extends Activity {

	TextView tvMsg;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enable_sync);

		tvMsg = (TextView)findViewById(R.id.tv_message);
		
		try{
			ContentResolver.setMasterSyncAutomatically(true);
		}catch(Exception e){
			tvMsg.setText(e.getMessage());
		}
		
		new FinishHandler().setActivity(this).sendEmptyMessageDelayed(0, 3000);
	}

}
