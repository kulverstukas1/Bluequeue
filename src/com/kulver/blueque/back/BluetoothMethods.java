package com.kulver.blueque.back;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import com.kulver.blueque.R;
import com.kulver.blueque.front.BluequeActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

public class BluetoothMethods {

	private BluetoothAdapter btAdapter = null;
	private ArrayList<BluetoothDevice> arrList = null;
	private Que que = null;
	

	
	public BluetoothMethods(Que que) throws Exception {
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		if (btAdapter == null) {
			throw new Exception("Bluetooth is not supported on this device");
		}
		arrList = new ArrayList<BluetoothDevice>();
		this.que = que;
	}
	
	public boolean isBluetoothEnabled() {
		
		return btAdapter.isEnabled();
	}
	
	public void enableBt() {
		btAdapter.enable();
	}
	
	public BluetoothAdapter getBtAdapter() {
		return btAdapter;
	}
	
	public ArrayList<BluetoothDevice> getDeviceList() {
		return arrList;
	}

	public MyBluetoothArrayAdapter getPairedDevices(Context cont) {
		arrList.clear();
		Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
		if (pairedDevices.size() > 0) {
		    for (BluetoothDevice device : pairedDevices) {
		        arrList.add(device);
		    }
		}
		return new MyBluetoothArrayAdapter(cont, R.layout.bluetooth_row, arrList);
	}
	
	public MyBluetoothArrayAdapter getScannedDevices(Context cont) {
		return new MyBluetoothArrayAdapter(cont, R.layout.bluetooth_row, arrList);
	}
	
	public void sendFile(Context cont, int deviceIndex, int file) {
		ContentValues values = new ContentValues();
		String address = arrList.get(deviceIndex).getAddress();
		values.put(BluetoothShare.URI, Uri.fromFile(que.getQueAsFileList().get(file)).toString());
		values.put(BluetoothShare.DESTINATION, address);
		values.put(BluetoothShare.DIRECTION, BluetoothShare.DIRECTION_OUTBOUND);
		Long ts = System.currentTimeMillis();
		values.put(BluetoothShare.TIMESTAMP, ts);
		Uri contentUri = cont.getContentResolver().insert(BluetoothShare.CONTENT_URI, values);
	}
	
	public void addDevice(BluetoothDevice device) {
		if (!arrList.contains(device)) {
			arrList.add(device);
		}
	}
}
