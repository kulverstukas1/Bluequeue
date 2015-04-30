package com.kulver.blueque.back;

import com.kulver.blueque.R;
import com.kulver.blueque.front.BluequeActivity;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;

public class BgWork {
/*
	private Context context = null;
	private ListActivity activity = null;
	
	private BluetoothMethods btMethods = null;
	private Que que = null;
	private Dialogs dialogs = null;
	
	public BgWork(Context context, ListActivity activity) {
		this.context = context;
		this.activity = activity;
	}
	
    public void updateTitle(Que que) {
    	activity.setTitle(context.getString(R.string.app_name)+" - "+que.getNumberOfEntries()+" items in queue");
    }
    
	public void turnOnBluetooth(final BluetoothMethods btMethods, final Que que, final Dialogs dialogs) {
		if (!btMethods.isBluetoothEnabled()) {
			final AlertDialog enableBtDialog = new AlertDialog.Builder(activity).create();
			enableBtDialog.setTitle("Enable bluetooth");
			enableBtDialog.setMessage("Bluetooth is disabled.\nWould you like to enable it?");
			enableBtDialog.setButton2("No", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					enableBtDialog.dismiss();
				}
			});
			enableBtDialog.setButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					new EnableBTTask().execute();
				}
			});
			enableBtDialog.show();
		} else if (que.getNumberOfEntries() == 0) {
			Toast.makeText(context, "Queue is empty", Toast.LENGTH_SHORT).show();
		} else {
			if (que.getNumberOfEntries() > 0) {
				this.btMethods = btMethods;
				this.que = que;
				this.dialogs = dialogs;
				dialogs.showBluetoothDialog();
			}
		}
	}
	
    private class EnableBTTask extends AsyncTask<Void, Void, Void> {
    	private ProgressDialog progressDialog = null;
    	
        @Override
        protected void onPreExecute() {
        	super.onPreExecute();
    		progressDialog = ProgressDialog.show(activity, "", "Turning on bluetooth...", true);
    		
        }
        
        @Override
        protected Void doInBackground(Void... params) {
    		try {
    			btMethods.enableBt();
        		while (btMethods.getBtAdapter().getState() == btMethods.getBtAdapter().STATE_TURNING_ON) {
        			// do nothing
        		}
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
        	return null;
        }
        
    	@Override
    	protected void onPostExecute(Void result) {
    		super.onPostExecute(result);
    		progressDialog.dismiss();
			if (que.getNumberOfEntries() > 0) {
				dialogs.showBluetoothDialog();
			}
    	}
    }
	*/
}
