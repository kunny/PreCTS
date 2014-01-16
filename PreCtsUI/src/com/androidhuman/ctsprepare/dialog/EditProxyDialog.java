package com.androidhuman.ctsprepare.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.androidhuman.ctsprepare.data.ProxySettings;
import com.androidhuman.ctsprepare.util.Utils;

public class EditProxyDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	private Text txtIp;
	private Text txtPort;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public EditProxyDialog(Shell parent, int style) {
		super(parent, style);
		setText("Proxy settings");
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
	
	ProxySettings settings;

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), SWT.CLOSE | SWT.MIN | SWT.TITLE);
		shell.setSize(320, 137);
		shell.setText(getText());
		
		Label lblIp = new Label(shell, SWT.RIGHT);
		lblIp.setBounds(18, 15, 34, 17);
		lblIp.setText("IP");
		
		txtIp = new Text(shell, SWT.BORDER);
		txtIp.setBounds(58, 10, 233, 27);
		
		txtPort = new Text(shell, SWT.BORDER);
		txtPort.setBounds(57, 43, 75, 27);
		
		Label lblPort = new Label(shell, SWT.RIGHT);
		lblPort.setText("Port");
		lblPort.setBounds(18, 46, 34, 17);
		
		Button btnOk = new Button(shell, SWT.NONE);
		btnOk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				settings.ip = txtIp.getText();
				settings.port = txtPort.getText();
				
				// Validate
				if(!settings.isValid()){
					Utils.showErrorMessage(shell, "IP/Port is not valid.");
					return;
				}
				
				Utils.setProxySettings(settings);
				Utils.showInfoMessageBox(shell, "Proxy setting saved.");
				shell.close();
				
			}
		});
		btnOk.setBounds(221, 79, 89, 27);
		btnOk.setText("OK");
		
		settings = Utils.getProxySettings();
		txtIp.setText(settings.ip!=null ? settings.ip : "");
		txtPort.setText(settings.port!=null ? settings.port : "");
	}
}
