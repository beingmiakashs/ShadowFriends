package com.omelet.shadowdriends;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.omelet.shadowdriends.R;
import com.omelet.shadowdriends.adapter.PickersListAdapter;
import com.omelet.shadowdriends.dataservice.LoadPickersList;
import com.omelet.shadowdriends.dataservice.PickerListDataHandler;
import com.omelet.shadowdriends.model.PickerItem;
import com.omelet.shadowfriends.util.GlobalConstant;
import com.omelet.shadowfriends.util.Network;

public class PickersListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<PickerListDataHandler>{
	private PickersListAdapter adapter;
	private SharedPreferences preferences;
	private TelephonyManager telephonyManager;
	private PhoneStateListener listener;
	private Network netCheck;
	private static Activity mActivity;
	private static ListView mListView;
	public PickersListFragment(){} // this Fragment will be called from MainActivity
	
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
		mListView.setBackground(getResources().getDrawable(R.drawable.hajj_bg_blue));
		preferences = PreferenceManager.getDefaultSharedPreferences(mActivity.getApplicationContext());
		
		setListAdapter(adapter);
		setListShown(false);
		if(netCheck.isNetworkConnected()){
			getActivity().getSupportLoaderManager().initLoader((int)System.currentTimeMillis(), null, this);
		}
		else{
			GlobalConstant.showMessage(getActivity(), "Network connection is not avaiable");
		}
	}

	@Override
	public Loader<PickerListDataHandler> onCreateLoader(int arg0, Bundle arg1) {
		LoadPickersList asynChro = new LoadPickersList(PickersListFragment.this.getActivity(), mActivity, mListView);
		asynChro.setUrlToGetAllPacks("http://omleteit.com/apps/pickpack/pickersList.php");
		asynChro.forceLoad();
		return asynChro;
	}

	@Override
	public void onLoadFinished(Loader<PickerListDataHandler> arg0, PickerListDataHandler arg1) {
		adapter = new PickersListAdapter(getActivity(), arg1.getData());
		mListView.setAdapter(new PickersListAdapter(getActivity(), arg1.getData()));
		setListShown(true);
	}

	@Override
	public void onLoaderReset(Loader<PickerListDataHandler> arg0) {
		mListView.setAdapter(null);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		
	  	/*Intent detailsIntent = new Intent(getActivity(), DetailsActivity.class);
	  	PackItem item = (PackItem) adapter.getItem(position);
	  	detailsIntent.putExtra(FetchDetails.TAG_RID, item.getRid());
	  	detailsIntent.putExtra(FetchDetails.TAG_TITLE, item.getPackTitle());
	  	detailsIntent.putExtra(FetchDetails.TAG_DES, item.getPackDescription());
	  	detailsIntent.putExtra(GlobalConstant.ACTION_TYPE, item.getCurrentStatus());
	  	detailsIntent.putExtra(GlobalConstant.PACK_OWNER, item.getOwner());
	  	getActivity().startActivity(detailsIntent);*/
		PickerItem pickerItem = (PickerItem) adapter.getItem(position);
		if(pickerItem.getMobileNumber().length()>2){
			makeCall(pickerItem.getMobileNumber());
		}
	}
	
	public void makeCall(String number) {
		String phoneCallUri = "tel:";
		phoneCallUri += number;

		// Get the telephony manager
		telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);

		listener = new PhoneStateListener() {

			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				String stateString = "N/A";
				switch (state) {
				case TelephonyManager.CALL_STATE_IDLE:
					stateString = "Idle";
					break;
				case TelephonyManager.CALL_STATE_OFFHOOK:
					stateString = "Off Hook";
					break;
				case TelephonyManager.CALL_STATE_RINGING:
					stateString = "Ringing";
					break;
				}
			}
		};
		telephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

		Intent phoneCallIntent = new Intent(Intent.ACTION_CALL);
		phoneCallIntent.setData(Uri.parse(phoneCallUri));
		this.startActivity(phoneCallIntent);

	}
}