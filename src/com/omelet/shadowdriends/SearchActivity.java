package com.omelet.shadowdriends;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.omelet.shadowdriends.dataservice.ServiceHandler;
import com.omelet.shadowfriends.util.GlobalConstant;


public class SearchActivity  extends  ListActivity{

       // URL to get contacts JSON

    private Button postStatus;
    // JSON Node names
    private ProgressDialog pDialog;
    // contacts JSONArray
    JSONArray contacts = null;

   // LazyAdapter adapter;
    ArrayList<HashMap<String, String>> categoryList;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        //System.out.println(" good error");
        categoryList = new ArrayList<HashMap<String, String>>();
        postStatus = (Button) findViewById(R.id.btnPost);

        ListView lv = getListView();
        // U need to extend ListActivity

        // Click event for single list row
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {

                // getting values from selected ListItem
                String fid = ((TextView) view.findViewById(R.id.fid)).getText().toString();
                String title = ((TextView) view.findViewById(R.id.title)).getText().toString();
                String subStatus = ((TextView) view.findViewById(R.id.txtDate)).getText().toString();
                String sub = ((TextView) view.findViewById(R.id.txtSub)).getText().toString();

                String txtAgree = ((TextView) view.findViewById(R.id.txtCountAgree)).getText().toString();
                String txtDisagree = ((TextView) view.findViewById(R.id.txtCountDisagree)).getText().toString();


                //String description = ((TextView) view.findViewById(R.id.txtStatus)).getText().toString();

                // Starting single contact activity
                Intent in = new Intent(getApplicationContext(),	PostDetailsActivity.class);
                in.putExtra(GlobalConstant.TAG_FID, fid);

                in.putExtra(GlobalConstant.TAG_TITLE, title);
                in.putExtra(GlobalConstant.TAG_SUB_STATUS, subStatus);
                in.putExtra(GlobalConstant.TAG_STATUS, sub);
                in.putExtra(GlobalConstant.TAG_TOTAL_AGREE, txtAgree);
                in.putExtra(GlobalConstant.TAG_TOTAL_DISAGREE, txtDisagree);

                System.out.println("Before call details Count "+ txtAgree+ " "+txtDisagree);
                startActivity(in);


            }
        });

        // Calling async task to get json
        new GetContacts().execute();

    }


//-----------------------------------------------------------------------------------------------------------------------

/**
 * Async task class to get json by making HTTP call
 * */
private class GetContacts extends AsyncTask<Void, Void, Void> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Showing progress dialog
        pDialog = new ProgressDialog(SearchActivity.this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

    }

    @Override
    protected Void doInBackground(Void... arg0) {
        // Creating service handler class instance
        ServiceHandler sh = new ServiceHandler();

        // Making a request to url and getting response
        String jsonStr = sh.makeServiceCall(GlobalConstant.getPostUrl, ServiceHandler.GET);

        Log.d("Response: ", "> " + jsonStr);

        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);

                // Getting JSON Array node
                contacts = jsonObj.getJSONArray(GlobalConstant.TAG_CONTACTS);

                // looping through All Contacts
                for (int i = 0; i < contacts.length(); i++) {
                    JSONObject c = contacts.getJSONObject(i);

                    String id = c.getString(GlobalConstant.TAG_FID);
                    String name = c.getString(GlobalConstant.TAG_TITLE);
                    String subStatus = c.getString(GlobalConstant.TAG_SUB_STATUS);
                    String status = c.getString(GlobalConstant.TAG_STATUS);

                    String txtAgree = c.getString(GlobalConstant.TAG_TOTAL_AGREE);
                    String txtDisagree = c.getString(GlobalConstant.TAG_TOTAL_DISAGREE);

                    // tmp hashmap for single contact
                    HashMap<String, String> contact = new HashMap<String, String>();

                    // adding each child node to HashMap key => value
                    contact.put(GlobalConstant.TAG_FID, id);
                    contact.put(GlobalConstant.TAG_TITLE, name);
                    contact.put(GlobalConstant.TAG_SUB_STATUS, subStatus);
                    contact.put(GlobalConstant.TAG_STATUS, status);

                    contact.put(GlobalConstant.TAG_TOTAL_AGREE, txtAgree);
                    contact.put(GlobalConstant.TAG_TOTAL_DISAGREE, txtDisagree);

                    // adding contact to contact list
                    categoryList.add(contact);
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
        // Dismiss the progress dialog
        if (pDialog.isShowing())
            pDialog.dismiss();
        /**
         * Updating parsed JSON data into ListView
         * */
        ListAdapter adapter = new SimpleAdapter(  SearchActivity.this, categoryList,
                R.layout.list_row, new String[] { GlobalConstant.TAG_FID , GlobalConstant.TAG_TITLE, GlobalConstant.TAG_SUB_STATUS, GlobalConstant.TAG_STATUS,
                             GlobalConstant.TAG_TOTAL_AGREE, GlobalConstant.TAG_TOTAL_DISAGREE },
                             new int[] {  R.id.fid, R.id.title,  R.id.txtDate,  R.id.txtSub, R.id.txtCountAgree, R.id.txtCountDisagree });

        setListAdapter(adapter);
    }

}

    public void pressPost(View view) {
        Intent intent = new Intent(  getApplicationContext(), PostActivity.class);
        startActivity(intent);

    }
}
