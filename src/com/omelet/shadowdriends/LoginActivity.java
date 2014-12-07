package com.omelet.shadowdriends;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.omelet.shadowdriends.R;
import com.omelet.shadowdriends.dataservice.CheckLogin;
import com.omelet.shadowfriends.util.GlobalConstant;
import com.omelet.shadowfriends.util.Network;
import com.omelet.shadowfriends.util.OnTaskCompleted;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnProfileListener;
import com.sromku.simple.fb.utils.Utils;

public class LoginActivity extends Activity implements OnTaskCompleted {
    private EditText userIDEditText;
	private EditText password;
	private Button loginBtn;
	private TextView registerScreen;
	private SharedPreferences preferences;
	private Editor editor;
	private CheckLogin checkLogin;
	private Network netCheck;
	
	private SimpleFacebook mSimpleFacebook;
	private ImageButton loginFbBtn;
	

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
        GlobalConstant.mContext = this;
        Log.d("hash key", Utils.getHashKey(this));
        
        netCheck = new Network(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		editor = preferences.edit();
		
		String loginStatus = preferences.getString(GlobalConstant.LOGIN_STATUS, GlobalConstant.LOGIN_STATUS_SIGNOUT);
		if(loginStatus.equals(GlobalConstant.LOGIN_STATUS_SIGNIN)){
			Intent i = new Intent(getApplicationContext(), MainActivity.class);
			startActivity(i);
			finish();
		}
        
		userIDEditText = (EditText) findViewById(R.id.mobile_number);
        password = (EditText) findViewById(R.id.password);
        loginBtn = (Button) findViewById(R.id.btnLogin);
        loginFbBtn = (ImageButton) findViewById(R.id.btnFbLogin);
        registerScreen = (TextView) findViewById(R.id.link_to_register);
        
		loginBtn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if(userIDEditText.getText().toString()==null){
					GlobalConstant.showMessage(LoginActivity.this, "Please enter your username");
					return;
				}
				if(password.getText().toString()==null){
					GlobalConstant.showMessage(LoginActivity.this, "Please enter your password");
					return;
				}
				if(netCheck.isNetworkConnected()){
					checkLogin = new CheckLogin(LoginActivity.this, LoginActivity.this, password.getText().toString(), userIDEditText.getText().toString(), "http://www.omleteit.com/apps/shadow/checkLogin.php");
					checkLogin.execute();
				}
				else{
					GlobalConstant.showMessage(LoginActivity.this, "Network connection is not avaiable");
				}
			}
		});
        
        registerScreen.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
				startActivity(i);
			}
		});
        
        Permission[] permissions = new Permission[] {
        	    Permission.USER_PHOTOS,
        	    Permission.USER_FRIENDS,
        	    Permission.USER_ABOUT_ME,
        	    Permission.PUBLIC_PROFILE,
        	    Permission.EMAIL
        	};
        SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
        .setAppId("1397110090516441")
        .setNamespace("infinity_codewin")
        .setPermissions(permissions)
        .build();
        SimpleFacebook.setConfiguration(configuration);
        
        mSimpleFacebook = SimpleFacebook.getInstance();
        setLogin();
    }
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mSimpleFacebook = SimpleFacebook.getInstance(this);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    mSimpleFacebook.onActivityResult(this, requestCode, resultCode, data); 
	    super.onActivityResult(requestCode, resultCode, data);
	} 

	private void setLogin() {
		
		final OnProfileListener onProfileListener = new OnProfileListener() {         
		    @Override
		    public void onComplete(Profile profile) {
		        Log.i("Facebook profile", "My profile id = " + profile.getId());
		        Log.i("Facebook access token", "Access token = " + mSimpleFacebook.getSession().getAccessToken());

				editor.putString(GlobalConstant.LOGIN_STATUS, GlobalConstant.LOGIN_STATUS_SIGNIN);
				editor.putString(GlobalConstant.LOGIN_TYPE, GlobalConstant.LOGIN_FB);
				editor.putString(GlobalConstant.USER_ID, profile.getId());
				editor.putString(GlobalConstant.LOGIN_ACCESSTOKEN, mSimpleFacebook.getSession().getAccessToken());
				editor.commit();
				
				Intent i = new Intent(getApplicationContext(), MainActivity.class);
				startActivity(i);
				finish();
		    }
		};
		
		// Login listener
		final OnLoginListener onLoginListener = new OnLoginListener() {

			@Override
			public void onFail(String reason) {
				Log.w("facebook", "Failed to login");
			}

			@Override
			public void onException(Throwable throwable) {
				Log.e("facebook", "Bad thing happened", throwable);
			}

			@Override
			public void onThinking() {
			}

			@Override
			public void onLogin() {
				Log.e("facebook", "login success");
				mSimpleFacebook.getProfile(onProfileListener);
			}

			@Override
			public void onNotAcceptingPermissions(Permission.Type type) {
				
				Toast.makeText(GlobalConstant.mContext, String.format("You didn't accept %s permissions", type.name()), Toast.LENGTH_SHORT).show();
			}
		};

		loginFbBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mSimpleFacebook.login(onLoginListener);
			}
		});
	}

	@Override
	public void onTaskCompleted(String status) {
		if(checkLogin.isSuccess()){
			editor.putString(GlobalConstant.LOGIN_STATUS, GlobalConstant.LOGIN_STATUS_SIGNIN);
			Log.d("mobile number",userIDEditText.getText().toString());
			editor.putString(GlobalConstant.LOGIN_TYPE, GlobalConstant.LOGIN_LOCAL);
			editor.putString(GlobalConstant.USER_ID, userIDEditText.getText().toString());
			editor.commit();
			
			Intent i = new Intent(getApplicationContext(), MainActivity.class);
			startActivity(i);
			finish();
		}
	}

	@Override
	public void showMessage(String message) {
		GlobalConstant.showMessage(this, message);
	}
}