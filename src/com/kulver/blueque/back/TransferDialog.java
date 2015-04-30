package com.kulver.blueque.back;

import com.kulver.blueque.R;
import com.kulver.blueque.front.BluequeActivity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class TransferDialog {

	private Dialog transferDialog = null;
	private Context context = null;
	private TextView transferStatusFiles = null;
	private TextView transferStatusCurrFile = null;
	private ProgressBar progressBar = null;
	private Que que = null;
	private int progressInFiles = 0;
	private boolean isShowing = false;
	
	public TransferDialog(Context context, Que que) {
		this.context = context;
		this.que = que;
	}
	
	public void constructTransferDialog() {
		transferDialog = new Dialog(context);
		transferDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		transferDialog.setContentView(R.layout.transfer_progress);
		transferDialog.getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		transferDialog.setCancelable(false);
		
		transferStatusFiles = (TextView) transferDialog.findViewById(R.id.transfer_status_files);
		transferStatusCurrFile = (TextView) transferDialog.findViewById(R.id.transfer_status_curr_file);
		progressBar = (ProgressBar) transferDialog.findViewById(R.id.transfer_progressBar);
		Button cancelButton = (Button) transferDialog.findViewById(R.id.cancel_transfer_button);
		cancelButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dismissDialog();
			}
		});
	}
	
	public void showDialog() {
		int filesInQue = que.getNumberOfEntries();
		progressBar.setMax(filesInQue);
		progressInFiles = 0;  // reset the progress
		progressBar.setProgress(progressInFiles);
		transferStatusFiles.setText("0 / "+filesInQue);
		transferDialog.show();
		updateDialog();
		isShowing = true;
	}
	
	public void dismissDialog() {
		transferDialog.dismiss();
		// has to be set to max out the que and emulate the end of transfer
		progressInFiles = que.getNumberOfEntries()-1;
		isShowing = false;
	}
	
	public int getProgress() {
		return progressInFiles;
	}
	
	public void increaseProgress() {
		progressInFiles++;
	}
	
	public void updateDialog() {
		transferStatusFiles.setText(progressInFiles+" / "+que.getNumberOfEntries());
		transferStatusCurrFile.setText("File: "+que.getQueAsList().get((progressInFiles > que.getNumberOfEntries()-1) ? progressInFiles-1 : progressInFiles));
		progressBar.setProgress(progressInFiles);
	}
	
	public boolean isShowing() {
		return isShowing;
	}
}
