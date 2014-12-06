package com.omelet.shadowfriends;

import com.omelet.sa.pickmypack.R;
import com.omelet.shadowdriends.adapter.PacksListAdapter;
import com.omelet.shadowdriends.dataservice.DataHandler;
import com.omelet.shadowdriends.dataservice.FetchDetails;
import com.omelet.shadowdriends.dataservice.LoadPacksList;
import com.omelet.shadowdriends.location.GPSTracker;
import com.omelet.shadowdriends.model.PackItem;
import com.omelet.shadowfriends.util.GlobalConstant;
import com.omelet.shadowfriends.util.Network;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class SafeZoneListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<DataHandler>{
	private PacksListAdapter adapter;
	private SharedPreferences preferences;
	private Network netCheck;
	private static Activity mActivity;
	private static ListView mListView;
	public SafeZoneListFragment(){} // this Fragment will be called from MainActivity
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    return super.onCreateView(inflater, container, savedInstanceState);
    }

	@SuppressLint("NewApi")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		netCheck = new Network(getActivity());
		
		mActivity = getActivity();
		mListView = getListView();
		mListView.setBackgroundDrawable(getResources().getDrawable(R.drawable.hajj_bg_blue));
		preferences = PreferenceManager.getDefaultSharedPreferences(mActivity.getApplicationContext());
		
		setListAdapter(adapter);
		setListShown(false);
		if(netCheck.isNetworkConnected()){
			getActivity().getSupportLoaderManager().initLoader(10, null, this);
		}
		else{
			GlobalConstant.showMessage(getActivity(), "Network connection is not avaiable");
		}
	}

	@Override
	public Loader<DataHandler> onCreateLoader(int arg0, Bundle arg1) {
		LoadPacksList asynChro = new LoadPacksList(SafeZoneListFragment.this.getActivity(), mActivity, mListView);
		String mobileNumber = preferences.getString(GlobalConstant.USER_MOBILE_NUMBER, "");
		asynChro.setMobileNumber(mobileNumber);
		asynChro.setUrlToGetAllPacks("http://omleteit.com/apps/pickpack/packsList.php");
		asynChro.forceLoad();
		return asynChro;
	}

	@Override
	public void onLoadFinished(Loader<DataHandler> arg0, DataHandler arg1) {
		adapter = new PacksListAdapter(getActivity(), arg1.getData());
		mListView.setAdapter(new PacksListAdapter(getActivity(), arg1.getData()));
		setListShown(true);
	}

	@Override
	public void onLoaderReset(Loader<DataHandler> arg0) {
		mListView.setAdapter(null);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		
	  	Intent detailsIntent = new Intent(getActivity(), DetailsActivity.class);
	  	PackItem item = (PackItem) adapter.getItem(position);
	  	detailsIntent.putExtra(FetchDetails.TAG_RID, item.getRid());
	  	detailsIntent.putExtra(FetchDetails.TAG_TITLE, item.getPackTitle());
	  	detailsIntent.putExtra(FetchDetails.TAG_DES, item.getPackDescription());
	  	detailsIntent.putExtra(GlobalConstant.ACTION_TYPE, item.getCurrentStatus());
	  	detailsIntent.putExtra(GlobalConstant.PACK_OWNER, item.getOwner());
	  	startActivityForResult(detailsIntent, 1);
	}
}