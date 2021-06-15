package com.tuber_mobile_application.MapsClasses;

import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.tuber_mobile_application.MapsFragment;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GetNearByPlaces extends AsyncTask<Object,String,String> {

    GoogleMap mMap;
    String url;
    InputStream inStream;
    BufferedReader bufferedReader;
    StringBuffer stringBuffer;
    StringBuilder stringBuilder;
    private String data;

    public GetNearByPlaces(MapsFragment mapsFragment) {
    }

    @Override
    protected String doInBackground(Object... objects) {
        //send Three objects
        mMap = (GoogleMap)objects[0];
        url = (String)objects[1];

        try{
            URL myUrl = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection)myUrl.openConnection();
            inStream = httpURLConnection.getInputStream();

            bufferedReader = new BufferedReader( new InputStreamReader(inStream));

            String line = "";
            stringBuilder = new StringBuilder();
            stringBuffer = new StringBuffer();

            while ((line = bufferedReader.readLine()) != null)
            {
                stringBuilder.append(line);
            }
            data = stringBuilder.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
