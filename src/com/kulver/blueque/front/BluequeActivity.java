package com.kulver.blueque.front;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.kulver.blueque.R;
import com.kulver.blueque.back.BluetoothShare;
import com.kulver.blueque.back.ListDir;
import com.kulver.blueque.back.MyArrayAdapter;
import com.kulver.blueque.back.Que;
import com.kulver.blueque.back.BluetoothMethods;
import com.kulver.blueque.back.TransferDialog;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

public class BluequeActivity extends ListActivity {
	
	/**
	 * Global variables here
	 */
	private ColorStateList oldColor = null;
	private Que que = null;
	private BluetoothMethods btMethods = null;
	private ListDir listDir = null;
	private ListView btList = null;
	private ProgressDialog progressDialog = null;
	private TransferDialog transferDialog = null;
	private Dialog btDialog = null;
	private int currentDeviceIndex = 0;
	
	
	
	/**
	 * Activity startup lines here
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        AdView adView = (AdView)findViewById(R.id.adView);
        adView.loadAd(new AdRequest());
        
        que = new Que();
        try {
			btMethods = new BluetoothMethods(que);
		} catch (Exception e) {
			final AlertDialog noBtDialog = new AlertDialog.Builder(this).create();
			noBtDialog.setCancelable(false);
			noBtDialog.setTitle("Error");
			noBtDialog.setMessage(e.getMessage());
			e.printStackTrace();
			noBtDialog.setButton("Exit", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if (noBtDialog.isShowing()) {
						noBtDialog.dismiss();
					}
					BluequeActivity.this.finish();
				}
			});
			noBtDialog.show();
		}
        // If the device does not support Bluetooth then the app won't start
        if (btMethods != null) {
	        oldColor = ((TextView)this.findViewById(R.id.path)).getTextColors(); // this sets the default text color
			updateTitle();
	        
        	listDir = new ListDir(que, this);
        	transferDialog = new TransferDialog(this, que);
	        listDir.setMyPath((TextView)findViewById(R.id.path));
	        setListAdapter(listDir.listDir(listDir.getRootDir()));
	        
			IntentFilter filter = new IntentFilter();
			filter.addAction(BluetoothDevice.ACTION_FOUND);
			filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
			filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
			filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
			registerReceiver(mReceiver, filter);
        }
    }
	
    
    
    
    
    /**
     * Que dialog consruction here
     */
    private void showQueDialog() {
        final Dialog listDialog = new Dialog(this, R.layout.que) {
            @Override
            public boolean onCreateOptionsMenu(Menu menu) {
            	MenuInflater inflater = getMenuInflater();
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
    				updateTitle();
    				Toast.makeText(BluequeActivity.this, "Queue has been cleared", Toast.LENGTH_SHORT).show();
    				dismiss();
    				setListAdapter(listDir.listDir(listDir.getCurrentPath()));
					return false;
				}};
				
			OnMenuItemClickListener sendListener = new OnMenuItemClickListener() {
				public boolean onMenuItemClick(MenuItem item) {
					turnOnBluetooth();
					return false;
				}};
        };
        
        listDialog.setTitle("File queue - "+que.getNumberOfEntries()+" items; Size: "+que.calculateTotalSize());
        LayoutInflater li = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View queView = li.inflate(R.layout.que, null, false);
        listDialog.setContentView(queView);
        listDialog.setCancelable(true);
        final ListView queList = (ListView)listDialog.findViewById(R.id.que_list);
        queList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, que.getQueAsList()));

    	ListView listView = (ListView)queView.findViewById(R.id.que_list);
    	listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				String fileName = que.removeFileFromQueByPos(arg2);
//				Toast.makeText(BluequeActivity.this, fileName+" removed from que", Toast.LENGTH_SHORT).show();
				queList.setAdapter(new ArrayAdapter<String>(BluequeActivity.this, android.R.layout.simple_list_item_1, que.getQueAsList()));
				listDialog.setTitle("File queue - "+que.getNumberOfEntries()+" items; Size: "+que.calculateTotalSize());
				updateTitle();
			}
		});
    	
    	listView.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					setListAdapter(listDir.listDir(listDir.getCurrentPath()));
					if (listDialog.isShowing()) {
						listDialog.dismiss();
					}
				}
				return false;
			}
		});
        listDialog.show();
        Toast.makeText(BluequeActivity.this, "Tap an item to remove", Toast.LENGTH_SHORT).show();
        
        AdView adView = (AdView)listDialog.findViewById(R.id.adViewQue);
        adView.loadAd(new AdRequest());
	};

    
    
    private void showBluetoothDialog() {
       btDialog = new Dialog(BluequeActivity.this, R.layout.bluetooth) {
            @Override
            public boolean onCreateOptionsMenu(Menu menu) {
            	MenuInflater inflater = getMenuInflater();
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
		            startActivityForResult(intentBluetooth, 0);
		            btMethods.getBtAdapter().cancelDiscovery();
					return false;
				}
            };
				
			OnMenuItemClickListener btScanListener = new OnMenuItemClickListener() {
				public boolean onMenuItemClick(MenuItem item) {
					if (btMethods.getBtAdapter().isDiscovering()) {
						btMethods.getBtAdapter().cancelDiscovery();
					}
					progressDialog = new ProgressDialog(BluequeActivity.this);
					progressDialog.setCancelable(false);
					progressDialog.setMessage("Scanning for new devices...");
					progressDialog.setButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
				        	if (btMethods.getBtAdapter().isDiscovering()) {
				        		btMethods.getBtAdapter().cancelDiscovery();
								progressDialog.show();
								btMethods.getBtAdapter().startDiscovery();
				        	}
				        	btList.setAdapter(btMethods.getScannedDevices(BluequeActivity.this));
				        	if (progressDialog.isShowing()) {
				        		progressDialog.dismiss();
				        	}
						}
					});
					progressDialog.show();
					btMethods.getBtAdapter().startDiscovery();
					return false;
				}
			};
        };
        btDialog.setTitle("Select a device");
        LayoutInflater li = (LayoutInflater) BluequeActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View btView = li.inflate(R.layout.bluetooth, null, false);
        btDialog.setContentView(btView);
        btDialog.setCancelable(true);
        BluequeActivity.this.btList = (ListView)btView.findViewById(R.id.bluetooth_list);
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
    	
    
    
    
    /**
     * Method that executes when bluetooth settings window is closed
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
        if (requestCode == 0) {
        	btList.setAdapter(btMethods.getPairedDevices(this));
        }
    } 
    
    
    
    
    /**
     * Method that updates the title
     */
    private void updateTitle() {
    	this.setTitle(this.getString(R.string.app_name)+" - "+que.getNumberOfEntries()+" items in queue");
    }


    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = this.getMenuInflater();
    	inflater.inflate(R.menu.menu, menu);
    	
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    		case R.id.about:
    			final AlertDialog aboutDialog = new AlertDialog.Builder(this).create();
    			aboutDialog.setTitle("About");
    			aboutDialog.setMessage(Html.fromHtml(this.getString(R.string.about_text)));
    			aboutDialog.setIcon(R.drawable.ic_launcher);
    			aboutDialog.setButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (aboutDialog.isShowing()) {
							aboutDialog.dismiss();
						}
					}});
    			aboutDialog.show();
    			return true;
    		case R.id.que:
    			if (que.getNumberOfEntries() > 0) {
    				showQueDialog();
    			} else {
    				Toast.makeText(this, "Queue is empty", Toast.LENGTH_SHORT).show();
    			}
    			return true;
    		case R.id.clear:
				que.clearQue();
				updateTitle();
				Toast.makeText(this, "Queue has been cleared", Toast.LENGTH_SHORT).show();
				this.setListAdapter(new MyArrayAdapter(this, R.layout.row, listDir.getItemList(), que));
				return true;
    		case R.id.addAll:
    			int counter = listDir.addAllFiles();
				updateTitle();
				Toast.makeText(this, counter+" files added to queue", Toast.LENGTH_SHORT).show();
				setListAdapter(new MyArrayAdapter(this, R.layout.row, listDir.getItemList(), que));
				return true;
    		case R.id.send_main:
    			if (que.getNumberOfEntries() > 0) {
    				turnOnBluetooth();
    			} else {
    				Toast.makeText(this, "Queue is empty", Toast.LENGTH_SHORT).show();
    			}
    			return true;
    		default:
    			return super.onOptionsItemSelected(item);
    	}
    }
	    
    
    
    
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		File file = new File(listDir.getPathList().get(position));
		if (file.isDirectory()) {
			if (file.canRead()) {
				setListAdapter(listDir.listDir(listDir.getPathList().get(position)));
			} else {
				final AlertDialog cannotReadDialog = new AlertDialog.Builder(this).create();
				cannotReadDialog.setIcon(R.drawable.ic_launcher);
				cannotReadDialog.setTitle("[" + file.getName() + "] folder can't be read!");
				cannotReadDialog.setButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (cannotReadDialog.isShowing()) {
							cannotReadDialog.dismiss();
						}
					}
				});
				cannotReadDialog.show();
			}
		} else {
			String response = que.addFileToQue(file, true);
			updateTitle();
			if (response.equals("added")) {
				oldColor = ((TextView)v.findViewById(R.id.rowtext)).getTextColors();
				((TextView)v.findViewById(R.id.rowtext)).setTextColor(Color.GREEN);
				((ImageView)v.findViewById(R.id.icon)).setImageResource(R.drawable.ic_added);
//				Toast.makeText(this, file.getName()+" added to queue", Toast.LENGTH_SHORT).show();
			} else if (response.equals("removed")) {
				((TextView)v.findViewById(R.id.rowtext)).setTextColor(oldColor);
				((ImageView)v.findViewById(R.id.icon)).setImageResource(R.drawable.ic_file);
//				Toast.makeText(this, file.getName()+" removed from queue", Toast.LENGTH_SHORT).show();
			}
		}
	}

	
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		super.onKeyDown(keyCode, event);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	Intent goHome = new Intent(Intent.ACTION_MAIN);
        	goHome.addCategory(Intent.CATEGORY_HOME);
        	goHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	startActivity(goHome);
        	return true;
        }
        return false;
	}
	
	
	
	
    private class EnableBTTask extends AsyncTask<Void, Void, Void> {
    	private ProgressDialog progressDialog = null;
    	
        @Override
        protected void onPreExecute() {
        	super.onPreExecute();
    		progressDialog = ProgressDialog.show(BluequeActivity.this, "", "Turning on bluetooth...", true);
    		
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
    		if (progressDialog.isShowing()) {
    			progressDialog.dismiss();
    		}
			if (que.getNumberOfEntries() > 0) {
				showBluetoothDialog();
			}
    	}
    }
    
    
    
	private void turnOnBluetooth() {
		if (!btMethods.isBluetoothEnabled()) {
			final AlertDialog enableBtDialog = new AlertDialog.Builder(BluequeActivity.this).create();
			enableBtDialog.setTitle("Enable bluetooth");
			enableBtDialog.setMessage("Bluetooth is disabled.\nWould you like to enable it?");
			enableBtDialog.setButton2("No", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if (enableBtDialog.isShowing()) {
						enableBtDialog.dismiss();
					}
				}
			});
			enableBtDialog.setButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					new EnableBTTask().execute();
				}
			});
			enableBtDialog.show();
		} else if (que.getNumberOfEntries() == 0) {
			Toast.makeText(this, "Queue is empty", Toast.LENGTH_SHORT).show();
		} else {
			if (que.getNumberOfEntries() > 0) {
				showBluetoothDialog();
			}
		}
	}
    
	
    
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
	    public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	            btMethods.addDevice(device);
	            btList.setAdapter(btMethods.getScannedDevices(BluequeActivity.this));
	        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
	        	if (btMethods.getBtAdapter().isDiscovering()) {
	        		btMethods.getBtAdapter().cancelDiscovery();
	        	}
	        	btList.setAdapter(btMethods.getScannedDevices(BluequeActivity.this));
	        	if ((progressDialog != null) && progressDialog.isShowing()) {
	        		progressDialog.dismiss();
	        	}
	        } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
	        	transferDialog.increaseProgress();
	        	transferDialog.updateDialog();
	        	if (que.getNumberOfEntries() > transferDialog.getProgress()) {
	        		btMethods.sendFile(BluequeActivity.this, currentDeviceIndex, transferDialog.getProgress());
	        	} else {
	        		if (transferDialog.isShowing()) {
	        			transferDialog.dismissDialog();
	        		}
	        	}
	        } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
	        	if (btMethods.getBtAdapter().getState() == BluetoothAdapter.STATE_OFF) {
	        		if ((btDialog != null) && btDialog.isShowing()) {
	        			if (btDialog.isShowing()) {
	        				btDialog.dismiss();
	        			}
	        		}
	        		turnOnBluetooth();
	        	}
	        }
	    }
	};
}