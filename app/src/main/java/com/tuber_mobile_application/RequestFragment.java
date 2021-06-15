package com.tuber_mobile_application;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.tuber_mobile_application.Controllers.BookingController;
import com.tuber_mobile_application.Controllers.EventController;
import com.tuber_mobile_application.Controllers.ModuleController;
import com.tuber_mobile_application.Controllers.NotificationController;
import com.tuber_mobile_application.Controllers.TutorController;
import com.tuber_mobile_application.Models.BookingRequest;
import com.tuber_mobile_application.Models.Event;
import com.tuber_mobile_application.Models.Module;
import com.tuber_mobile_application.Models.Notification;
import com.tuber_mobile_application.Models.User;
import com.tuber_mobile_application.helper.App_Global_Variables;
import com.tuber_mobile_application.helper.HelperMethods;
import com.tuber_mobile_application.helper.SessionManagement;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RequestFragment  extends Fragment {

    private RequestFragmentListener mListener;
    private Spinner moduleSpinner;
    private Spinner tutorSpinner;
    private EditText txtDate,txtTime;
    private EditText txtLocation;
    private EditText periods;
    private Button btnRequest;
    private Button btnGetRelevantTutors;

    private  String[] chosenModule = new String[1];
    private  String[] chosenTutor = new String[1];
    private  String[] chosenTutorID = new String[1];
    private  String[] chosenTutorUserTableID = new String[1];
    int[] responseBookingReqID = new int[1];
    int[] eventID = new int[1];
    //Controllers
    private ModuleController moduleController_;
    private TutorController tutorController;
    private BookingController bookController;
    private EventController eventController;
    private NotificationController notificationController;

    public RequestFragment() { //Leave empty...
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void updateLocationText(String loc){
        txtLocation.setText(loc);
    }

    public void toMap(){
        if(mListener != null){
            mListener.goToMap();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_request,container,false);
        moduleSpinner = root.findViewById(R.id.lstModules);
        tutorSpinner = root.findViewById(R.id.Tutors);

                getActivity().setTitle("Request A Tutor");
        //Set up retrofit for this activity
        setUpRetrofit();
        getModules();
        //initializeSpinner();


        txtLocation = root.findViewById(R.id.txtLocation);

        txtTime = root.findViewById(R.id.time);
        txtDate = root.findViewById(R.id.date);
        periods = root.findViewById(R.id.txtPeriods);
        txtTime.setInputType(InputType.TYPE_NULL);
        txtDate.setInputType(InputType.TYPE_NULL);


        btnRequest = root.findViewById(R.id.btnRequest);
        btnGetRelevantTutors = root.findViewById(R.id.getRelevantTutors);
        btnRequest.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {

                requestTutor();

            }
        });

        //Event listener for when a the client attempts to see the tutors
        btnGetRelevantTutors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getRelevantTutors();
            }
        });

        txtDate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                showDateDialog(txtDate);
            }
        });

        txtTime.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                showTimeDialog(txtTime);
            }
        });

        txtLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toMap();
            }
        });

        Bundle bundle = this.getArguments();
        if(bundle != null){
            String location = bundle.getString("Location");
            txtLocation.setText(location);
        }

        return root;
    }
    /**
     *  Set up retrofit for the module controller and the booking controller
     */
    private void setUpRetrofit() {
        AtomicReference<Retrofit> retrofit = new AtomicReference<>(new Retrofit.Builder()
                .baseUrl(App_Global_Variables.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())// for Json type conversions
                .addConverterFactory(ScalarsConverterFactory.create()) //for string conversions
                .build());

        //Set up controllers
        bookController = retrofit.get().create(BookingController.class);
        moduleController_ = retrofit.get().create(ModuleController.class);
        tutorController = retrofit.get().create(TutorController.class);
        eventController = retrofit.get().create(EventController.class);
        notificationController = retrofit.get().create(NotificationController.class);
    }

    /**
     *  format the date
     * @param date
     */
    @SuppressLint("SimpleDateFormat")
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showDateDialog(EditText date)
    {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
                txtDate.setText(simpleDateFormat.format(calendar.getTime()));
            }
        };
        new DatePickerDialog(Objects.requireNonNull(getContext()),R.style.pickerStyles,dateSetListener,calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    /**
     *
     * @param date
     */
    @SuppressLint("SimpleDateFormat")
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showTimeDialog(EditText date)
    {
        final Calendar calendar = Calendar.getInstance();

        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
                txtTime.setText(simpleDateFormat.format(calendar.getTime()));

            }
        };
        new TimePickerDialog(getContext(),R.style.pickerStyles,onTimeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),true).show();

    }

    public interface RequestFragmentListener{
        void goToMap();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof RequestFragmentListener){
            mListener = (RequestFragmentListener) context;
        } else {
            throw new RuntimeException("Main activity needs to implement the RequestFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
    /**
     *  get the modules for the client that is currently booking
     */
    private void getModules() {

        SessionManagement sessionManagement = new SessionManagement(getContext());
        final int userID = sessionManagement.getSession();
        Call<List<Module>> call = null;

        if(App_Global_Variables.USER_CURRENT_STATUS.toString().equals(App_Global_Variables.CLIENT_STATUS.toString()))
            call = moduleController_.GetClientModulesByUserTableID_Mobile(userID);
        else
            call = moduleController_.GetTutorModulesByUserTableID(userID);

        if(call != null){
            call.enqueue(new Callback<List<Module>>() {
                @Override
                public void onResponse(Call<List<Module>> call, Response<List<Module>> response) {

                    if (!response.isSuccessful()) {
                        Toast.makeText(getContext(), "Something went wrong with retrieving modules", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //get the modules from the response body
                    List<Module> modules = response.body();

                    //Insert modules
                    ArrayList<String> list = new ArrayList<>();
                    if(modules.size() > 0){
                        list.add("Select Module");
                        for(Module module : modules){
                            list.add(module.getModule_Name() + ":" + module.getId());
                        }
                    }else {
                        list.add("You are not registered to any modules");
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, list);
                    moduleSpinner.setAdapter(adapter);
                    moduleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String itemValue = parent.getItemAtPosition(position).toString();
                            chosenModule[0] = itemValue;
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            // TODO Auto-generated method stub
                        }
                    });
                }

                @Override
                public void onFailure(Call<List<Module>> call, Throwable t) {
                    Toast.makeText(getContext(), "Could not load Modules", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     *  retrieves the relevant tutors according to the selected module
     */
    private void getRelevantTutors(){

        if(chosenModule[0] != null && !chosenModule[0].equals("Select Module")){

            Call<List<User>> call = tutorController.GetTutorsByTheRespectiveModuleTheyTutor(chosenModule[0].split(":")[0]);

            call.enqueue(new Callback<List<User>>() {
                @Override
                public void onResponse(Call<List<User>> call, Response<List<User>> response) {

                    if (!response.isSuccessful()) {
                        Toast.makeText(getContext(), "Something went wrong with retrieving modules", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //get the modules from the response body
                    List<User> tutors = response.body();

                    //Insert modules
                    ArrayList<String> list = new ArrayList<>();
                    if(tutors.size() > 0){
                        list.add("Select Tutor");
                        for(User tutor: tutors){
                            list.add(tutor.getFullNames() + " " + tutor.getSurname() +":" + tutor.getId());
                        }
                    }else {
                        list.add("Sorry, Module Doesn't have any tutors registered for it");
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, list);
                    tutorSpinner.setAdapter(adapter);
                    tutorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String itemValue = parent.getItemAtPosition(position).toString();
                            chosenTutor[0] = itemValue;
                            if(itemValue.equals("Sorry, Module Doesn't have any tutors registered for it") || //if no tutor is selected
                                    itemValue.equals("Select Tutor")){
                                    //Do nothing
                            }else {
                                getTutorTableID(itemValue.split(":")[1]);
                                //store the tutor's User table reference
                                chosenTutorUserTableID[0] = itemValue.split(":")[1];
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            // TODO Auto-generated method stub
                        }
                    });
                }

                @Override
                public void onFailure(Call<List<User>> call, Throwable t) {
                    Toast.makeText(getContext(), "Could not load Modules", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(getContext(), "Please select a module", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Once the Client Has selected the
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void requestTutor()
    {
        final BookingRequest req = new BookingRequest();

        SessionManagement sessionManagement = new SessionManagement(this.getContext());
        final int userID = sessionManagement.getSession();
        final int myClientTableID = sessionManagement.getClientSession();

        // Get The selected module
        String selectedModule = chosenModule[0];
        if(selectedModule.equals("You are not registered to any modules") || //if no tutor is selected
                selectedModule.equals("Select Module")){
            Toast.makeText(getContext(), "Please select a module", Toast.LENGTH_SHORT).show();
            return;
        }
        // Get The selected tutor
        String selectedTutor = chosenTutor[0];
        if(selectedTutor.equals("Sorry, Module Doesn't have any tutors registered for it") || //if no tutor is selected
                                                        selectedTutor.equals("Select Tutor")){
            Toast.makeText(getContext(), "Please select a tutor", Toast.LENGTH_SHORT).show();
            return;
        }

        if(txtDate.toString()!= " ")
        {
            //Call booking function
        }else{
            Toast.makeText(getContext(), "Please select Date", Toast.LENGTH_SHORT).show();
            return;
        }

        //Set 0 id
        req.setId(0);
        //Set the requested Date
        req.setRequestDate(txtDate.getText().toString() + " " + txtTime.getText().toString());
        //Set the requested Time
        req.setRequestTime(txtTime.getText().toString());
        //set the periods
        req.setPeriods(Integer.parseInt(periods.getText().toString()));
        //set end time with date time arithmetic
        String endDate = "";
        String endTime = "";
        try {
            int intPeriods = Integer.parseInt(periods.getText().toString());

            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss" );
            Date cDate = simpleDateFormat.parse(txtDate.getText().toString() +" "+ txtTime.getText().toString());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(cDate);

            //calendar.add(Calendar.HOUR_OF_DAY,intPeriods);
            calendar.add(Calendar.HOUR, intPeriods);

            simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy" );

            endDate = "" + simpleDateFormat.format(calendar.getTime());
            SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm:ss");
            endTime = "" + simpleTimeFormat.format(calendar.getTime());
            req.setEndTime( endTime);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(endDate.equals(""))
        {
            Toast.makeText(getContext(), "Could not calculate end time", Toast.LENGTH_SHORT).show();
            return;
        }
        //Is accepted
        req.setIs_Accepted(0);

        //Set module reference
        String[] moduleParts = HelperMethods.separateString(selectedModule, ":");
        req.setModuleID(Integer.parseInt(moduleParts[1]));
        //Set Is responded to
        req.setIsRespondedTo(0);
        //set the desired location
        req.setClientProposedLocation(txtLocation.getText().toString());
        //set the desired location
        req.setTutorProposedLocation("");
        //set the tutor reference
        req.setTutor_Reference(Integer.parseInt(chosenTutorID[0]));
        //Booking Request Client Reference
        req.setClient_Reference(myClientTableID);
        Call<BookingRequest> call = bookController.AddBookingRequest(req);
        call.enqueue(new Callback<BookingRequest>() {
            @Override
            public void onResponse(Call<BookingRequest> call, Response<BookingRequest> response) {
                if(response.isSuccessful())
                {
                    Toast.makeText(getContext(), "Your request has been sent successfully", Toast.LENGTH_SHORT).show();

                    //Send an event and notification to the tutor
                    // The description has to be separated because it also contains the 0) The event description, 1) Module name, 2) BookingRequest Id , and 3) the clients ID
                    BookingRequest bookingRequest = response.body();
                    responseBookingReqID[0] = bookingRequest.getId();
                    sendNotificationToTutor();
                }
            }

            @Override
            public void onFailure(Call<BookingRequest> call, Throwable t) {
                Toast.makeText(getContext(), "Please check your network, your request is unsuccessful!", Toast.LENGTH_LONG).show();
            }
        });

    }

    /**
     * Get the tutorTable Id for the tutor as soon as the tutor is selected
     */
    private void getTutorTableID(String userTableID){

        Call<String> call = tutorController.getTutorTableID(Integer.parseInt(userTableID));

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Something went wrong with retrieving Tutor ID", Toast.LENGTH_SHORT).show();
                    return;
                }
                //get the tutor module ID
                String tutorsID = response.body();
                chosenTutorID[0] = tutorsID;
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getContext(), "Could not load Tutor ID", Toast.LENGTH_SHORT).show();
            }
        });
    }
    /**
     * needs a notification and event body
     */
    private void sendNotificationToTutor()
    {
        //set up notification system
        Event bookingEvent = new Event();

        bookingEvent.setEventType("BookingRequest");
        // C# code: bookingEvent.Description = "Client just requested a Tutorial session_" + moduleList.SelectedValue + "_" + bookingRequestObj.Id + "_" + Session["ID"].ToString();
        SessionManagement sessionManagement = new SessionManagement(this.getContext());
        final int userID = sessionManagement.getSession();
        bookingEvent.setDescription("Client just requested a Tutorial session_" + chosenModule[0].split(":")[0] + "_" + chosenModule[0].split(":")[1] + "_" + responseBookingReqID[0] + "_" + userID);
        setEvent(bookingEvent);

    }

    /**
     * Post the booking event to the database
     * @param bookingEvent
     */
    private void setEvent(Event bookingEvent) {

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
                    setNotification();

                }
            }
            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(getContext(), "Could not load Tutor ID", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Set the Notification for the Request booking event
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setNotification() {

        @SuppressLint("SimpleDateFormat")
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a");
        @SuppressLint("SimpleDateFormat")
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        Calendar calendar = Calendar.getInstance();

        Notification eventNotification = new Notification();
        //eventNotification.setDatePosted(HelperMethods.getCurrentDateTime());
        eventNotification.setDatePosted(dateFormat.format(calendar.getTime()));
        //eventNotification.setTime(HelperMethods.getCurrentTime());
        eventNotification.setTime(timeFormat.format(calendar.getTime()));
        eventNotification.setSeen(0);
        eventNotification.setPersonTheNotificationConcerns(App_Global_Variables.TUTOR_STATUS);
        eventNotification.setUser_Table_Reference(Integer.parseInt(chosenTutorUserTableID[0]));
        eventNotification.setEvent_Table_Reference(eventID[0]);
        setBookingNotification(eventNotification);
    }

    /**
     *
     * @param notification - to be recorded to the database
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
                    Toast.makeText(getContext(), "Could not send notification too the tutor from withing the server", Toast.LENGTH_SHORT).show();
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
}

