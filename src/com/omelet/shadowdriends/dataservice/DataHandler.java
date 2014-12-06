package com.omelet.shadowdriends.dataservice;

import java.util.ArrayList;

import com.omelet.shadowdriends.model.PackItem;

public class DataHandler {

	ArrayList<PackItem> mListPacks = new ArrayList<PackItem>();

	public void setData(ArrayList<PackItem> temp) {
		mListPacks = temp;
	}

	public ArrayList<PackItem> getData() {
		return mListPacks;
	}
}
