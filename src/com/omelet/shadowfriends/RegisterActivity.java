package com.omelet.shadowfriends;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.omelet.shadowdriends.R;
import com.omelet.shadowdriends.dataservice.CreateAccount;
import com.omelet.shadowfriends.util.GlobalConstant;
import com.omelet.shadowfriends.util.Network;
import com.omelet.shadowfriends.util.OnTaskCompleted;

public class RegisterActivity extends Activity implements OnTaskCompleted {
    private TextView loginScreen;
	private EditText mobileNumber;
	private EditText password;
	private EditText confirmPassword;
	private EditText firstName;
	private EditText lastName;
	private EditText pid;
	private Button registerBtn;
	
	private CreateAccount createAccount;
	private Network netCheck;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set View to register.xml
        setContentView(R.layout.register);
        netCheck = new Network(this);
        
        firstName = (EditText) findViewById(R.id.reg_first_name);
        lastName = (EditText) findViewById(R.id.reg_last_name);
        pid = (EditText) findViewById(R.id.reg_id);
        mobileNumber = (EditText) findViewById(R.id.reg_mobile_number);
        password = (EditText) findViewById(R.id.reg_password);
        confirmPassword = (EditText) findViewById(R.id.reg_password_confrim);
        loginScreen = (TextView) findViewById(R.id.link_to_login);
        registerBtn = (Button) findViewById(R.id.btnRegister);
        
        registerBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!password.getText().toString().equals(confirmPassword.getText().toString())){
					GlobalConstant.showMessage(RegisterActivity.this, "Password mismatch");
					return;
				}
				if(netCheck.isNetworkConnected()){
					createAccount = new CreateAccount(RegisterActivity.this, RegisterActivity.this, firstName.getText().toString(), lastName.getText().toString(), pid.getText().toString(), mobileNumber.getText().toString(), password.getText().toString(), "http://www.omleteit.com/apps/shadow/CreateUser.php");
					createAccount.execute();
				}
				else{
					GlobalConstant.showMessage(RegisterActivity.this, "Network connection is not avaiable");
				}
			}
		});
        
        // Listening to Login Screen link
        loginScreen.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				finish();
			}
		});
    }

	@Override
	public void onTaskCompleted(String status) {
		if(createAccount.isSuccess()){
			finish();
		}
	}

	@Override
	public void showMessage(String message) {
		GlobalConstant.showMessage(this, message);
	}
}