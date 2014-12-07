package com.omelet.shadowdriends;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.omelet.shadowdriends.dataservice.ServiceHandler;
import com.omelet.shadowfriends.util.DbConnect;
import com.omelet.shadowfriends.util.GlobalConstant;

//@ContentView(R.layout.search_result_layout)                 //  add  setContentView

public class PostDetailsActivity extends Activity {
    private ListView listView;                           // List view
    ArrayAdapter<String> adapter;                      	// Listview Adapter
    private String fid;
    JSONArray contacts = null;

    private String agreeFlag;
    private String disagreeFlag;
    private Button btnAgree;
    private Button btnDisagree;

    private String getUserComment;
    private String getUserCommentResult;
    
    private SharedPreferences preferences;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String comments[] = {"Thanks to share", "Really inspiring post", "Thanks", "good"};

        listView = (ListView) findViewById(R.id.list_view_comment);


        adapter = new ArrayAdapter<String>(this, R.layout.list_row_comment, R.id.comment_details, comments);        // Adding items to listview
        listView.setAdapter(adapter);

        // getting intent data
        Intent in = getIntent();

        // Get JSON values from previous intent


         fid = in.getStringExtra(GlobalConstant.TAG_FID);
        String title = in.getStringExtra(GlobalConstant.TAG_TITLE);
        String subStatus = in.getStringExtra(GlobalConstant.TAG_SUB_STATUS);
        String status = in.getStringExtra(GlobalConstant.TAG_STATUS);

        String cAgree = in.getStringExtra(GlobalConstant.TAG_TOTAL_AGREE);
        String cDisagree = in.getStringExtra(GlobalConstant.TAG_TOTAL_DISAGREE);


        // Displaying all values on the screen
        TextView lblName = (TextView) findViewById(R.id.title_label);
        TextView lblDate = (TextView) findViewById(R.id.date_label);
        TextView lblStatus = (TextView) findViewById(R.id.status_label);
        TextView lblAgree = (TextView) findViewById(R.id.count_agree_label);
        TextView lblDisagree = (TextView) findViewById(R.id.count_disagree_label);

        lblName.setText(title);
        lblDate.setText(subStatus);
        lblStatus.setText(status);
        lblAgree.setText(cAgree);
        lblDisagree.setText(cDisagree);

        System.out.println("Count "+ cAgree+ " "+cDisagree);

        final EditText edittext = (EditText) findViewById(R.id.editComment);
          String tem = edittext.getText().toString();
            edittext.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                   // Toast.makeText( getApplicationContext(), edittext.getText().toString(), Toast.LENGTH_SHORT).show();

                    //new DownloadFilesTask().execute(edittext.getText().toString() , 0, 0);
                    getUserComment= edittext.getText().toString();
                    new GetComments().execute( );

                    /*
                    DbConnect obj = new DbConnect();
                    String queryItem[]= new String[7];
                    String queryValue[]= new String[7];

                    System.out.println("Comment add "+ edittext.getText().toString());
                    String temUrl = GlobalConstant.insertCommentUrl +"?"+GlobalConstant.TAG_ID +"="+"anik" +"&"+ GlobalConstant.TAG_REQ_FID +"="+ fid +"&"+ "des"+"="+edittext.getText().toString();
                    System.out.println("url "+temUrl);
                    String result = obj.getResult( temUrl ,  queryItem, queryValue, 0);
                    System.out.println("comment server: "+result);


                    try {
                        JSONObject reader = new JSONObject(result);
                        System.out.println(reader.get("status"));
                        result =    reader.get("status").toString();


                    } catch (JSONException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }

                    */
                   // Toast.makeText(getApplicationContext(),result, Toast.LENGTH_LONG).show();


                    return true;
                }
                return false;
            }
        });


        // Calling async task to get json
        new GetContacts().execute();
    }



    /**
     * Async task class to get json by making HTTP call
     * */
    private class GetComments extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            DbConnect obj = new DbConnect();
            String queryItem[]= new String[7];
            String queryValue[]= new String[7];

            System.out.println("Comment add "+ getUserComment );
            
            String userID = preferences.getString(GlobalConstant.USER_ID, "");
            String temUrl = GlobalConstant.insertCommentUrl +"?"+GlobalConstant.TAG_ID +"="+userID +"&"+ GlobalConstant.TAG_REQ_FID +"="+ fid +"&"+ "des"+"="+getUserComment;
            System.out.println("url "+temUrl);
            String result = obj.getResult( temUrl ,  queryItem, queryValue, 0);
            System.out.println("comment server: "+result);


            try {
                JSONObject reader = new JSONObject(result);
                System.out.println(reader.get("status"));
                getUserCommentResult =    reader.get("status").toString();


            } catch (JSONException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }


            // Toast.makeText(getApplicationContext(),result, Toast.LENGTH_LONG).show();


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Toast.makeText(getApplicationContext(),getUserCommentResult, Toast.LENGTH_LONG).show();
        }

    }



    /**
     * Async task class to get json by making HTTP call
     * */
    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(GlobalConstant.getButtonStatus, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    contacts = jsonObj.getJSONArray(GlobalConstant.TAG_CONTACTS);

                    // looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);

                        agreeFlag = c.getString("AGREE");
                        disagreeFlag = c.getString("DISAGREE");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            btnAgree = (Button)        findViewById(R.id.btnAgree);
            btnDisagree = (Button)        findViewById(R.id.btnDisagree);

            if(agreeFlag.equalsIgnoreCase("0")){
                btnAgree.setClickable(true);
            }
            if(disagreeFlag.equalsIgnoreCase("0")){
                btnDisagree.setClickable(true);
            }

            }

    }

    public void pressBtnAgree(View view) {
        //Toast.makeText( getApplicationContext(), " Agree", Toast.LENGTH_LONG).show();


        DbConnect obj = new DbConnect();
        String queryItem[]= new String[7];
        String queryValue[]= new String[7];


        String userID = preferences.getString(GlobalConstant.USER_ID, "");
        String temUrl = GlobalConstant.insertVoteUrl +"?userid="+ userID+"&"+  GlobalConstant.TAG_REQ_FID +"="+ fid +"&action=agree";
        System.out.println("url "+temUrl);
        String result = obj.getResult( temUrl ,  queryItem, queryValue, 0);
        System.out.println("comment server: "+result);
        String mes ="";

        try {
            JSONObject reader = new JSONObject(result);
            System.out.println(reader.get("status"));
            result =    reader.get("status").toString();
            mes =    reader.get("message").toString();

        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        Toast.makeText( getApplicationContext(), result +" "+ mes, Toast.LENGTH_LONG).show();
        btnAgree.setClickable(false);
        btnDisagree.setClickable(true);
    }


    public void pressBtnDisagree(View view) {
        Toast.makeText( getApplicationContext(), " Dis Agree", Toast.LENGTH_LONG).show();

        DbConnect obj = new DbConnect();
        String queryItem[]= new String[7];
        String queryValue[]= new String[7];

        String userID = preferences.getString(GlobalConstant.USER_ID, "");
        
        String temUrl = GlobalConstant.insertVoteUrl +"?userid="+ userID+"&"+  GlobalConstant.TAG_REQ_FID +"="+ fid +"&action=disagree";
        System.out.println("url "+temUrl);
        String result = obj.getResult( temUrl ,  queryItem, queryValue, 0);
        System.out.println("comment server: "+result);
        String mes ="";

        try {
            JSONObject reader = new JSONObject(result);
            System.out.println(reader.get("status"));
            result =    reader.get("status").toString();
            mes =    reader.get("message").toString();

        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        Toast.makeText( getApplicationContext(), result +" "+ mes, Toast.LENGTH_LONG).show();
        btnAgree.setClickable(false);
        btnDisagree.setClickable(true);
    }

}