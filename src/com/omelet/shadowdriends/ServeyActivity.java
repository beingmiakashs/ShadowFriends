package com.omelet.shadowdriends;
import org.json.JSONArray;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created with IntelliJ IDEA.
 * User: Ashis Kumar Chanda
 * Date: 12/7/14
 * Time: 9:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class ServeyActivity extends Activity {
    private RadioGroup radioQuestionGroup;
    private RadioButton radioAnsButton;
    private Button btnNext;
    private String index;
    private String INDEX_Flag;
    JSONArray contacts = null;

    private String QuestionStr []= new String[10];
    private String OptionsStr [][]= new String[10][4];

    private TextView qtxt;
    private RadioButton   opt1;
    private RadioButton   opt2;
    private RadioButton   opt3;


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        Intent in = getIntent();

        // Get JSON values from previous intent


        //index = in.getStringExtra(GlobalConstant.TAG_INDEX_Flag);
        addListenerOnButton();

    }


    public void addListenerOnButton() {

        radioQuestionGroup = (RadioGroup) findViewById(R.id.radioQuestion);
        btnNext = (Button) findViewById(R.id.btnNext);

        btnNext.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                // get selected radio button from radioGroup
                int selectedId = radioQuestionGroup.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                radioAnsButton = (RadioButton) findViewById(selectedId);

                Toast.makeText( getApplicationContext(),    radioAnsButton.getText(), Toast.LENGTH_SHORT).show();

            }

        });

    }




    /**
     // Async task class to get json by making HTTP call

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
                    int j= Integer.parseInt(index);
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);

                        QuestionStr[i] = c.getString("Questions");
                        OptionsStr[j][i] = c.getString("Options");

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
            //qtxt =



        }

    }
     * */

}
