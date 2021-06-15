package com.tuber_mobile_application;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.tuber_mobile_application.Controllers.BookingController;
import com.tuber_mobile_application.Controllers.DocumentController;
import com.tuber_mobile_application.Controllers.EventController;
import com.tuber_mobile_application.Controllers.NotificationController;
import com.tuber_mobile_application.Controllers.UserController;
import com.tuber_mobile_application.Models.Document;
import com.tuber_mobile_application.Models.User;
import com.tuber_mobile_application.helper.App_Global_Variables;
import com.tuber_mobile_application.helper.SessionManagement;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class DashboardFragment extends Fragment
{
    private DocumentController imageController; // used to get user image
    private UserController userController; // used to get user object
    private View dashboardView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        dashboardView = inflater.inflate(R.layout.fragment_dashboard,container,false);
        setUpRetrofit();

        return dashboardView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).hide();
        fillUserDetails(); // name & profile picture
        setUserCards(); // activating the on click listeners
    }

    public void setUserCards()
    {
        CardView requestCard = dashboardView.findViewById(R.id.dash_request_card);
        CardView sessionCard = dashboardView.findViewById(R.id.dash_session_card);
        CardView moduleCard = dashboardView.findViewById(R.id.dash_modules_card);
        CardView notificationCard = dashboardView.findViewById(R.id.dash_notifications_card);
        CardView profileCard = dashboardView.findViewById(R.id.dash_profile_card);
        CardView paymentCard = dashboardView.findViewById(R.id.dash_payment_card);

        requestCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                getFragmentManager().beginTransaction().addToBackStack(null);
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new RequestFragment()).commit();
            }
        });

        sessionCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                getFragmentManager().beginTransaction().addToBackStack(null);
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new SessionFragment()).commit();

            }
        });

        moduleCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                getFragmentManager().beginTransaction().addToBackStack(null);
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new ModuleFragment()).commit();
            }
        });

        notificationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                getFragmentManager().beginTransaction().addToBackStack(null);
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new NotificationsFragment()).commit();

            }
        });

        profileCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                getFragmentManager().beginTransaction().addToBackStack(null);
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();

            }
        });

        paymentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                getFragmentManager().beginTransaction().addToBackStack(null);
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new PaymentsFragment()).commit();

            }
        });
    }

    private void setUpRetrofit() {
        AtomicReference<Retrofit> retrofit = new AtomicReference<>(new Retrofit.Builder()
                .baseUrl(App_Global_Variables.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())// for Json type conversions
                .addConverterFactory(ScalarsConverterFactory.create()) //for string conversions
                .build());

        imageController = retrofit.get().create(DocumentController.class);
        userController = retrofit.get().create(UserController.class);
    }

    private void fillUserDetails()
    {
        final ProgressBar imageLoader = dashboardView.findViewById(R.id.img_loader); // initialising the image loader
        final TextView txtUsername = dashboardView.findViewById(R.id.dash_txt_username);
        final ImageView imgProfile = dashboardView.findViewById(R.id.dash_profile_picture);


        //Get user details using the user ID
        SessionManagement sessionManagement = new SessionManagement(getContext());
        final int userID = sessionManagement.getSession();

        Call<User> userCall = userController.getUserDetails(userID);
        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response)
            {
                if(!response.isSuccessful()){ return; }

                if(response.body() != null)
                {
                    txtUsername.setText(response.body().getFullNames() + " " + response.body().getSurname());
                    Call<Document> documentCall = imageController.getDocument(response.body().getImage());
                    documentCall.enqueue(new Callback<Document>()
                    {
                        @Override
                        public void onResponse(Call<Document> call, Response<Document> response)
                        {
                            if(!response.isSuccessful()){
                                Toast.makeText(getContext(), "Couldn't get profile picture", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            Document profilePicture = response.body();
                            if(profilePicture != null){
                                byte[] decodedString = Base64.decode(profilePicture.getDocumentData(), Base64.DEFAULT);
                                Bitmap decodedImage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                imgProfile.setImageBitmap(decodedImage); // setting up profile picture
                            }

                        }

                        @Override
                        public void onFailure(Call<Document> call, Throwable t)
                        {
                            // todo: Handle failure
                        }
                    });

                    imageLoader.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure(@NotNull Call<User> call, @NotNull Throwable t) {

            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).show();
    }
}
