package com.androidhuman.ctsprepare.shell;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.androidhuman.ctsprepare.data.BasicDeviceInfo;
import com.androidhuman.ctsprepare.data.Task;
import com.androidhuman.ctsprepare.util.AdbCommand;
import com.androidhuman.ctsprepare.util.AdbCommand.AdbCommandException;
import com.androidhuman.ctsprepare.util.AdbCommand.AdbCommandResultListener;
import com.androidhuman.ctsprepare.util.AdbWrapper;
import com.androidhuman.ctsprepare.util.AdbWrapper.KeyCode;
import com.androidhuman.ctsprepare.util.AdbWrapper.ResultListener;
import com.androidhuman.ctsprepare.util.Utils;

public class Main {
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final String version = " 1.0";

	protected Shell shlPrects;
	private Table table;
	private ArrayList<BasicDeviceInfo> deviceList = new ArrayList<BasicDeviceInfo>();
	
	Button btnCopyMediaFiles;
	Button btnInstallCtsdeviceadminapk;
	
	Button btnActivateCtsdeviceadminOn;
	Button btnConfigureWifi;
	Button btnConfigureScreenTimeout;
	
	Button btnRefresh;
	Button btnStart;
	
	ProgressBar progressBar_1;
	
	ProgressBar progressBar;
	Label lblReady;
	
	LinkedList<Task> taskList;
	int currentDevIdx = -1;
	boolean isInstallBlockDlgDismissed = false;
	private Text txtLog;
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Main window = new Main();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shlPrects.open();
		shlPrects.layout();
		while (!shlPrects.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlPrects = new Shell();
		shlPrects.setSize(702, 589);
		shlPrects.setText("Pre-CTS"+version);
		
		Group grpOptions = new Group(shlPrects, SWT.NONE);
		grpOptions.setText("File / Installation");
		grpOptions.setBounds(10, 10, 312, 118);
		
		btnCopyMediaFiles = new Button(grpOptions, SWT.CHECK);
		btnCopyMediaFiles.setSelection(true);
		btnCopyMediaFiles.setBounds(10, 46, 195, 16);
		btnCopyMediaFiles.setText("Copy media files");
		
		btnInstallCtsdeviceadminapk = new Button(grpOptions, SWT.CHECK);
		btnInstallCtsdeviceadminapk.setSelection(true);
		btnInstallCtsdeviceadminapk.setBounds(10, 24, 176, 16);
		btnInstallCtsdeviceadminapk.setText("Install CtsDeviceAdmin.apk");
		
		table = new Table(shlPrects, SWT.BORDER | SWT.FULL_SELECTION);
		table.setBounds(10, 134, 666, 210);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn tblclmnSerial = new TableColumn(table, SWT.NONE);
		tblclmnSerial.setWidth(114);
		tblclmnSerial.setText("Serial");
		
		TableColumn tblclmnModel = new TableColumn(table, SWT.NONE);
		tblclmnModel.setWidth(144);
		tblclmnModel.setText("Model");
		
		TableColumn tblclmnVersion = new TableColumn(table, SWT.NONE);
		tblclmnVersion.setWidth(68);
		tblclmnVersion.setText("Version");
		
		TableColumn tblclmnProgress = new TableColumn(table, SWT.NONE);
		tblclmnProgress.setWidth(62);
		tblclmnProgress.setText("Progress");
		
		TableColumn tblclmnDetails = new TableColumn(table, SWT.NONE);
		tblclmnDetails.setWidth(259);
		tblclmnDetails.setText("Details");
		
		Menu menu_2 = new Menu(table);
		table.setMenu(menu_2);
		
		btnStart = new Button(shlPrects, SWT.NONE);
		btnStart.setEnabled(false);
		btnStart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {

				((Button)e.widget).setEnabled(false);	
				btnRefresh.setVisible(false);
				progressBar_1.setVisible(true);
				
				progressBar.setSelection(0);
				
				final boolean copyMediaFiles = btnCopyMediaFiles.getSelection();
				final boolean installCtsDeviceAdmin = btnInstallCtsdeviceadminapk.getSelection();
				
				final boolean activateCtsDeviceAdmin = btnActivateCtsdeviceadminOn.getSelection();
				final boolean configureWifi = btnConfigureWifi.getSelection();
				final boolean configureScreenTimeout = btnConfigureScreenTimeout.getSelection();
				
				// Process task list for device
				new Thread(new Runnable(){
					public void run(){
						
						try{
							// for each devices
							int devCount = deviceList.size();
							for(int i=0; i<devCount; i++){
								final BasicDeviceInfo info = deviceList.get(i);
								currentDevIdx = i;
								// Add tasks into task list
								taskList = new LinkedList<Task>();
								
								if(installCtsDeviceAdmin){
									taskList.add(new Task(Task.INSTALL_CTS_DEV_ADMIN));
								}
								if(copyMediaFiles){
									taskList.add(new Task(Task.COPY_MEDIA));
								}
								if(activateCtsDeviceAdmin){
									taskList.add(new Task(Task.ACTIVATE_DEV_ADMIN));
								}
								if(configureWifi){
									taskList.add(new Task(Task.CONFIGURE_WIFI));
								}
								if(configureScreenTimeout){
									taskList.add(new Task(Task.CONFIGURE_SCR_TIMEOUT));
								}
										
								final int initialTaskCnt = taskList.size();
								
								boolean automationJarInstalled = false;
								while(true){
									try{
										final Task task = taskList.remove();
										
										updateProgress(currentDevIdx, 
												initialTaskCnt-taskList.size(), initialTaskCnt, task.toString());
																				
										boolean result = false;
										
										
										switch(task.type){
										case Task.INSTALL_CTS_DEV_ADMIN:
											String apkPath = null;
											if(info.version.contains("4.1")){
												apkPath = "cts_device_admin/4.1/CtsDeviceAdmin.apk";
											}else if(info.version.contains("4.2")){
												apkPath = "cts_device_admin/4.2/CtsDeviceAdmin.apk";
											}else if(info.version.contains("4.3")){
												apkPath = "cts_device_admin/4.3/CtsDeviceAdmin.apk";
											}else if(info.version.contains("4.4")){
												apkPath = "cts_device_admin/4.4/CtsDeviceAdmin.apk";
											}else{
												throw new IllegalStateException("Not supported version : "+ info.version);
											}
											
											isInstallBlockDlgDismissed = false;
											new Thread(new Runnable(){
												public void run(){
													try {
														System.out.print("Waiting for dialog popup...");
														log(info.serial, "Waiting for app verification service popup...");
														Thread.sleep(7000);
														if(!isInstallBlockDlgDismissed){
															// Install block dialog still not dismissed.
															System.out.print("Attempting dismiss install block dialog...");
															log(info.serial, "Attempting dialog dismissal...");
															AdbWrapper wrapper = new AdbWrapper();
															wrapper.sendKeyCode(info.serial, AdbWrapper.KeyCode.DOWN);
															Thread.sleep(500);
															wrapper.sendKeyCode(info.serial, KeyCode.RIGHT);
															Thread.sleep(500);
															wrapper.sendKeyCode(info.serial, KeyCode.OK);
															System.out.println("Done");
															log(info.serial, "Popup successfully dismissed.");
														}else{
															System.out.println("Dialog dismissed or already accepted app verification service.");
															log(info.serial, "Seems to no popup showed up.");
														}
													} catch (InterruptedException e) {
														e.printStackTrace();
													}
												}
											}).start();
											result = new AdbCommand().executeSimple(String.format("-s %s install %s", info.serial, apkPath));
											if(!result){
												throw new IllegalStateException("Error installing CtsDeviceAdmin");
											}
											isInstallBlockDlgDismissed = true;
											break;
											
										case Task.COPY_MEDIA:
											// 1920*1080
											updateStatusMessage(currentDevIdx, "Copying media (1920x1280)");
											log(info.serial, "Copying media (1920x1280)");
											result = new AdbCommand().executeSimple(
													String.format("-s %s push cts_media/bbb_short/1920x1080 /mnt/sdcard/test/bbb_short/1920x1080", info.serial));
											if(!result){
												throw new IllegalStateException("Error copying 1920*1080 short");
											}
											result = new AdbCommand().executeSimple(
													String.format("-s %s push cts_media/bbb_full/1920x1080 /mnt/sdcard/test/bbb_full/1920x1080", info.serial));
											if(!result){
												throw new IllegalStateException("Error copying 1920*1080 full");
											}
											
											// 1280*720
											updateStatusMessage(currentDevIdx, "Copying media (1280x720)");
											log(info.serial, "Copying media (1280x720)");
											result = new AdbCommand().executeSimple(
													String.format("-s %s push cts_media/bbb_short/1280x720 /mnt/sdcard/test/bbb_short/1280x720", info.serial));
											if(!result){
												throw new IllegalStateException("Error copying 1280x720 short");
											}
											result = new AdbCommand().executeSimple(
													String.format("-s %s push cts_media/bbb_full/1280x720 /mnt/sdcard/test/bbb_full/1280x720", info.serial));
											if(!result){
												throw new IllegalStateException("Error copying 1280x720 full");
											}
																						
											// 720*480
											updateStatusMessage(currentDevIdx, "Copying media (720x480)");
											log(info.serial, "Copying media (720x480)");
											result = new AdbCommand().executeSimple(
													String.format("-s %s push cts_media/bbb_short/720x480 /mnt/sdcard/test/bbb_short/720x480", info.serial));
											if(!result){
												throw new IllegalStateException("Error copying 720x480 short");
											}
											result = new AdbCommand().executeSimple(
													String.format("-s %s push cts_media/bbb_full/720x480 /mnt/sdcard/test/bbb_full/720x480", info.serial));
											if(!result){
												throw new IllegalStateException("Error copying 720x480 full");
											}
											
											// Others
											updateStatusMessage(currentDevIdx, "Copying media (Others)");
											log(info.serial, "Copying media (Others)");
											result = new AdbCommand().executeSimple(
													String.format("-s %s push cts_media/bbb_short/176x144 /mnt/sdcard/test/bbb_short/176x144", info.serial));
											if(!result){
												throw new IllegalStateException("Error copying 176x144 short");
											}
											result = new AdbCommand().executeSimple(
													String.format("-s %s push cts_media/bbb_full/176x144 /mnt/sdcard/test/bbb_full/176x144", info.serial));
											if(!result){
												throw new IllegalStateException("Error copying 176x144 full");
											}
											result = new AdbCommand().executeSimple(
													String.format("-s %s push cts_media/bbb_short/480x360 /mnt/sdcard/test/bbb_short/480x360", info.serial));
											if(!result){
												throw new IllegalStateException("Error copying 480x360 short");
											}
											result = new AdbCommand().executeSimple(
													String.format("-s %s push cts_media/bbb_full/480x360 /mnt/sdcard/test/bbb_full/480x360", info.serial));
											if(!result){
												throw new IllegalStateException("Error copying 480x360 full");
											}
											break;
											
										case Task.ACTIVATE_DEV_ADMIN:
											if(!automationJarInstalled){
												log(info.serial, "Installing automation jar...");
												automationJarInstalled = new AdbCommand().executeSimple(
														String.format("-s %s push automation/PreCtsAutomator.jar /data/local/tmp", info.serial));
												if(!automationJarInstalled){
													throw new IllegalStateException("Error installing automation jar.");
												}
											}
											new AdbCommand().execute(String.format("-s %s shell am start -S \"com.android.settings/.Settings\\$DeviceAdminSettingsActivity\"", info.serial));
											new AdbCommand().execute(
													String.format(
															"-s %s shell uiautomator runtest PreCTSAutomator.jar -c com.androidhuman.ctsprepare.automator.PreCTSAutomate#testActivateDeviceAdmin", 
															info.serial), 
													new AdbCommandResultListener(){

												@Override
												public void onPreExecute() {
													log(info.serial, task.toString()+" started");
												}

												@Override
												public void onError(
														String errmsg) {
													log(info.serial, errmsg);
												}

												@Override
												public void onOutput(String line) {
													log(info.serial, line);
												}

												@Override
												public void onSuccess() {
													log(info.serial, task.toString()+" finished");
												}
												
											});
											break;
											
										case Task.CONFIGURE_WIFI:
											if(!automationJarInstalled){
												log(info.serial, "Installing automation jar...");
												automationJarInstalled = new AdbCommand().executeSimple(
														String.format("-s %s push automation/PreCTSAutomator.jar /data/local/tmp", info.serial));
												if(!automationJarInstalled){
													throw new IllegalStateException("Error installing automation jar.");
												}
											}
											new AdbCommand().execute(String.format("-s %s shell am start -a android.settings.WIFI_SETTINGS", info.serial));
											new AdbCommand().execute(
													String.format(
															"-s %s shell uiautomator runtest PreCTSAutomator.jar -c com.androidhuman.ctsprepare.automator.PreCTSAutomate#testActivateWifi", 
															info.serial), 
													new AdbCommandResultListener(){

												@Override
												public void onPreExecute() {
													log(info.serial, task.toString()+" started");
												}

												@Override
												public void onError(
														String errmsg) {
													log(info.serial, errmsg);
												}

												@Override
												public void onOutput(String line) {
													log(info.serial, line);
												}

												@Override
												public void onSuccess() {
													log(info.serial, task.toString()+" finished");
												}
												
											});
											break;
											
										case Task.CONFIGURE_SCR_TIMEOUT:
											if(!automationJarInstalled){
												log(info.serial, "Installing automation jar...");
												automationJarInstalled = new AdbCommand().executeSimple(
														String.format("-s %s push automation/PreCTSAutomator.jar /data/local/tmp", info.serial));
												if(!automationJarInstalled){
													throw new IllegalStateException("Error installing automation jar.");
												}
											}
											new AdbCommand().execute(String.format("-s %s shell am start -a android.settings.DISPLAY_SETTINGS", info.serial));
											new AdbCommand().execute(
													String.format(
															"-s %s shell uiautomator runtest PreCTSAutomator.jar -c com.androidhuman.ctsprepare.automator.PreCTSAutomate#testSetTimeout", 
															info.serial), 
													new AdbCommandResultListener(){

												@Override
												public void onPreExecute() {
													log(info.serial, task.toString()+" started");
												}

												@Override
												public void onError(
														String errmsg) {
													log(info.serial, errmsg);
												}

												@Override
												public void onOutput(String line) {
													log(info.serial, line);
												}

												@Override
												public void onSuccess() {
													log(info.serial, task.toString()+" finished");
												}
												
											});
											break;
										}
										
									}catch(NoSuchElementException e){
										// Exit loop
										break;
									}
								}
								Display.getDefault().syncExec(new Runnable(){
									public void run(){
										updateProgress(currentDevIdx, 
												initialTaskCnt-taskList.size(), initialTaskCnt,
												"Completed");
									}
								});
								log(info.serial, "Completed");
							}
							Display.getDefault().syncExec(new Runnable(){
								public void run(){
									lblReady.setText("Done");
									// Update progress on progress bar
									progressBar.setSelection(100);
								}
							});
							
						}catch(IllegalStateException e){
							log(e.getMessage());
						}finally{
							Display.getDefault().syncExec(new Runnable(){
								public void run(){
									((Button)e.widget).setEnabled(true);
									btnRefresh.setVisible(true);
									progressBar_1.setVisible(false);
								}
							});
						}
						
					}
				}).start();
				
			}
		});
		btnStart.setBounds(546, 360, 130, 38);
		btnStart.setText("Start");
		
		progressBar = new ProgressBar(shlPrects, SWT.NONE);
		progressBar.setBounds(9, 360, 398, 17);
		
		lblReady = new Label(shlPrects, SWT.NONE);
		lblReady.setBounds(10, 383, 242, 15);
		lblReady.setText("Ready");
		
		Group grpAutomation = new Group(shlPrects, SWT.NONE);
		grpAutomation.setText("Automation");
		grpAutomation.setBounds(328, 10, 348, 118);
		
		btnActivateCtsdeviceadminOn = new Button(grpAutomation, SWT.CHECK);
		btnActivateCtsdeviceadminOn.setBounds(10, 24, 227, 16);
		btnActivateCtsdeviceadminOn.setSelection(true);
		btnActivateCtsdeviceadminOn.setText("Activate CtsDeviceAdmin on device");
		
		btnConfigureWifi = new Button(grpAutomation, SWT.CHECK);
		btnConfigureWifi.setBounds(10, 46, 127, 16);
		btnConfigureWifi.setSelection(true);
		btnConfigureWifi.setText("Configure Wi-Fi");
		
		btnConfigureScreenTimeout = new Button(grpAutomation, SWT.CHECK);
		btnConfigureScreenTimeout.setBounds(10, 68, 176, 16);
		btnConfigureScreenTimeout.setSelection(true);
		btnConfigureScreenTimeout.setText("Configure screen timeout");
		
		btnRefresh = new Button(shlPrects, SWT.NONE);
		btnRefresh.setBounds(419, 360, 121, 38);
		btnRefresh.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				refreshDeviceList();
			}
		});
		btnRefresh.setText("Refresh device list");
		
		progressBar_1 = new ProgressBar(shlPrects, SWT.INDETERMINATE);
		progressBar_1.setVisible(false);
		progressBar_1.setBounds(10, 344, 666, 8);
		
		txtLog = new Text(shlPrects, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
		txtLog.setBounds(10, 415, 666, 126);
		
	}
	
	private void log(final String msg){
		Display.getDefault().asyncExec(new Runnable(){
			public void run(){
				txtLog.append(msg);txtLog.append("\n");
				txtLog.setTopIndex(txtLog.getLineCount()-1);
			}
		});
	}
	
	private void log(String serial, String msg){
		log(String.format("[%s | %s] %s", format.format(new Date()), serial, msg));
	}
	
	private void updateProgress(final int index, final int currentProgress, final int totalProgress, final String message){
		Display.getDefault().asyncExec(new Runnable(){
			public void run(){

			// Update progress on table
			TableItem item = table.getItem(index);
			item.setText(3, String.format("%d/%d", currentProgress, totalProgress));
			item.setText(4, message);
			
			// Update progress on progress bar
			int progress = currentDevIdx*100/deviceList.size();
			progressBar.setSelection(progress);
			
			// Update label
			lblReady.setText("Running task on device "+deviceList.get(currentDevIdx).serial);
			}
		}
		);
	}
	
	private void refreshDeviceList(){
		// Refreshes device list
		log("Refreshing device list...");
		// Clear current device list
		deviceList.clear();
		table.removeAll();
		table.setItemCount(0);
		
		// Get attached device info
		try{
			new AdbWrapper(Display.getDefault()).getAttachedDevices(new ResultListener<ArrayList<BasicDeviceInfo>>(){

						@Override
						public void onResult(ArrayList<BasicDeviceInfo> result) {
							// Received device info
							deviceList = result;
							log(String.format("Found %d device(s).", result.size()));
							for(BasicDeviceInfo info : deviceList){
								// Create table entry
								TableItem item = new TableItem(table, SWT.NULL);
								item.setText(0, info.serial);
								item.setText(1, info.model);
								item.setText(2, info.version);
								item.setText(3, "N/A");
								item.setText(4, "Ready");
							}
							
						}

						@Override
						public void onFailed(String msg) {
							Utils.showErrorMessage(shlPrects, msg);
						}

						@Override
						public void onPreExecute() {
							btnRefresh.setEnabled(false);
							btnRefresh.setText("Wait...");
						}

						@Override
						public void onPostExecute() {
							btnRefresh.setEnabled(true);
							btnRefresh.setText("Refresh");
							btnStart.setEnabled(deviceList.size()!=0);
							
						}
						
					});
			
		}catch(AdbCommandException ex){
			Utils.showErrorMessage(null, ex.getMessage());
		}
		
	}
	
	private void updateStatusMessage(final int index, final String message){
		Display.getDefault().asyncExec(new Runnable(){
			public void run(){
				TableItem item = table.getItem(index);
				item.setText(4, message);
			}
		});
	}
	
}
