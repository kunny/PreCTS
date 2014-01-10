package com.androidhuman.ctsprepare.dialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.androidhuman.ctsprepare.data.WifiAp;
import com.androidhuman.ctsprepare.util.Utils;

public class EditWifiApDialog extends Dialog {

	protected Object result;
	protected Shell shlWifiAp;
	private Text txtApName;
	private Text txtPassword;
	private Button btnApply;
	
	WifiAp apData;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public EditWifiApDialog(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlWifiAp.open();
		shlWifiAp.layout();
		Display display = getParent().getDisplay();
		while (!shlWifiAp.isDisposed()) {
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
		shlWifiAp = new Shell(getParent(), SWT.CLOSE | SWT.MIN | SWT.TITLE);
		shlWifiAp.setSize(373, 136);
		shlWifiAp.setText("Wi-Fi AP");
		
		Label lblEmail = new Label(shlWifiAp, SWT.NONE);
		lblEmail.setAlignment(SWT.RIGHT);
		lblEmail.setBounds(11, 15, 70, 17);
		lblEmail.setText("AP Name");
		
		Label lblPassword = new Label(shlWifiAp, SWT.NONE);
		lblPassword.setText("Password");
		lblPassword.setAlignment(SWT.RIGHT);
		lblPassword.setBounds(11, 47, 70, 17);
		
		txtApName = new Text(shlWifiAp, SWT.BORDER);
		txtApName.setBounds(86, 10, 266, 27);
		
		txtPassword = new Text(shlWifiAp, SWT.BORDER | SWT.PASSWORD);
		txtPassword.setBounds(86, 43, 266, 27);
		
		btnApply = new Button(shlWifiAp, SWT.NONE);
		btnApply.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// Apply account information
				apData.apName = txtApName.getText();
				apData.password = txtPassword.getText();
				
				if(apData.apName.length()==0 || apData.password.length()==0){
					Utils.showErrorMessage(shlWifiAp, "Please enter valid WiFi AP information.");
					return;
				}
				
				// Open account information file
				File file = null;
				FileWriter writer = null;
				try{
					file = new File("automation/wifi.info");
					file.createNewFile();
					writer = new FileWriter(file);
					
					// Write into file
					writer.write(apData.toJson());
					
					writer.close();
				}catch(IOException e){
					Utils.showErrorMessage(shlWifiAp, e.getMessage());
				}finally{
					if(writer!=null){
						try{writer.close();}catch(IOException e){}
					}
				}
				Utils.showInfoMessageBox(shlWifiAp, "WiFi AP information saved.");
				shlWifiAp.close();
			}
		});
		btnApply.setBounds(263, 76, 89, 27);
		btnApply.setText("Apply");
		
		Button btnCheckButton = new Button(shlWifiAp, SWT.CHECK);
		btnCheckButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				Button thisWidget = (Button)arg0.widget;
				String tmpPassword = txtPassword.getText();
				
				if(thisWidget.getSelection()){
					txtPassword.dispose();
					txtPassword = new Text(shlWifiAp, SWT.BORDER);
					txtPassword.setBounds(86, 43, 266, 27);
				}else{
					txtPassword.dispose();
					txtPassword = new Text(shlWifiAp, SWT.BORDER | SWT.PASSWORD);
					txtPassword.setBounds(86, 43, 266, 27);
				}
				
				if(tmpPassword!=null){
					txtPassword.setText(tmpPassword);
				}
			}
		});
		btnCheckButton.setBounds(86, 76, 171, 24);
		btnCheckButton.setText("Show password");
		shlWifiAp.setTabList(new Control[]{txtApName, txtPassword, btnCheckButton, btnApply});

		
		// Load data from account configuration file if exists
		File file = new File("automation/wifi.info");
		if(!file.exists()){
			apData = new WifiAp();
		}else{
			BufferedReader reader = null;
			try{
				reader = new BufferedReader(new FileReader(file));
				JSONObject obj = (JSONObject)JSONValue.parse(reader);
				if(obj!=null){
					apData = WifiAp.fromJson(obj.toJSONString());
				}else{
					apData = new WifiAp();
				}
				
				txtApName.setText(apData.apName!=null ? apData.apName : "");
				txtPassword.setText(apData.password!=null ? apData.password : "");
			}catch(IOException e){
				Utils.showErrorMessage(shlWifiAp, e.getMessage());
			}finally{
				if(reader!=null){
					try{ reader.close(); }catch(IOException e){}
				}
			}
		}
	}
}
