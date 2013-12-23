package com.androidhuman.ctsprepare.automator;

import com.android.uiautomator.core.UiDevice;
import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

public class PreCTSAutomate extends UiAutomatorTestCase{

	public void testActivateDeviceAdmin() throws UiObjectNotFoundException {
		// Get installed device admin listview
		UiScrollable deviceAdminList = 
				new UiScrollable(new UiSelector().className("android.widget.ListView"));
		
		// First device admin receiver
		UiObject receiver1 = 
				deviceAdminList.getChildByText(
						new UiSelector().className("android.widget.LinearLayout"), 
						"android.deviceadmin.cts.CtsDeviceAdminReceiver");
		UiObject checkbox1 = receiver1.getChild(new UiSelector().className("android.widget.CheckBox"));
		// If not checked
		if(!checkbox1.isChecked()){
			receiver1.click();
			(new UiObject(new UiSelector().text("Activate"))).clickAndWaitForNewWindow();
		}
		
		// Second device admin receiver
		UiObject receiver2 = 
				deviceAdminList.getChildByText(
						new UiSelector().className("android.widget.LinearLayout"), 
						"android.deviceadmin.cts.CtsDeviceAdminReceiver2");
		UiObject checkbox2 = receiver2.getChild(new UiSelector().className("android.widget.CheckBox"));
		// If not checked
		if(!checkbox2.isChecked()){
			receiver2.click();
			(new UiObject(new UiSelector().text("Activate"))).clickAndWaitForNewWindow();
		}
		
		UiDevice.getInstance().pressHome();
	}
	
	public void testActivateWifi() throws UiObjectNotFoundException {
		UiObject wifiSwitch = new UiObject(new UiSelector().className("android.widget.Switch"));
		if(!wifiSwitch.isChecked()){
			wifiSwitch.clickAndWaitForNewWindow();
		}
		
		UiScrollable wifiList = new UiScrollable(new UiSelector().className("android.widget.ListView"));
		UiObject ap = null;
		while(true){
			try{
				// Select AP
				ap = wifiList.getChildByText(new UiSelector().className("android.widget.LinearLayout"), "AndroidNet");
				break;
			}catch(UiObjectNotFoundException e){
				UiDevice.getInstance().waitForWindowUpdate("com.android.settings", 1500);
			}
		}
		ap.clickAndWaitForNewWindow();
		
		// If input method dialog popup showed up
		try{
			UiObject okButton = new UiObject(new UiSelector().textMatches("OK"));
			okButton.clickAndWaitForNewWindow(1000);
		}catch(UiObjectNotFoundException e){
			// No dialog showed up
		}
		
		// If swype instruction dialog popup showed up
		try{
			UiObject okButton = new UiObject(new UiSelector().textMatches("Dismiss"));
			okButton.clickAndWaitForNewWindow(1000);
		}catch(UiObjectNotFoundException e){
			// No dialog showed up
		}
		
		UiObject editText = new UiObject(new UiSelector().className("android.widget.EditText"));
		editText.setText("welcomegsm!"); // Enter password
		
		UiObject connectButton = new UiObject(new UiSelector().textMatches("Connect"));
		connectButton.clickAndWaitForNewWindow();
		
		UiDevice.getInstance().pressHome();
	}
	
	public void testSetTimeout() throws UiObjectNotFoundException{
		UiScrollable settingsList = new UiScrollable(new UiSelector().className("android.widget.ListView"));
		UiObject timeout = settingsList.getChildByText(new UiSelector().className("android.widget.LinearLayout"), "Screen timeout");
		// Select display timeout
		timeout.clickAndWaitForNewWindow();
		
		// Select '10 minutes' for timeout
		UiObject timeoutItem = new UiObject(new UiSelector().textContains("10 min"));
		timeoutItem.clickAndWaitForNewWindow();
		
		UiDevice.getInstance().pressHome();
	}
}
