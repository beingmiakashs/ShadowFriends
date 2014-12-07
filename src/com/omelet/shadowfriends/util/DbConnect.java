package com.omelet.shadowfriends.util;

import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

          // call this in static mode

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class DbConnect  {

    public String  getResult(String url, String item[], String value[], int queryItem){

        JSONArray jArray;
        String result = null;
        InputStream is = null;
        StringBuilder sb = null;

        //outputStream = (TextView)findViewById(R.id.dataTxt);

        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        //if(item[0].equalsIgnoreCase(""))
        //  System.out.println("Empty rony");

        for(int i=0; i<queryItem; i++)
            nameValuePairs.add(new BasicNameValuePair( item[i], value[i] ));


        try
        {

            //http post
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet( url );

           // httpget.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();



            if (response.getStatusLine().getStatusCode() != 200)
            {
                Log.d("MyApp", "Error Message");
            }
            System.out.println("Server Connect Successfully");
        }
        catch(Exception e)
        {
            //Toast.makeText( this ,e.toString() ,Toast.LENGTH_LONG).show();
        }

        //Convert response to string
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF8"));
            sb = new StringBuilder();
            sb.append(reader.readLine() + "\n");
            String line = null;

            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
        }
        catch(Exception e)
        {
            Log.e("log_tag", "Error converting result "+e.toString());
        }
        //END Convert response to string

        return result;
    }

}
