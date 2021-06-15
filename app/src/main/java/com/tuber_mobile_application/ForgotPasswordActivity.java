package com.tuber_mobile_application;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tuber_mobile_application.Controllers.UserController;
import com.tuber_mobile_application.helper.App_Global_Variables;
import com.tuber_mobile_application.helper.EmailHelper;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ForgotPasswordActivity extends AppCompatActivity
{
    private UserController userController; // to check email & edit password
    private EmailHelper emailHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);


    }

    public void sendRecoveryEmail(View view)
    {
        initialize(); // retrofit setup

        final TextView txtEmail = findViewById(R.id.txtRecoveryEmail);
        final String email = txtEmail.getText().toString();

        Call<String> checkEmail = userController.CheckIfEmailExists(email);
        checkEmail.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response)
            {
                if(response.isSuccessful())
                {
                    if(response.body() != null)
                    {
                        if(response.body().equals("1")) // the email exists
                        {
                            emailHelper = new EmailHelper(ForgotPasswordActivity.this,email,"Password Reset.",makeReset(email));
                            emailHelper.execute();
                        }
                        else
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
                            builder.setMessage("your email is not in our records, please try again")
                                    .setCancelable(true).show(); // success message
                        }
                    }
                }

            }

            @Override
            public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {

            }
        });

        //Toast.makeText(this,"Email sent to " + txtEmail.getText().toString(),Toast.LENGTH_SHORT).show();
    }


    public String makeReset(String recipient)
    {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        String now = dateFormat.format(Calendar.getInstance().getTime());

        return setHTML("Click the button below to reset your password.", "reset my password",
                App_Global_Variables.Base_SITE_URL + "anoynmus_tuber_password_reset.aspx?email=" + recipient + "&timeStamp=" + now);
    }
    public  String setHTML(String message, String buttonText, String buttonLink)
    {
        String display = "<html><head></head><body>";
        display += "<div style='width:1200px; margin:0 auto; font-family: Arial, Helvetica, sans-serif;'>";
        display += "<p style='padding: 10px'>" + message + "</p><br>"; // message must be formal
        display += "<a href='" + buttonLink + "' style='display:block; width:200px; height:25px; " +
                "padding: 3px; border-radius: 25px; " +
                "color:#f5f5f5; text-align:center; text-decoration:none;" +
                "border:none;background:orangered;'>";
        display += buttonText;
        display += "</a></div></body></html>";


        return display;
    }

    public void initialize() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        AtomicReference<Retrofit> retrofit = new AtomicReference<>(new Retrofit.Builder()
                .baseUrl(App_Global_Variables.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(ScalarsConverterFactory.create())
                .build());

        userController = retrofit.get().create(UserController.class);

    }

}