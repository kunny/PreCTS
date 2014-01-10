package com.androidhuman.ctsprepare.data;


public class BasicDeviceInfo {

	public String serial;
	public String model;
	public String version;
	
	@Override
	public String toString() {
		return "BasicDeviceInfo [serial=" + serial + ", model=" + model + "]";
	}
	
	/**
	 * To support Configuration class while running automation,
	 * minimum 4.3 version is required. (API Level 18)
	 * @return
	 */
	public boolean isAutomationSupported(){
		Float version = Float.valueOf(this.version);
		return version >= 4.3f ? true : false;
	}

}
