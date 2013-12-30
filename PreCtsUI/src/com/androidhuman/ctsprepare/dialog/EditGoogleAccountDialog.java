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
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.androidhuman.ctsprepare.data.GoogleAccount;
import com.androidhuman.ctsprepare.util.Utils;
import org.eclipse.swt.widgets.Control;

public class EditGoogleAccountDialog extends Dialog {

	protected Object result;
	protected Shell shlGoogleAccount;
	private Text txtEmail;
	private Text txtPassword;
	private Button btnApply;
	
	GoogleAccount account;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public EditGoogleAccountDialog(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlGoogleAccount.open();
		shlGoogleAccount.layout();
		Display display = getParent().getDisplay();
		while (!shlGoogleAccount.isDisposed()) {
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
		shlGoogleAccount = new Shell(getParent(), SWT.CLOSE | SWT.MIN | SWT.TITLE);
		shlGoogleAccount.setSize(373, 136);
		shlGoogleAccount.setText("Google account");
		
		Label lblEmail = new Label(shlGoogleAccount, SWT.NONE);
		lblEmail.setAlignment(SWT.RIGHT);
		lblEmail.setBounds(11, 15, 70, 17);
		lblEmail.setText("E-mail");
		
		Label lblPassword = new Label(shlGoogleAccount, SWT.NONE);
		lblPassword.setText("Password");
		lblPassword.setAlignment(SWT.RIGHT);
		lblPassword.setBounds(11, 47, 70, 17);
		
		txtEmail = new Text(shlGoogleAccount, SWT.BORDER);
		txtEmail.setBounds(86, 10, 266, 27);
		
		txtPassword = new Text(shlGoogleAccount, SWT.BORDER | SWT.PASSWORD);
		txtPassword.setBounds(86, 43, 266, 27);
		
		btnApply = new Button(shlGoogleAccount, SWT.NONE);
		btnApply.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// Apply account information
				account.email = txtEmail.getText();
				account.password = txtPassword.getText();
				
				if(account.email.length()==0 || account.password.length()==0){
					Utils.showErrorMessage(shlGoogleAccount, "Please enter valid account information.");
					return;
				}
				
				// Open account information file
				File file = null;
				FileWriter writer = null;
				try{
					file = new File("automation/account.info");
					file.createNewFile();
					writer = new FileWriter(file);
					
					// Write into file
					writer.write(account.toJson());
					
					writer.close();
				}catch(IOException e){
					Utils.showErrorMessage(shlGoogleAccount, e.getMessage());
				}finally{
					if(writer!=null){
						try{writer.close();}catch(IOException e){}
					}
				}
				Utils.showInfoMessageBox(shlGoogleAccount, "Account information saved.");
				shlGoogleAccount.close();
			}
		});
		btnApply.setBounds(263, 76, 89, 27);
		btnApply.setText("Apply");
		
		Button btnCheckButton = new Button(shlGoogleAccount, SWT.CHECK);
		btnCheckButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				Button thisWidget = (Button)arg0.widget;
				String tmpPassword = txtPassword.getText();
				
				if(thisWidget.getSelection()){
					txtPassword.dispose();
					txtPassword = new Text(shlGoogleAccount, SWT.BORDER);
					txtPassword.setBounds(86, 43, 266, 27);
				}else{
					txtPassword.dispose();
					txtPassword = new Text(shlGoogleAccount, SWT.BORDER | SWT.PASSWORD);
					txtPassword.setBounds(86, 43, 266, 27);
				}
				
				if(tmpPassword!=null){
					txtPassword.setText(tmpPassword);
				}
			}
		});
		btnCheckButton.setBounds(86, 76, 171, 24);
		btnCheckButton.setText("Show password");
		shlGoogleAccount.setTabList(new Control[]{txtEmail, txtPassword, btnCheckButton, btnApply});

		
		// Load data from account configuration file if exists
		File file = new File("automation/account.info");
		if(!file.exists()){
			account = new GoogleAccount();
		}else{
			BufferedReader reader = null;
			try{
				reader = new BufferedReader(new FileReader(file));
				JSONObject obj = (JSONObject)JSONValue.parse(reader);
				if(obj!=null){
					account = GoogleAccount.fromJson(obj.toJSONString());
				}else{
					account = new GoogleAccount();
				}
				
				txtEmail.setText(account.email!=null ? account.email : "");
				txtPassword.setText(account.password!=null ? account.password : "");
			}catch(IOException e){
				Utils.showErrorMessage(shlGoogleAccount, e.getMessage());
			}finally{
				if(reader!=null){
					try{ reader.close(); }catch(IOException e){}
				}
			}
		}
	}
}
