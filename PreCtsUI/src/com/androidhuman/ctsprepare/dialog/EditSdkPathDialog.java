package com.androidhuman.ctsprepare.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.androidhuman.ctsprepare.util.Utils;

public class EditSdkPathDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	private Text txtSdkPath;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public EditSdkPathDialog(Shell parent, int style) {
		super(parent, style);
		setText("Android SDK Path selection");
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
		shell.setSize(450, 151);
		shell.setText(getText());
		
		txtSdkPath = new Text(shell, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		txtSdkPath.setEnabled(false);
		txtSdkPath.setEditable(false);
		txtSdkPath.setBounds(10, 10, 430, 65);
		
		Button btnBrowse = new Button(shell, SWT.NONE);
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				DirectoryDialog dlg = new DirectoryDialog(shell, SWT.NONE);
				dlg.setText("Select Android SDK directory");
				String path = dlg.open();
				if(path!=null){
					try{
						Utils.setAdbPath(path);
					}catch(Exception e){
						Utils.showErrorMessage(shell, e.getMessage());
					}
				}
				txtSdkPath.setText(path);
			}
		});
		btnBrowse.setBounds(10, 81, 107, 27);
		btnBrowse.setText("Browse...");
		
		Button btnApply = new Button(shell, SWT.NONE);
		btnApply.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				shell.close();
			}
		});
		btnApply.setBounds(351, 81, 89, 27);
		btnApply.setText("Close");
		
		// Load data
		String adbPath = Utils.getAdbPath();
		txtSdkPath.setText(adbPath==null ? "Not set" : adbPath);

	}
}
