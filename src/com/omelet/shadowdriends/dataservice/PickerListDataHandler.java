package com.omelet.shadowdriends.dataservice;

import java.util.ArrayList;

import com.omelet.shadowdriends.model.PackItem;
import com.omelet.shadowdriends.model.PickerItem;

public class PickerListDataHandler {

	ArrayList<PickerItem> mListPacks = new ArrayList<PickerItem>();

	public void setData(ArrayList<PickerItem> temp) {
		mListPacks = temp;
	}

	public ArrayList<PickerItem> getData() {
		return mListPacks;
	}
}
