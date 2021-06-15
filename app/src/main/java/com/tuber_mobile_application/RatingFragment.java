package com.tuber_mobile_application;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


import com.tuber_mobile_application.Controllers.NotificationController;
import com.tuber_mobile_application.Controllers.Tutorial_SessionController;
import com.tuber_mobile_application.Models.Rating;
import com.tuber_mobile_application.helper.AlertReceiver;
import com.tuber_mobile_application.helper.App_Global_Variables;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RatingFragment extends Fragment
{
    private double rating;
    private int notificationID;
    private Rating rate;
    private NotificationController notificationController;
    private Tutorial_SessionController sessionController;
    private EditText txtComment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        Objects.requireNonNull(getActivity()).setTitle("Rate The Session");
        View view = inflater.inflate(R.layout.fragment_rating,container,false);

        RatingBar ratingBar = view.findViewById(R.id.ratingBar);
        txtComment = view.findViewById(R.id.rating_comment);

        setUpRetrofit();
        Button btnSubmit = view.findViewById(R.id.btnSubmit_rating);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                rating = v;

            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitRating();
                saveRating(rate);

            }
        });

        return view;
    }

    public void submitRating()
    {
        Bundle ratingBundle = this.getArguments();
        if(ratingBundle != null)
        {
            rate = new Rating();

            String parcel = ratingBundle.getString("toRatingFromNotification");

            if(parcel != null)
            {

                StringTokenizer tokenizer = new StringTokenizer(parcel,"/");

                // parcel = notificationID/sessionID/clientID/tutorID
                notificationID = Integer.parseInt(tokenizer.nextToken());
                makeNotificationSeen(); // to change the notification status

                int sessionID = Integer.parseInt(tokenizer.nextToken());
                int clientID = Integer.parseInt(tokenizer.nextToken());
                int tutorID = Integer.parseInt(tokenizer.nextToken());

                rate.setId(0);
                rate.setClient_ID(clientID);
                rate.setTutor_ID(tutorID);
                rate.setSession_Reference(sessionID);

                if(App_Global_Variables.USER_CURRENT_STATUS.equals(App_Global_Variables.CLIENT_STATUS))
                {
                    rate.setClient_Rating(rating);
                    rate.setComment("Client*" + txtComment.getText().toString());
                }
                else
                {
                    rate.setTutor_Rating(rating);
                    rate.setComment("Tutor*" + txtComment.getText().toString());
                }
            }
        }

    }

    private void saveRating(final Rating rate)
    {
        Call<Rating> ratingCall = sessionController.saveRating(rate);
        ratingCall.enqueue(new Callback<Rating>() {
            @Override
            public void onResponse(@NotNull Call<Rating> call, @NotNull Response<Rating> response)
            {
                if(!response.isSuccessful())
                {
                    //saveRating(rate); // phinda mzala
                    Toast.makeText(getContext(), "Something went wrong with recording the rating", Toast.LENGTH_SHORT).show();
                }
                Rating rating = response.body();
                    if(rating != null)
                    {
                        //stopReminder(); // cancel alarm if it hasn't went off
                        // say thanks
                        getFragmentManager().beginTransaction().replace(R.id.fragment_container, new RatingsAppreciationFragment()).commit();

                   }
            }

            @Override
            public void onFailure(@NotNull Call<Rating> call, @NotNull Throwable t) {
                Toast.makeText(getContext(), "Save rating failed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void stopReminder()
    {
        AlarmManager alarmManager = (AlarmManager) Objects.requireNonNull(getActivity()).getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 10, intent,0);
        alarmManager.cancel(pendingIntent);
    }

    private void makeNotificationSeen()
    {

        Call<Integer> userCall = notificationController.changeNotificationStatus(notificationID);
        userCall.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(@NotNull Call<Integer> call, @NotNull Response<Integer> userResponse) {
                if (!userResponse.isSuccessful()) {
                    return;
                }
                Integer response = userResponse.body();

                if (response != null) {
                    switch (response){
                        case 1:
                        {
                            // do nothing, mission complete
                        }
                        break;
                        case  0:
                        {
                            makeNotificationSeen(); // try again
                        }
                        break;
                    }
                }
            }
            @Override
            public void onFailure(@NotNull Call<Integer> call, @NotNull Throwable t) {
                makeNotificationSeen(); // try again
            }
        });
    }

    private void setUpRetrofit()
    {

        AtomicReference<Retrofit> retrofit = new AtomicReference<>(new Retrofit.Builder()
                .baseUrl(App_Global_Variables.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())// for Json type conversions
                .addConverterFactory(ScalarsConverterFactory.create()) //for string conversions
                .build());

        notificationController = retrofit.get().create(NotificationController.class);
        sessionController = retrofit.get().create(Tutorial_SessionController.class);
    }
}
