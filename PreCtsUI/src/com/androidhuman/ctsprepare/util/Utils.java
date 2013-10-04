package com.androidhuman.ctsprepare.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class Utils {
	
	public interface ProgressListener{
		public void onPrepare(long totalLength);
		public void onProgress(long current);
		public void onFinished();
	}
	
	public static void getConfirmMessageBox(Shell shell, String title, String message){
		MessageBox msg = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
		msg.setText(title);
		msg.setMessage(message);
		msg.open();
	}
	
	public static void showErrorMessage(Shell shell, String message){
		MessageBox msg = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
		msg.setText("Error");
		msg.setMessage(message);
		msg.open();
	}
	
	public static void showInfoMessageBox(Shell shell, String message){
		MessageBox msg = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
		msg.setText("Information");
		msg.setMessage(message);
		msg.open();
	}
	
	public static void copyFile(File inFile, File outFile) throws IOException{
		FileChannel source = null;
		FileChannel dest = null;
		
		source = new FileInputStream(inFile).getChannel();
		dest = new FileOutputStream(outFile).getChannel();
		   
		dest.transferFrom(source, 0, source.size());
		 
		  
		if(source!=null){
			source.close();
		}
		
		if(dest!=null){
			dest.close();
		  }
		  
	}
	
	public static String getSeparator(){
		boolean isWindows = System.getProperty("os.name").toLowerCase().indexOf("win") >=0 ? true : false;
		return isWindows ? "\\" : "/";
	}
	
	public static void copyFile(File inFile, File outFile, ProgressListener listener) {
		if(listener==null){
			throw new IllegalStateException("Listener cannot be null");
		}
		
		try {
			FileInputStream fis = new FileInputStream(inFile);
			FileOutputStream fos = new FileOutputStream(outFile);
			   
			listener.onPrepare(inFile.length());
			int data = 0;
			long bytes = 0;
			
			while((data=fis.read())!=-1) {
				fos.write(data);
				listener.onProgress(++bytes);
			}
			listener.onFinished();
			
			fis.close();
			fos.close();
		   
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
