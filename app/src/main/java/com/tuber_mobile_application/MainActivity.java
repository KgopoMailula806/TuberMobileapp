package com.tuber_mobile_application;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tuber_mobile_application.Controllers.ClientController;
import com.tuber_mobile_application.Controllers.TutorController;
import com.tuber_mobile_application.Controllers.UserController;
import com.tuber_mobile_application.Models.Client;
import com.tuber_mobile_application.Models.Tutor;
import com.tuber_mobile_application.Models.User;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.tuber_mobile_application.helper.*;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainActivity extends AppCompatActivity {

    //
    public final static String BASE_URL = "https://tubersandbox.azurewebsites.net/";

    // TO HELP MAINTAIN STATE
    public static String UserStatus = "";

    // private TextView textViewResult;
    private UserController userController;
    private ClientController clientController;
    private TutorController tutorController;
    private Button btnLogin;
    private TextView txtForgotPass;
    private TextView txtSignUp;

    //UserController userController= null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoadingDialog loadingDialog = new LoadingDialog(this);
        //GetUsers();
        //set up the controller methods
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextView txtEmail = (TextView) findViewById(R.id.txtEmail);
                TextView txtPassword = (TextView) findViewById(R.id.txtPassword);
                if (txtEmail.getText().length() > 0 && txtPassword.getText().length() > 0) {
                    loginUser();

                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Please Enter Credentials", Toast.LENGTH_SHORT);
                    toast.setMargin(50, 50);
                    toast.show();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        //check if user is logged in
        AtomicReference<Retrofit> retrofit = new AtomicReference<>(new Retrofit.Builder()
                .baseUrl(App_Global_Variables.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))// for Json type conversions
                .addConverterFactory(ScalarsConverterFactory.create()) //for string conversions
                .build());

        userController = retrofit.get().create(UserController.class);
        clientController = retrofit.get().create(ClientController.class);
        tutorController = retrofit.get().create(TutorController.class);

        SessionManagement sessionManagement = new SessionManagement(MainActivity.this);
        int userID = sessionManagement.getSession();
        if (userID != -1) {
            //if user is logged in move to Home
            //set user status
            //setUserStatus(userID);

            setClientTableID(); //set client table ID

            setTutorTableReference(); //set tutor table ID
            //set up global variables
            moveToHomeActivity();
        } else {
            //user is not logged in
        }
    }

    /**
     *
     */
    private void loginUser() {
        TextView txtEmail = (TextView) findViewById(R.id.txtEmail);
        TextView txtPassword = (TextView) findViewById(R.id.txtPassword);

        Call<User> call = userController.Login(txtEmail.getText().toString(), txtPassword.getText().toString());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                if (!response.isSuccessful()) {
                    return;
                }

                User user = response.body();
                if (user != null) {

                    if (user.getIsActive() == 0) {
                        moveToWaitingArea();
                    } else {
                        App_Global_Variables.USER_CURRENT_STATUS = user.getUser_Discriminator();
                        //Create as session
                        SessionManagement sessionManagement = new SessionManagement(MainActivity.this);
                        UserStatus = user.getUser_Discriminator();
                        sessionManagement.saveSession(user);

                        //set the client table reference
                        setClientTableID();
                        //Set the tutor table reference
                        setTutorTableReference();

                        moveToHomeActivity();
                    }

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Invalid email or password"); // no button

                    AlertDialog msg = builder.create();
                    msg.show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
          //      Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Save the client table Id Session
     */
    public void setClientTableID() {
        SessionManagement sessionManagement = new SessionManagement(getApplicationContext());
        final int userID = sessionManagement.getSession();

        Call<Client> call = clientController.getClient(userID);
        call.enqueue(new Callback<Client>() {
            @Override
            public void onResponse(Call<Client> call, Response<Client> response) {

                if (!response.isSuccessful()) {
                    return;
                }

                if (response.body() != null)
                {
                    //Create a session for the client table ID
                    SessionManagement sessionManagement = new SessionManagement(MainActivity.this);
                    //UserStatus = user.getUser_Discriminator();
                    //Client client = new Client(Id, "", "", 0, null);
                    sessionManagement.saveSession(response.body());
                }

            }

            @Override
            public void onFailure(Call<Client> call, Throwable t) {
                //textViewResult.setText(t.getMessage());
                Toast.makeText(MainActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Set the Tutor table ID as a session
     */
    public void setTutorTableReference() {
        SessionManagement sessionManagement = new SessionManagement(getApplicationContext());
        final int userID = sessionManagement.getSession();

        Call<String> call = tutorController.getTutorTableID(userID);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (!response.isSuccessful()) {
                    return;
                }

                String Id = response.body();
                //Create a session for the client table ID
                SessionManagement sessionManagement = new SessionManagement(MainActivity.this);
                //  UserStatus = user.getUser_Discriminator();
                Tutor tutor = new Tutor(Integer.parseInt(Id), 0, 0, null);
                sessionManagement.saveSession(tutor);

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                //textViewResult.setText(t.getMessage());
                Toast.makeText(MainActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     *
     */
    private void moveToHomeActivity() {
        Intent intent = new Intent(MainActivity.this, Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /**
     * redirects to the registration type UI
     *
     * @param view
     */
    public void ForgotPassword(View view) {

        Intent intent = new Intent(MainActivity.this, ForgotPasswordActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /**
     * redirects to the registration type UI
     *
     * @param view
     */
    public void goToRegister(View view) {
        Intent intent = new Intent(MainActivity.this, RegistrationType.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void moveToWaitingArea() {
        Intent intent = new Intent(MainActivity.this, WaitingAreaActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
