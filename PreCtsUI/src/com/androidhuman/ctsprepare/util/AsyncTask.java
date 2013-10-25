package com.androidhuman.ctsprepare.util;

import org.eclipse.swt.widgets.Display;

public abstract class AsyncTask<T, U, V> {
	
	Thread thread;
	
	public final void execute(final T... params){
		Display.getDefault().syncExec(new Runnable(){
			public void run(){
				onPreExecute();
			}
		});
		
		thread = new Thread(new Runnable(){
			public void run(){
				
				if(!Thread.currentThread().isInterrupted()){
					final V result = doInBackground(params);
					Display.getDefault().syncExec(new Runnable(){
						public void run(){
							onPostExecute(result);
						}
					});
				}
				
			}
		});
		thread.start();
	}
	
	public final void updateProgress(final U progress){
		Display.getDefault().asyncExec(new Runnable(){
			public void run(){
				onProgressUpdate(progress);
			}
		});
	}
	
	
	public void cancel(){
		thread.interrupt();
	}
	
	public abstract void onPreExecute();
	public abstract V doInBackground(@SuppressWarnings("unchecked") T... params);
	public abstract void onPostExecute(V result);
	public abstract void onProgressUpdate(U progress);
}
