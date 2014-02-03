package com.androidhuman.ctsprepare.data;

public final class Task {
	public static final int COPY_MEDIA = 0xff000;
	public static final int INSTALL_CTS_DEV_ADMIN = 0xff001;
	public static final int ACTIVATE_DEV_ADMIN = 0xff002;
	public static final int CONFIGURE_WIFI = 0xff003;
	public static final int CONFIGURE_SCR_TIMEOUT = 0xff004;
	public static final int SET_GOOGLE_ACCOUNT = 0xff005;
	public static final int SET_INTERNET_AS_DEFAULT = 0xff006;
	public static final int DISABLE_MASTER_SYNC = 0xff007;
	
	public int type = -1; // Not assigned by default
	
	public Task(){
		
	}
	
	public Task(int type){
		this.type = type;
	}
	
	@Override
	public String toString(){
		switch(type){
		case COPY_MEDIA:
			return "Copy media files";
		case INSTALL_CTS_DEV_ADMIN:
			return "Install CtsDeviceAdmin.apk";
		case ACTIVATE_DEV_ADMIN:
			return "Activate CTS Device admin";
		case CONFIGURE_WIFI:
			return "Configure Wi-Fi";
		case CONFIGURE_SCR_TIMEOUT:
			return "Configure screen timeout";
		case SET_GOOGLE_ACCOUNT:
			return "Set Google Account";
		case SET_INTERNET_AS_DEFAULT:
			return "Set Internet as default";
		case DISABLE_MASTER_SYNC:
			return "Disabling Sync setting";
		default:
			throw new IllegalArgumentException("Unknown task type");
		}
	}

	@Override
	public boolean equals(Object arg) {
		try{
			Task aTask = (Task)arg;
			if(this.type==aTask.type){
				return true;
			}else{
				return false;
			}
		}catch(ClassCastException e){
			return false;
		}
	}
	
	
}
