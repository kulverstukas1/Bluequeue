package com.kulver.blueque.back;

import java.util.List;

import com.kulver.blueque.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyArrayAdapter extends ArrayAdapter {

	private List<String> rowTexts = null;
	private Que que = null;
	
	public MyArrayAdapter(Context context, int textViewResourceId, List objects, Que que) {
		super(context, textViewResourceId, objects);
		rowTexts = objects;
		this.que = que;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View row = LayoutInflater.from(this.getContext()).inflate(R.layout.row, null);
		TextView fileName = (TextView)row.findViewById(R.id.rowtext);
		fileName.setText(rowTexts.get(position));
		ImageView rowImage = (ImageView)row.findViewById(R.id.icon);
		if (rowTexts.get(position).startsWith("/")) {
			rowImage.setImageResource(R.drawable.ic_home);
		} else if (rowTexts.get(position).equals("../")) {
			rowImage.setImageResource(R.drawable.ic_dir_up);
		} else if (rowTexts.get(position).endsWith("/")) {
			rowImage.setImageResource(R.drawable.ic_folder);
		} else {
			if (que.checkIfInList(rowTexts.get(position))) {
				rowImage.setImageResource(R.drawable.ic_added);
				fileName.setTextColor(Color.GREEN);
			} else {
				rowImage.setImageResource(R.drawable.ic_file);
			}
		}
		
		return row;
	}
}
