package com.omelet.shadowdriends;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.omelet.shadowdriends.R;
import com.omelet.shadowdriends.adapter.NavDrawerListAdapter;
import com.omelet.shadowdriends.basic.SexualHarrasmentLawActivity;
import com.omelet.shadowdriends.createpack.CreateWalkWithMeAcrivity;
import com.omelet.shadowdriends.createpack.DangerMapAcrivity;
import com.omelet.shadowdriends.createpack.ShowTrackMeAcrivity;
import com.omelet.shadowdriends.createpack.ShowWalkWithMeAcrivity;
import com.omelet.shadowdriends.emergency.EmergencyBoard;
import com.omelet.shadowdriends.model.NavDrawerItem;
import com.omelet.shadowdriends.pushnotification.GCMRegisterActivity;
import com.omelet.shadowfriends.util.GlobalConstant;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;
import com.sromku.simple.fb.listeners.OnLogoutListener;

public class MainActivity extends FragmentActivity {
	
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	// NavigationDrawer title "Nasdaq" in this example
	private CharSequence mDrawerTitle;

	//  App title "Navigation Drawer" in this example 
	private CharSequence mTitle;

	// slider menu items details 
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;
	private SharedPreferences preferences;
	private Editor editor;
	private int fragmentID;
	private OnLogoutListener onLogoutListener;
	private SimpleFacebook mSimpleFacebook;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		editor = preferences.edit();
		
		mTitle = mDrawerTitle = getTitle();

		// getting items of slider from array
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

		// getting Navigation drawer icons from res 
		navMenuIcons = getResources()
				.obtainTypedArray(R.array.nav_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		navDrawerItems = new ArrayList<NavDrawerItem>();

		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[6], navMenuIcons.getResourceId(6, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[7], navMenuIcons.getResourceId(7, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[8], navMenuIcons.getResourceId(8, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[9], navMenuIcons.getResourceId(9, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[10], navMenuIcons.getResourceId(10, -1)));

		navDrawerItems.add(new NavDrawerItem(navMenuTitles[11], navMenuIcons.getResourceId(11, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[12], navMenuIcons.getResourceId(12, -1)));
		

		// Recycle array
		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// setting list adapter for Navigation Drawer
		adapter = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems);
		mDrawerList.setAdapter(adapter);

		// Enable action bar icon_luncher as toggle Home Button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.app_name, R.string.app_name) {
			
					public void onDrawerClosed(View view) {
						getActionBar().setTitle(mTitle);
						invalidateOptionsMenu(); //Setting, Refresh and Rate App
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
					invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		fragmentID = getIntent().getIntExtra("fragmentID", 0);
		displayView(fragmentID);
		/*if (savedInstanceState == null) {
			  displayView(0);
		}*/
		
		Permission[] permissions = new Permission[] {
        	    Permission.USER_PHOTOS,
        	    Permission.USER_FRIENDS,
        	    Permission.USER_ABOUT_ME,
        	    Permission.EMAIL,
        	    Permission.PUBLISH_ACTION
        	};
        SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
        .setAppId("1397110090516441")
        .setNamespace("infinity_codewin")
        .setPermissions(permissions)
        .build();
        SimpleFacebook.setConfiguration(configuration);
        
        mSimpleFacebook = SimpleFacebook.getInstance();
        setLogout();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mSimpleFacebook = SimpleFacebook.getInstance(this);
	}
	
	/**
	 * Slider menu item click listener
	 * */
	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected item
			displayView(position);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//  title/icon
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	//called when invalidateOptionsMenu() invoke 
	 
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if Navigation drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	private void displayView(int position) {
		// update the main content with called Fragment
		Fragment fragment = null;
		Intent in;
		switch (position) {
		case 0:
			fragment = new SafeZoneListFragment();
			fragmentID = 0;
			break;
		case 1:
			in = new Intent(getApplicationContext(), DangerMapAcrivity.class);
			startActivity(in);
			fragmentID = 1;
			break;
		case 2:
			in = new Intent(getApplicationContext(), SearchActivity.class);
			startActivity(in);
			fragmentID = 2;
			break;
		case 3:
			fragmentID = 3;
			in = new Intent(getApplicationContext(), CreateWalkWithMeAcrivity.class);
			startActivity(in);
			break;
		case 4:
			in = new Intent(getApplicationContext(), ShowWalkWithMeAcrivity.class);
			startActivity(in);
			fragmentID = 4;
			break;
		
		case 5:
			in = new Intent(getApplicationContext(), ShowTrackMeAcrivity.class);
			startActivity(in);
			fragmentID = 4;
			break;
			
		case 6:
			in = new Intent(getApplicationContext(), EmergencyBoard.class);
			startActivity(in);
			fragmentID = 4;
			break;
			
		case 7:
			in = new Intent(getApplicationContext(), VoiceRecorderActivity.class);
			startActivity(in);
			fragmentID = 4;
			break;
		
		case 8:
			in = new Intent(getApplicationContext(), GraphActivity.class);
			startActivity(in);
			fragmentID = 4;
			break;
			
		case 9:
			in = new Intent(getApplicationContext(), SexualHarrasmentLawActivity.class);
			startActivity(in);
			fragmentID = 4;
			break;
		
		case 10:
			in = new Intent(getApplicationContext(), FirActivity.class);
			startActivity(in);
			fragmentID = 4;
			break;
		
		case 11:
			in = new Intent(getApplicationContext(), ServeyActivity.class);
			startActivity(in);
			fragmentID = 4;
			break;
			
		case 12:
			fragmentID = 5;
			mSimpleFacebook.logout(onLogoutListener);
			
			break;

		default:
			break;
		}

		if (fragment != null) {
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			setTitle(navMenuTitles[position]);
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			
			Log.e("this is mainActivity", "Error in else case");
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}


	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		mSimpleFacebook.onActivityResult(this, requestCode, resultCode, data);
		Log.d("get activity result","get data");
		if(resultCode==RESULT_OK){
			//super.onActivityResult(requestCode, resultCode, data);
			Intent in = getIntent();
			in.putExtra("fragmentID", fragmentID);
			finish();
			startActivity(in);
		}
	}
	
	private void setLogout() {
		onLogoutListener = new OnLogoutListener() {

			@Override
			public void onFail(String reason) {
				Log.w("FacebookLogout", "Failed to login");
			}

			@Override
			public void onException(Throwable throwable) {
				Log.e("FacebookLogout", "Bad thing happened", throwable);
			}

			@Override
			public void onThinking() {
			}

			@Override
			public void onLogout() {
				Log.e("facebook", "Logout successfull");
				
				editor.putString(GlobalConstant.LOGIN_STATUS, GlobalConstant.LOGIN_STATUS_SIGNOUT);
				editor.putString(GlobalConstant.USER_ID, "");
				editor.putString(GlobalConstant.LOGIN_ACCESSTOKEN, "");
				editor.commit();
				GlobalConstant.showMessage(MainActivity.this, "Logout successfull");
				Intent in = new Intent(getApplicationContext(), LoginActivity.class);
				startActivity(in);
				finish();
			}

		};
	}

}
