package com.androidhuman.ctsprepare.dialog;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

import com.androidhuman.ctsprepare.util.Download;

public class CtsMediaDownloadDialog extends Dialog implements Observer{

	protected Object result;
	protected Shell shell;
	private ProgressBar progressBar;
	private Button btnCancel;
	private Label lblStatus;
	
	Download download;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public CtsMediaDownloadDialog(Shell parent, int style) {
		super(parent, style);
		setText("CTS media");
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
		shell = new Shell(getParent(), SWT.MIN | SWT.TITLE);
		shell.setSize(586, 108);
		shell.setText(getText());
		
		lblStatus = new Label(shell, SWT.NONE);
		lblStatus.setBounds(10, 10, 276, 17);
		lblStatus.setText("Downloading...");
		
		progressBar = new ProgressBar(shell, SWT.NONE);
		progressBar.setBounds(10, 33, 566, 17);
		
		btnCancel = new Button(shell, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if(download.getStatus()==Download.DOWNLOADING){
					download.cancel();
				}
				shell.close();
			}
		});
		btnCancel.setBounds(487, 66, 89, 27);
		btnCancel.setText("Cancel");

		try {
			download = new Download(new URL("https://dl.google.com/dl/android/cts/android-cts-media-1.0.zip"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		download.addObserver(this);
		
		new Thread(new Runnable(){
			public void run(){
				while(download.getStatus()==Download.DOWNLOADING){
					Display.getDefault().asyncExec(new Runnable(){
						public void run(){
							progressBar.setSelection((int)download.getProgress());
						}
					});
					System.out.println("Download prog : "+download.getProgress());
					try{ Thread.sleep(1000); }catch(Exception e){};
				}
			}
		}).start();
		
		download.download();
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		Display.getDefault().asyncExec(new Runnable(){
			public void run(){
				switch(download.getStatus()){
				case Download.ERROR:
					lblStatus.setText("Download error");
					btnCancel.setText("Close");
					break;
				case Download.COMPLETE:
					lblStatus.setText("Download completed.");
					break;
				}	
			}
		});
		
	}
}
