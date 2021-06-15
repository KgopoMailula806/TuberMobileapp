package com.tuber_mobile_application.helper.ui;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;


import androidx.core.app.NotificationCompat;

import com.tuber_mobile_application.Controllers.EventController;
import com.tuber_mobile_application.Home;
import com.tuber_mobile_application.Models.Event;
import com.tuber_mobile_application.helper.*;
import com.tuber_mobile_application.Controllers.NotificationController;
import com.tuber_mobile_application.Models.Notification;
import com.tuber_mobile_application.helper.App_Global_Variables;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NotificationManagement {
    private Activity activity;
    private Context context;
    private NotificationController notificationController;
    private EventController eventController;

    public NotificationManagement(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
        setUpRetrofit();

    }

    public void checkForNotifications(int UserId) {
        setUpRetrofit();
        Call<List<Notification>> call = notificationController.popUsersUnseenNotificationsMobileForIteration(UserId);
        call.enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(@NotNull Call<List<Notification>> call, @NotNull Response<List<Notification>> response) {
                if (!response.isSuccessful()) {
                    return;
                }

                List<Notification> notifications = response.body();
                if (notifications != null) {
                    App_Global_Variables.NUM_NOTIFICATIONS += notifications.size();
                    for (final Notification notification : notifications) {

                        final Call<Event> eventCall = eventController.GetEvent(notification.getEvent_Table_Reference());
                        eventCall.enqueue(new Callback<Event>() {
                            @Override
                            public void onResponse(@NotNull Call<Event> call, @NotNull Response<Event> response) {
                                if (!response.isSuccessful()) {
                                    return;
                                }
                                Event event = response.body();
                                if (event != null) {
                                    NotificationMaker notificationMaker;

                                    // From_Maker/nId/eId/eType/eDescription
                                    String[] details = HelperMethods.separateString(event.getDescription(),"_");
                                    String parcel = null;
                                    //String parcel = "From-Maker/" + notification.getId() + "/" + event.getId() + "/" + event.getEventType() +"/" + event.getDescription();
                                    switch (event.getEventType()) {
                                        case "BookingRequest":
                                            parcel = "From-Notifications~" + notification.getId() + "~" + event.getId() + "~" +  details[3] + "~"
                                                    + event.getEventType() + "~" +  event.getDescription() + "~" +  details[1];
                                            break;
                                        case "Session":

                                            break;
                                        case "BookingFinalization":

                                            break;
                                        case "BookingCancelation":

                                            break;
                                        case "BookingRenegotiation":
                                            parcel = "From-Notifications~" + notification.getId() + "~" + event.getId() + "~" + details[3] + "~"//details[3] -> bookingRequest
                                                    + event.getEventType() + "~" + event.getDescription()+ "~" + details[1]+ "~" + details[4];//details[1] -> OtherUserID, details[1] -> moduleName
                                            break;
                                        case "BookingRejection":
                                            parcel = "From-Notifications~" + notification.getId() + "~" + event.getId() + "~" +  details[2] + "~"
                                            + event.getEventType() + "~" +  event.getDescription()+ "~" +  details[1];
                                    break;
                                }

                                    Intent intent = new Intent(context, Home.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    NotificationChannels requestChannel = new NotificationChannels(context);
                                    NotificationCompat.Builder builder;
                                    if (event.getEventType().equals("SessionRating"))
                                    {
                                        StringTokenizer tokenizer = new StringTokenizer(event.getDescription(),"_");
                                        builder = requestChannel.getRequestChannel(NotificationTitle(event.getEventType()),tokenizer.nextToken());
                                        String toRatingFromNotification = notification.getId() + "/" + tokenizer.nextToken();
                                        intent.putExtra("toRatingFromNotification", toRatingFromNotification);

                                    }
                                    else
                                    {
                                        intent.putExtra("Parcel", parcel);
                                        builder = requestChannel.getRequestChannel(NotificationTitle(event.getEventType()), getNotificationDescription(event));
                                    }


                                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                    builder.setContentIntent(pendingIntent);

                                    requestChannel.getManager().notify(event.getId(), builder.build());

                                }
                            }

                            @Override
                            public void onFailure(@NotNull Call<Event> call, @NotNull Throwable t) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<Notification>> call, @NotNull Throwable t) {
            }
        });

        //refresh  5 every seconds
        refresh(UserId);
    }

    private void refresh(final int userId) {
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                checkForNotifications(userId);

            }
        };
        handler.postDelayed(runnable, 5000);
    }

    public void setUpRetrofit() {
        AtomicReference<Retrofit> retrofit = new AtomicReference<>(new Retrofit.Builder()
                .baseUrl(App_Global_Variables.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build());

        notificationController = retrofit.get().create(NotificationController.class);
        eventController = retrofit.get().create(EventController.class);
    }

    public String getNotificationDescription(Event event) {
        StringTokenizer tokenizer = new StringTokenizer(event.getDescription(), "_:");
        return tokenizer.nextToken();
    }

    public static String NotificationTitle(String eventType) {
        String type = "";
        switch (eventType) {
            case "BookingRequest":
                type = "Booking Request";
                break;
            case "Session":
                type = "Session";
                break;
            case "BookingFinalization":
                type = "Booking Finalization";
                break;
            case "BookingCancelation":
                type = "Booking Cancellation";
                break;
            case "BookingRenegotiation":
                type = "Booking Renegotiation";
                break;
            case "BookingRejection":
                type = "Booking Rejected";
                break;
            case "SessionRating":
                type = "Rate Session";
                break;
            case "":
                type = "Tutor Application Verdict";
        }
        return type;
    }
}
