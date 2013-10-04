package com.androidhuman.ctsprepare.util;

import java.util.ArrayList;

import org.eclipse.swt.widgets.Display;

import com.androidhuman.ctsprepare.data.BasicDeviceInfo;
import com.androidhuman.ctsprepare.util.AdbCommand.AdbCommandException;
import com.androidhuman.ctsprepare.util.AdbCommand.AdbCommandResultListener;

public class AdbWrapper{
	
	private Display disp;
	
	public AdbWrapper(){
		disp = Display.getDefault();
	}
	
	public AdbWrapper(Display display){
		disp = display;
	}
	
	public void sendKeyCode(int code){
		new AdbCommand(disp).executeAsync("shell input keyevent "+code, new AdbCommandResultListener(){

			@Override
			public void onPreExecute() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onError(String errmsg) {
				System.out.println(errmsg);
			}

			@Override
			public void onOutput(String line) {
				System.out.println(line);
			}

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onFinished() {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	
	public void sendKeyCode(String deviceSerial, int code){
		new AdbCommand(disp).executeAsync(String.format("-s %s shell input keyevent %s", deviceSerial, code), new AdbCommandResultListener(){

			@Override
			public void onPreExecute() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onError(String errmsg) {
				System.out.println(errmsg);
			}

			@Override
			public void onOutput(String line) {
				System.out.println(line);
			}

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onFinished() {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	
	public void sendText(String text){
		new AdbCommand(disp).executeAsync(String.format("shell input text %s", text), new AdbCommandResultListener(){

			@Override
			public void onPreExecute() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onError(String errmsg) {
				System.out.println(errmsg);
			}

			@Override
			public void onOutput(String line) {
				System.out.println(line);
			}

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onFinished() {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	
	public void sendText(String deviceSerial, String text){
		new AdbCommand(disp).executeAsync(String.format("-s %s shell input text %s", deviceSerial, text), new AdbCommandResultListener(){

			@Override
			public void onPreExecute() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onError(String errmsg) {
				System.out.println(errmsg);
			}

			@Override
			public void onOutput(String line) {
				System.out.println(line);
			}

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onFinished() {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	
	public void getAttachedDevices(final ResultListener<ArrayList<BasicDeviceInfo>> listener) throws AdbCommandException{
		final ArrayList<BasicDeviceInfo> list = new ArrayList<BasicDeviceInfo>();
		
		new AdbCommand(disp, new AdbCommandResultListener(){	

			@Override
			public void onError(String errmsg) {
				listener.onFailed(errmsg);
			}

			@Override
			public void onOutput(String line) {
				if(line.contains("List of devices") || line.length()==0){
					return;
				}
				String[] elems = line.split("\t");
				BasicDeviceInfo info = new BasicDeviceInfo();
				info.serial = elems[0];
				
				// Get device serial number
				list.add(info);
			}

			@Override
			public void onSuccess() {
				for(final BasicDeviceInfo info : list){
					new AdbCommand(disp).execute(
							String.format("-s %s shell getprop", info.serial), 
							new AdbCommandResultListener(){

								@Override
								public void onError(String errmsg) {
									listener.onFailed(errmsg);
								}

								@Override
								public void onOutput(String line) {
									if(line.contains("ro.product.model")){
										int splitStartIdx = line.lastIndexOf(": [");
										info.model = line.substring(splitStartIdx+3, line.length()-1);
									}else if(line.contains("ro.build.version.release")){
										int splitStartIdx = line.lastIndexOf(": [");
										info.version = line.substring(splitStartIdx+3, line.length()-1);
									}
								}

								@Override
								public void onSuccess() {
									
								}

								@Override
								public void onFinished() {
								}

								@Override
								public void onPreExecute() {
									// TODO Auto-generated method stub
									
								}
					});
				}
				// Pass result to main window
				listener.onResult(list);
			}

			@Override
			public void onFinished() {
				listener.onPostExecute();
			}

			@Override
			public void onPreExecute() {
				listener.onPreExecute();
			}
			
		}).executeAsync("devices");
	
	}
	
	public static class KeyCode{
		// KeyCode reference : http://developer.android.com/reference/android/view/KeyEvent.html
		public static final int MENU = 82;
		public static final int HOME = 3;
		public static final int BACK = 4;
		public static final int UP = 19;
		public static final int DOWN = 20;
		public static final int LEFT = 21;
		public static final int RIGHT = 22;
		public static final int OK = 23;
		
		public static String toString(int keyCode){
			switch(keyCode){
			case MENU:
				return "Menu";
			case HOME:
				return "Home";
			case BACK:
				return "Back";
			case UP:
				return "Up";
			case DOWN:
				return "Down";
			case LEFT:
				return "Left";
			case RIGHT:
				return "Right";
			case OK:
				return "OK";
			default:
				throw new IllegalArgumentException();
			}
		}
	}
	
	public interface ResultListener<T>{
		public void onPreExecute();
		public void onResult(T result);
		public void onFailed(String msg);
		public void onPostExecute();
	}
	
}
