package com.androidhuman.ctsprepare.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.swt.widgets.Display;

public class AdbCommand {
	private static final boolean D = true;
	
	private boolean shouldContinue = false;
	private AdbCommandResultListener mListener;
	private Display display;
	private String line;
	
	public AdbCommand(){
		this.display = Display.getDefault();
	}
	
	public AdbCommand(Display display){
		this.display = display;
	}
	
	public AdbCommand(Display disp, AdbCommandResultListener listener){
		this(disp);
		setResultListener(listener);
	}
	
	public synchronized void stopReading(){
		shouldContinue = false;
	}
	
	public synchronized void execute(String adbCommand, AdbCommandResultListener listener){
		this.mListener = listener;
		execute(adbCommand);
	}
	
	public synchronized boolean executeSimple(String adbCommand){
		shouldContinue = true;
		boolean result = true;
		
		Process proc = null;
		try{
			if(D){System.out.println("Executing adb command : "+adbCommand);}
			String[] cmds = adbCommand.split(" ");
			String[] c = new String[cmds.length+1];
			c[0] = String.format("%s/platform-tools/adb", Utils.getAdbPath());
			for(int i=0; i<cmds.length; i++){
				c[i+1] = cmds[i];
			}
			proc = new ProcessBuilder(c).redirectErrorStream(true).start();
			
			if(D){System.out.println("Checking for error..");}
			
			InputStream errStream = proc.getErrorStream();
			BufferedReader errReader = new BufferedReader(new InputStreamReader(errStream));
			
			final String errmsg = errReader.readLine();
			if(errmsg!=null){
				throw new IOException();				
			}else{
				errReader.close();
				errStream.close();
			}
			
			if(D){System.out.println("Preparing result output..");}
		
			BufferedReader inReader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			
			if(D){System.out.println("Preparing reader..");}
			
			while(shouldContinue && (line=inReader.readLine())!=null){
				if(line.contains("error:") || line.contains("not running")){
					throw new IllegalStateException(line);
				}
				
				if(mListener!=null){
					mListener.onOutput(line);
				}
				
			}
			inReader.close();
			
			if(D){System.out.println("Command executed successfully.");}
			
		}catch(final IOException e){
			result = false;
		}finally{
			if(proc!=null){
				proc.destroy();
			}
		}
		return result;
	}
	
	public synchronized void execute(String adbCommand){
		shouldContinue = true;
		display.syncExec(new Runnable(){
			public void run(){
				if(mListener!=null){
					mListener.onPreExecute();
				}
			}
		});
		
		Process proc = null;
		try{
			if(D){System.out.println("Executing adb command : "+adbCommand);}
			
			String[] cmds = adbCommand.split(" ");
			String[] c = new String[cmds.length+1];
			c[0] = String.format("%s/platform-tools/adb", Utils.getAdbPath());
			for(int i=0; i<cmds.length; i++){
				c[i+1] = cmds[i];
			}
			proc = new ProcessBuilder(c).redirectErrorStream(true).start();
			
			if(D){System.out.println("Checking for error..");}
			InputStream errStream = proc.getErrorStream();
			BufferedReader errReader = new BufferedReader(new InputStreamReader(errStream));
			
			final String errmsg = errReader.readLine();
			if(errmsg!=null){
				display.syncExec(new Runnable(){
					public void run(){
						if(mListener!=null){
							mListener.onError(errmsg);
						}
					}
				});
				
				return;
			}else{
				errReader.close();
				errStream.close();
			}
			
			if(D){System.out.println("Preparing result output..");}
		
			BufferedReader inReader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			
			if(D){System.out.println("Preparing reader..");}
			
			while(shouldContinue && (line=inReader.readLine())!=null){
				if(line.contains("error:") || line.contains("not running") || line.contains("offline")){
					throw new IllegalStateException(line);
				}
				
				if(mListener!=null){
					mListener.onOutput(line);
				}
				
			}
			inReader.close();
			display.syncExec(new Runnable(){
				public void run(){
					if(mListener!=null){
						mListener.onSuccess();
					}
				}
			});
			
			if(D){System.out.println("Command executed successfully.");}
			
		}catch(final IOException e){
			display.syncExec(new Runnable(){
				public void run(){
					if(mListener!=null){
						mListener.onError(e.getMessage());
					}
				}
			});
			
		}catch(final IllegalStateException e){
			display.syncExec(new Runnable(){
				public void run(){
					if(mListener!=null){
						mListener.onError(e.getMessage());
					}
				}
			});
			
		}finally{
			if(proc!=null){
				proc.destroy();
			}
		}
		display.syncExec(new Runnable(){
			public void run(){
				if(mListener!=null){
					mListener.onFinished();
				}
			}
		});
	}
	
	public synchronized void executeAsync(String adbCommand, AdbCommandResultListener listener){
		this.mListener = listener;
		executeAsync(adbCommand);
	}
	
	public synchronized void executeAsync(final String adbCommand){
		shouldContinue = true;
		
		// Run command on separate thread
		new Thread(new Runnable(){
			public void run(){
				execute(adbCommand);
			}
		}).start();
		
	}
	
	public synchronized void setResultListener(AdbCommandResultListener listener){
		this.mListener = listener;
	}
	
	
	public static abstract class AdbCommandResultListener{
		public void onPreExecute(){
			
		}
		public void onError(String errmsg){
			
		}
		public abstract void onOutput(String line);
		public void onSuccess(){
			
		}
		public void onFinished(){
			
		}
	}
	
	public static class AdbCommandException extends Exception{

		/**
		 * 
		 */
		private static final long serialVersionUID = 2045284915415843474L;

		public AdbCommandException() {
			super();
			// TODO Auto-generated constructor stub
		}

		public AdbCommandException(String arg0, Throwable arg1) {
			super(arg0, arg1);
			// TODO Auto-generated constructor stub
		}

		public AdbCommandException(String arg0) {
			super(arg0);
			// TODO Auto-generated constructor stub
		}

		public AdbCommandException(Throwable arg0) {
			super(arg0);
			// TODO Auto-generated constructor stub
		}
		
	}
	
}
