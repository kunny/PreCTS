package com.androidhuman.ctsprepare.dialog;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.androidhuman.ctsprepare.data.BasicDeviceInfo;
import com.androidhuman.ctsprepare.data.Model;
import com.androidhuman.ctsprepare.util.AdbCommand.AdbCommandException;
import com.androidhuman.ctsprepare.util.AdbWrapper;
import com.androidhuman.ctsprepare.util.AdbWrapper.ResultListener;
import com.androidhuman.ctsprepare.util.Utils;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class TargetSelectionDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	private Table table;
	private Button btnRefresh;
	
	private ArrayList<BasicDeviceInfo> deviceList = new ArrayList<BasicDeviceInfo>();

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public TargetSelectionDialog(Shell parent, int style) {
		super(parent, style);
		setText("Target selection");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), SWT.SHELL_TRIM);
		shell.setSize(450, 300);
		shell.setText(getText());
		
		btnRefresh = new Button(shell, SWT.NONE);
		btnRefresh.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				refreshDeviceList();
			}
		});
		btnRefresh.setBounds(10, 227, 76, 25);
		btnRefresh.setText("Refresh");
		
		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				new Model(deviceList.get(table.getSelectionIndex()).model);
				result = deviceList.get(table.getSelectionIndex());
				shell.close();
			}
		});
		table.setBounds(10, 10, 414, 211);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn tblclmnSerial = new TableColumn(table, SWT.NONE);
		tblclmnSerial.setWidth(116);
		tblclmnSerial.setText("Serial");
		
		TableColumn tblclmnModel = new TableColumn(table, SWT.NONE);
		tblclmnModel.setWidth(161);
		tblclmnModel.setText("Model");
		
		TableColumn tblclmnVersion = new TableColumn(table, SWT.NONE);
		tblclmnVersion.setWidth(115);
		tblclmnVersion.setText("Version");

		refreshDeviceList();
	}
	
	private void refreshDeviceList(){
		// Refreshes device list
		
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
							
							for(BasicDeviceInfo info : deviceList){
								// Create table entry
								TableItem item = new TableItem(table, SWT.NULL);
								item.setText(0, info.serial);
								item.setText(1, info.model);
								item.setText(2, info.version);
							}
						}

						@Override
						public void onFailed(String msg) {
							Utils.showErrorMessage(shell, msg);
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
						}
						
					});
			
		}catch(AdbCommandException ex){
			Utils.showErrorMessage(null, ex.getMessage());
		}
		
	}
}
