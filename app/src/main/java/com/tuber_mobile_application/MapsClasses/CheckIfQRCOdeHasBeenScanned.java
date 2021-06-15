package com.tuber_mobile_application.MapsClasses;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tuber_mobile_application.Controllers.BookingController;
import com.tuber_mobile_application.Controllers.ClientBookingController;
import com.tuber_mobile_application.Controllers.Tutorial_SessionController;
import com.tuber_mobile_application.LiveSessionFragment;
import com.tuber_mobile_application.Models.Tutorial_Session;
import com.tuber_mobile_application.QRCodeGenFragment;
import com.tuber_mobile_application.R;
import com.tuber_mobile_application.helper.App_Global_Variables;
import com.tuber_mobile_application.helper.SessionManagement;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class CheckIfQRCOdeHasBeenScanned extends AsyncTask {
    private  QRCodeGenFragment qrCodeGenFragment;
    private String bookingId;
    private String tutorID;
    private String clientID;
    private Tutorial_SessionController tutorial_SessionController;
    private String userID;
    public final boolean[] result = new boolean[1];
    public final int[] TutSessionID = new int[1];
    public final boolean[] resultDone = new boolean[1];

    public CheckIfQRCOdeHasBeenScanned(QRCodeGenFragment qrCodeGenFragment) {
        initialize();
        resultDone[0] = false;
        this.qrCodeGenFragment = qrCodeGenFragment;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        //Get 2 objects
        // 1)
        // 2)
        Bundle Genobj = (Bundle) objects[0];
        if(Genobj != null){
            bookingId = Genobj.getString("bookingId");
            clientID = Genobj.getString("myClientID");
            tutorID = Genobj.getString("tutorID");
            userID = Genobj.getString("userID");
        }
        //Check if the
        int counter = 0;
        int counter2 = 0;
        boolean continue_ = true;
        while(continue_)
        {
            counter++;
            if(counter >600000 || result[0])
            {
                QueryAPiForLiveSession(Integer.parseInt(bookingId));
                //Done
                counter = 0;
                if(counter2 < 3){//3 oppotunities
                    counter2++;
                }else{
                    //Toast.makeText(qrCodeGenFragment.getContext(), "Done", Toast.LENGTH_SHORT).show();
                    resultDone[0] = true;
                    continue_ = false; //loop
                    break;
                }

            }
        }
        return result[0];
    }
    /**
     *  initialises Retrofit connections
     */
    public void initialize() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        AtomicReference<Retrofit> retrofit = new AtomicReference<>(new Retrofit.Builder()
                .baseUrl(App_Global_Variables.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(ScalarsConverterFactory.create())
                .build());

         tutorial_SessionController = retrofit.get().create(Tutorial_SessionController.class);
    }
    /**
     *
     * @return
     */
    public void QueryAPiForLiveSession(int clientBookingID){

        result[0] = false;
        Call<String> call = tutorial_SessionController.checkIfSessionExists(clientBookingID);
        call.enqueue(new Callback<String>(){

            @Override
            public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                if(!response.isSuccessful()){
                    //Toast.makeText(qrCodeGenFragment, "Could not load the location", Toast.LENGTH_SHORT).show();
                    return;
                }
                String response_ = response.body();
                TutSessionID[0] = Integer.parseInt(response_);
                if(response_.equals("0"))
                    result[0] = false;
                else
                {
                    result[0] = true;
                    TutSessionID[0] = Integer.parseInt(response_);
                }
                /*try {
                    wait(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
            }
            @Override
            public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                //Toast.makeText(getContext(), "Could not load the location", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
