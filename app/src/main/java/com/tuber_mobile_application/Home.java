package com.tuber_mobile_application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.tuber_mobile_application.helper.*;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tuber_mobile_application.Controllers.*;
import com.tuber_mobile_application.Models.*;
import com.tuber_mobile_application.helper.ui.NotificationItem;
import com.tuber_mobile_application.helper.ui.NotificationManagement;

import org.jetbrains.annotations.NotNull;

import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, RequestFragment.RequestFragmentListener {

    private DrawerLayout drawer;
    private UserController userController;
    private ClientController clientController;
    private TutorController tutorController;
    private View headerView;
    private MenuItem switchAccount;
    private MenuItem becomeOtherAccount;
    private MenuItem requestTutor;
    private MenuItem payments;
    private MenuItem scanQr;
    private MenuItem generateQr;
    private Menu menu;
    private NotificationManagement notificationManagement;
    private int userID;
    private NotificationController notificationController;
    private EventController eventController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);

        menu = navigationView.getMenu();
        switchAccount = menu.findItem(R.id.menu_switch);
        becomeOtherAccount = menu.findItem(R.id.menu_become_tutor);
        requestTutor = menu.findItem(R.id.menu_request);
        scanQr = menu.findItem(R.id.menu_QRScan);
        generateQr = menu.findItem(R.id.menu_QR);
        payments = menu.findItem(R.id.menu_payments);
        payments.setVisible(false);

        SessionManagement sess = new SessionManagement(Home.this);
        /*
            if(App_Global_Variables.USER_CURRENT_STATUS.equals(App_Global_Variables.TUTOR_STATUS))
            {
                generateQr.setVisible(true);
            }
            if(App_Global_Variables.USER_CURRENT_STATUS.equals(App_Global_Variables.CLIENT_STATUS))
            {

        } */
        generateQr.setVisible(false);
        //scanQr.setVisible(false);


        headerView = navigationView.getHeaderView(0);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);


        drawer.addDrawerListener(toggle);
        toggle.syncState();

        initialize();
        SessionManagement sessionManagement = new SessionManagement(Home.this);
        int userID = sessionManagement.getSession();

        //check to check if the state is changed, like phone rotation etc: the fragment must no change
        // default home fragment
        if (savedInstanceState == null)
        {
            navigationView.setCheckedItem(R.id.menu_dashboard);
            Call<String> call = userController.getCurrentUserStatus(userID);
            call.enqueue(new Callback<String>(){
                @Override
                public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                    if(!response.isSuccessful()){
                        return;
                    }
                    {
                        App_Global_Variables.USER_CURRENT_STATUS = response.body();

                        if(App_Global_Variables.USER_CURRENT_STATUS.equals(App_Global_Variables.TUTOR_STATUS))
                        {
                            scanQr.setVisible(true);

                            // Tutor Dashboard
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    new TutorDashboardFragment()).commit();
                        }
                        if(App_Global_Variables.USER_CURRENT_STATUS.equals(App_Global_Variables.CLIENT_STATUS))
                        {
                            payments.setVisible(true);

                            // Client Dashboard
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    new DashboardFragment()).commit();
                        }
                    }
                }
                @Override
                public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                    Toast.makeText(getApplicationContext(),"There was an error with setting up the user role",Toast.LENGTH_SHORT).show();
                }
            });


        }


        setUserStatus(userID);
        //checkIfUserHasSecondaryAccountForFirstTime();

        Intent intent = getIntent();
        if (intent.hasExtra("Parcel"))
        {
            notificationNavigation(intent);
        }
        else if(intent.hasExtra("toRatingFromNotification"))
        {
            ratingNavigation(intent, "toRatingFromNotification");
        }

    }



    public void initialize() {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        AtomicReference<Retrofit> retrofit = new AtomicReference<>(new Retrofit.Builder()
                .baseUrl(App_Global_Variables.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(ScalarsConverterFactory.create())
                .build());

        userController = retrofit.get().create(UserController.class);
        clientController = retrofit.get().create(ClientController.class);
        tutorController = retrofit.get().create(TutorController.class);
        notificationController = retrofit.get().create(NotificationController.class);
        eventController = retrofit.get().create(EventController.class);
        setUserDetails();
    }

    /**
     * Checks if whether the user is truly logged in
     */
    @Override
    protected void onStart() {
        super.onStart();
        //check if user is logged in

        SessionManagement sessionManagement = new SessionManagement(Home.this);
        int userID = sessionManagement.getSession();

        if (userID == -1) {
            //if user is logged in move to Home
            moveToLogin();
        } else {
            //user is not logged in
        }
    }

    public void setUserDetails() {
        //Get user details using the user ID
        SessionManagement sessionManagement = new SessionManagement(Home.this);
        final int userID = sessionManagement.getSession();
        this.userID = userID;
        Call<User> call = userController.getUserDetails(userID);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NotNull Call<User> call, @NotNull Response<User> response) {


                if (!response.isSuccessful()) {
                    Toast.makeText(Home.this, "Could receive user details", Toast.LENGTH_SHORT).show();
                    return;
                }

                User user = response.body();
                TextView txtName = (TextView) headerView.findViewById(R.id.home_name);
                TextView txtEmail = (TextView) headerView.findViewById(R.id.home_email);

                txtName.setText(user.getFullNames() + " " + user.getSurname());
                txtEmail.setText(user.getEmail_Address());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                //textViewResult.setText(t.getMessage());
            }
        });
    }

    private void moveToLogin() {
        Intent intent = new Intent(Home.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen((GravityCompat.START))) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        notificationManagement = new NotificationManagement(this,this.getApplicationContext());
        notificationManagement.checkForNotifications(userID);
    }

    @Override
    protected void onPause() {
        super.onPause();
        notificationManagement = new NotificationManagement(this,this.getApplicationContext());
        notificationManagement.checkForNotifications(userID);
    }

    /**
     * This is for handling the selected menus
     * */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_request:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new RequestFragment()).commit();
                break;
            case R.id.menu_sessions:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new SessionFragment()).commit();
                break;

            case R.id.menu_dashboard:
            {
                if(App_Global_Variables.USER_CURRENT_STATUS.equals(App_Global_Variables.TUTOR_STATUS))
                {
                    // Tutor Dashboard
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new TutorDashboardFragment()).commit();
                }
                if(App_Global_Variables.USER_CURRENT_STATUS.equals(App_Global_Variables.CLIENT_STATUS))
                {
                    // Client Dashboard
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new DashboardFragment()).commit();
                }
            }
            break;

            case R.id.menu_modules:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ModuleFragment()).commit();
                break;
            case R.id.menu_notifications:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new NotificationsFragment()).commit();
                break;
            case R.id.menu_payments:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new PaymentsFragment()).commit();
                break;
            case R.id.menu_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
                break;

            /* TODO: Not Delete
                case R.id.menu_QR:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new QRCodeGenFragment()).commit();
                break;
                case R.id.menu_QRScan:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new QRScannerFragment()).commit();
                    break;
            */

            case R.id.menu_switch:
                {
                    switchAccount();
                }
                break;
            case R.id.menu_become_tutor:
            {
                //ToDo:
                // (become a tutor)
                //  menu item acts as an activator to registering as either a tutor or a client

            }
            break;

            case R.id.menu_signout:
                //Logging Out
                Toast.makeText(this, "Signing you out...", Toast.LENGTH_SHORT).show();
                SessionManagement sessionManagement = new SessionManagement((Home.this));
                sessionManagement.removeSession();
                moveToLogin();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     *
     */
    private void switchAccount() {
        SessionManagement sessionManagement = new SessionManagement(getApplicationContext());
        final int userID = sessionManagement.getSession();
        Call<String> call = null;
        if(App_Global_Variables.USER_CURRENT_STATUS.equals(App_Global_Variables.CLIENT_STATUS))
            call = userController.changeCurrentUserStatus(userID,App_Global_Variables.TUTOR_STATUS);
        else
            call = userController.changeCurrentUserStatus(userID,App_Global_Variables.CLIENT_STATUS);


        call.enqueue(new Callback<String>(){
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if(!response.isSuccessful()){
                    return;
                }
                String Id = response.body();
                if(App_Global_Variables.USER_CURRENT_STATUS.equals(App_Global_Variables.CLIENT_STATUS))
                {
                    App_Global_Variables.USER_CURRENT_STATUS = App_Global_Variables.TUTOR_STATUS;
                    requestTutor.setVisible(false);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SessionFragment()).commit();

                }
                else
                {
                    App_Global_Variables.USER_CURRENT_STATUS = App_Global_Variables.CLIENT_STATUS;
                    requestTutor.setVisible(true);

                    //Move to the booking request page, i.e. home page
                    Intent intent = new Intent(getApplicationContext(), Home.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(Home.this, "Request failure: switchAccount()", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void goToMap() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new MapsFragment()).commit();
    }

    public void ratingNavigation(final Intent intent, final String tag)
    {
        // parcel = notificationID/sessionID/clientID/tutorID
        final String parcel = intent.getStringExtra(tag);

        StringTokenizer parcelTokens = new StringTokenizer(parcel,"/"); // the values are separated by /

        int notificationID = Integer.parseInt(parcelTokens.nextToken());

        Call<Integer> callChangeStatus = notificationController.changeNotificationStatus(notificationID);
        callChangeStatus.enqueue(new Callback<Integer>()
        {
            @Override
            public void onResponse(@NotNull Call<Integer> call, @NotNull Response<Integer> response) {
                if (!response.isSuccessful()) {
                    ratingNavigation(intent, tag);
                }
                else {

                    RatingFragment destination = new RatingFragment();

                    Bundle bundle = new Bundle();
                    bundle.putString(tag, parcel);
                    destination.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, destination).commit();
                }
            }

            @Override
            public void onFailure(@NotNull Call<Integer> call, @NotNull Throwable t) {

            }
        });

    }

    public void notificationNavigation(Intent intent)
    {
        // From_Maker/nId/eId/eType/eDescription
        final String parcel = intent.getStringExtra("Parcel");
        StringTokenizer parcelTokens = new StringTokenizer(parcel,"~"); // the values are separated by /

        parcelTokens.nextToken(); // moving the iterator cursor, to skip 'From_Maker'
        int notificationID = Integer.parseInt(parcelTokens.nextToken());

        //TODO: Call<Integer> callChangeStatus = notificationController.changeNotificationStatus(notificationID);
        Call<Notification> notificationCall = notificationController.getNotification(notificationID);
        notificationCall.enqueue(new Callback<Notification>() {
            @Override
            public void onResponse(Call<Notification> call, final Response<Notification> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "No Successful Response", Toast.LENGTH_SHORT).show();
                    return;
                }
                final Notification notification = response.body();

                if (notification != null) {
                    Call<Event> eventCall = eventController.GetEvent(notification.getEvent_Table_Reference());
                    eventCall.enqueue(new Callback<Event>() {
                        @Override
                        public void onResponse(@NotNull Call<Event> call, @NotNull Response<Event> eventResponse) {
                            if (!eventResponse.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "No Successful Response", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Event event_ = eventResponse.body();
                            String[] details = HelperMethods.separateString(parcel,"~");
                            switch (event_.getEventType()) {
                                case "BookingRequest": { //Redirect to multipurpose
                                    MultipurposeFragment destination = new MultipurposeFragment();

                                    Bundle bundle = new Bundle();
                                    bundle.putString("Parcel", parcel);
                                    bundle.putString("NotificationType","BookingRequest");
                                    bundle.putString("moduleName",details[6]);
                                    destination.setArguments(bundle);
                                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, destination).commit();
                                }
                                break;
                                case "BookingFinalization":
                                case "BookingRejection":
                                case "BookingCancelation": {
                                    makeNotificationSeen(notification.getId());
                                }
                                break;
                                case "Session": {

                                }break;
                                case "BookingRenegotiation": {

                                    NegotiationFragment destination = new NegotiationFragment();

                                    Bundle bundle = new Bundle();

                                    bundle.putString("moduleName",details[4]);
                                    bundle.putString("otherUserId",details[6]);
                                    //Set the bundle arguments
                                    bundle.putString("Parcel", parcel);
                                    bundle.putString("NotificationType","BookingRenegotiation");
                                    destination.setArguments(bundle);
                                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, destination).commit();
                                }
                                break;
                                case "InterviewMeeting": {

                                }
                                break;
                                case "SessionRating":{
// extract the clicked notification information using pos.

                                    StringTokenizer tokens = new StringTokenizer(event_.getDescription(),"_");
                                    tokens.nextToken(); // moving cursor

                                    // parcel = notificationID/sessionID/clientID/tutorID
                                    Bundle bundle = new Bundle();
                                    String parcel = notification.getId() + "/" + tokens.nextToken();
                                    bundle.putString("toRatingFromNotification",parcel);
                                    RatingFragment ratingFragment = new RatingFragment();

                                    ratingFragment.setArguments(bundle); // setting arguments for next fragment

                                    FragmentManager manager = getFragmentManager();
                                    if(manager != null)
                                    {
                                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ratingFragment).commit();
                                    }
                                }break;

                            }
                        }

                        @Override
                        public void onFailure(Call<Event> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "Could not fetch events", Toast.LENGTH_SHORT).show();
                        }
                    });
                        }
            }
            @Override
            public void onFailure(Call<Notification> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Could not load notifications", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     *
     * @param userID
     */
    private void setUserStatus(int userID) {

        Call<String> call = userController.getCurrentUserStatus(userID);
        call.enqueue(new Callback<String>(){
            @Override
            public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                if(!response.isSuccessful()){
                    return;
                }
                Object responseObj = response.body();
                if(responseObj instanceof String)
                    App_Global_Variables.USER_CURRENT_STATUS = (String) responseObj;
                checkIfUserHasSecondaryAccount();
                /**if(App_Global_Variables.USER_CURRENT_STATUS.equals(App_Global_Variables.TUTOR_STATUS))
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new SessionFragment()).commit();**/


            }
            @Override
            public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                Toast.makeText(getApplicationContext(),"There was an error with setting up the user role",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void checkIfUserHasSecondaryAccount(){
        SessionManagement sessionManagement = new SessionManagement(getApplicationContext());
        final int userID = sessionManagement.getSession();
        Call<String> call = null;
        if(App_Global_Variables.USER_CURRENT_STATUS.equals(App_Global_Variables.CLIENT_STATUS))
            call = tutorController.doesTutorExists(userID); //check if user is also a tutor
        else
            call = clientController.doesClientExists(userID); //check if user is also a client

        call.enqueue(new Callback<String>(){
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if(!response.isSuccessful()){
                    return;
                }

                String Id = response.body();
                if(Id.equals("1"))
                {
                    switchAccount.setVisible(true);
                    becomeOtherAccount.setVisible(false);

                    if(App_Global_Variables.USER_CURRENT_STATUS.equals(App_Global_Variables.CLIENT_STATUS))
                        requestTutor.setVisible(true);
                    else
                        requestTutor.setVisible(false);
                }
                else
                {
                    switchAccount.setVisible(false);
                    becomeOtherAccount.setVisible(false);
                    //check the current account status
                    if(App_Global_Variables.USER_CURRENT_STATUS.equals(App_Global_Variables.CLIENT_STATUS))
                        //Become a tutor
                    {
                        becomeOtherAccount.setTitle("Become a Tutor");
                        requestTutor.setVisible(true);
                    }
                    else
                    {
                        becomeOtherAccount.setTitle("Become a Student");
                        requestTutor.setVisible(false);
                    }
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(Home.this, "Request failure: checkIfUserHasSecondaryAccount()", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     *
     */
    private void checkIfUserHasSecondaryAccountForFirstTime() {
        SessionManagement sessionManagement = new SessionManagement(getApplicationContext());
        final int userID = sessionManagement.getSession();
        Call<String> call = null;

        call = tutorController.doesTutorExists(userID); //check if user is also a tutor

        call.enqueue(new Callback<String>(){
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if(!response.isSuccessful()){
                    return;
                }

                String Id = response.body();
                if(Id.equals("1"))
                {

                    Call<String> call2 = clientController.doesClientExists(userID); //check if user is also a client

                    call2.enqueue(new Callback<String>(){
                        @Override
                        public void onResponse(Call<String> call2, Response<String> response) {

                            if(!response.isSuccessful()){
                                return;
                            }

                            String Id = response.body();
                            if(Id.equals("1"))
                            {
                                switchAccount.setVisible(true);
                                becomeOtherAccount.setVisible(false);

                                if(App_Global_Variables.USER_CURRENT_STATUS.equals(App_Global_Variables.CLIENT_STATUS))
                                    requestTutor.setVisible(true);
                                else
                                    requestTutor.setVisible(false);
                            }
                            else
                            {
                                switchAccount.setVisible(false);
                                becomeOtherAccount.setVisible(false);
                                App_Global_Variables.USER_CURRENT_STATUS = "Tutor";
                                //check the current account status
                                if(App_Global_Variables.USER_CURRENT_STATUS.equals(App_Global_Variables.CLIENT_STATUS))
                                //Become a tutor
                                {
                                    becomeOtherAccount.setTitle("Become a Tutor");
                                    requestTutor.setVisible(true);
                                }
                                else
                                {
                                    becomeOtherAccount.setTitle("Become a Student");
                                    requestTutor.setVisible(false);
                                }
                            }
                        }
                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(Home.this, "Request failure: checkIfUserHasSecondaryAccount()", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                else
                {
                    switchAccount.setVisible(false);
                    becomeOtherAccount.setVisible(false);
                    //check the current account status
                    if(App_Global_Variables.USER_CURRENT_STATUS.equals(App_Global_Variables.CLIENT_STATUS))
                    //Become a tutor
                    {
                        becomeOtherAccount.setTitle("Become a Tutor");
                        requestTutor.setVisible(true);
                    }
                    else
                    {
                        becomeOtherAccount.setTitle("Become a Student");
                        requestTutor.setVisible(false);
                    }
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(Home.this, "Request failure: checkIfUserHasSecondaryAccount()", Toast.LENGTH_SHORT).show();
            }
        });

    }
    /**
     *
     * @param notificationID
     */
    private void makeNotificationSeen(int notificationID ) {
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

}
