package com.tuber_mobile_application;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;

import android.content.Context;

import android.content.DialogInterface;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.tuber_mobile_application.Controllers.BookingController;
import com.tuber_mobile_application.Controllers.ClientBookingController;
import com.tuber_mobile_application.Controllers.ClientController;
import com.tuber_mobile_application.Controllers.EventController;
import com.tuber_mobile_application.Controllers.ModuleController;
import com.tuber_mobile_application.Controllers.NotificationController;
import com.tuber_mobile_application.Controllers.TutorController;
import com.tuber_mobile_application.Controllers.UserController;
import com.tuber_mobile_application.Models.BookingRequest;
import com.tuber_mobile_application.Models.ClientBooking;
import com.tuber_mobile_application.Models.Event;
import com.tuber_mobile_application.Models.Notification;
import com.tuber_mobile_application.Models.User;
import com.tuber_mobile_application.helper.App_Global_Variables;
import com.tuber_mobile_application.helper.SessionManagement;
import com.tuber_mobile_application.helper.ui.RequestAdapter;
import com.tuber_mobile_application.helper.ui.RequestItem;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MultipurposeFragment extends Fragment
{
    private int bookingRequestID;
    private String[] description;
    private  String type;
    private BookingController bookingController; // used to get booking information
    private UserController userController; // used to get user information
    private final BookingRequest[] bookingRequests = new BookingRequest[1];
    private Button btnNegotiate, btnAllStar;
    private TextInputLayout txtLocation, txtPeriods, txtDate, txtTime, txtReason; // all of the inactive ui text input layouts
    private TextView txtDisplay; // text view used to display request information
    private View mlpView;
    private String NotificationType;
    private ClientBookingController clientBookingController;
    private EventController eventController;
    private int[] eventID = new int[1];
    private NotificationController notificationController;
    private int[] notificationID = new int[1];
    private int[] tutorUserId = new int[1];
    private int[] clientUserId = new int[1];
    private User[] tutorUserDetails = new User[1];
    private User[] clientUserDetails = new User[1];
    private String[] moduleName= new String[1];
    private TutorController tutorController;
    private ClientController clientController;
    private ModuleController moduleController;
    private String parcel;
    private LinearLayout negotiate;
    private RecyclerView recyclerView;
    private RequestAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<RequestItem> requestItems;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mlpView = inflater.inflate(R.layout.fragment_multipurpose, container, false);

        setUpNotificationMode(); // function to fill up views in the ui
        setUpActions(); // method that holds the onclick listeners

        final Bundle Genobj = this.getArguments();
        TextInputLayout sessionLocationTXT = mlpView.findViewById(R.id.mlp_location);
        sessionLocationTXT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putString("ComingFromType","Location-request");
                bundle.putString("Parcel",Genobj.getString("Parcel"));
                bundle.putString("UserLocation",bookingRequests[0].getClientProposedLocation());
                bundle.putString("moduleName",Genobj.getString("moduleName"));
                bundle.putString("NotificationType",Genobj.getString("NotificationType") );

                MapsFragment mapsFragment = new MapsFragment();
                mapsFragment.setArguments(bundle); // setting arguments for next fragment
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, mapsFragment).commit();
            }
        });
        Bundle bundle = this.getArguments();
        if(bundle != null){
            String location = bundle.getString("Location-request");
            sessionLocationTXT.getEditText().setText(location);
        }
        //txtLocation, txtPeriods, txtDate, txtTime, txtReason; // all of the inactive ui text input layouts

        return mlpView;
    }
    private RequestFragment.RequestFragmentListener mListener;

    public void toMap(){

        if(mListener != null){
            mListener.goToMap();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof RequestFragment.RequestFragmentListener){
            mListener = (RequestFragment.RequestFragmentListener) context;
        } else {
            throw new RuntimeException("Main activity needs to implement the RequestFragmentListener");
        }
    }

    /**
     *
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
    //todo: Method Incomplete
    private void setUpActions()
    {
        //Arguments from previous Activity
        final Bundle Genobj = this.getArguments();

        btnAllStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogInterface.OnClickListener makeDecision = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        btnAllStar.setVisibility(View.GONE);

                        switch (which)
                        {
                            case DialogInterface.BUTTON_POSITIVE:
                            {
                                acceptBooking(Genobj);  // accept the booking
                            }
                            break;

                            case DialogInterface.BUTTON_NEUTRAL:
                            {
                                showNegotiationControls(); // show linear layout & button
                            }
                            break;

                            case DialogInterface.BUTTON_NEGATIVE:
                            {
                                rejectBooking(Genobj); // reject the booking
                            }
                            break;

                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
                builder.setMessage("What action do want to take towards the booking request?") // alert box question
                        .setPositiveButton("Accept", makeDecision) // yes button
                        .setNegativeButton("Reject", makeDecision) // no button
                        .setNeutralButton("Negotiate", makeDecision) // negotiate button
                        .show();
            }
        });

        btnNegotiate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                renegotiate(Genobj);
            }
        });

    }

    private void acceptBooking(final Bundle Genobj)
    {
        if(Genobj != null){
             NotificationType = Genobj.getString("NotificationType");
             if(NotificationType.equals("BookingRequest")){
            //todo: accept request method
            //  Create a new Booking request table entry
            // set up a notification
            // {
            //  "id": 0,
            //  "date_Time": "string",
            //  "isActive": 0,
            //  "bookingDetails_BookingRequestTable_Reference": 0,
            //  "periods": 0,
            //  "endTime": "string",
            //  "tutor_Table_Reference": 0,
            //  "client_Table_Reference": 0
            //  }
            ClientBooking CBooking =  new ClientBooking();
            CBooking.setId(0);
            CBooking.setDate_Time(bookingRequests[0].getRequestDate());
            CBooking.setIsActive(1);
            CBooking.setBookingDetails_BookingRequestTable_Reference(bookingRequests[0].getId());
            CBooking.setPeriods(bookingRequests[0].getPeriods());
            CBooking.setEndTime(bookingRequests[0].getEndTime());
            CBooking.setTutor_Table_Reference(bookingRequests[0].getTutor_Reference());
            CBooking.setClient_Table_Reference(bookingRequests[0].getClient_Reference());

            submitBookingRequest(CBooking);
            //cb
            }
        }
    }

    private void rejectBooking(final Bundle Genobj)
    {
        if(Genobj != null){
            NotificationType = Genobj.getString("NotificationType");
            if(NotificationType.equals("BookingRequest")){
                //todo: accept request method
                //  Create a new Booking request table entry
                // set up a notification
                // {
                //  "id": 0,
                //  "date_Time": "string",
                //  "isActive": 0,
                //  "bookingDetails_BookingRequestTable_Reference": 0,
                //  "periods": 0,
                //  "endTime": "string",
                //  "tutor_Table_Reference": 0,
                //  "client_Table_Reference": 0
                //  }
                ClientBooking CBooking =  new ClientBooking();
                CBooking.setId(0);
                CBooking.setDate_Time(bookingRequests[0].getRequestDate());
                CBooking.setIsActive(1);
                CBooking.setBookingDetails_BookingRequestTable_Reference(bookingRequests[0].getId());
                CBooking.setPeriods(bookingRequests[0].getPeriods());
                CBooking.setEndTime(bookingRequests[0].getEndTime());
                CBooking.setTutor_Table_Reference(bookingRequests[0].getTutor_Reference());
                CBooking.setClient_Table_Reference(bookingRequests[0].getClient_Reference());

                submitBookingRequestREjection(CBooking);
                //cb
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void renegotiate(final Bundle Genobj){
        if(Genobj != null){
            NotificationType = Genobj.getString("NotificationType");
            if(NotificationType.equals("BookingRequest")){
                //todo: accept request method
                //  Create a new Booking request table entry
                // set up a notification
                // {
                //  "id": 0,
                //  "date_Time": "string",
                //  "isActive": 0,
                //  "bookingDetails_BookingRequestTable_Reference": 0,
                //  "periods": 0,
                //  "endTime": "string",
                //  "tutor_Table_Reference": 0,
                //  "client_Table_Reference": 0
                //  }
                ClientBooking CBooking =  new ClientBooking();
                CBooking.setId(0);
                CBooking.setDate_Time(bookingRequests[0].getRequestDate());
                CBooking.setIsActive(1);
                CBooking.setBookingDetails_BookingRequestTable_Reference(bookingRequests[0].getId());
                CBooking.setPeriods(bookingRequests[0].getPeriods());
                CBooking.setEndTime(bookingRequests[0].getEndTime());
                CBooking.setTutor_Table_Reference(bookingRequests[0].getTutor_Reference());
                CBooking.setClient_Table_Reference(bookingRequests[0].getClient_Reference());

                sendNotificationNegotiateBookingNotification(CBooking);
            }
        }
    }

    /**
     *
     */
    private void setUpNotificationMode()
    {
        initialize();
        setUpRetrofit();
        Bundle bundle = this.getArguments();
        if (bundle != null)
        {

            final String[] fragmentName = {""};
              parcel = bundle.getString("Parcel");
            moduleName[0] = bundle.getString("moduleName");
            // From_Notifications/nID/eID/bID/eventType/eventDescription || From_Maker/nId/eId/eType/eDescription
            StringTokenizer notificationTokens = new StringTokenizer(parcel, "~");

            String from = notificationTokens.nextToken();

            int eventID;
            String desc = "";

            if(from.equals("From-Maker")) // from notification panel
            {
                notificationID[0] = Integer.parseInt(notificationTokens.nextToken());
                eventID = Integer.parseInt(notificationTokens.nextToken());
                type = notificationTokens.nextToken();
                description = NotificationsFragment.breakupDescription(type, notificationTokens.nextToken());
                bookingRequestID = Integer.parseInt(description[3]);

            }
            else if(from.equals("From-Notifications")) // from notification fragment
            {
                notificationID[0] = Integer.parseInt(notificationTokens.nextToken());
                eventID = Integer.parseInt(notificationTokens.nextToken());
                bookingRequestID = Integer.parseInt(notificationTokens.nextToken());
                type = notificationTokens.nextToken();
                desc = notificationTokens.nextToken();
                description = NotificationsFragment.breakupDescription(type, desc);
            }

            Call<BookingRequest> bookingRequestCall = bookingController.getRequest(bookingRequestID);
            bookingRequestCall.enqueue(new Callback<BookingRequest>()
            {
                @Override
                public void onResponse(@NotNull Call<BookingRequest> call, @NotNull final Response<BookingRequest> bookingRequestResponse)
                {
                    if(bookingRequestResponse.body() != null)
                    {
                        // switch to control visibility & control their actions
                        switch (type) {
                            case "BookingRequest": {
                                getActivity().setTitle("Booking Request");
                                fragmentName[0] = "Booking Request";

                                int userId_ = Integer.parseInt(description[4]);
                                Call<User> userCall = userController.getUserDetails(userId_);
                                userCall.enqueue(new Callback<User>() {
                                    @Override
                                    public void onResponse(@NotNull Call<User> call, @NotNull Response<User> userResponse) {
                                        if (!userResponse.isSuccessful()) {
                                            return;
                                        }
                                        User user = userResponse.body();

                                        if (user != null)
                                        {
                                            requestItems = new ArrayList<>();

                                            RequestItem item = new RequestItem(user.getFullNames() + " " + user.getSurname(), user.getValid_Phone_Number(),
                                                    bookingRequestResponse.body().getRequestDate(), bookingRequestResponse.body().getRequestTime(), bookingRequestResponse.body().getEndTime(),
                                                    bookingRequestResponse.body().getClientProposedLocation(),
                                                    String.valueOf(bookingRequestResponse.body().getPeriods()), description[1],""); // no reason because its a request

                                            requestItems.add(item);
                                            recyclerView.setHasFixedSize(true);
                                            layoutManager = new LinearLayoutManager(getContext());
                                            adapter = new RequestAdapter(requestItems);
                                            recyclerView.setLayoutManager(layoutManager);
                                            recyclerView.setAdapter(adapter);

                                            adapter.setOnClickListener(new RequestAdapter.OnItemClickListener() {
                                                @Override
                                                public void onPathClick(int pos) {

                                                    final DialogInterface.OnClickListener pathListener = new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i)
                                                        {
                                                            if (i == DialogInterface.BUTTON_POSITIVE)
                                                            {
                                                                //todo: put code here
                                                                Toast.makeText(getContext(), "Show Path", Toast.LENGTH_SHORT).show();
                                                            }

                                                        }
                                                    };

                                                    AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
                                                    builder.setMessage("Display the path from your location to the request location?") // alert box question
                                                            .setPositiveButton("Yes", pathListener).show(); // no button
                                                }
                                            });


                                            bookingRequests[0] = new BookingRequest();
                                            bookingRequests[0].setId(0);
                                            bookingRequests[0].setRequestDate(bookingRequestResponse.body().getRequestDate());
                                            bookingRequests[0].setRequestTime(bookingRequestResponse.body().getRequestTime());
                                            bookingRequests[0].setModuleID(bookingRequestResponse.body().getModuleID1());
                                            bookingRequests[0].setId(bookingRequestResponse.body().getId());
                                            bookingRequests[0].setPeriods(bookingRequestResponse.body().getPeriods());
                                            bookingRequests[0].setEndTime(bookingRequestResponse.body().getEndTime());
                                            bookingRequests[0].setClientProposedLocation(bookingRequestResponse.body().getClientProposedLocation());
                                            bookingRequests[0].setTutor_Reference(bookingRequestResponse.body().getTutor_Reference());
                                            bookingRequests[0].setClient_Reference(bookingRequestResponse.body().getClient_Reference());

                                            //getModule(bookingRequestResponse.body().getModuleID1());
                                            setTutorUserTableID();
                                            setClientUserTableID();
                                            getTutorUserDetails(bookingRequestResponse.body().getTutor_Reference());
                                            makeNotificationUnseen(notificationID[0]);

                                        }
                                    }
                                    @Override
                                    public void onFailure(@NotNull Call<User> call, @NotNull Throwable t) {
                                        // todo: handle
                                    }
                                });
                            }
                            break;
                        }
                    }
                    Objects.requireNonNull(getActivity()).setTitle(fragmentName[0]);
                }

                @Override
                public void onFailure(@NotNull Call<BookingRequest> call, @NotNull Throwable t) {

                }
            });
        }
    }

    /**
     *
     */
    private void showNegotiationControls() {
        negotiate.setVisibility(View.VISIBLE);
        btnNegotiate.setVisibility(View.VISIBLE);

        StringTokenizer tokenizer = new StringTokenizer(bookingRequests[0].getRequestDate());
        txtDate.getEditText().setText(tokenizer.nextToken());
        txtLocation.getEditText().setText(bookingRequests[0].getClientProposedLocation());
        txtPeriods.getEditText().setText(String.valueOf(bookingRequests[0].getPeriods()));
        txtTime.getEditText().setText(tokenizer.nextToken());

        txtTime.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                showTimeDialog();
            }
        });

        txtDate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                showDateDialog();
            }
        });
    }

    private void setText(String location, String periods, String date, String time) {
        txtLocation.getEditText().setText(location);
        txtPeriods.getEditText().setText(periods);
        txtTime.getEditText().setText(time);
        txtDate.getEditText().setText(date);
    }

    private void initialize() {

        recyclerView = mlpView.findViewById(R.id.mlp_recycler_view);
        btnAllStar = mlpView.findViewById(R.id.mlp_one_stop);
        btnNegotiate = mlpView.findViewById(R.id.mlp_submit_negotiation);
        negotiate = mlpView.findViewById(R.id.booking_renegotiation_ll);
        txtLocation = mlpView.findViewById(R.id.mlp_location);
        txtPeriods = mlpView.findViewById(R.id.mlp_periods);
        txtTime = mlpView.findViewById(R.id.mlp_time);
        txtDate = mlpView.findViewById(R.id.mlp_date);
        txtReason = mlpView.findViewById(R.id.mlp_reason);
    }

    private void setUpRetrofit() {
        AtomicReference<Retrofit> retrofit = new AtomicReference<>(new Retrofit.Builder()
                .baseUrl(App_Global_Variables.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())// for Json type conversions
                .addConverterFactory(ScalarsConverterFactory.create()) //for string conversions
                .build());

        userController = retrofit.get().create(UserController.class);
        bookingController = retrofit.get().create(BookingController.class);
        clientBookingController = retrofit.get().create(ClientBookingController.class);
        eventController = retrofit.get().create(EventController.class);
        notificationController = retrofit.get().create(NotificationController.class);
        tutorController = retrofit.get().create(TutorController.class);
        clientController = retrofit.get().create(ClientController.class);
        moduleController = retrofit.get().create(ModuleController.class);

    }

    /**
     *
     * @param cBooking
     */
    private void submitBookingRequest(final ClientBooking cBooking) {
        Call<String> userCall = clientBookingController.AddClientBooking(cBooking);
        userCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NotNull Call<String> call, @NotNull Response<String> userResponse) {
                if (!userResponse.isSuccessful()) {
                    return;
                }
                String response = userResponse.body();

                if (response != null) {
                    switch (response){
                        case"1":
                        {
                            sendNotificationFinalizationNotification();
                            setBookingRequestAcceptence(cBooking);
                        }break;
                        case "0":
                        {
                            Toast.makeText(getContext(), "Event table ID couldn't be loaded", Toast.LENGTH_SHORT).show();
                        }break;
                    }
                }
            }
            @Override
            public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                // todo: handle
            }
        });
    }


    public void setBookingRequestAcceptence(final ClientBooking cBooking){
        //Make booking request not active and change accepted status
        Call<String> userCall = bookingController.changeBookingRequestAcceptedStatus(cBooking.getBookingDetails_BookingRequestTable_Reference(),1);
        userCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NotNull Call<String> call, @NotNull Response<String> bookingRequestResponse) {
                if (!bookingRequestResponse.isSuccessful()) {
                    return;
                }
                String response = bookingRequestResponse.body();

                if (response != null) {
                    switch (response){
                        case"1":
                        {


                        }break;
                        case "0":
                        {
                            Toast.makeText(getContext(), "Could not update booking request acceptance code", Toast.LENGTH_SHORT).show();
                        }break;
                    }
                }
            }
            @Override
            public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                // todo: handle
            }
        });
    }
    /**
     *
     * @param cBooking
     */
    private void submitBookingRequestREjection(final ClientBooking cBooking) {
        //Make booking request not active and change accepted status
        Call<String> userCall = bookingController.changeBookingRequestAcceptedStatus(cBooking.getBookingDetails_BookingRequestTable_Reference(),-1);
        userCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NotNull Call<String> call, @NotNull Response<String> bookingRequestResponse) {
                if (!bookingRequestResponse.isSuccessful()) {
                    return;
                }
                String response = bookingRequestResponse.body();

                if (response != null) {
                    switch (response){
                        case"1":
                        {
                            getClientUserTableDetailsForBookingRejection();

                        }break;
                        case "0":
                        {
                            Toast.makeText(getContext(), "Event table ID couldn't be loaded", Toast.LENGTH_SHORT).show();
                        }break;
                    }
                }
            }
            @Override
            public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                // todo: handle
            }
        });
    }

    /**
     * Method gets the user client details
     */
    private void getClientUserTableDetailsForBookingRejection() {
        SessionManagement sessionManagement = new SessionManagement(getContext());
        final int tutorId = sessionManagement.getTutorSession();
        Call<User> call = userController.getUserDetailsByTutorID(tutorId);

        call.enqueue(new Callback<User>(){
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                if(!response.isSuccessful()){
                    return;
                }

                User tutorDetails = response.body();
                //set up notification system
                Event bookingEvent = new Event();
                bookingEvent.setEventType("BookingRejection");
                // C# code: bookingEvent.Description = "Client just requested a Tutorial session_" + moduleList.SelectedValue + "_" + bookingRequestObj.Id + "_" + Session["ID"].ToString();
                SessionManagement sessionManagement = new SessionManagement(getContext());
                final int userID = sessionManagement.getSession();

                bookingEvent.setDescription("The Tutor: "+tutorDetails.getFullNames() + " " + tutorDetails.getSurname() + " has rejected the request for "+ moduleName[0] + "_" +  moduleName[0]+ "_" + bookingRequests[0].getId() + "_" + bookingRequests[0].getClient_Reference());
                setRejectionEvent(bookingEvent);

            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                //textViewResult.setText(t.getMessage());
            }
        });
    }

    private void setRejectionEvent(Event bookingEvent) {
        Call<Integer> call = eventController.pushEvent(bookingEvent);

        call.enqueue(new Callback<Integer>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {

                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Something went wrong with retrieving Tutor ID", Toast.LENGTH_SHORT).show();
                    return;
                }
                //get the tutor module ID
                int eventTableId = response.body();
                if(eventTableId == 0)
                    Toast.makeText(getContext(), "Event table ID couldn't be loaded", Toast.LENGTH_SHORT).show();
                else
                {
                    //set notification
                    eventID[0] = eventTableId;
                    @SuppressLint("SimpleDateFormat")
                    DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a");
                    @SuppressLint("SimpleDateFormat")
                    DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                    Calendar calendar = Calendar.getInstance();

                    Notification eventNotification = new Notification();
                    eventNotification.setDatePosted(dateFormat.format(calendar.getTime()));
                    eventNotification.setTime(timeFormat.format(calendar.getTime()));
                    eventNotification.setSeen(0);
                    if(App_Global_Variables.USER_CURRENT_STATUS.equals(App_Global_Variables.CLIENT_STATUS))
                        eventNotification.setPersonTheNotificationConcerns(App_Global_Variables.TUTOR_STATUS);
                    else
                        eventNotification.setPersonTheNotificationConcerns(App_Global_Variables.CLIENT_STATUS);
                    //get User ID
                    eventNotification.setUser_Table_Reference(clientUserId[0]);
                    eventNotification.setEvent_Table_Reference(eventTableId);
                    setBookingNotification(eventNotification);
                }
            }
            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(getContext(), "Could not load Tutor ID", Toast.LENGTH_SHORT).show();
            }
        });
    }



    /**
     *
     */
    private void makeNotificationUnseen(int notificationID ) {
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
                        }break;
                        case  0:
                        {
                            // TODO Create an alert
                        }break;
                    }
                }
            }
            @Override
            public void onFailure(@NotNull Call<Integer> call, @NotNull Throwable t) {
                // todo: handle
            }
        });
    }

    /**
     * Notification functionality for the case where the booking is rejected
     */
    private void sendNotificationFinalizationNotification()
    {
        //set up notification system
        Event bookingEvent = new Event();
        bookingEvent.setEventType("BookingFinalization");
        // C# code: bookingEvent.Description = "Client just requested a Tutorial session_" + moduleList.SelectedValue + "_" + bookingRequestObj.Id + "_" + Session["ID"].ToString();
        SessionManagement sessionManagement = new SessionManagement(Objects.requireNonNull(this.getContext()));
        final int userID = sessionManagement.getSession();
        //bookingEvent.Description = "Booking has been finalized with tutor_" + Request.QueryString["ModuleName"] + "" + bookingRequestJsonObj + "" + Request.QueryString["BookingRequestID"] + "_" + HttpContext.Current.Session["ID"].ToString();

        bookingEvent.setDescription("Booking has been finalized with tutor_" +  moduleName[0]+ "_" + bookingRequests[0].getRequestDate() + "_" + bookingRequests[0].getId() + "_" + userID);
        setEventForBookingRequestAcceptance(bookingEvent);
    }



    /**
     *
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void sendNotificationNegotiateBookingNotification(final ClientBooking cBooking)
    {
        //set up notification system
        Event bookingEvent = new Event();
        bookingEvent.setEventType("BookingRenegotiation");
        // C# code: bookingEvent.Description = "Client just requested a Tutorial session_" + moduleList.SelectedValue + "_" + bookingRequestObj.Id + "_" + Session["ID"].ToString();
        SessionManagement sessionManagement = new SessionManagement(this.getContext());
        final int userID = sessionManagement.getSession();
        //bookingEvent.Description = "Booking has been finalized with tutor_" + Request.QueryString["ModuleName"] + "" + bookingRequestJsonObj + "" + Request.QueryString["BookingRequestID"] + "_" + HttpContext.Current.Session["ID"].ToString();
        String description = "";

        //Get date a  time
        String proposedDate = "";
        String endTime = "";
        String date = txtDate.getEditText().getText().toString();
        String time = txtTime.getEditText().getText().toString();
        try {
            int intPeriods =1;

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss" );
            Date cDate = simpleDateFormat.parse(txtDate.getEditText().getText().toString() +" "+ txtTime.getEditText().getText().toString());
            Calendar calendar = Calendar.getInstance();
            proposedDate = "" + simpleDateFormat.format(calendar.getTime());
            calendar.setTime(cDate);
            calendar.add(Calendar.HOUR_OF_DAY,intPeriods);

            SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm:ss");
            endTime = "" + simpleTimeFormat.format(calendar.getTime());


        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(proposedDate.equals(""))
        {
            Toast.makeText(getContext(), "Could not calculate end time", Toast.LENGTH_SHORT).show();
            return;
        }

        bookingEvent.setDescription("The Tutor: " + tutorUserDetails[0].getFullNames() + " " +
                                                                      tutorUserDetails[0].getSurname() + " you requested for " +
                                                                      moduleName[0] + " is asking for a date change _" +
                                                                      userID + "_" +
                                                                      proposedDate + "_" +
                                                                      bookingRequests[0].getId() + "_" +
                                                                      moduleName[0] + "_" +
                                                                      endTime+ "_" +
                                                                      txtReason.getEditText().getText());

        //clientUserDetails[0].get
        setEventForBookingRequestRenegotiation(bookingEvent);
    }

    /**
     *
     * @param tutorID
     */
    public void getTutorUserDetails(final int tutorID){
        Call<User> call = tutorController.GetUserTableDetails(tutorID);

        call.enqueue(new Callback<User>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Something went wrong with retrieving Notification ID", Toast.LENGTH_SHORT).show();
                    return;
                }
                //get the tutor module ID
                User tutorDetails = response.body();
                if(tutorDetails != null)
                    tutorUserDetails[0] = tutorDetails;

            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getContext(), "Could not send notification too the tutor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     *
     * @param notification
     */
    private void setBookingNotification(Notification notification) {

        Call<Integer> call = notificationController.pushnotification(notification);

        call.enqueue(new Callback<Integer>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {

                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Something went wrong with retrieving Notification ID", Toast.LENGTH_SHORT).show();
                    return;
                }
                //get the tutor module ID
                int notificationId = response.body();
                if(notificationId == 0)
                {
                    Toast.makeText(getContext(), "Could not send notification too the tutor from withing the server", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    Toast.makeText(getContext(), "Notification sent to the Tutor", Toast.LENGTH_SHORT).show();

                    BookedSessionFragment bookedSessionFragment = new BookedSessionFragment();
                    //adding the multipurpose fragment on top of notifications for backStack
                    //getFragmentManager().beginTransaction().addToBackStack(null);
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, bookedSessionFragment).commit();
                }
            }
            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(getContext(), "Could not send notification too the tutor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     *
     */
    private void setTutorUserTableID(){

        Call<String> call = tutorController.GetUserTableIdByPK(bookingRequests[0].getTutor_Reference());

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Something went wrong with retrieving Tutor ID", Toast.LENGTH_SHORT).show();
                    return;
                }
                //get the tutor module ID
                String tutorsID = response.body();
                tutorUserId[0]= Integer.parseInt(tutorsID);

            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getContext(), "Could not load Tutor ID", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setClientUserTableID(){
        Call<String> call = clientController.getClientTableID(bookingRequests[0].getClient_Reference());
        call.enqueue(new Callback<String>(){
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if(!response.isSuccessful()){
                    return;
                }

                String Id = response.body();
                clientUserId[0]= Integer.parseInt(Id);

            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                //textViewResult.setText(t.getMessage());
            }
        });
    }
    /**
     *
     * @param textInputLayout
     * @return
     */
    private boolean validateInput(TextInputLayout textInputLayout) {
        String text = textInputLayout.getEditText().getText().toString().trim();
        if (text.isEmpty()) {
            textInputLayout.setError("Field can't be empty");
            return false;
        } else {
            textInputLayout.setError(null);
            return true;
        }
    }

    private void setEventForBookingRequestAcceptance(Event bookingEvent) {
        Call<Integer> call = eventController.pushEvent(bookingEvent);
        call.enqueue(new Callback<Integer>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {

                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Something went wrong with retrieving Tutor ID", Toast.LENGTH_SHORT).show();
                    return;
                }
                //get the tutor module ID
                int eventTableId = response.body();
                if(eventTableId == 0)
                    Toast.makeText(getContext(), "Event table ID couldn't be loaded", Toast.LENGTH_SHORT).show();
                else
                {
                    //set notification
                    eventID[0] = eventTableId;
                    @SuppressLint("SimpleDateFormat")
                    DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a");
                    @SuppressLint("SimpleDateFormat")
                    DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                    Calendar calendar = Calendar.getInstance();

                    Notification eventNotification = new Notification();
                    eventNotification.setDatePosted(dateFormat.format(calendar.getTime()));
                    eventNotification.setTime(timeFormat.format(calendar.getTime()));
                    eventNotification.setSeen(0);
                    if(App_Global_Variables.USER_CURRENT_STATUS.equals(App_Global_Variables.CLIENT_STATUS))
                        eventNotification.setPersonTheNotificationConcerns(App_Global_Variables.TUTOR_STATUS);
                    else
                        eventNotification.setPersonTheNotificationConcerns(App_Global_Variables.CLIENT_STATUS);
                    //get User ID
                    eventNotification.setUser_Table_Reference(clientUserId[0]);
                    eventNotification.setEvent_Table_Reference(eventID[0]);
                    setBookingNotification(eventNotification);
                }
            }
            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(getContext(), "Could not load Tutor ID", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     *
     * @param bookingEvent
     */
    private void setEventForBookingRequestRenegotiation(Event bookingEvent) {

        Call<Integer> call = eventController.pushEvent(bookingEvent);

        call.enqueue(new Callback<Integer>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {

                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Something went wrong with retrieving Tutor ID", Toast.LENGTH_SHORT).show();
                    return;
                }
                //get the tutor module ID
                int eventTableId = response.body();
                if(eventTableId == 0)
                    Toast.makeText(getContext(), "Event table ID couldn't be loaded", Toast.LENGTH_SHORT).show();
                else
                {
                    //set notification
                    eventID[0] = eventTableId;
                    @SuppressLint("SimpleDateFormat")
                    DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a");
                    @SuppressLint("SimpleDateFormat")
                    DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                    Calendar calendar = Calendar.getInstance();

                    Notification eventNotification = new Notification();
                    eventNotification.setDatePosted(dateFormat.format(calendar.getTime()));
                    eventNotification.setTime(timeFormat.format(calendar.getTime()));
                    eventNotification.setSeen(0);
                    eventNotification.setPersonTheNotificationConcerns(App_Global_Variables.CLIENT_STATUS);
                    //get User ID
                    eventNotification.setUser_Table_Reference(clientUserId[0]);
                    eventNotification.setEvent_Table_Reference(eventID[0]);
                    setBookingNotification(eventNotification);
                }
            }
            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(getContext(), "Could not load Tutor ID", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     *
     */
    @SuppressLint("SimpleDateFormat")
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showDateDialog()
    {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
                txtDate.getEditText().setText(simpleDateFormat.format(calendar.getTime()));
            }
        };
        new DatePickerDialog(Objects.requireNonNull(getContext()),R.style.pickerStyles,dateSetListener,calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();

    }


    @SuppressLint("SimpleDateFormat")
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showTimeDialog()
    {
        final Calendar calendar = Calendar.getInstance();

        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                txtTime.getEditText().setText(simpleDateFormat.format(calendar.getTime()));

            }
        };
        new TimePickerDialog(getContext(),R.style.pickerStyles,onTimeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),true).show();

    }

}

