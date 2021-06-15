package com.tuber_mobile_application;


import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.Result;

import com.tuber_mobile_application.helper.AlertReceiver;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;

import com.tuber_mobile_application.Controllers.BookingController;
import com.tuber_mobile_application.Controllers.ClientBookingController;
import com.tuber_mobile_application.Controllers.Tutorial_SessionController;
import com.tuber_mobile_application.Models.BookingRequest;
import com.tuber_mobile_application.Models.ClientBooking;
import com.tuber_mobile_application.Models.Tutorial_Session;
import com.tuber_mobile_application.helper.App_Global_Variables;
import com.tuber_mobile_application.helper.HelperMethods;
import com.tuber_mobile_application.helper.SessionManagement;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.atomic.AtomicReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static com.tuber_mobile_application.helper.HelperMethods.separateString;

public class QRScannerFragment extends Fragment {
    TextView startTime;
    TextView endTime;
    CodeScannerView codeScannerView;
    CodeScanner codeScanner;
    TextView qrTextView;
    TextView countDownText;
    ClientBooking clientBooking;
    Tutorial_Session tutorial_session;
    private BookingController bookingController;
    private ClientBookingController clientBookingController;
    private Tutorial_SessionController tutorial_SessionController;
    private String[] qrText = new String[1];
    private ProgressBar progressBar;
    private String endDateTime;

    private static int CAM_REQUEST_CODE = 1982;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Session Scan");
        View view = inflater.inflate(R.layout.fragment_scan, container, false);

        codeScannerView = view.findViewById(R.id.scannerView);
        codeScanner = new CodeScanner(getActivity(),codeScannerView);
        qrTextView = view.findViewById(R.id.QRCOdeData);
        progressBar = view.findViewById(R.id.progressBar_scanner);

       //countDownText = view.findViewById();
        initialize();
        //Set decode callback
        Thread thread = new Thread(){
            @Override
            public void run() {
                scan();
            }
        };
        thread.start();
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAM_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(getContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
            }
        }
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

        bookingController = retrofit.get().create(BookingController.class);
        clientBookingController = retrofit.get().create(ClientBookingController.class);
        tutorial_SessionController = retrofit.get().create(Tutorial_SessionController.class);
    }

    /**
     *
     */
    public void scan(){
        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result)
            {
                //Stage (1)
                // data: 0) bookingId, 1) clientID 2) tutorID  userID en);
                qrText[0] = result.toString();
                String[] tokens = separateString(qrText[0], "_");


                final String bookingId = tokens[0];
                final String clientID = tokens[1];
                final String tutorID = tokens[2];
                String userID = tokens[3];
                endDateTime = tokens[4]; // session endTime for alarm

                Thread thread = new Thread() {
                    @Override
                    public void run() {

                        //TODO:
                        //  1) Get Data
                        //  2) Make tutorial session (i.e. insert Tutorial session row)
                        //  {
                        //  "id": 0,
                        //  "session_Date": "string",
                        //  "session_Start_Time": "string",
                        //  "session_End_Time": "string",
                        //  "tutors_Client_Rating": 0,
                        //  "clients_tutor_Rating": 0,
                        //  "tutors_Session_FeedBack": "string",
                        //  "clients_Session_FeedBack": "string",
                        //  "geographic_Location": "string",
                        //  "isCompleted": 0,
                        //  "tutors_Paths": "string",
                        //  "clients_Paths": "string",
                        //  "client_Reference": 0,
                        //  "tutor_Id": 0
                        //  }
                        //  3) redirect to live session

                        clientBooking= new ClientBooking();
                        //set Available clientBooking data
                        clientBooking.setId(Integer.parseInt(bookingId));
                        clientBooking.setIsActive(1);
                        clientBooking.setClient_Table_Reference(Integer.parseInt(clientID));
                        clientBooking.setTutor_Table_Reference(Integer.parseInt(tutorID));
                        //set Available tutorial_session data
                        tutorial_session = new Tutorial_Session();
                        tutorial_session.setClientBookingID(Integer.parseInt(bookingId));
                        tutorial_session.setClient_Reference(Integer.parseInt(clientID));
                        tutorial_session.setTutor_Id(Integer.parseInt(tutorID));
                        tutorial_session.setIsCompleted(0);

                        getBookedSessionDetails(clientBooking, tutorial_session);

                    }
                };
                thread.start();
            }
        });
    }
    /**
     *
     * @param session
     * @param currentTutorialSession
     */
    public void getBookedSessionDetails(final ClientBooking session,final Tutorial_Session currentTutorialSession){
        Call<ClientBooking> call = clientBookingController.getBookingAt(session.getId());
        call.enqueue(new Callback<ClientBooking>(){

            @Override
            public void onResponse(@NotNull Call< ClientBooking > call, @NotNull Response< ClientBooking > response) {
                if(!response.isSuccessful()){
                    Toast.makeText(getContext(), "Could not load booking details", Toast.LENGTH_SHORT).show();
                    return;
                }

                ClientBooking booking = response.body();
                if(booking != null){
                    //Set Remaining ClientBooking Details
                    session.setBookingDetails_BookingRequestTable_Reference(booking.getBookingDetails_BookingRequestTable_Reference());
                    session.setDate_Time(booking.getDate_Time());
                    session.setEndTime(booking.getEndTime());
                    session.setIsActive(booking.getIsActive());
                    //Set Remaining Tutorial Details
                    currentTutorialSession.setIsCompleted(0);
                    currentTutorialSession.setSession_Date(booking.getDate_Time());
                    currentTutorialSession.setQRContent(qrText[0]);

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                    try {
                        String startTime = simpleDateFormat.parse(session.getDate_Time()).toString();

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    getRemainSessionDetails(session,currentTutorialSession);
                }
            }
            @Override
            public void onFailure(@NotNull Call< ClientBooking > call, @NotNull Throwable t) {
                Toast.makeText(getContext(), "Could not load booking details", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     *gET:
     * - periods
     * - endtime
     * - Address
     * @param session
     * @param currentTutorialSession
     */
    public void getRemainSessionDetails(final ClientBooking session, final Tutorial_Session currentTutorialSession){
        Call<BookingRequest> call = bookingController.GetIndividualBookingRequest(session.getBookingDetails_BookingRequestTable_Reference());
        call.enqueue(new Callback<BookingRequest>(){

            @Override
            public void onResponse(@NotNull Call<BookingRequest> call, @NotNull Response<BookingRequest> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(getContext(), "Remaining details fails", Toast.LENGTH_SHORT).show();
                    return;
                }

                BookingRequest bookingRequest = response.body();
                if(bookingRequest != null){
                    //Set Remaining ClientBooking Details
                    currentTutorialSession.setSession_Start_Time(bookingRequest.getRequestTime());
                    //**

                    currentTutorialSession.setSession_End_Time(bookingRequest.getEndTime());
                    currentTutorialSession.setGeographic_Location(bookingRequest.getClientProposedLocation());
                    session.setPeriods(bookingRequest.getPeriods());
                    //Record the Object

                    setUpTutorial(session,currentTutorialSession);
                }

            }
            @Override
            public void onFailure(@NotNull Call<BookingRequest> call, @NotNull Throwable t) {
                Toast.makeText(getContext(), "Could get remaining session details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setAlarm(Calendar time) // used to set ranking alert alarm
    {
        AlarmManager alarmManager = (AlarmManager) Objects.requireNonNull(getActivity()).getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 10, intent,0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), pendingIntent);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onResume() {
        super.onResume();
        int permissionCheck = Objects.requireNonNull(getContext()).checkSelfPermission(Manifest.permission.CAMERA);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[] {Manifest.permission.CAMERA},CAM_REQUEST_CODE);
        }
        else
        {
            codeScanner.startPreview();
        }

    }

    public void setUpTutorial(final ClientBooking session, final Tutorial_Session currentTutorialSession)
    {
        Call<Integer> call = tutorial_SessionController.recordTutorialSession(currentTutorialSession);
        call.enqueue(new Callback<Integer>(){

            @Override
            public void onResponse(@NotNull Call<Integer> call, @NotNull Response<Integer> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(getContext(), "Could not set up tutorial", Toast.LENGTH_SHORT).show();
                    return;
                }

                Integer tutorial_sessionID = response.body();
                getTSession(session,currentTutorialSession.getClientBookingID(),tutorial_sessionID);
                if(tutorial_session != null){
                    //Redirect to live session fragment
                    SessionFragment sessionFragment = new SessionFragment();
                    Bundle genBundle = new Bundle();
                    SessionManagement sessionManagement = new SessionManagement(Objects.requireNonNull(getContext()));
                    int userID = sessionManagement.getSession();

                    //Booking ID , Student Id, Tutor ID
                    genBundle.putString("bookingId", "" + currentTutorialSession.getClientBookingID());
                    genBundle.putString("tutorID", "" + currentTutorialSession.getTutor_Id());
                    genBundle.putString("myClientID", "" + currentTutorialSession.getClient_Reference());
                    genBundle.putString("tutorialSeshID", "" + tutorial_sessionID);
                    genBundle.putString("userID", "" + userID);
                    genBundle.putString("StartTime", "" + currentTutorialSession.getSession_Start_Time());
                    genBundle.putString("EndTime", "" + currentTutorialSession.getSession_End_Time());
                    genBundle.putString("periods", "" + session.getPeriods());

                    sessionFragment.setArguments(genBundle);
                    FragmentManager fragmentManager = getFragmentManager();
                    if(fragmentManager != null)
                    {
                        StringTokenizer sessionTokens = new StringTokenizer(currentTutorialSession.getSession_End_Time()); // breaking up the endDate Time

                        // debug string = 20/09/2020 20:30:43
                        String endDate = sessionTokens.nextToken(); // endTime = 20/09/2020 , move tokenizer cursor

                        String endTime = sessionTokens.nextToken();
                        String[] endTimeItems = HelperMethods.separateString(endTime,":");  // endTime = 20:30:43

                        //StringTokenizer timeTokens = new StringTokenizer(endTime,":");

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endTimeItems[0])); // timeTokens.nextToken = 20
                        calendar.set(Calendar.MINUTE, Integer.parseInt(endTimeItems[1])); // timeTokens.nextToken = 30
                        calendar.set(Calendar.SECOND, Integer.parseInt(endTimeItems[2])); // we need alarm to fire at 0 secs of the actual end time

                        calendar.add(Calendar.MINUTE,5); // alert user 5 minutes after endTime

                        setAlarm(calendar); // set alarm, for given end time, so that user can be reminded to rate session
                        getFragmentManager().beginTransaction().replace(R.id.fragment_container,new TutorDashboardFragment()).commit();
                    }
                }

            }



            @Override
            public void onFailure(@NotNull Call<Integer> call, @NotNull Throwable t) {
                Toast.makeText(getContext(), "Could not load the location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     *
     * @param clientBookingID
     * @param tutorial_sessionID
     */
    private void getTSession(final ClientBooking session, int clientBookingID, Integer tutorial_sessionID)
    {
        Call<List<Tutorial_Session>> call = tutorial_SessionController.getTutorial_SessionViaClientBookingID(clientBookingID);
        call.enqueue(new Callback<List<Tutorial_Session>>(){

            @Override
            public void onResponse(@NotNull Call<List<Tutorial_Session>> call, @NotNull Response<List<Tutorial_Session>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Could not set up tutorial", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<Tutorial_Session> currentTutorialSession = response.body();
            }


            @Override
            public void onFailure(@NotNull Call<List<Tutorial_Session>> call, @NotNull Throwable t) {
                Toast.makeText(getContext(), "Could not load the location", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
