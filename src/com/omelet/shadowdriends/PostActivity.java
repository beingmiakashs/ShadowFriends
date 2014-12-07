package com.omelet.shadowdriends;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.omelet.shadowdriends.model.Post;
import com.omelet.shadowfriends.util.DbConnect;
import com.omelet.shadowfriends.util.GlobalConstant;

public class PostActivity extends Activity {
    private EditText editPostTitle;
    private Spinner  editPostTag;

    private EditText editPostDetails;
    private SharedPreferences preferences;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypost);
    }

    public void pressSubmit(View view) {
        Post myPost = new Post();
        editPostTitle =  (EditText) findViewById(R.id.edtPostTitle)   ;
        editPostTag =    (Spinner) findViewById(R.id.spinPostTag);
        editPostDetails =(EditText) findViewById(R.id.edtPostDetails)   ;



        //myPost.setUser_id();
        myPost.setTitle( editPostTitle.getText().toString().trim() );
        myPost.setSubtitle( editPostTag.getSelectedItem().toString().trim() );
        myPost.setStatus( editPostDetails.getText().toString().trim() );
        //myPost.setTimeStamp();
        myPost.setAgreeCount(0);
        myPost.setDisagreeCount(0);

                   System.out.println("SUBTAG" + myPost.getSubtitle()) ;
        // ******************************   Now send to Server
        DbConnect obj = new DbConnect();
        String queryItem[]= new String[7];
        String queryValue[]= new String[7];
        queryItem[0]= GlobalConstant.TAG_ID;
        //queryValue[0]= GlobalConstant.TAG_User_ID_value ;
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        queryValue[0] = preferences.getString(GlobalConstant.USER_ID, "");
        
        queryItem[1]= GlobalConstant.TAG_REQ_TITLE;     // LABEL
        queryValue[1]= myPost.getTitle();

        queryItem[2]= GlobalConstant.TAG_REQ_SUB_STATUS;     // LABEL
        queryValue[2]=  editPostTag.getSelectedItem().toString().trim();

        queryItem[3]= GlobalConstant.TAG_REQ_STATUS;
        queryValue[3]= myPost.getStatus() ;

        queryItem[4]= GlobalConstant.TAG_REQ_USER_LATITUDE;
        queryValue[4]= "23.7354677" ;

        queryItem[5]= GlobalConstant.TAG_REQ_USER_LONGITUDE;
        queryValue[5]= "90.4183073" ;


        System.out.println("Go way" + editPostTag.getSelectedItem().toString().trim() );
        String temUrl = GlobalConstant.insertPostUrl +"?"+queryItem[0]+"="+queryValue[0] +"&"+ queryItem[1]+"="+queryValue[1]  +"&"+ queryItem[3]+"="+queryValue[3]+"&"+ queryItem[4]+"="+queryValue[4] +"&"+ queryItem[5]+"="+queryValue[5] +"&"+ queryItem[2]+"="+queryValue[2];
        System.out.println( "url: "+ temUrl);
        String result = obj.getResult( temUrl  ,  queryItem, queryValue, 6);
        System.out.println(result);

        try {
            JSONObject reader = new JSONObject(result);
            System.out.println(reader.get("status"));
            result =    reader.get("status").toString();


        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        Toast.makeText(getApplicationContext(),result, Toast.LENGTH_LONG).show();
        // clt + slash
        // sop to System.out.println     sop then tab


//        String resultAns= "";
//        JSONArray jArray;
//
//        System.out.println("Return way");
//        try
//        {
//            jArray = new JSONArray(result);      // Now put result into JSON
//            JSONObject json_data=null;
//            for(int i=0;i<jArray.length();i++)
//            {
//                json_data = jArray.getJSONObject(i);
//                //outputStream.append( "\n"+"Id= "+json_data.getString("name") );
//                resultAns= json_data.getString("status");
//                //Toast.makeText( getApplicationContext(), json_data.getString("success") , Toast.LENGTH_LONG).show();
//                result += "\n" + jArray.getJSONObject(i);
//            }
//        }
//        catch(JSONException e1)
//        {
//            e1.printStackTrace();
//            Toast.makeText( getApplicationContext(), " Error in connection "+e1.getMessage(), Toast.LENGTH_LONG).show();
//
//        }
//        // *****************************************************  end of server
//        System.out.println("NET_CHECK"+ resultAns);
//        Toast.makeText(getApplicationContext(), resultAns, Toast.LENGTH_LONG).show();
        /*
        if(result.equalsIgnoreCase("success")){
            // DB connect
            Toast.makeText(getApplicationContext(), "Successful Submission", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(  getApplicationContext(), PostActivity.class);
            startActivity(intent);
        }
          */
    }

}
