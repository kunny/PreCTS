package com.androidhuman.ctsprepare.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
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
	
	public static boolean showYesNoMessageBox(Shell shell, String message){
		MessageBox msg = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.YES | SWT.NO);
		msg.setText("Question");
		msg.setMessage(message);
		int result = msg.open();
		return result==SWT.YES;
	}
	
	public static void copyFile(File inFile, File outFile) throws IOException{
		FileChannel source = null;
		FileChannel dest = null;
		
		FileInputStream fis = new FileInputStream(inFile);
		FileOutputStream fos = new FileOutputStream(outFile);
		source = fis.getChannel();
		dest = fos.getChannel();
		   
		dest.transferFrom(source, 0, source.size());
		 
		  
		if(source!=null){
			source.close();
		}
		if(fis!=null){
			fis.close();
		}
		
		if(dest!=null){
			dest.close();
		}
		if(fos!=null){
			fos.close();
		}
		  
	}
	
	public static boolean isWindows(){
		return System.getProperty("os.name").toLowerCase().indexOf("win") >=0 ? true : false;
	}
	
	public static String getSeparator(){
		return isWindows() ? "\\" : "/";
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
	
	public static void setAdbPath(String path) throws IllegalArgumentException{
		File file = new File(path);
		
		if(!file.isDirectory()){
			throw new IllegalArgumentException("Not a valid directory.");
		}
		
		// Check adb executable existence
		String separator = getSeparator();
		StringBuilder b = new StringBuilder().append(path).append(separator).append("platform-tools").append(separator).append("adb");
		if(isWindows()){
			b.append(".exe");
		}
		File adb = new File(b.toString());
		if(!adb.exists() || !adb.isFile()){
			throw new IllegalArgumentException("adb binary not found. Try download the SDK again.");
		}
		
		// adb is placed in right place
		FileWriter writer = null;
		try{
			writer = new FileWriter("environment.info");
			writer.write(path);
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			if(writer!=null){
				try{ writer.close(); }catch(IOException e){}
			}
		}
	}
	
	public static String getAdbPath(){
		String path = null;
		File file = new File("environment.info");
		if(!file.exists()){
			return null;
		}
		
		BufferedReader reader = null;
		
		try{
			reader = new BufferedReader(new FileReader(file));
			path = reader.readLine();
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			if(reader!=null){
				try{ reader.close(); }catch(IOException e){}
			}
		}
		return path;
	}
}
