package com.tuber_mobile_application;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class RegistrationType extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_type);
    }

    /**
     *
     * @param view
     */
    public void signUpAsClient(View view) {
        Intent intent = new Intent(RegistrationType.this,RegistrationActivity.class);
       intent.putExtra("UserType","Client");
        startActivity(intent);
    }

    /**
     * redirect to the register as tutor
     * @param view
     */
    public void signUpAsTutor(View view) {
        Intent intent = new Intent(RegistrationType.this,RegistrationActivity.class);
        intent.putExtra("UserType","Tutor");
        startActivity(intent);
    }
}
