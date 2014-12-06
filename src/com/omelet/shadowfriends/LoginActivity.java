package com.omelet.shadowfriends;

import com.omelet.sa.pickmypack.R;
import com.omelet.shadowdriends.dataservice.CheckLogin;
import com.omelet.shadowfriends.util.GlobalConstant;
import com.omelet.shadowfriends.util.Network;
import com.omelet.shadowfriends.util.OnTaskCompleted;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity implements OnTaskCompleted {
    private EditText mobileNumber;
	private EditText password;
	private Button loginBtn;
	private TextView registerScreen;
	private SharedPreferences preferences;
	private Editor editor;
	private CheckLogin checkLogin;
	private Network netCheck;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        netCheck = new Network(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		editor = preferences.edit();
		
		String loginStatus = preferences.getString(GlobalConstant.LOGIN_STATUS, GlobalConstant.LOGIN_STATUS_SIGNOUT);
		if(loginStatus.equals(GlobalConstant.LOGIN_STATUS_SIGNIN)){
			Intent i = new Intent(getApplicationContext(), MainActivity.class);
			startActivity(i);
			finish();
		}
        
		mobileNumber = (EditText) findViewById(R.id.mobile_number);
        password = (EditText) findViewById(R.id.password);
        loginBtn = (Button) findViewById(R.id.btnLogin);
        registerScreen = (TextView) findViewById(R.id.link_to_register);
        mobileNumber.setRawInputType(Configuration.KEYBOARD_QWERTY);
        
		loginBtn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if(mobileNumber.getText().toString()==null){
					GlobalConstant.showMessage(LoginActivity.this, "Please enter your mobile number");
					return;
				}
				if(password.getText().toString()==null){
					GlobalConstant.showMessage(LoginActivity.this, "Please enter your password");
					return;
				}
				if(netCheck.isNetworkConnected()){
					checkLogin = new CheckLogin(LoginActivity.this, LoginActivity.this, password.getText().toString(), mobileNumber.getText().toString(), "http://omleteit.com/apps/pickpack/checkLogin.php");
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
    }

	@Override
	public void onTaskCompleted(String status) {
		if(checkLogin.isSuccess()){
			editor.putString(GlobalConstant.LOGIN_STATUS, GlobalConstant.LOGIN_STATUS_SIGNIN);
			Log.d("mobile number",mobileNumber.getText().toString());
			editor.putString(GlobalConstant.USER_MOBILE_NUMBER, mobileNumber.getText().toString());
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