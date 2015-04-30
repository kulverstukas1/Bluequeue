package com.kulver.blueque.back;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.kulver.blueque.R;
import com.kulver.blueque.front.BluequeActivity;

public class Dialogs {
/*
	private Context context = null;
	private ListActivity activity = null;
	private Dialog btDialog = null;
	private ProgressDialog progressDialog = null;
	
	public Dialogs(Context context, ListActivity activity) {
		this.context = context;
		this.activity = activity;
	}
	
	public void showErrorDialog(Exception e) {
		final AlertDialog noBtDialog = new AlertDialog.Builder(context).create();
		noBtDialog.setCancelable(false);
		noBtDialog.setTitle("Error");
		noBtDialog.setMessage(e.getMessage());
		e.printStackTrace();
		noBtDialog.setButton("Exit", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				noBtDialog.dismiss();
				System.exit(0);
			}
		});
		noBtDialog.show();
	}

    public void showQueDialog(final Que que, final ListDir listDir, final BgWork bgWork, final BluetoothMethods btMethods) {
        final Dialog listDialog = new Dialog(context, R.layout.que) {
            @Override
            public boolean onCreateOptionsMenu(Menu menu) {
            	MenuInflater inflater = activity.getMenuInflater();
            	inflater.inflate(R.menu.que_menu, menu);
            	MenuItem clearItem = menu.findItem(R.id.que_clear);
            	MenuItem sendItem = menu.findItem(R.id.send);
            	clearItem.setOnMenuItemClickListener(clearListener);
            	sendItem.setOnMenuItemClickListener(sendListener);
            	
            	super.onCreateOptionsMenu(menu);
            	
            	return true;
            }
            
            OnMenuItemClickListener clearListener = new OnMenuItemClickListener() {
				public boolean onMenuItemClick(MenuItem item) {
    				que.clearQue();
    				bgWork.updateTitle(que);
    				Toast.makeText(activity, "Queue has been cleared", Toast.LENGTH_SHORT).show();
    				dismiss();
    				activity.setListAdapter(listDir.listDir(listDir.getCurrentPath()));
					return false;
				}};
				
			OnMenuItemClickListener sendListener = new OnMenuItemClickListener() {
				public boolean onMenuItemClick(MenuItem item) {
					bgWork.turnOnBluetooth(btMethods, que, new Dialogs(context, activity));
					return false;
				}};
        };
        
        listDialog.setTitle("File queue - "+que.getNumberOfEntries()+" items; Size: "+que.calculateTotalSize());
        LayoutInflater li = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View queView = li.inflate(R.layout.que, null, false);
        listDialog.setContentView(queView);
        listDialog.setCancelable(true);
        final ListView queList = (ListView)listDialog.findViewById(R.id.que_list);
        queList.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, que.getQueAsList()));

    	ListView listView = (ListView)queView.findViewById(R.id.que_list);
    	listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				String fileName = que.removeFileFromQueByPos(arg2);
//				Toast.makeText(BluequeActivity.this, fileName+" removed from que", Toast.LENGTH_SHORT).show();
				queList.setAdapter(new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, que.getQueAsList()));
				listDialog.setTitle("File queue - "+que.getNumberOfEntries()+" items; Size: "+que.calculateTotalSize());
				bgWork.updateTitle(que);
			}
		});
    	
    	listView.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					activity.setListAdapter(listDir.listDir(listDir.getCurrentPath()));
					listDialog.dismiss();
				}
				return false;
			}
		});
        listDialog.show();
        Toast.makeText(activity, "Tap an item to remove", Toast.LENGTH_SHORT).show();
        AdView adView = (AdView)listDialog.findViewById(R.id.adViewQue);
        adView.loadAd(new AdRequest());
	};

    public void showBluetoothDialog(final BluetoothMethods btMethods, final ListView btList) {
        btDialog = new Dialog(activity, R.layout.bluetooth) {
             @Override
             public boolean onCreateOptionsMenu(Menu menu) {
             	MenuInflater inflater = activity.getMenuInflater();
             	inflater.inflate(R.menu.bluetooth_menu, menu);
             	MenuItem btSettingsItem = menu.findItem(R.id.blue_settings);
             	MenuItem scanItem = menu.findItem(R.id.scan);
             	btSettingsItem.setOnMenuItemClickListener(btSettingsListener);
             	scanItem.setOnMenuItemClickListener(btScanListener);
             	
             	return true;
             }
             
             OnMenuItemClickListener btSettingsListener = new OnMenuItemClickListener() {
 				public boolean onMenuItemClick(MenuItem item) {
 					Intent intentBluetooth = new Intent();
 				    intentBluetooth.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
 		            activity.startActivityForResult(intentBluetooth, 0);
 		            btMethods.getBtAdapter().cancelDiscovery();
 					return false;
 				}
             };
 				
 			OnMenuItemClickListener btScanListener = new OnMenuItemClickListener() {
 				public boolean onMenuItemClick(MenuItem item) {
 					if (btMethods.getBtAdapter().isDiscovering()) {
 						btMethods.getBtAdapter().cancelDiscovery();
 					}
 					progressDialog = new ProgressDialog(activity);
 					progressDialog.setCancelable(false);
 					progressDialog.setMessage("Scanning for new devices...");
 					progressDialog.setButton("Cancel", new DialogInterface.OnClickListener() {
 						public void onClick(DialogInterface dialog, int which) {
 				        	if (btMethods.getBtAdapter().isDiscovering()) {
 				        		btMethods.getBtAdapter().cancelDiscovery();
 				        	}
 				        	btList.setAdapter(btMethods.getScannedDevices(activity));
 				        	progressDialog.dismiss();
 						}
 					});
 					progressDialog.show();
 					btMethods.getBtAdapter().startDiscovery();
 					return false;
 				}
 			};
         };
         btDialog.setTitle("Select a device");
         LayoutInflater li = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         View btView = li.inflate(R.layout.bluetooth, null, false);
         btDialog.setContentView(btView);
         btDialog.setCancelable(true);
         btList = (ListView)btView.findViewById(R.id.bluetooth_list);
         btList.setAdapter(btMethods.getPairedDevices(BluequeActivity.this));
         btList.setOnItemClickListener(new OnItemClickListener() {
 			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
 				transferDialog.constructTransferDialog();
 				transferDialog.showDialog();
 				currentDeviceIndex = arg2;
 				btMethods.sendFile(BluequeActivity.this, currentDeviceIndex, transferDialog.getProgress());
 			}
         });
         
         btDialog.show();
         AdView adView = (AdView)btDialog.findViewById(R.id.adViewBt);
         adView.loadAd(new AdRequest());
 	}
    
    public boolean isBtDialogShowing() {
    	return ((btDialog != null) && btDialog.isShowing());
    }
    
    public void dismissBtDialog() {
    	if (isBtDialogShowing()) {
    		btDialog.dismiss();
    	}
    }
    */
}
