package com.androidhuman.ctsprepare.shell;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.androidhuman.ctsprepare.data.BasicDeviceInfo;
import com.androidhuman.ctsprepare.data.Event;
import com.androidhuman.ctsprepare.data.Model;
import com.androidhuman.ctsprepare.data.Task;
import com.androidhuman.ctsprepare.dialog.EditMacroDialog;
import com.androidhuman.ctsprepare.dialog.TargetSelectionDialog;

public class ConfigureMacroShell extends Shell {
	private Table table;
	Combo combo;
	private ArrayList<File> modelList = new ArrayList<File>();
	private ArrayList<Task> taskList = new ArrayList<Task>();
	private ArrayList<ArrayList<Event>> eventList = new ArrayList<ArrayList<Event>>();
	
	private Model currentModel;
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			ConfigureMacroShell shell = new ConfigureMacroShell(display);
			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the shell.
	 * @param display
	 */
	public ConfigureMacroShell(Display display) {
		super(display, SWT.SHELL_TRIM);
		
		Group grpTarget = new Group(this, SWT.NONE);
		grpTarget.setText("Target");
		grpTarget.setBounds(10, 10, 523, 67);
		
		combo = new Combo(grpTarget, SWT.READ_ONLY);
		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				refreshTable();
			}
		});
		combo.setItems(new String[] {});
		combo.setBounds(10, 26, 139, 23);
		combo.select(0);
		
		Button btnAddTarget = new Button(grpTarget, SWT.NONE);
		btnAddTarget.setBounds(402, 24, 111, 25);
		btnAddTarget.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Object res = new TargetSelectionDialog(getShell(), SWT.NONE).open();
				if(res!=null){
					// Retrieve selected device info
					BasicDeviceInfo info = (BasicDeviceInfo)res;
					
					refreshComboBoxes();
					String[] items = combo.getItems();
					int itemCnt = items.length;
					
					for(int i=0; i<itemCnt; i++){
						String item = items[i];
						if(item.equals(info.model)){
							combo.select(i);
							break;
						}
					}
					refreshTable();
				}
			}
		});
		btnAddTarget.setText("Add target...");
		
		Group grpTasks = new Group(this, SWT.NONE);
		grpTasks.setText("Tasks");
		grpTasks.setBounds(10, 83, 523, 251);
		
		table = new Table(grpTasks, SWT.BORDER | SWT.FULL_SELECTION);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO open macro edit
				Task task = taskList.get(table.getSelectionIndex());
				Object res = new EditMacroDialog(getShell(), SWT.NONE)
					.setMacroEntries(task.toString(), 
						eventList.get(table.getSelectionIndex())).open();
				if(res!=null){
					@SuppressWarnings("unchecked")
					ArrayList<Event> result = (ArrayList<Event>)res;
					// Set items on macro shell list 
					eventList.set(table.getSelectionIndex(), result);
					
					// apply to model file
					currentModel.setTask(task, result);
					currentModel.saveAsFile();
					
					refreshTable();
				}
			}
		});
		table.setBounds(10, 22, 503, 219);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn tblclmnName = new TableColumn(table, SWT.NONE);
		tblclmnName.setWidth(388);
		tblclmnName.setText("Name");
		
		TableColumn tblclmnStatus = new TableColumn(table, SWT.NONE);
		tblclmnStatus.setWidth(100);
		tblclmnStatus.setText("Status");
		createContents();
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("Configure macro");
		setSize(559, 382);
		refreshComboBoxes();
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	private void refreshComboBoxes(){
		// File naming format : [Model].cat
		// ex) SAMSUNG-SGH-I747.cat
		
		// Clear model list
		modelList.clear();
		combo.removeAll();
		combo.add("Select model");
		combo.select(0);
		
		File[] files = new File("models").listFiles();
		for(File file : files){
			modelList.add(file);
			combo.add(file.getName().substring(0, file.getName().lastIndexOf('.')));
		}
		
	}
	
	private void refreshTable(){
		String selItem = combo.getItem(combo.getSelectionIndex());
		if(selItem.equals("Select model")){
			table.clearAll();
			table.setItemCount(0);
			return;
		}
		// Load data
		currentModel = new Model(selItem);
		Map<Task, ArrayList<Event>> tasks = currentModel.getTasks();
		
		// Clear table for data refresh
		table.clearAll();
		table.setItemCount(0);
		Iterator<Entry<Task, ArrayList<Event>>> it = tasks.entrySet().iterator();
		taskList.clear();
		
		while(it.hasNext()){
			Entry<Task, ArrayList<Event>> entry = it.next();
			TableItem item = new TableItem(table, SWT.NONE);
			taskList.add(entry.getKey());
			eventList.add(entry.getValue());
			
			item.setText(0, entry.getKey().toString());
			item.setText(1, entry.getValue().size()==0 ? "Not assigned" : "Assiged");
		}
	}
}
