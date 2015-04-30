package com.kulver.blueque.back;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Que {

	private ArrayList<File> fileQue = null;
	
	public Que() {
		fileQue = new ArrayList<File>();
	}
	
	public String addFileToQue(File file, boolean remove) {
		String rtn = "";
		if (remove && fileQue.contains(file)) {
			fileQue.remove(file);
			rtn = "removed";
		} else {
			if (!fileQue.contains(file)) {
				fileQue.add(file);
				rtn = "added";
			}
		}
		return rtn;
	}
	
	public void removeFileFromQueByName(String fileName) {
		int arrayLen = fileQue.size();
		for (int i = 0; i < arrayLen; i++) {
			if (fileQue.get(i).getName().equals(fileName)) {
				fileQue.remove(i);
			}
		}
	}
	
	public void removeFileFromQueByObject(File file) {
		fileQue.remove(file);
	}
	
	public String removeFileFromQueByPos(int pos) {
		// return a file name of the removed file
		return fileQue.remove(pos).getName();
	}
	
	public int getNumberOfEntries() {
		return fileQue.size();
	}
	
	public boolean checkIfInList(String fileName) {
		int arrayLen = fileQue.size();
		boolean inList = false;
		for (int i = 0; i < arrayLen; i++) {
			if (fileQue.get(i).getName().equals(fileName)) {
				inList = true;
				break;
			}
		}
		return inList;
	}
	
	public ArrayList<String> getQueAsList() {
		ArrayList<String> queList = new ArrayList<String>();
		int listSize = fileQue.size();
		for (int i = 0; i < listSize; i++) {
			queList.add(fileQue.get(i).getName());
		}
		return queList;
	}
	
	public ArrayList<File> getQueAsFileList() {
		return fileQue;
	}
	
	public String calculateTotalSize() {
		String returnStr = "";
		float totalSize = 0;
		for (int i = 0; i < fileQue.size(); i++) {
			totalSize += fileQue.get(i).length();
		}
		// convert bytes to kilobytes
		totalSize = totalSize / 1024.2f;
		if (totalSize > 1024) { // if there are over a megabyte
			totalSize = totalSize / 1024.2f; // convert kilobytes to megabytes
			returnStr = String.format("%.2f", totalSize)+" MB";
		} else {
			returnStr = String.format("%.2f", totalSize)+" KB";
		}
		
		return returnStr;
	}
	
	public void clearQue() {
		fileQue.clear();
	}
}
