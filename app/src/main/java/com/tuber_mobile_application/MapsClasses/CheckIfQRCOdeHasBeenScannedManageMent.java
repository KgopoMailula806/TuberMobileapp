package com.tuber_mobile_application.MapsClasses;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import com.tuber_mobile_application.Controllers.Tutorial_SessionController;
import com.tuber_mobile_application.QRCodeGenFragment;
import com.tuber_mobile_application.helper.App_Global_Variables;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CheckIfQRCOdeHasBeenScannedManageMent {
    private Activity activity;
    private Context context;
    private Bundle Genobj;

    private QRCodeGenFragment qrCodeGenFragment;
    private String bookingId;
    private String tutorID;
    private String clientID;
    private Tutorial_SessionController tutorial_SessionController;
    private String userID;
    public final boolean[] result = new boolean[1];
    public final int[] TutSessionID = new int[1];
    public final boolean[] resultDone = new boolean[1];
    private String StartTime;

    public CheckIfQRCOdeHasBeenScannedManageMent(Activity activity, Context context, Bundle Genobj) {
        this.activity = activity;
        this.context = context;
        this.Genobj = Genobj;
        setUpRetrofit();
    }

    /**
     *
     * @param UserId
     */
    public  void checkIfSessionHasStarted(int UserId) {

        result[0] = false;
        Call<String> call = tutorial_SessionController.checkIfSessionExists(UserId);
        call.enqueue(new Callback<String>(){

            @Override
            public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                if(!response.isSuccessful()){
                    //Toast.makeText(qrCodeGenFragment, "Could not load the location", Toast.LENGTH_SHORT).show();
                    return;
                }
                String response_ = response.body();

                if(response_.equals("0"))
                    result[0] = false;
                else
                {
                    result[0] = true;
                    TutSessionID[0] = Integer.parseInt(response_);
                    resultDone[0]=true;

                }
            }
            @Override
            public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                //Toast.makeText(getContext(), "Could not load the location", Toast.LENGTH_SHORT).show();
            }
        });
        //refresh 5 every seconds
        refresh(UserId);
    }

    /**
     *
     * @param userId
     */
    private  void refresh(final int userId) {
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                checkIfSessionHasStarted(userId);

            }
        };
        handler.postDelayed(runnable, 5000);
    }

    /**
     *
     */
    public  void setUpRetrofit() {
        AtomicReference<Retrofit> retrofit = new AtomicReference<>(new Retrofit.Builder()
                .baseUrl(App_Global_Variables.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build());

        tutorial_SessionController = retrofit.get().create(Tutorial_SessionController.class);
    }

}
