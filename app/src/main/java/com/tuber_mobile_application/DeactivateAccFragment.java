package com.tuber_mobile_application;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.tuber_mobile_application.Controllers.UserController;
import com.tuber_mobile_application.Models.User;
import com.tuber_mobile_application.helper.App_Global_Variables;
import com.tuber_mobile_application.helper.EmailHelper;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class DeactivateAccFragment extends Fragment
{
    private View deactivateView;
    private EditText txtEmail, txtPassword, txtReason;
    private UserController userController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        deactivateView = inflater.inflate(R.layout.fragment_deactivate_acc,container,false);
        Objects.requireNonNull(getActivity()).setTitle("Deactivate Account");

        setUpRetrofit();
        Button btnSend = deactivateView.findViewById(R.id.d_acc_btn);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail();
            }
        });


        return deactivateView;
    }

    public void sendEmail()
    {
        txtEmail = deactivateView.findViewById(R.id.d_acc_email);
        txtPassword = deactivateView.findViewById(R.id.d_acc_password);
        txtReason = deactivateView.findViewById(R.id.d_acc_reason);

        final String recipient = txtEmail.getText().toString();
        String pass = txtPassword.getText().toString();
        final String reason = txtReason.getText().toString();

        if(!recipient.equals("") && !pass.equals(""))
        {
            Call<User> userCall = userController.Login(recipient,pass);
            userCall.enqueue(new Callback<User>() {
                @Override
                public void onResponse(@NotNull Call<User> call, @NotNull Response<User> response)
                {
                    if(response.isSuccessful())
                    {
                        User me = response.body();
                        if(me != null)
                        {
                            if(recipient.equals(me.getEmail_Address()))
                            {
                                EmailHelper emailHelper = new EmailHelper(getContext(), recipient,"Account Deactivation",
                                        makeDeactivateAccEmail(me.getFullNames()+ " " + me.getSurname(), "Jacob Muzonde",
                                                "?bariId=" + me.getId() + "&resContent=" + reason));

                                emailHelper.execute(); // send the email
                                clearTextBoxes();
                            }
                        }
                        else
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
                            builder.setMessage("Invalid email or password.")
                                    .setCancelable(true).show(); // success message
                        }
                    }
                    else
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
                        builder.setMessage("Something went wrong, please try again.")
                                .setCancelable(true).show(); // success message
                    }

                }

                @Override
                public void onFailure(@NotNull Call<User> call, @NotNull Throwable t)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
                    builder.setMessage("Something went wrong, please try again.")
                            .setCancelable(true).show(); // success message

                }
            });
        }
    }

    public String makeDeactivateAccEmail(String name, String manager, String urlParameters)
    {
        String message = "";

        message += "Dear " + name + "<br>";
        message += "Please use the link below to deactivated your account <br>";
        message += "Kind Regards <br>";
        message += manager + " (Systems Manager)";
        return setHTML(message, "deactivate account", App_Global_Variables.Base_SITE_URL + "/ExitingPage.aspx" + urlParameters);
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

    private void clearTextBoxes()
    {
        txtPassword.setText("");
        txtEmail.setText("");
        txtReason.setText("");
    }

    private void setUpRetrofit() {
        AtomicReference<Retrofit> retrofit = new AtomicReference<>(new Retrofit.Builder()
                .baseUrl(App_Global_Variables.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())// for Json type conversions
                .addConverterFactory(ScalarsConverterFactory.create()) //for string conversions
                .build());

        userController = retrofit.get().create(UserController.class);
    }


}
