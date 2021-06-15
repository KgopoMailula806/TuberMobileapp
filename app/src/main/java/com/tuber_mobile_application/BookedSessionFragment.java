package com.tuber_mobile_application;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tuber_mobile_application.Controllers.BookingController;
import com.tuber_mobile_application.Controllers.ClientBookingController;
import com.tuber_mobile_application.Controllers.ClientController;
import com.tuber_mobile_application.Controllers.EventController;
import com.tuber_mobile_application.Controllers.ModuleController;
import com.tuber_mobile_application.Controllers.NotificationController;
import com.tuber_mobile_application.Controllers.SessionController;
import com.tuber_mobile_application.Controllers.TutorController;
import com.tuber_mobile_application.Controllers.UserController;
import com.tuber_mobile_application.Models.BookingRequest;
import com.tuber_mobile_application.Models.ClientBooking;
import com.tuber_mobile_application.Models.Event;
import com.tuber_mobile_application.Models.Module;
import com.tuber_mobile_application.Models.Notification;
import com.tuber_mobile_application.Models.User;
import com.tuber_mobile_application.helper.App_Global_Variables;
import com.tuber_mobile_application.helper.HelperMethods;
import com.tuber_mobile_application.helper.SessionManagement;
import com.tuber_mobile_application.helper.ui.SessionAdapter;
import com.tuber_mobile_application.helper.ui.SessionItem;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
public class BookedSessionFragment extends Fragment
{
    //Controllers
    private SessionController sessionController;
    private BookingController bookingController;
    private ClientController clientController;
    private TutorController tutorController;
    private ModuleController moduleController;
    private ClientBookingController clientBookingController;
    private UserController userController;
    private EventController eventController;
    private NotificationController notificationController;
    private RecyclerView recyclerView;
    private SessionAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<SessionItem> sessionItems;
    private ProgressBar progressBar;
    public final String[] StartTime = new String[1];
    public final String[] EndTime = new String[1];
    public final String[] periods = new String[1];
    private int[] otherUserId = new int[1];

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booked_sessions,container,false);
        Objects.requireNonNull(getActivity()).setTitle("Booked Sessions");
        sessionItems = new ArrayList<>();
        //setUp retrofit
        initialize();
        setSessions();
        progressBar = view.findViewById(R.id.progressBar_sessions);
        recyclerView = view.findViewById(R.id.booked_session_RecyclerView);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.VISIBLE);

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

        sessionController = retrofit.get().create(SessionController.class);
        bookingController = retrofit.get().create(BookingController.class);
        clientController = retrofit.get().create(ClientController.class);
        tutorController = retrofit.get().create(TutorController.class);
        moduleController = retrofit.get().create(ModuleController.class);
        clientBookingController = retrofit.get().create(ClientBookingController.class);
        userController = retrofit.get().create(UserController.class);
        eventController = retrofit.get().create(EventController.class);
        notificationController = retrofit.get().create(NotificationController.class);

    }
    /**
     *  get Sessions method by means of the getAllActiveClientBookings in session controller
     */
    public void setSessions() {

        SessionManagement sessionManagement = new SessionManagement(Objects.requireNonNull(getContext()));
        final int tutorID = sessionManagement.getTutorSession();
        final int clientID = sessionManagement.getClientSession();

        Call<List<ClientBooking>> call;
        if(App_Global_Variables.USER_CURRENT_STATUS.equals(App_Global_Variables.CLIENT_STATUS))
            call = sessionController.getAllActiveClientBookings(clientID);
        else
            call = sessionController.getAllActiveTutorBookings(tutorID);
        call.enqueue(new Callback<List<ClientBooking>>(){

            @Override
            public void onResponse(@NotNull Call<List<ClientBooking>> call, @NotNull Response<List<ClientBooking>> response) {
                if(!response.isSuccessful()){

                    return;
                }
                List<ClientBooking> sessions = response.body();

                if(sessions != null)
                {
                    progressBar.setVisibility(View.INVISIBLE);
                    if(sessions.size() > 0){
                        for(ClientBooking session : sessions){

                            //Incrementally add the session
                            SessionItem bookedSessionItem = new SessionItem();
                            bookedSessionItem.setBookingId(session.getId());
                            bookedSessionItem.setBookingRequestID(session.getBookingDetails_BookingRequestTable_Reference());
                            bookedSessionItem.setDate(session.getDate_Time());

                            //Set the other users reference
                            if(App_Global_Variables.USER_CURRENT_STATUS.equals(App_Global_Variables.CLIENT_STATUS))
                            {
                                bookedSessionItem.setOtherUserID(session.getTutor_Table_Reference());
                                setTutorUserDetails(bookedSessionItem);
                                getOtherUsersID(bookedSessionItem);
                            }
                            else
                            {
                                bookedSessionItem.setOtherUserID(session.getClient_Table_Reference());
                                setClientUserDetails(bookedSessionItem);

                                getOtherUsersID(bookedSessionItem);
                            }
                            setUpOtherBookingRequestDetails(bookedSessionItem);
                        }
                    }else{

                    }
                }else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getContext(), "You have no booking requests", Toast.LENGTH_SHORT).show();
                }


            }
            @Override
            public void onFailure(@NotNull Call<List<ClientBooking>> call, @NotNull Throwable t) {
                Toast.makeText(getContext(), "Could not load booking request details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * gets the tutor details
     * @param bookedSessionItem
     * display item
     */
    private void setTutorUserDetails(final SessionItem bookedSessionItem) {
        Call<User> call = tutorController.GetUserTableDetails(bookedSessionItem.getOtherUserID());
        call.enqueue(new Callback<User>(){

            @Override
            public void onResponse(@NotNull Call<User> call, @NotNull Response<User> response) {
                if(!response.isSuccessful()){

                    return;
                }

                User userDetails = response.body();
                //get the session that ha the same booking request reference as the one in the parameter

                if(userDetails != null)
                {
                    if(App_Global_Variables.USER_CURRENT_STATUS.equals(App_Global_Variables.CLIENT_STATUS))
                        bookedSessionItem.setTutorName("Tutor Name: " + userDetails.getFullNames() + " " + userDetails.getSurname());
                    else
                        bookedSessionItem.setTutorName("Student Name: " + userDetails.getFullNames() + " " + userDetails.getSurname());
                }

            }
            @Override
            public void onFailure(@NotNull Call<User> call, @NotNull Throwable t) {
                Toast.makeText(getContext(), "Could not load tutor details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     *
     * @param bookedSessionItem
     * booked session display item
     */
    private void setClientUserDetails(final SessionItem bookedSessionItem) {

        Call<List<User>> call = clientController.GetUserTableDetails(bookedSessionItem.getOtherUserID());
        call.enqueue(new Callback<List<User>>(){

            @Override
            public void onResponse(@NotNull Call<List<User>> call, @NotNull Response<List<User>> response) {
                if(!response.isSuccessful()){

                    return;
                }
                List<User> userDetails = response.body();
                if(userDetails != null)
                {
                    //get the session that ha the same booking request reference as the one in the parameter
                    if(App_Global_Variables.USER_CURRENT_STATUS.equals(App_Global_Variables.CLIENT_STATUS))
                        bookedSessionItem.setTutorName("Tutor Name: " + userDetails.get(0).getFullNames() + " " + userDetails.get(0).getSurname());
                    else
                        bookedSessionItem.setTutorName("Student Name: " + userDetails.get(0).getFullNames() + " " + userDetails.get(0).getSurname());
                }

            }
            @Override
            public void onFailure(@NotNull Call<List<User>> call, @NotNull Throwable t) {
                Toast.makeText(getContext(), "Could not load client request details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     *
     * @param bookedSessionItem
     * display item
     */
    private void setUpOtherBookingRequestDetails(final SessionItem bookedSessionItem) {
        if(sessionItems != null){
            Call<BookingRequest> call = bookingController.GetIndividualBookingRequest(bookedSessionItem.getBookingRequestID());
            call.enqueue(new Callback<BookingRequest>(){

                @Override
                public void onResponse(@NotNull Call<BookingRequest> call, @NotNull Response<BookingRequest> response) {
                    if(!response.isSuccessful()){

                        return;
                    }
                    BookingRequest bookingRequest = response.body();
                    //get the session that ha the same booking request reference as the one in the paramenter

                    if(bookingRequest != null)
                    {
                        bookedSessionItem.setEndTime(bookingRequest.getEndTime());
                        bookedSessionItem.setVenue(HelperMethods.separateString(bookingRequest.getClientProposedLocation(),"_")[0]);
                        bookedSessionItem.setVenueAndCoordinates(bookingRequest.getClientProposedLocation());
                        bookedSessionItem.setPeriods("Period(s):  " + bookingRequest.getPeriods());
                        bookedSessionItem.setModuleID(bookingRequest.getModuleID());
                        StartTime[0] = bookingRequest.getRequestTime();
                        EndTime[0] =  bookingRequest.getEndTime();
                        periods[0] = ""+bookingRequest.getPeriods();
                        //set up the module name title
                        setModuleName(bookedSessionItem);

                    }
                }
                @Override
                public void onFailure(@NotNull Call<BookingRequest> call, @NotNull Throwable t) {
                    Toast.makeText(getContext(), "Could not load booking request details", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * Set the Module name of the user
     * @param bookedSessionItem
     * display item
     */
    private void setModuleName(final SessionItem bookedSessionItem) {
        if(sessionItems != null){
            Call<Module> call = moduleController.GetModules(bookedSessionItem.getModuleID());
            call.enqueue(new Callback<Module>(){

                @Override
                public void onResponse(@NotNull Call<Module> call, @NotNull Response<Module> response) {
                    if(!response.isSuccessful()){

                        return;
                    }
                    Module module = response.body();

                    if(module != null)
                        bookedSessionItem.setModuleName(module.getModule_Name() + " Tutorial");
                    sessionItems.add(bookedSessionItem);
                    recyclerView.setHasFixedSize(true);
                    layoutManager = new LinearLayoutManager(getContext());
                    adapter = new SessionAdapter(sessionItems, App_Global_Variables.USER_CURRENT_STATUS); // visibility control
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);

                    adapter.SetOnItemClickListener(new SessionAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int pos)
                        {
                        }
                        @Override
                        public void onPlayClick(final int pos)
                        {
                            final DialogInterface.OnClickListener startSessionListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which){
                                        case DialogInterface.BUTTON_POSITIVE:
                                            {
                                                if(App_Global_Variables.USER_CURRENT_STATUS.equals(App_Global_Variables.CLIENT_STATUS))
                                                {
                                                    SessionManagement sessionManagement = new SessionManagement(Objects.requireNonNull(getContext()));
                                                    int userID = sessionManagement.getSession();
                                                    int myClientID = sessionManagement.getClientSession();
                                                    QRCodeGenFragment qrCodeGenFragment = new QRCodeGenFragment();
                                                    Bundle genBundle = new Bundle();
                                                    //Booking ID , Student Id, Tutor ID
                                                    genBundle.putString("bookingId", "" +sessionItems.get(pos).getBookingId());
                                                    genBundle.putString("tutorID", "" + sessionItems.get(pos).getOtherUserID());
                                                    genBundle.putString("myClientID", "" + myClientID);
                                                    genBundle.putString("userID", "" + userID);
                                                    genBundle.putString("StartTime", "" + StartTime[0]);
                                                    genBundle.putString("EndTime", "" + EndTime[0]);
                                                    genBundle.putString("periods", "" + periods[0]);

                                                    qrCodeGenFragment.setArguments(genBundle);
                                                    FragmentManager fragmentManager = getFragmentManager();
                                                    if(fragmentManager != null)
                                                    {
                                                        // todo: alarm manager
                                                        getFragmentManager().beginTransaction().replace(R.id.session_fragment_container,qrCodeGenFragment).commit();
                                                    }
                                                }if(App_Global_Variables.USER_CURRENT_STATUS.equals(App_Global_Variables.TUTOR_STATUS))
                                                 {
                                                     SessionManagement sessionManagement = new SessionManagement(Objects.requireNonNull(getContext()));
                                                     int userID = sessionManagement.getSession();
                                                     QRScannerFragment  qrScannerFragment = new QRScannerFragment();
                                                     Bundle genBundle = new Bundle();
                                                     int myTutorID = sessionManagement.getTutorSession();
                                                     //Booking ID , Student Id, Tutor ID
                                                     genBundle.putString("bookingId", "" +sessionItems.get(pos).getBookingId());
                                                     genBundle.putString("clientID", "" + sessionItems.get(pos).getOtherUserID());
                                                     genBundle.putString("myTutorID", "" + myTutorID);
                                                     genBundle.putString("userID", "" + userID);
                                                     genBundle.putString("StartTime", "" + StartTime[0]);
                                                     genBundle.putString("EndTime", "" + EndTime[0]);
                                                     genBundle.putString("endTime", EndTime[0]); // session end time
                                                     qrScannerFragment.setArguments(genBundle);
                                                     FragmentManager fragmentManager = getFragmentManager();
                                                     if(fragmentManager != null)
                                                     {
                                                         getFragmentManager().beginTransaction().replace(R.id.session_fragment_container,qrScannerFragment).commit();
                                                     }
                                                 }
                                            }
                                            break;
                                        case DialogInterface.BUTTON_NEGATIVE:
                                            // do nothing for now
                                            break;
                                    }
                                }
                            };
                            AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
                            builder.setMessage("You about to start this tutorial session, are you currently with the tutor?") // alert box question
                                    .setPositiveButton("Yes", startSessionListener) // yes button
                                    .setNegativeButton("No", startSessionListener).show(); // no button
                        }
                        @Override
                        public void onCancelClick(final int pos)
                        {
                            final DialogInterface.OnClickListener endSessionListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which){
                                        case DialogInterface.BUTTON_POSITIVE:
                                        {
                                            cancelSession(pos);
                                        }
                                        break;
                                        case DialogInterface.BUTTON_NEGATIVE:
                                            // do nothing for now
                                            break;
                                    }
                                }
                            };
                            AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
                            builder.setMessage("You are about to cancel this tutorial session, are you sure?") // alert box question
                                    .setPositiveButton("Yes", endSessionListener) // yes button
                                    .setNegativeButton("No", endSessionListener).show(); // no button
                        }
                        @Override
                        public void onPathClick(final int pos)
                        {
                            final DialogInterface.OnClickListener pathListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i)
                                {
                                    if (i == DialogInterface.BUTTON_POSITIVE)
                                    {
                                        //todo: put code here
                                        // Toast.makeText(getContext(), "Show Path", Toast.LENGTH_SHORT).show();
                                        final SessionItem SessionItem  =  sessionItems.get(pos);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("ComingFromType","Location-bookings");
                                        //bundle.putString("Parcel",Genobj.getString("Parcel"));
                                        bundle.putString("UserLocation",SessionItem.getVenueAndCoordinates());
                                        //bundle.putString("moduleName",Genobj.getString("moduleName"));
                                        //bundle.putString("NotificationType",Genobj.getString("NotificationType") );
                                       // bundle.putString("otherUserId",Genobj.getString("otherUserId"));
                                        MapsFragment mapsFragment = new MapsFragment();
                                        mapsFragment.setArguments(bundle); // setting arguments for next fragment
                                        getFragmentManager().beginTransaction().replace(R.id.fragment_container, mapsFragment).commit();
                                    }
                                }
                            };

                            AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
                            builder.setMessage("Display the path from your location to the request location?") // alert box question
                                    .setPositiveButton("Yes", pathListener).show(); // no button
                        }
                    });
                    progressBar.setVisibility(View.INVISIBLE); // making progress bar invisible
                }
                @Override
                public void onFailure(@NotNull Call<Module> call, @NotNull Throwable t) {
                    Toast.makeText(getContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void cancelSession(int pos) {
        //String deactivationURL = SITEConstants.BASE_URL + "api/ClientBooking/deactivateBooking/" + Request.QueryString["BooKingID"].ToString() + "/" + HttpContext.Current.Session["UserStatus"].ToString();
        final SessionItem SessionItem  =  sessionItems.get(pos);

        Call<String> call = clientBookingController.deactivateBooking(SessionItem.getBookingId(),App_Global_Variables.USER_CURRENT_STATUS);
        call.enqueue(new Callback<String>(){

            @Override
            public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                if(!response.isSuccessful()){

                    return;
                }
                String bookingRequest = response.body();
                //get the session that ha the same booking request reference as the one in the paramenter

                if(bookingRequest != null)
                {
                    getClientUserTableDetailsForBookingCancelation(SessionItem);

                }
            }
            @Override
            public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                Toast.makeText(getContext(), "Could not load booking request details", Toast.LENGTH_SHORT).show();
            }
        });
    }
    /**
     *
     * @param sessionItem
     */
    private void getClientUserTableDetailsForBookingCancelation(final SessionItem sessionItem) {
        SessionManagement sessionManagement = new SessionManagement(getContext());
        int id = 0;
        if(App_Global_Variables.USER_CURRENT_STATUS.equals(App_Global_Variables.CLIENT_STATUS))
            id = sessionManagement.getClientSession();
        else
            id = sessionManagement.getTutorSession();
        final int Id_ = id;

        Call<User> call = userController.getUserDetailsByTutorID(Id_);
        call.enqueue(new Callback<User>(){
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                if(!response.isSuccessful()){
                    return;
                }

                User tutorDetails = response.body();
                //set up notification system
                Event bookingEvent = new Event();
                bookingEvent.setEventType("BookingCancelation");
                // C# code: bookingEvent.Description = "Client just requested a Tutorial session_" + moduleList.SelectedValue + "_" + bookingRequestObj.Id + "_" + Session["ID"].ToString();
                SessionManagement sessionManagement = new SessionManagement(getContext());
                final int userID = sessionManagement.getSession();
                String Description = "";
                if(App_Global_Variables.USER_CURRENT_STATUS.equals(App_Global_Variables.CLIENT_STATUS))
                    Description += "The Client: "+tutorDetails.getFullNames() + " " + tutorDetails.getSurname() + " has canceled the request for "+ sessionItem.getModuleName() + " Set For " + sessionItem.getDate() ;
                else
                    Description += "The Tutor: "+tutorDetails.getFullNames() + " " + tutorDetails.getSurname() + " has canceled the request for "+ sessionItem.getModuleName() + "For " + sessionItem.getDate() ;
                Description += "_" + sessionItem.getModuleName() + "_" + Id_ ;

                bookingEvent.setDescription(Description);
                setCancellationEvent(bookingEvent,sessionItem);

            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                //textViewResult.setText(t.getMessage());
            }
        });
    }

    /**
     *
     * @param bookingEvent
     * @param sessionItem
     */
    private void setCancellationEvent(Event bookingEvent, final SessionItem sessionItem) {
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
                    eventNotification.setUser_Table_Reference(sessionItem.getOtherUserUserTableID());
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
            if (notificationId == 0)
                Toast.makeText(getContext(), "Could not send notification too the tutor from withing the server", Toast.LENGTH_SHORT).show();
            else {
                Toast.makeText(getContext(), "Session Canceled, notification has been sent", Toast.LENGTH_SHORT).show();
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
     * @param sessionItem
     */
    private void getOtherUsersID(final SessionItem sessionItem ) {
        Call<String> call = null;
        if(App_Global_Variables.USER_CURRENT_STATUS.equals(App_Global_Variables.CLIENT_STATUS))
        {
            //SessionManagement sessionManagement = new SessionManagement(getContext());
            //final int clientPK = sessionManagement.getClientSession();
            call = tutorController.GetUserTableID(sessionItem.getOtherUserID());
        }
        else
            call = clientController.GetUserTableID(sessionItem.getOtherUserID());

        call.enqueue(new Callback<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Something went wrong with retrieving Notification ID", Toast.LENGTH_SHORT).show();
                    return;
                }
                //get the tutor module ID
                sessionItem.setOtherUserUserTableID(Integer.parseInt(response.body()));

            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getContext(), "Could not send notification too the tutor", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
