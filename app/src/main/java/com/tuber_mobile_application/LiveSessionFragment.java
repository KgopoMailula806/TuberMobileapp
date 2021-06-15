package com.tuber_mobile_application;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tuber_mobile_application.Controllers.ClientBookingController;
import com.tuber_mobile_application.Controllers.EventController;
import com.tuber_mobile_application.Controllers.InvoiceController;
import com.tuber_mobile_application.Controllers.NotificationController;
import com.tuber_mobile_application.Controllers.TutorController;
import com.tuber_mobile_application.Controllers.Tutorial_SessionController;
import com.tuber_mobile_application.Models.Event;
import com.tuber_mobile_application.Models.Invoice;
import com.tuber_mobile_application.Models.Notification;
import com.tuber_mobile_application.helper.AlertReceiver;
import com.tuber_mobile_application.helper.App_Global_Variables;
import com.tuber_mobile_application.helper.HelperMethods;
import com.tuber_mobile_application.helper.SessionManagement;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;



public class LiveSessionFragment extends Fragment {

    private TextView txtNoSession; // contains no session text
    private Button btnCancel; // Cancels session before session has elapsed

    private TextView countDownText;
    private Button startSession;
    private ClientBookingController clientBookingController;
    private Tutorial_SessionController tutorial_sessionController;
    private InvoiceController invoiceController;
    private CountDownTimer countDownTimer;
    private long timeLftInMilliSeconds = 600000; //10 min
    private String bookingId;
    private String tutorID;
    private String clientID;
    private String tutorialSeshID;
    private String userID;
    private String periods;

    private EventController eventController;
    private NotificationController notificationController;
    private TutorController tutorController;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // making view global for easy access
        View liveSessionView = inflater.inflate(R.layout.fragment_live_sessions, container, false);

        Objects.requireNonNull(getActivity()).setTitle("Live Tutorial Session");
        //Initialise controls
        txtNoSession = liveSessionView.findViewById(R.id.txt_no_session);
        btnCancel = liveSessionView.findViewById(R.id.btnCancel_Session);
        countDownText = liveSessionView.findViewById(R.id.count_down_text);
        TextView sessionStartTimeText = liveSessionView.findViewById(R.id.session_start_time_text);
        TextView sessionEndTimeText = liveSessionView.findViewById(R.id.session_end_time_text);

        startSession = liveSessionView.findViewById(R.id.btn_start);
        timeLftInMilliSeconds*= .06; //makes 1 hour
        timeLftInMilliSeconds*= .006; //makes 1 hour
       // timeLftInMilliSeconds*= .6; //makes 1 hour
        initialize();


        startSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                starTimer();
            }
        });

        upDateTimer();
        Bundle Genobj = this.getArguments();

        if(Genobj != null) // set up ui only if there is a session
        {
            String startTime;
            String endTime;
            if(App_Global_Variables.USER_CURRENT_STATUS.equals(App_Global_Variables.CLIENT_STATUS))
            {
                //Coming from the generate code fragment
                bookingId = Genobj.getString("bookingId");
                clientID = Genobj.getString("myClientID");
                tutorID = Genobj.getString("tutorID");
                tutorialSeshID = Genobj.getString("tutorialSeshID");
                userID = Genobj.getString("userID");
                startTime = Genobj.getString("StartTime");
                endTime = Genobj.getString("EndTime");
                periods = Genobj.getString("periods");
                //setTexts
                if(periods != null)
                    timeLftInMilliSeconds *= Integer.parseInt(periods);

                sessionStartTimeText.setText("Start: "+ startTime);
                sessionEndTimeText.setText("End: "+ endTime);
            }
            else
            {
                //Coming from the scanner fragment
                bookingId = Genobj.getString("bookingId");
                clientID = Genobj.getString("myClientID");
                tutorID = Genobj.getString("tutorID");
                tutorialSeshID = Genobj.getString("tutorialSeshID");
                userID = Genobj.getString("userID");
                startTime = Genobj.getString("StartTime");
                endTime = Genobj.getString("EndTime");
                periods = Genobj.getString("periods");
                //setTexts
                if(periods != null)
                    timeLftInMilliSeconds *= Integer.parseInt(periods);
                sessionStartTimeText.setText(startTime);
                sessionEndTimeText.setText(endTime);
            }

            makeSessionInformationVisible(); // making the session information visible to the user
        }
        else
        {
            countDownText.setVisibility(View.GONE);
            startSession.setVisibility(View.GONE);
        }

        return liveSessionView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     *
     */
    private void starTimer() {

        countDownTimer = new CountDownTimer(timeLftInMilliSeconds,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLftInMilliSeconds = millisUntilFinished;
                upDateTimer();
                startSession.setVisibility(View.GONE);
            }

            @Override
            public void onFinish() {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        closedClientBooking(Integer.parseInt(bookingId),Integer.parseInt(tutorialSeshID),"Finished");
                    }
                });
                thread.start();
            }
        }.start();
    }

    /**
     *
     */
    private void stopTimer() {
        countDownTimer.cancel();
    }

    /**
     *
     */
    public void upDateTimer(){
        int minutes = (int) timeLftInMilliSeconds/60000;
        int seconds = (int) timeLftInMilliSeconds % 60000 /1000;


        String timeLeftText;
        timeLeftText = "" + minutes;
        timeLeftText += ":";

        if (seconds < 10)
        {
            timeLeftText += "0";

        }
        timeLeftText += seconds;
        countDownText.setText(timeLeftText);
        countDownText.setVisibility(View.VISIBLE);
    }

    public void closedClientBooking(final int clientBookingID, final int TutorialSessionID, final String closeType)
    {

        Call<String> call = clientBookingController.closeBooking(clientBookingID);
        call.enqueue(new Callback<String>(){

            @Override
            public void onResponse(@NotNull Call< String > call, @NotNull Response< String > response) {
                if(!response.isSuccessful()){
                    Toast.makeText(getContext(), "Could not load booking details", Toast.LENGTH_SHORT).show();
                    return;
                }

                String bookingID = response.body();
                if(bookingID != null)
                {
                    if(bookingID.equals("1"))
                    {
                        //close the session
                        closedSession(Integer.parseInt(String.valueOf(clientBookingID)),TutorialSessionID,closeType);
                        Toast.makeText(getContext(), "successfully registered", Toast.LENGTH_SHORT).show();
                    }else
                        Toast.makeText(getContext(), "Could not load booking details", Toast.LENGTH_SHORT).show();
                }

            }
            @Override
            public void onFailure(@NotNull Call< String > call, @NotNull Throwable t) {
                Toast.makeText(getContext(), "Could not load booking details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     *
     * @param clientBookingID id
     * @param TutorialSessionID id
     */
    public void closedSession(final int clientBookingID, final int TutorialSessionID, final String closeType)
    {
        Call<String> call = tutorial_sessionController.closeSession(TutorialSessionID,clientBookingID);
        call.enqueue(new Callback<String>(){

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(@NotNull  Call< String > call, @NotNull Response< String > response) {
                if(!response.isSuccessful()){
                    Toast.makeText(getContext(), "Could not load booking details", Toast.LENGTH_SHORT).show();
                    return;
                }

                String bookingID = response.body();
                if(bookingID != null)
                {
                    if(closeType.equals("Finished")) // generate invoice only if session was completed
                    {
                        //close the session
                        //Make An Invoice
                        Invoice invoice = new Invoice();
                        invoice.setSession_ID(Integer.parseInt(bookingID));

                        @SuppressLint("SimpleDateFormat")
                        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                        Calendar calendar = Calendar.getInstance();

                        //String date = HelperMethods.getCurrentDateTime();
                        String date = timeFormat.format(calendar.getTime());
                        invoice.setDate_Issued(date);
                        invoice.setDescription("Tutorial session was completed on " + date);
                        invoice.setClient_ID(Integer.parseInt(String.valueOf(clientID)));
                        //find the amount
                        double Amount = App_Global_Variables.PricePerSession* Integer.parseInt(periods);
                        invoice.setAmount(""+ Amount);
                        invoice.setIs_Paid(0);
                        invoice.setPayment_Method("PayPal");
                        generateInvoice(invoice);
                        Toast.makeText(getContext(), "successfully registered", Toast.LENGTH_SHORT).show();
                    }

                }
                else
                    Toast.makeText(getContext(), "Could not load booking details", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(@NotNull Call< String > call, @NotNull Throwable t) {
                Toast.makeText(getContext(), "Could not load booking details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     *
     * @param invoice object
     */
    private void generateInvoice(Invoice invoice) {
        Call<Invoice> call = invoiceController.recordNewInvoice(invoice);
        call.enqueue(new Callback<Invoice>(){

            @Override
            public void onResponse(@NotNull Call< Invoice > call, @NotNull Response< Invoice > response) {
                if(!response.isSuccessful()){
                    Toast.makeText(getContext(), "Could not load booking details", Toast.LENGTH_SHORT).show();
                    return;
                }

                Invoice responseInvoice = response.body();
                if(responseInvoice != null){
                    //close the sesion
                    //Send notification to the client
                    //Redirect
                    sendEventForSessionRating(); // send notification so that can rate

                    Toast.makeText(getContext(), "Session Complete", Toast.LENGTH_SHORT).show();
                }else
                    Toast.makeText(getContext(), "Could not load booking details", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(@NotNull Call< Invoice > call, @NotNull Throwable t) {
                Toast.makeText(getContext(), "Could not load booking details", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     *
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

        clientBookingController = retrofit.get().create(ClientBookingController.class);
        tutorial_sessionController = retrofit.get().create(Tutorial_SessionController.class);
        invoiceController = retrofit.get().create(InvoiceController.class);
        eventController = retrofit.get().create(EventController.class);
        notificationController = retrofit.get().create(NotificationController.class);
        tutorController = retrofit.get().create(TutorController.class);
    }


    /**
     *
     */
    private void makeSessionInformationVisible()
    {
        btnCancel.setVisibility(View.VISIBLE);
        txtNoSession.setVisibility(View.INVISIBLE); // hiding the no session text

        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view)
            {
                final DialogInterface.OnClickListener startSessionListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                            {
                                closedClientBooking(Integer.parseInt(bookingId),Integer.parseInt(tutorialSeshID), "Cancel");
                                stopTimer();
                                cancelAlarm(); // cancel the set alarm for the rating
                            }
                            break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                // do nothing in general
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
                builder.setMessage("You about to cancel this tutorial session, do you want to continue with this?") // alert box question
                        .setPositiveButton("Yes", startSessionListener) // yes button
                        .setNegativeButton("No", startSessionListener).show(); // no button
            }
        });
    }

    /**
     *
     */
    private void cancelAlarm() // used when client cancels the session early
    {
        AlarmManager alarmManager = (AlarmManager) Objects.requireNonNull(getActivity()).getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 10, intent,0);
        alarmManager.cancel(pendingIntent);

    }

    private void sendEventForSessionRating()
    {
        //set up notification system
        Event bookingEvent = new Event();
        bookingEvent.setId(0);
        bookingEvent.setEventType("SessionRating");
        bookingEvent.setDescription("Please rate the elapsed session_" + tutorialSeshID + "/" + clientID + "/" + tutorID);

        Call<Integer> call = eventController.pushEvent(bookingEvent);

        call.enqueue(new Callback<Integer>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(@NotNull Call<Integer> call, @NotNull Response<Integer> response) {

                if (!response.isSuccessful()) {
                    //sendEventForSessionRating();
                    return;
                }

                if(response.body() != null)
                {
                    //get the tutor module ID
                    int eventTableId = response.body();
                    if(eventTableId == 0)
                        sendEventForSessionRating();
                    else
                    {
                        sendNotification(eventTableId);
                        sendTutorNotification(eventTableId);
                    }
                }

            }
            @Override
            public void onFailure(@NotNull Call<Integer> call, @NotNull Throwable t) {
                Toast.makeText(getContext(), "Could not load Tutor ID", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void sendTutorNotification(final int eventTableId)
    {
        Call<String> idCall = tutorController.GetUserTableID(Integer.parseInt(tutorID));
        idCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response)
            {
                if(response.isSuccessful())
                {
                    if(response.body() != null)
                    {
                        int tutorUserId = Integer.parseInt(response.body());

                        @SuppressLint("SimpleDateFormat")
                        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                        @SuppressLint("SimpleDateFormat")
                        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                        Calendar calendar = Calendar.getInstance();

                        Notification eventNotification = new Notification();
                        eventNotification.setDatePosted(dateFormat.format(calendar.getTime()));
                        eventNotification.setTime(timeFormat.format(calendar.getTime()));
                        eventNotification.setSeen(0);
                        eventNotification.setPersonTheNotificationConcerns("Tutor");

                        eventNotification.setEvent_Table_Reference(eventTableId);
                        eventNotification.setUser_Table_Reference(tutorUserId);

                        Call<Integer> notificationCall = notificationController.pushnotification(eventNotification);
                        notificationCall.enqueue(new Callback<Integer>() {
                            @Override
                            public void onResponse(@NotNull Call<Integer> call, @NotNull Response<Integer> response)
                            {
                                if(response.isSuccessful())
                                {
                                    if(response.body() != null)
                                    {
                                        switch (response.body())
                                        {
                                            case 1:
                                                // do nothing
                                            {
                                                BookedSessionFragment bookedSessionFragment = new BookedSessionFragment();
                                                FragmentManager fragmentManager = getFragmentManager();
                                                if(fragmentManager != null)
                                                {
                                                    getFragmentManager().beginTransaction().replace(R.id.session_fragment_container,bookedSessionFragment).commit();
                                                }
                                            }
                                            break;
                                            case 0:
                                                sendNotification(eventTableId);
                                                break;
                                        }
                                    }
                                }

                            }

                            @Override
                            public void onFailure(@NotNull Call<Integer> call, @NotNull Throwable t) {
                                sendNotification(eventTableId);
                            }
                        });

                    }
                }

            }

            @Override
            public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {

            }
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void sendNotification(final int eventTableId)
    {
        SessionManagement sessionManagement = new SessionManagement(Objects.requireNonNull(this.getContext()));
        final int userID = sessionManagement.getSession();

        @SuppressLint("SimpleDateFormat")
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        @SuppressLint("SimpleDateFormat")
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        Calendar calendar = Calendar.getInstance();

        Notification eventNotification = new Notification();
        eventNotification.setDatePosted(dateFormat.format(calendar.getTime()));
        eventNotification.setTime(timeFormat.format(calendar.getTime()));
        eventNotification.setSeen(0);

        if(App_Global_Variables.USER_CURRENT_STATUS.equals(App_Global_Variables.CLIENT_STATUS))
        {
            eventNotification.setPersonTheNotificationConcerns(App_Global_Variables.CLIENT_STATUS);

        } else if(App_Global_Variables.USER_CURRENT_STATUS.equals(App_Global_Variables.TUTOR_STATUS))
        {
            eventNotification.setPersonTheNotificationConcerns(App_Global_Variables.TUTOR_STATUS);
        }

        eventNotification.setEvent_Table_Reference(eventTableId);
        eventNotification.setUser_Table_Reference(userID);

        Call<Integer> notificationCall = notificationController.pushnotification(eventNotification);
        notificationCall.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(@NotNull Call<Integer> call, @NotNull Response<Integer> response)
            {
                if(response.isSuccessful())
                {
                    if(response.body() != null)
                    {
                        switch (response.body())
                        {
                            case 1:
                                // do nothing
                            {
                                BookedSessionFragment bookedSessionFragment = new BookedSessionFragment();
                                FragmentManager fragmentManager = getFragmentManager();
                                if(fragmentManager != null)
                                {
                                    // todo: alarm manager
                                    getFragmentManager().beginTransaction().replace(R.id.session_fragment_container,bookedSessionFragment).commit();
                                }
                            }
                                break;
                            case 0:
                                sendNotification(eventTableId);
                                break;
                        }
                    }
                }

            }

            @Override
            public void onFailure(@NotNull Call<Integer> call, @NotNull Throwable t) {
                sendNotification(eventTableId);
            }
        });
    }

}
