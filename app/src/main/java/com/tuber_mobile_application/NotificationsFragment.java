package com.tuber_mobile_application;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tuber_mobile_application.Controllers.EventController;
import com.tuber_mobile_application.Controllers.NotificationController;
import com.tuber_mobile_application.Models.Event;
import com.tuber_mobile_application.Models.Notification;
import com.tuber_mobile_application.helper.App_Global_Variables;
import com.tuber_mobile_application.helper.HelperMethods;
import com.tuber_mobile_application.helper.SessionManagement;
import com.tuber_mobile_application.helper.ui.NotificationAdapter;
import com.tuber_mobile_application.helper.ui.NotificationItem;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class NotificationsFragment extends Fragment {
    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<NotificationItem> notificationItems;
    private NotificationController notificationController;
    private EventController eventController;
    private ProgressBar progressBar;
    private final String[] moduleName = new String[1];
    private final String[] otherUserId = new String[1];
    private final String[] readableType = new String[1];
    private int[] numNotifications = new int[1];

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        Objects.requireNonNull(getActivity()).setTitle("Notifications");
        setUpRetrofit();

        progressBar = view.findViewById(R.id.progressBar_notifications);
        recyclerView = view.findViewById(R.id.notifications_recycler_view);
        getNotificationForOnce();
        setNumberOfNotifications();

        return view;
    }

    private void setUpRetrofit() {
        AtomicReference<Retrofit> retrofit = new AtomicReference<>(new Retrofit.Builder()
                .baseUrl(App_Global_Variables.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())// for Json type conversions
                .addConverterFactory(ScalarsConverterFactory.create()) //for string conversions
                .build());

        notificationController = retrofit.get().create(NotificationController.class);
        eventController = retrofit.get().create(EventController.class);
    }

    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.VISIBLE);
        //getNotifications();
    }

    /*private void getNotifications()
    {
        SessionManagement sessionManagement = new SessionManagement(getContext());
        final int userID = sessionManagement.getSession();

        Call<List<Notification>> notificationCall = notificationController.popUsersUnseenNotificationsMobileForIteration(userID);
        notificationCall.enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "No Successful Response", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<Notification> notifications = response.body();

                if (notifications != null) {
                    notificationItems = new ArrayList<>();
                    for (final Notification n : notifications) {

                        if(n.getPersonTheNotificationConcerns().equals(App_Global_Variables.CLIENT_STATUS))
                        {
                            getEvent(n);
                        } else if(n.getPersonTheNotificationConcerns().equals(App_Global_Variables.TUTOR_STATUS))
                        {
                            getEvent(n);
                        }

                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {
                Toast.makeText(getContext(), "Could not load notifications", Toast.LENGTH_SHORT).show();
            }
        });

        progressBar.setVisibility(View.INVISIBLE);
    }*/
    private void getNotificationForOnce()
    {
        SessionManagement sessionManagement = new SessionManagement(Objects.requireNonNull(getContext()));
        final int userID = sessionManagement.getSession();

        Call<List<Notification>> notificationCall = notificationController.popUsersUnseenNotificationsMobileForOnce(userID);
        notificationCall.enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(@NotNull Call<List<Notification>> call, @NotNull Response<List<Notification>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "No Successful Response", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<Notification> notifications = response.body();

                if (notifications != null) {
                    notificationItems = new ArrayList<>();
                    for (final Notification n : notifications) {

                        if(n.getPersonTheNotificationConcerns().equals(App_Global_Variables.CLIENT_STATUS))
                        {
                            getEvent(n);
                        } else if(n.getPersonTheNotificationConcerns().equals(App_Global_Variables.TUTOR_STATUS))
                        {
                            getEvent(n);
                        }

                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }
            }
            @Override
            public void onFailure(@NotNull Call<List<Notification>> call, @NotNull Throwable t) {
                Toast.makeText(getContext(), "Could not load notifications", Toast.LENGTH_SHORT).show();
            }
        });
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void getEvent(final Notification n) {
        Call<Event> eventCall = eventController.GetEvent(n.getEvent_Table_Reference());
        eventCall.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(@NotNull Call<Event> call, @NotNull Response<Event> eventResponse) {
                if (!eventResponse.isSuccessful()) {
                    Toast.makeText(getContext(), "No Successful Response", Toast.LENGTH_SHORT).show();
                    return;
                }

                final Event event = eventResponse.body();
                if (event != null) {

                    //Break down the Event Description
                    final String[] details = HelperMethods.separateString(event.getDescription(),"_");
                    //Determine the event type
                    switch (event.getEventType()) {
                        case "BookingRequest": {
                            setBookingRequestItems(event,n,details);
                        }
                        break;
                        case "BookingFinalization": {
                            setBookingFinalizationItems(event,n,details);
                        }
                        break;
                        case "Session": {
                            setSessionItems(event,n,details);
                        }break;
                        case "BookingCancelation": {
                            setBookingCancelationItems(event,n,details);
                        }
                        break;
                        case "BookingRejection": {
                            setBookingRejectionItems(event,n,details);
                        }
                        break;
                        case "BookingRenegotiation": {
                            setBookingRenegotiationItems(event,n,details);
                        }
                        break;
                        case "InterviewMeeting": {
                            setInterviewMeetingItems(event,n,details);
                        }
                        break;
                        case "SessionRating":
                        {
                            StringTokenizer ratingToken = new StringTokenizer(event.getDescription(),"_");
                            NotificationItem ratingItem = new NotificationItem(n.getId(),event.getId(),0, event.getEventType(), event.getDescription(),
                                                                                makeEventTypeReadable(event.getEventType()),ratingToken.nextToken(),makeAgoTime(n.getDatePosted()));
                            notificationItems.add(ratingItem);
                        }
                        break;

                    }

                    recyclerView.setHasFixedSize(true);
                    layoutManager = new LinearLayoutManager(getContext());
                    adapter = new NotificationAdapter(notificationItems);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);

                    adapter.SetOnItemClickListener(new NotificationAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int pos) {
                            // extract the clicked notification information using pos.
                            //check if all of the notifications have been loaded

                            setClickEventAction(pos, notificationItems.get(pos).getEventType());

                            /*if(numNotifications[0] == notificationItems.size()){

                            }else{
                                Toast.makeText(getContext(), "please wait, notifications still loading", Toast.LENGTH_SHORT).show();
                            }*/


                        }
                    });

                    progressBar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure(@NotNull Call<Event> call, @NotNull Throwable t) {
                Toast.makeText(getContext(), "Could not fetch events", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * set the notification Item for the booking request
     */
    private void setBookingRequestItems(final Event event, final Notification n, final String[] details){
        final NotificationItem[] notificationItem = new NotificationItem[1];
        final String timeAgo = makeAgoTime(n.getDatePosted());

        moduleName[0] = details[1];
        readableType[0] = makeEventTypeReadable(event.getEventType());

        notificationItem[0] = new NotificationItem(n.getId(), event.getId(), Integer.parseInt(details[3]), event.getEventType(), event.getDescription(),
                readableType[0], details[0] +","+
                moduleName[0] + " request", timeAgo);

        notificationItems.add(notificationItem[0]);
        progressBar.setVisibility(View.INVISIBLE);

    }

    /**
     * set the notification Item for the Booking Finalization
     */
    private void setBookingFinalizationItems(final Event event, final Notification n, final String[] details) {
        final NotificationItem[] notificationItem = new NotificationItem[1];
        final String timeAgo = makeAgoTime(n.getDatePosted());
        final String[] moduleName = new String[1];
        final String[] readableType = new String[1];

        //TODO:
        //  Decryption= + separatedDescriptionText[0]

        readableType[0] = makeEventTypeReadable(event.getEventType());
        moduleName[0] = details[1];

        notificationItem[0] = new NotificationItem(n.getId(), event.getId(), 0, event.getEventType(), event.getDescription(),
                readableType[0], details[0] + ", " +
                moduleName[0].toUpperCase() + " Booking Finalization", timeAgo);

        notificationItems.add(notificationItem[0]);
    }
    /**
     * set the notification Item for the Session
     */
    private void setSessionItems(final Event event, final Notification n, final String[] details) {
        final NotificationItem[] notificationItem = new NotificationItem[1];
        final String timeAgo = makeAgoTime(n.getDatePosted());

        readableType[0] = makeEventTypeReadable(event.getEventType());
        moduleName[0] = details[4];

        notificationItem[0] = new NotificationItem(n.getId(), event.getId(), 0, event.getEventType(), event.getDescription(),
                readableType[0], details[0] + ", " +
                moduleName[0].toUpperCase() + " Booking Renegotiation", timeAgo);

        //set booking ID
        notificationItem[0].setRequestID(Integer.parseInt(details[3]));
        //Set the notification Item
        notificationItems.add(notificationItem[0]);
    }
    /**
     * set the notification Item for the Booking Cancellation
     */
    private void setBookingCancelationItems(Event event, Notification n, String[] details) {
        final NotificationItem[] notificationItem = new NotificationItem[1];
        final String timeAgo = makeAgoTime(n.getDatePosted());

        readableType[0] = makeEventTypeReadable(event.getEventType());
        //moduleName[0] = details[4];

        notificationItem[0] = new NotificationItem(n.getId(), event.getId(), 0, event.getEventType(), event.getDescription(),
                readableType[0], details[0] + ", " +
                "" + " Booking Cancellation", timeAgo);

        //set booking ID
        notificationItem[0].setRequestID(Integer.parseInt(details[1]));
        //Set the notification Item
        notificationItems.add(notificationItem[0]);
    }
    /**
     * set the notification Item for the Booking Rejection
     */
    private void setBookingRejectionItems(Event event, Notification n, String[] details) {
        final NotificationItem[] notificationItem = new NotificationItem[1];
        final String timeAgo = makeAgoTime(n.getDatePosted());

        readableType[0] = makeEventTypeReadable(event.getEventType());
        moduleName[0] = details[1];

        notificationItem[0] = new NotificationItem(n.getId(), event.getId(), 0, event.getEventType(), details[0],
                readableType[0], details[0] + ", " +
                moduleName[0].toUpperCase() + " Booking Rejection", timeAgo);

        //set booking ID
        notificationItem[0].setRequestID(Integer.parseInt(details[2]));
        //Set the notification Item
        notificationItems.add(notificationItem[0]);
    }

    /**
     * set the notification Item for the Booking Renegotiation
     */
    private void setBookingRenegotiationItems(final Event event, final Notification n, final String[] details) {
        final NotificationItem[] notificationItem = new NotificationItem[1];
        final String timeAgo = makeAgoTime(n.getDatePosted());

        readableType[0] = makeEventTypeReadable(event.getEventType());
        moduleName[0] = details[4];
        otherUserId[0] = details[1];

        notificationItem[0] = new NotificationItem(n.getId(), event.getId(), 0, event.getEventType(), event.getDescription(),
                readableType[0], details[0] + ", " +
                moduleName[0].toUpperCase() + " Booking Renegotiation", timeAgo);

        //set booking ID
        notificationItem[0].setRequestID(Integer.parseInt(details[3]));
        //Set the notification Item
        notificationItems.add(notificationItem[0]);

    }

    /**
     * Method for controlling the actions upon clicking on the notification items
     */
    public void setClickEventAction(int pos,String type)
    {
        switch (type){
            case "BookingRequest":{
                NotificationItem clickedItem = notificationItems.get(pos);
                //Find event Type
                //From_Notifications/nID/eID/bID/eventType/eventDescription
                String parcel = "From-Notifications~" + clickedItem.getNotificationID() + "~" + clickedItem.getEventID() + "~" + clickedItem.getRequestID() + "~"
                        + clickedItem.getEventType() + "~" + clickedItem.getEventDescription() + "~" + moduleName[0];

                // moving to multipurposeFragment w/ extra information
                MultipurposeFragment multipurposeFragment = new MultipurposeFragment();
                Bundle notificationBundle = new Bundle(); // to carry information to next fragment

                //Set the bundle arguments
                notificationBundle.putString("Parcel", parcel);
                notificationBundle.putString("NotificationType","BookingRequest");
                notificationBundle.putString("moduleName",moduleName[0]);

                multipurposeFragment.setArguments(notificationBundle);

                FragmentManager manager = getFragmentManager();
                if(manager != null)
                {
                    manager.beginTransaction().replace(R.id.fragment_container, multipurposeFragment).commit();
                }

            }
            break;
            case "BookingRenegotiation":{
                NotificationItem clickedItem = notificationItems.get(pos);

                //From_Notifications/nID/eID/bID/eventType/eventDescription
                String parcel = "From-Notifications~" + clickedItem.getNotificationID() + "~" + clickedItem.getEventID() + "~" + clickedItem.getRequestID() + "~"
                        + clickedItem.getEventType() + "~" + clickedItem.getEventDescription();

                // moving to multipurposeFragment w/ extra information
                NegotiationFragment multipurposeFragment = new NegotiationFragment();
                Bundle notificationBundle = new Bundle(); // to carry information to next fragment
                notificationBundle.putString("moduleName",moduleName[0]);
                notificationBundle.putString("otherUserId",otherUserId[0]);
                //Set the bundle arguments
                notificationBundle.putString("Parcel", parcel);
                notificationBundle.putString("NotificationType","BookingRenegotiation");

                multipurposeFragment.setArguments(notificationBundle);

                FragmentManager manager = getFragmentManager();
                if(manager != null)
                {
                    manager.beginTransaction().replace(R.id.fragment_container, multipurposeFragment).commit();
                }

            }break;
            case "BookingFinalization":
            case "BookingCancelation":
            case "BookingRejection": {

                // extract the clicked notification information using pos.
                NotificationItem clickedItem = notificationItems.get(pos);
                makeNotificationSeen(clickedItem.getNotificationID());
                //From_Notifications/nID/eID/bID/eventType/eventDescription
                String parcel = "From-Notifications~" + clickedItem.getNotificationID() + "~" + clickedItem.getEventID() + "~" + clickedItem.getRequestID() + "~"
                        + clickedItem.getEventType() + "~" + clickedItem.getEventDescription();

                // moving to multipurposeFragment w/ extra information
                MultipurposeFragment multipurposeFragment = new MultipurposeFragment();
                Bundle notificationBundle = new Bundle(); // to carry information to next fragment

                //Set the bundle arguments
                notificationBundle.putString("Parcel", parcel);
                notificationBundle.putString("NotificationType","BookingFinalization");

                multipurposeFragment.setArguments(notificationBundle);

                FragmentManager manager = getFragmentManager();
                if(manager != null)
                {
                    manager.beginTransaction().replace(R.id.fragment_container, multipurposeFragment).commit();
                }


            }
            break;
            case "SessionRating":
            {
                // extract the clicked notification information using pos.
                NotificationItem clickedItem = notificationItems.get(pos);

                StringTokenizer tokens = new StringTokenizer(clickedItem.getEventDescription(),"_");
                tokens.nextToken(); // moving cursor

                // parcel = notificationID/sessionID/clientID/tutorID
                Bundle bundle = new Bundle();
                String parcel = clickedItem.getNotificationID() + "/" + tokens.nextToken();
                bundle.putString("toRatingFromNotification",parcel);
                RatingFragment ratingFragment = new RatingFragment();

                ratingFragment.setArguments(bundle); // setting arguments for next fragment

                FragmentManager manager = getFragmentManager();
                if(manager != null)
                {
                    manager.beginTransaction().replace(R.id.fragment_container, ratingFragment).commit();
                }
            }
            break;
            case "InterviewMeeting": {

            }break;

        }

    }

    void makeNotificationSeen(int notificationID ) {
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
     * set the notification Item for the Interview Meeting
     * @param details details array
     * @param event event object
     * @param n notification object
     * */
    private void setInterviewMeetingItems(Event event, Notification n, String[] details) {
    }

    private String makeEventTypeReadable(String eventType) {
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
        }
        return type;
    }

    public static String[] breakupDescription(String eventType, String eventDescription) {
        String[] data = new String[7];

            StringTokenizer tokenizer = new StringTokenizer(eventDescription, "_");
            switch (eventType) {
                case "BookingRequest":
                case "Session":
                case "BookingFinalization": {

                    //Client just requested a Tutorial Session_Applied Math:4_11_21
                    data[0] = tokenizer.nextToken();// Description
                    data[1] = tokenizer.nextToken();// ModuleName
                    data[2] = tokenizer.nextToken();// ModuleID || ||SessionDate
                    data[3] = tokenizer.nextToken();// BookingRequestID
                    data[4] = tokenizer.nextToken();// UserID || || TutorID
                }
                break;

                case "BookingRenegotiation": {
                    data[0] = tokenizer.nextToken();// Description
                    data[1] = tokenizer.nextToken();// OtherUserID
                    data[2] = tokenizer.nextToken();// AltDate
                    data[3] = tokenizer.nextToken();// BookingRequestID
                    data[4] = tokenizer.nextToken();// ModuleName
                    data[5] = tokenizer.nextToken();// EndTime
                    data[6] = tokenizer.nextToken();// Reason

                }
                break;

                case "BookingCancelation": {
                    data[0] = tokenizer.nextToken();// Description
                    data[1] = tokenizer.nextToken();// BookingRequestID
                }
                break;
                case "BookingRejection": {

                }
                break;
            }


        return data;
    }

    public static String makeAgoTime(String date) 
    {
        String ago = "";
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
        SimpleDateFormat dateFormat3 = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

		Calendar calendar = Calendar.getInstance();

		try 
		{
			Date currentTime = calendar.getTime();
			Date givenTime = dateFormat.parse(date); // passed date
			
			long difference =  currentTime.getTime() - givenTime.getTime();
			int secondsAgo = Math.abs((int) TimeUnit.MILLISECONDS.toSeconds(difference));
            if((secondsAgo/86400 > 60))
            {
                givenTime = dateFormat2.parse(date);
                difference =  currentTime.getTime() - givenTime.getTime();
                secondsAgo = Math.abs((int) TimeUnit.MILLISECONDS.toSeconds(difference));
                if((secondsAgo/86400 > 60))
                {
                    givenTime = dateFormat3.parse(date);
                    difference =  currentTime.getTime() - givenTime.getTime();
                    secondsAgo = Math.abs((int) TimeUnit.MILLISECONDS.toSeconds(difference));
                }
            }

			if(secondsAgo/60 < 0)
			{
				int secs = secondsAgo/60;
				ago = Math.abs(secs) + " seconds ago";
				
			}			
			else if((secondsAgo/60 > 0) && (secondsAgo/60 <= 60))
			{
				ago = secondsAgo/60 + " minutes ago";
			}
			else if((secondsAgo/3600 > 0) && (secondsAgo/3600 < 24))
			{
				ago = secondsAgo/3600 + " hours ago";
			}
			else if((secondsAgo/86400 > 0))
			{
				ago = secondsAgo/86400 + " days ago";
			}
			
		} 
		catch (ParseException e) 
		{
			e.printStackTrace();
		}

        return ago;
    }

    private void setNumberOfNotifications()
    {
        SessionManagement sessionManagement = new SessionManagement(Objects.requireNonNull(getContext()));
        final int userID = sessionManagement.getSession();

        Call<Integer> notificationCall = notificationController.getNumberOfUnreadNotifications(userID);
        notificationCall.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(@NotNull Call<Integer> call, @NotNull Response<Integer> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "No Successful Response", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(response.body() != null)
                {
                    numNotifications[0] = response.body();
                }
            }
            @Override
            public void onFailure(@NotNull Call<Integer> call, @NotNull Throwable t) {
                Toast.makeText(getContext(), "Could not load notifications", Toast.LENGTH_SHORT).show();
            }
        });
        progressBar.setVisibility(View.INVISIBLE);
    }
}
