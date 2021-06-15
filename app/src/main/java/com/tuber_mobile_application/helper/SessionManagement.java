package com.tuber_mobile_application.helper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.tuber_mobile_application.Models.Client;
import com.tuber_mobile_application.Models.Tutor;
import com.tuber_mobile_application.Models.User;

public class SessionManagement {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


    String SHARED_PREF_NAME = "session";
    String SESSION_KEY = "session_user";
    String CLIENT_SESSION_KEY = "client_session";
    String TUTOR_SESSION_KEY = "tutor_session";
    public SessionManagement (Context context){
        sharedPreferences = context.getSharedPreferences("SHARED_PREF_NAME",Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }


    public void saveSession(User user){
        //sve session of user wherever user is logged in
        int id = user.getId();
        editor.putInt(SESSION_KEY,id).commit();
    }

    /**
     * saves the client session by saving the client's user table ID
     * @param client
     */
    public void saveSession(Client client){
        //sve session of user wherever user is logged in
        int clientId = client.getId();
        editor.putInt(CLIENT_SESSION_KEY,clientId).commit();
    }

    /**
     *
     * @param tutor - Tutor class that has relevant Tutor data
     */
    public void saveSession(Tutor tutor){
        //sve session of user wherever user is logged in
        int tutorId = tutor.getId();
        editor.putInt(TUTOR_SESSION_KEY,tutorId).commit();
    }


    public int getSession(){ return sharedPreferences.getInt(SESSION_KEY,-1);}

    public int getClientSession(){
        return sharedPreferences.getInt(CLIENT_SESSION_KEY,-1);
    }

    public int getTutorSession(){
        return sharedPreferences.getInt(TUTOR_SESSION_KEY,-1);
    }

    public void removeSession(){
        editor.putInt(SESSION_KEY,-1).commit();
        editor.putInt(CLIENT_SESSION_KEY,-1).commit();
        editor.putInt(TUTOR_SESSION_KEY,-1).commit();
    }


}
