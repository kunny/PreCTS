package com.androidhuman.ctsprepare.dialog;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.androidhuman.ctsprepare.data.BasicDeviceInfo;
import com.androidhuman.ctsprepare.data.Event;
import com.androidhuman.ctsprepare.util.AdbCommand;
import com.androidhuman.ctsprepare.util.Utils;
import com.androidhuman.ctsprepare.util.AdbCommand.AdbCommandException;
import com.androidhuman.ctsprepare.util.AdbWrapper;
import com.androidhuman.ctsprepare.util.AdbWrapper.KeyCode;
import com.androidhuman.ctsprepare.util.AdbWrapper.ResultListener;

public class EditMacroDialog extends Dialog {

	protected Object result;
	protected Shell shlEditMacro;
	
	boolean onlyOneDeviceConnected = false;
	
	private AdbWrapper adbWrapper;
	Button btnLeft;
	Button btnUp;
	Button btnOk;
	Button btnDown;
	Button btnRight;
	Button btnMenu;
	Button btnHome;
	Button btnBack;
	
	Button btnAddText;
	Button btnAddDelay;
	
	private Text txtText;
	private Text txtHoldMs;
	
	Button btnDeviceAdministrator;
	Button btnWifiSettings;
	Button btnDisplaySettings;
	
	Button btnStop;
	
	private Table table;
	
	private String macroName;
	private ArrayList<Event> entries;
	
	public EditMacroDialog setMacroEntries(String macroName, ArrayList<Event> entries){
		this.entries = entries;
		this.macroName = macroName;
		return this;
	}

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public EditMacroDialog(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
		adbWrapper = new AdbWrapper(Display.getDefault());
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlEditMacro.open();
		shlEditMacro.layout();
		Display display = getParent().getDisplay();
		while (!shlEditMacro.isDisposed()) {
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
		shlEditMacro = new Shell(getParent(), SWT.SHELL_TRIM);
		shlEditMacro.setSize(736, 520);
		shlEditMacro.setText("Edit macro");
		
		Group grpKeypad = new Group(shlEditMacro, SWT.NONE);
		grpKeypad.setText("Keypad");
		grpKeypad.setBounds(10, 10, 260, 221);
		
		btnLeft = new Button(grpKeypad, SWT.NONE);
		btnLeft.setBounds(10, 70, 76, 40);
		btnLeft.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Event event = Event.newKeyStrokeEvent(KeyCode.LEFT);
				adbWrapper.sendKeyCode(KeyCode.LEFT);
				entries.add(event);
				addToList(event);
			}
		});
		btnLeft.setText("Left");
		
		btnUp = new Button(grpKeypad, SWT.NONE);
		btnUp.setBounds(92, 24, 76, 40);
		btnUp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Event event = Event.newKeyStrokeEvent(KeyCode.UP);
				adbWrapper.sendKeyCode(KeyCode.UP);
				entries.add(event);
				addToList(event);
			}
		});
		btnUp.setText("Up");
		
		btnOk = new Button(grpKeypad, SWT.NONE);
		btnOk.setBounds(92, 70, 76, 40);
		btnOk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Event event = Event.newKeyStrokeEvent(KeyCode.OK);
				adbWrapper.sendKeyCode(KeyCode.OK);
				entries.add(event);
				addToList(event);
			}
		});
		btnOk.setText("OK");
		
		btnDown = new Button(grpKeypad, SWT.NONE);
		btnDown.setBounds(92, 116, 76, 40);
		btnDown.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Event event = Event.newKeyStrokeEvent(KeyCode.DOWN);
				adbWrapper.sendKeyCode(KeyCode.DOWN);
				entries.add(event);
				addToList(event);
			}
		});
		btnDown.setText("Down");
		
		btnRight = new Button(grpKeypad, SWT.NONE);
		btnRight.setBounds(174, 70, 76, 40);
		btnRight.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Event event = Event.newKeyStrokeEvent(KeyCode.RIGHT);
				adbWrapper.sendKeyCode(KeyCode.RIGHT);
				entries.add(event);
				addToList(event);
			}
		});
		btnRight.setText("Right");
		
		btnMenu = new Button(grpKeypad, SWT.NONE);
		btnMenu.setBounds(10, 179, 76, 32);
		btnMenu.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Event event = Event.newKeyStrokeEvent(KeyCode.MENU);
				adbWrapper.sendKeyCode(KeyCode.MENU);
				entries.add(event);
				addToList(event);
			}
		});
		btnMenu.setText("Menu");
		
		btnHome = new Button(grpKeypad, SWT.NONE);
		btnHome.setBounds(92, 179, 76, 32);
		btnHome.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Event event = Event.newKeyStrokeEvent(KeyCode.HOME);
				adbWrapper.sendKeyCode(KeyCode.HOME);
				entries.add(event);
				addToList(event);
			}
		});
		btnHome.setText("Home");
		
		btnBack = new Button(grpKeypad, SWT.NONE);
		btnBack.setBounds(174, 179, 76, 32);
		btnBack.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Event event = Event.newKeyStrokeEvent(KeyCode.BACK);
				adbWrapper.sendKeyCode(KeyCode.BACK);
				addToList(event);
				entries.add(event);
			}
		});
		btnBack.setText("Back");
		
		Group grpTextInput = new Group(shlEditMacro, SWT.NONE);
		grpTextInput.setText("Text input");
		grpTextInput.setBounds(10, 237, 260, 65);
		
		txtText = new Text(grpTextInput, SWT.BORDER);
		txtText.setBounds(10, 26, 158, 21);
		
		btnAddText = new Button(grpTextInput, SWT.NONE);
		btnAddText.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Event event = Event.newTextEvent(txtText.getText());
				entries.add(event);
				addToList(event);
				new AdbCommand().executeAsync("shell input text "+event.getText());
				txtText.setText("");
			}
		});
		btnAddText.setBounds(174, 24, 76, 25);
		btnAddText.setText("Add");
		
		Group grpMiscelle = new Group(shlEditMacro, SWT.NONE);
		grpMiscelle.setText("Miscellaneous");
		grpMiscelle.setBounds(10, 308, 260, 164);
		
		Label lblHoldFor = new Label(grpMiscelle, SWT.NONE);
		lblHoldFor.setBounds(10, 30, 50, 15);
		lblHoldFor.setText("Hold for");
		
		txtHoldMs = new Text(grpMiscelle, SWT.BORDER);
		txtHoldMs.setBounds(66, 27, 73, 21);
		
		Label lblSec = new Label(grpMiscelle, SWT.NONE);
		lblSec.setBounds(146, 30, 26, 15);
		lblSec.setText("ms");
		
		btnAddDelay = new Button(grpMiscelle, SWT.NONE);
		btnAddDelay.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Event event = Event.newHoldEvent(Integer.valueOf(txtHoldMs.getText()));
				entries.add(event);
				addToList(event);
				txtHoldMs.setText("");
			}
		});
		btnAddDelay.setBounds(174, 25, 76, 25);
		btnAddDelay.setText("Add");
		
		btnDeviceAdministrator = new Button(grpMiscelle, SWT.NONE);
		btnDeviceAdministrator.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				new AdbCommand().executeAsync("shell am start -S \"com.android.settings/.Settings\\$DeviceAdminSettingsActivity\"");
				Event event = Event.newLaunchEvent("-S \"com.android.settings/.Settings\\$DeviceAdminSettingsActivity\"");
				entries.add(event);
				addToList(event);
			}
		});
		btnDeviceAdministrator.setBounds(10, 62, 240, 25);
		btnDeviceAdministrator.setText("Device administrator settings");
		
		btnWifiSettings = new Button(grpMiscelle, SWT.NONE);
		btnWifiSettings.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				new AdbCommand().executeAsync("shell am start -a android.settings.WIFI_SETTINGS");
				Event event = Event.newLaunchEvent("-a android.settings.WIFI_SETTINGS");
				entries.add(event);
				addToList(event);
			}
		});
		btnWifiSettings.setBounds(10, 93, 240, 25);
		btnWifiSettings.setText("Wifi settings");
		
		btnDisplaySettings = new Button(grpMiscelle, SWT.NONE);
		btnDisplaySettings.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				new AdbCommand().executeAsync("shell am start -a android.settings.DISPLAY_SETTINGS");
				Event event = Event.newLaunchEvent("-a android.settings.DISPLAY_SETTINGS");
				entries.add(event);
				addToList(event);
			}
		});
		btnDisplaySettings.setBounds(10, 124, 240, 25);
		btnDisplaySettings.setText("Display settings");
		
		table = new Table(shlEditMacro, SWT.BORDER | SWT.FULL_SELECTION);
		table.setBounds(276, 10, 433, 423);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setWidth(35);
		tableColumn.setText("#");
		
		TableColumn tblclmnType = new TableColumn(table, SWT.NONE);
		tblclmnType.setWidth(85);
		tblclmnType.setText("Type");
		
		TableColumn tblclmnDetails = new TableColumn(table, SWT.NONE);
		tblclmnDetails.setWidth(291);
		tblclmnDetails.setText("Details");
		
		Menu menu = new Menu(table);
		table.setMenu(menu);
		
		MenuItem mntmToUpper = new MenuItem(menu, SWT.NONE);
		mntmToUpper.setText("To upper");
		
		MenuItem mntmToLower = new MenuItem(menu, SWT.NONE);
		mntmToLower.setText("To lower");
		
		new MenuItem(menu, SWT.SEPARATOR);
		
		MenuItem mntmDelete = new MenuItem(menu, SWT.NONE);
		mntmDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				entries.remove(table.getSelectionIndex());
				refreshList();
			}
		});
		mntmDelete.setText("Delete");
		
		Button btnApply = new Button(shlEditMacro, SWT.NONE);
		btnApply.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result = entries;
				shlEditMacro.close();
			}
		});
		btnApply.setBounds(604, 439, 106, 33);
		btnApply.setText("Apply");
		
		Button btnStartRecording = new Button(shlEditMacro, SWT.NONE);
		btnStartRecording.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				try {
					onlyOneDeviceConnected = false;
					new AdbWrapper().getAttachedDevices(new ResultListener<ArrayList<BasicDeviceInfo>>(){

						@Override
						public void onPreExecute() {
							((Button)e.widget).setText("Wait...");
							((Button)e.widget).setEnabled(false);
						}

						@Override
						public void onResult(ArrayList<BasicDeviceInfo> result) {
							if(result.size()>1){
								Utils.showErrorMessage(shlEditMacro, "Connect only one device.");
							}else if(result.size()==0){
								Utils.showErrorMessage(shlEditMacro, "No device connected.");
							}else{
								onlyOneDeviceConnected = true;
							}
						}

						@Override
						public void onFailed(String msg) {
							Utils.showErrorMessage(shlEditMacro, msg);
						}

						@Override
						public void onPostExecute() {
							enableControlComponents(onlyOneDeviceConnected);
							((Button)e.widget).setEnabled(!onlyOneDeviceConnected);
							btnStop.setEnabled(onlyOneDeviceConnected);
							((Button)e.widget).setText("Start recording");
						}
						
					});
				} catch (AdbCommandException e1) {
					e1.printStackTrace();
				}
			}
		});
		btnStartRecording.setBounds(276, 439, 125, 33);
		btnStartRecording.setText("Start recording");
		
		shlEditMacro.setText(String.format("Edit macro for : %s", macroName));
		
		btnStop = new Button(shlEditMacro, SWT.NONE);
		btnStop.setEnabled(false);
		btnStop.setBounds(407, 439, 106, 33);
		btnStop.setText("Stop");
		refreshList();
		enableControlComponents(false);
	}
	
	private void refreshList(){
		table.clearAll();
		table.setItemCount(0);
		
		for(Event event : entries){
			addToList(event);
		}
		
	}
	
	private void addToList(Event event){
		TableItem item = new TableItem(table, SWT.NONE);
		item.setText(0, String.valueOf(table.getItemCount()));
		item.setText(1, event.toString());
		item.setText(2, event.getType()==Event.Type.KEYSTROKE ? KeyCode.toString(event.getKeyCode()) : event.getText());
	}
	
	private void enableControlComponents(boolean enabled){
		btnUp.setEnabled(enabled);
		btnLeft.setEnabled(enabled);
		btnOk.setEnabled(enabled);
		btnRight.setEnabled(enabled);
		btnDown.setEnabled(enabled);
		btnMenu.setEnabled(enabled);
		btnHome.setEnabled(enabled);
		btnBack.setEnabled(enabled);
		txtText.setEnabled(enabled);
		btnAddText.setEnabled(enabled);
		txtHoldMs.setEnabled(enabled);
		btnAddDelay.setEnabled(enabled);
		btnDeviceAdministrator.setEnabled(enabled);
		btnWifiSettings.setEnabled(enabled);
		btnDisplaySettings.setEnabled(enabled);
	}
}
