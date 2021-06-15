package com.tuber_mobile_application;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.tuber_mobile_application.Controllers.Tutorial_SessionController;
import com.tuber_mobile_application.MapsClasses.CheckIfQRCOdeHasBeenScannedManageMent;
import com.tuber_mobile_application.helper.*;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicReference;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class QRCodeGenFragment extends Fragment {
    //Components on the scan QR page
    Button btnGenerateCode;
    ImageView qrImage;
    String bookingId;
    String tutorID;
    String clientID;
    String userID;
    Bundle Genobj;
    private String StartTime;
    private String EndTime;
    private String periods;
    private Tutorial_SessionController tutorial_SessionController;
    int sessionID;

    private SessionManagement sessionManagement;

    public final boolean[] result = new boolean[1];
    public final int[] TutSessionID = new int[1];
    public final boolean[] resultDone = new boolean[1];

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_qrgenerator,container,false);
        Objects.requireNonNull(getActivity()).setTitle("Session QR-Code");
        btnGenerateCode = root.findViewById(R.id.btn_generateQR);
        qrImage = root.findViewById(R.id.qrDisplay);

        sessionManagement = new SessionManagement(Objects.requireNonNull(getContext()));

        setUpRetrofit();
        Genobj = this.getArguments();
        if(Genobj != null){
            bookingId = Genobj.getString("bookingId");
            clientID = Genobj.getString("myClientID");
            tutorID = Genobj.getString("tutorID");
            userID = Genobj.getString("userID");
            StartTime = Genobj.getString("StartTime");
            EndTime = Genobj.getString("EndTime");
            periods = Genobj.getString("periods");
            //endDateTime = Genobj.getString("endTime");

        }
        generateQRCode();
        //Setup button on click listener

        //btnGenerateCode.setVisibility(View.GONE);

        final Object dataTransfer[] = new Object[2];
        dataTransfer[0] = Genobj;
        //final CheckIfQRCOdeHasBeenScanned sessionCheck = new CheckIfQRCOdeHasBeenScanned(this);
        final CheckIfQRCOdeHasBeenScannedManageMent checkIfQRCOdeHasBeenScannedManageMent = new CheckIfQRCOdeHasBeenScannedManageMent(this.getActivity(),this.getContext(),Genobj);

        final Thread sessionCheckThread = new Thread() {

            @Override
            public void run() {
                //Object obj = sessionCheck.execute(dataTransfer);
                boolean continue_ = true;
                while(continue_){
                    checkIfQRCOdeHasBeenScannedManageMent.checkIfSessionHasStarted(Integer.parseInt(bookingId));
                    if(checkIfQRCOdeHasBeenScannedManageMent.resultDone[0])
                        break;
                }


            }
        };
        //sessionCheckThread.start();
        final Thread responseThread = new Thread(){
            @Override
            public void run() {
                //Boolean result = (Boolean) obj;
                while (true){
                    if(checkIfQRCOdeHasBeenScannedManageMent.resultDone[0]){
                        LiveSessionFragment liveSessionFragment = new LiveSessionFragment();
                        Bundle genBundle = new Bundle();
                        SessionManagement sessionManagement = new SessionManagement(Objects.requireNonNull(getContext()));
                        int userID = sessionManagement.getSession();
                        sessionID = checkIfQRCOdeHasBeenScannedManageMent.TutSessionID[0];
                        //Booking ID , Student Id, Tutor ID
                        genBundle.putString("bookingId", "" + bookingId);
                        genBundle.putString("tutorID", "" + tutorID);
                        genBundle.putString("myClientID", "" + clientID);
                        genBundle.putString("tutorialSeshID", "" + sessionID);
                        genBundle.putString("userID", "" + userID);
                        genBundle.putString("StartTime", "" + StartTime);
                        genBundle.putString("EndTime", "" + EndTime);
                        genBundle.putString("periods", "" + periods);
                        liveSessionFragment.setArguments(genBundle);
                        FragmentManager fragmentManager = getFragmentManager();
                        //sessionCheckThread.interrupt();

                        if(fragmentManager != null)
                        {
                            setAlarmTime(); // setting the alarm since the session has started
                            getFragmentManager().beginTransaction().replace(R.id.session_fragment_container,liveSessionFragment).commit();
                        }
                        break;
                    }
                }
            }
        };
        //responseThread.start();

        btnGenerateCode.setOnClickListener(
                new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(View view) {
                        //User chooses to end the checking
                        //checkIfQRCOdeHasBeenScannedManageMent.resultDone[0] = true;
                        BookedSessionFragment bookedSessionFragment = new BookedSessionFragment();

                        FragmentManager fragmentManager = getFragmentManager();
                        //sessionCheckThread.interrupt();
                        if(fragmentManager != null)
                        {
                            responseThread.stop();
                            sessionCheckThread.stop();
                            getFragmentManager().beginTransaction().replace(R.id.session_fragment_container,bookedSessionFragment).commit();
                        }
                    }
                });

        return root;

    }

    @Override
    public void onResume() {

        super.onResume();
        final CheckIfQRCOdeHasBeenScannedManageMent checkIfQRCOdeHasBeenScannedManageMent = new CheckIfQRCOdeHasBeenScannedManageMent(this.getActivity(),this.getContext(),Genobj);
        checkIfSessionHasStarted(Integer.parseInt(bookingId));

        //refresh(checkIfQRCOdeHasBeenScannedManageMent);
    }


    public void generateQRCode()
    {
        //Get the data we want to be stored in the QR code
        //Store tutor details in the QR code
        //SessionManagement sessionManagement = new SessionManagement(this.getContext());
        //final int userID = sessionManagement.getSession();
        String data = String.valueOf(bookingId  + "_" + clientID + "_" + tutorID+ "_" + userID + "_" + EndTime);
        if(data.isEmpty())
        {
            //Default case
            data= "0";
        }

            //Encode the QR code
            QRGEncoder qrEncoder = new QRGEncoder(data,null, QRGContents.Type.TEXT,800);
            //Create a bitmap for the encoded code
            Bitmap qrBts = qrEncoder.getBitmap();
            qrImage.setImageBitmap(qrBts);

    }

    private void setAlarmTime() // sets the alarm time using the string endTime, used only when users
    {
        StringTokenizer sessionTokens = new StringTokenizer(EndTime); // breaking down the date

        // debug string = 20/09/2020 20:30:43
        sessionTokens.nextToken(); // endTime = 20/09/2020 , move tokenizer cursor
        String endTime = sessionTokens.nextToken();  // endTime = 20:30:43

        StringTokenizer timeTokens = new StringTokenizer(endTime,":");

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeTokens.nextToken())); // timeTokens.nextToken = 20
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeTokens.nextToken())); // timeTokens.nextToken = 30
        calendar.set(Calendar.SECOND, 0); // we need alarm to fire at 0 secs of the actual end time

        calendar.add(Calendar.MINUTE, 5); // alert user 5 minutes after endTime

        setAlarm(calendar); // set alarm, for given end time, so that user can be reminded to rate session
    }

    public void setAlarm(Calendar time) // used to set ranking alert alarm
    {
        AlarmManager alarmManager = (AlarmManager) Objects.requireNonNull(getActivity()).getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 10, intent,0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), pendingIntent);

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

                            LiveSessionFragment liveSessionFragment = new LiveSessionFragment();
                            Bundle genBundle = new Bundle();

                            int userID = sessionManagement.getSession();

                            //Booking ID , Student Id, Tutor ID
                            genBundle.putString("bookingId", "" + bookingId);
                            genBundle.putString("tutorID", "" + tutorID);
                            genBundle.putString("myClientID", "" + clientID);
                            genBundle.putString("tutorialSeshID", "" + TutSessionID[0]);
                            genBundle.putString("userID", "" + userID);
                            genBundle.putString("StartTime", "" + StartTime);
                            genBundle.putString("EndTime", "" + EndTime);
                            genBundle.putString("periods", "" + periods);
                            liveSessionFragment.setArguments(genBundle);
                            FragmentManager fragmentManager = getFragmentManager();
                            //sessionCheckThread.interrupt();

                            if(fragmentManager != null)
                            {
                                //setAlarmTime(); // setting the alarm since the session has started
                                getFragmentManager().beginTransaction().replace(R.id.session_fragment_container,liveSessionFragment).commit();
                            }
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

    public void setUpRetrofit() {
        AtomicReference<Retrofit> retrofit = new AtomicReference<>(new Retrofit.Builder()
                .baseUrl(App_Global_Variables.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build());

        tutorial_SessionController = retrofit.get().create(Tutorial_SessionController.class);
    }
}
