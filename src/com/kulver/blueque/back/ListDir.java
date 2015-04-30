package com.kulver.blueque.back;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.os.Environment;
import android.text.Html;
import android.widget.TextView;

import com.kulver.blueque.R;

public class ListDir {

	private String currPath = "";
	private String root = "";
	private TextView myPath = null;
	private List<String> item = null;
	private List<String> path = null;
	private Que que = null;
	private Context cont = null;

	public ListDir(Que que, Context cont) {
		root = Environment.getExternalStorageDirectory().getPath();
		this.que = que;
		this.cont = cont;
	}
	
	public void setMyPath(TextView myPath) {
		this.myPath = myPath;
	}
	
	public List<String> getItemList() {
		return item;
	}
	
	public List<String> getPathList() {
		return path;
	}
	
	public String getRootDir() {
		return root;
	}
	
	public String getCurrentPath() {
		return currPath;
	}
	
	public int addAllFiles() {
		File f = new File(getCurrentPath());
		File[] fArr = f.listFiles();
		int addedFileCounter = 0;
		String resp = "";
		for (int i = 0; i < fArr.length; i++) {
			if (fArr[i].isFile() && !fArr[i].isHidden()) {
				resp = que.addFileToQue(fArr[i], false);
				if (!resp.equals("")) {
					addedFileCounter++;
				}
			}
		}
		return addedFileCounter;
	}
	
    public MyArrayAdapter listDir(String dirPath) {
	   	currPath = dirPath;
	   	String[] split = dirPath.split("/");
	   	String tmp = "";
	   	for (int i = 0; i < split.length-1; i++) {
	   		tmp += split[i]+"/";
		}
		myPath.setText(Html.fromHtml("<b>Location: "+
					tmp+
		    		"<font color=\"red\">"+split[split.length-1]+"</font></b>"));
		item = new ArrayList<String>();
		path = new ArrayList<String>();
		File f = new File(dirPath);
		File[] files = f.listFiles();
	    
		if(!dirPath.equals(root)) {
			    item.add(root);
			    path.add(root);
			    item.add("../");
			    path.add(f.getParent()); 
		}
	    
	    Arrays.sort(files, fileComparator);
	    for(int i = 0; i < files.length; i++) {
	    	File file = files[i];
	    	if(!file.isHidden() && file.canRead()){
	    		path.add(file.getPath());
	    		if(file.isDirectory()){
	    			item.add(file.getName() + "/");
	    		} else {
	    			item.add(file.getName());
	    		}
	    	}
	    }

	    return new MyArrayAdapter(cont, R.layout.row, item, que);
   }

   Comparator<? super File> fileComparator = new Comparator<File>() {
   	  public int compare(File file1, File file2) {
   		  if(file1.isDirectory()){
   			  if (file2.isDirectory()){
   				  return String.valueOf(file1.getName().toLowerCase()).compareTo(file2.getName().toLowerCase());
   			  } else {
   				  return -1;
   			  }
   		  } else {
   			  if (file2.isDirectory()) {
   				  return 1;
   			  } else {
   				  return String.valueOf(file1.getName().toLowerCase()).compareTo(file2.getName().toLowerCase());
   			  }
   		  }
   	  }
   };
	
}
