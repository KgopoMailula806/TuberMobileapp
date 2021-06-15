package com.tuber_mobile_application;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.tuber_mobile_application.Controllers.ClientController;
import com.tuber_mobile_application.Controllers.DocumentController;
import com.tuber_mobile_application.Controllers.UserController;
import com.tuber_mobile_application.Models.Client;
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

public class ProfileFragment extends Fragment
{
    View profileFragment;
    private UserController userController;
    private DocumentController imageController;
    private ClientController clientController;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        profileFragment = inflater.inflate(R.layout.fragment_profile,container,false);
        setUpRetrofit();
        fillUserDetails();

        Button btnDeactivate = profileFragment.findViewById(R.id.btnDeactivate);

        btnDeactivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assert getFragmentManager() != null;
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new DeactivateAccFragment()).commit();
            }
        });


        return profileFragment;
    }

    private void setUpRetrofit() {
        AtomicReference<Retrofit> retrofit = new AtomicReference<>(new Retrofit.Builder()
                .baseUrl(App_Global_Variables.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())// for Json type conversions
                .addConverterFactory(ScalarsConverterFactory.create()) //for string conversions
                .build());

        imageController = retrofit.get().create(DocumentController.class);
        userController = retrofit.get().create(UserController.class);
        clientController = retrofit.get().create(ClientController.class);
    }

    private void fillUserDetails()
    {
        final ImageView profile_pic = profileFragment.findViewById(R.id.pp);
        final TextView txtName = profileFragment.findViewById(R.id.p_name);
        final TextView txtEmail = profileFragment.findViewById(R.id.p_email);
        final TextView txtNumber = profileFragment.findViewById(R.id.p_num);
        final TextView txtRating = profileFragment.findViewById(R.id.p_rating);
        final TextView txtSchool = profileFragment.findViewById(R.id.p_school);
        final TextView txtGender = profileFragment.findViewById(R.id.p_gender);
        final TextView txtGrade = profileFragment.findViewById(R.id.p_grade);
        final TextView txtType = profileFragment.findViewById(R.id.p_type);

        //Get user details using the user ID
        SessionManagement sessionManagement = new SessionManagement(Objects.requireNonNull(getContext()));
        final int userID = sessionManagement.getSession();

        Call<User> userCall = userController.getUserDetails(userID);
        userCall.enqueue(new Callback<User>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NotNull Call<User> call, @NotNull Response<User> response)
            {
                if(!response.isSuccessful()){ return; }

                if(response.body() != null)
                {
                    txtName.setText(response.body().getFullNames() + " " + response.body().getSurname());
                    txtEmail.setText(response.body().getEmail_Address().toLowerCase());
                    txtNumber.setText(response.body().getValid_Phone_Number());
                    txtSchool.setText(response.body().getGender());
                    txtRating.setText("0.0");
                    txtType.setText(response.body().getUser_Discriminator());
                    txtGender.setText(response.body().getGender());

                    if(response.body().getUser_Discriminator().equals(App_Global_Variables.CLIENT_STATUS))
                    {
                        Call<Client> clientCall = clientController.getClient(response.body().getId());
                        clientCall.enqueue(new Callback<Client>() {
                            @Override
                            public void onResponse(@NotNull Call<Client> call, @NotNull Response<Client> clientResponse)
                            {
                                if(clientResponse.isSuccessful())
                                {
                                    if(clientResponse.body() !=null)
                                    {
                                        txtSchool.setText(clientResponse.body().getInstitution());
                                        txtGrade.setText(clientResponse.body().getCurrent_Grade());
                                    }
                                }

                            }

                            @Override
                            public void onFailure(@NotNull Call<Client> call, @NotNull Throwable t) {

                            }
                        });
                    }

                    Call<Document> documentCall = imageController.getDocument(response.body().getImage());
                    documentCall.enqueue(new Callback<Document>()
                    {
                        @Override
                        public void onResponse(@NotNull Call<Document> call, @NotNull Response<Document> response)
                        {
                            if(!response.isSuccessful()){
                                Toast.makeText(getContext(), "Couldn't get profile picture", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            Document profilePicture = response.body();
                            if(profilePicture != null){
                                byte[] decodedString = Base64.decode(profilePicture.getDocumentData(), Base64.DEFAULT);
                                Bitmap decodedImage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                profile_pic.setImageBitmap(decodedImage); // setting up profile picture
                            }

                        }

                        @Override
                        public void onFailure(@NotNull Call<Document> call, @NotNull Throwable t)
                        {
                            // todo: Handle failure
                        }
                    });
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

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).hide();

    }

}
