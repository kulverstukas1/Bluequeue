package com.kulver.blueque.back;

import java.util.ArrayList;
import java.util.List;

import com.kulver.blueque.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;

public class MyBluetoothArrayAdapter extends ArrayAdapter {

	private ArrayList<BluetoothDevice> deviceList = null;
	
	public MyBluetoothArrayAdapter(Context context, int textViewResourceId, ArrayList objects) {
		super(context, textViewResourceId, objects);
		deviceList = objects;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = LayoutInflater.from(this.getContext()).inflate(R.layout.bluetooth_row, null);
		TextView deviceNameLabel = (TextView)row.findViewById(R.id.bluetooth_name);
		TextView deviceStateLabel = (TextView)row.findViewById(R.id.bluetooth_state);
		TextView deviceAddressLabel = (TextView)row.findViewById(R.id.bluetooth_address);
		deviceNameLabel.setText(deviceList.get(position).getName());
		String pairedText = "";
		if (deviceList.get(position).getBondState() == deviceList.get(position).BOND_BONDED) {
			deviceStateLabel.setText("Paired");
		} else if (deviceList.get(position).getBondState() == deviceList.get(position).BOND_NONE) {
			deviceStateLabel.setText("Not Paired");
		}
		deviceAddressLabel.setText(deviceList.get(position).getAddress());
		
		return row;
	}

}
