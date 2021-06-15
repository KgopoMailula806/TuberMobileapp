package com.tuber_mobile_application;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.tuber_mobile_application.Controllers.DocumentController;
import com.tuber_mobile_application.Models.Document;
import com.tuber_mobile_application.helper.*;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tuber_mobile_application.Controllers.ClientController;
import com.tuber_mobile_application.Controllers.UserController;
import com.tuber_mobile_application.Models.User;

import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistrationActivity extends AppCompatActivity {

    private UserController userController;
    private ClientController clientController;
    private DocumentController documentController;
    private Intent intentFromRegistrationTypeSelection;
    public static int userID = 0;

    private int imageId;
    private TextView txtPath;
    private String gender;

    private TextInputLayout txtPass,txtNumber;
    private TextInputLayout txtConfirm, txtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        //Set up retrofit communication hub
        AtomicReference<Retrofit> retrofit = new AtomicReference<>(new Retrofit.Builder()
                .baseUrl(App_Global_Variables.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build());

        userController = retrofit.get().create(UserController.class);
        documentController = retrofit.get().create(DocumentController.class);

        //instantiate the intent
        intentFromRegistrationTypeSelection = getIntent();

        frontEndValidation();
        getGender();
        txtPath = findViewById(R.id.txtPath);

    }


    /**
     * This method mainly asserts that all of the fields are filled in
     *
     * @param view
     */
    public void signUp(View view) {
        //get all of the data

        boolean fieldsAreAllFilled = true;
        EditText fullNamesTXT = findViewById(R.id.txtFullNames);

        // the user wishes to register as a client
        String fullNames = fullNamesTXT.getText().toString();
        if (!(fullNames.length() > 0))
            fieldsAreAllFilled = false;

        EditText surnameTXT = findViewById(R.id.txtSurname);
        String surnames = surnameTXT.getText().toString();
        if (!(surnames.length() > 0))
            fieldsAreAllFilled = false;

        if (gender.equals("--Specify your gender--")) {
            fieldsAreAllFilled = false;
        }

        String email = txtEmail.getEditText().getText().toString();
        if (!(email.length() > 0))
            fieldsAreAllFilled = false;

        String phoneNumbers = txtNumber.getEditText().getText().toString();
        if (!(phoneNumbers.length() > 0))
            fieldsAreAllFilled = false;

        EditText ageTXT = findViewById(R.id.age);
        String age = ageTXT.getText().toString();
        if (!(age.length() > 0))
            fieldsAreAllFilled = false;

        //for the client Table
        String password = txtPass.getEditText().getText().toString();
        if (!(password.length() > 0))
            fieldsAreAllFilled = false;

        String passwordCon = txtConfirm.getEditText().getText().toString();
        if (!(passwordCon.length() > 0))
            fieldsAreAllFilled = false;

        //Check if check if all of the fields are filled in
        if (fieldsAreAllFilled) {

            //Check if password match
            if (password.equals(passwordCon))
            {
                User newUser; // = new User(0, fullNames, surnames, phoneNumbers, email, password, gender, imageId, Integer.parseInt(age), intentFromRegistrationTypeSelection.getStringExtra("UserType"), 1);
               if( intentFromRegistrationTypeSelection.getStringExtra("UserType").equals("Client"))
               {
                   newUser = new User(0, fullNames, surnames, phoneNumbers, email, password, gender, imageId, Integer.parseInt(age), intentFromRegistrationTypeSelection.getStringExtra("UserType"), 1);
               }
               else
               {
                   newUser = new User(0, fullNames, surnames, phoneNumbers, email, password, gender, imageId, Integer.parseInt(age), intentFromRegistrationTypeSelection.getStringExtra("UserType"),0);
               }

                //Register the User
                RegisterUserDetails(newUser);


            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Passwords do not match match", Toast.LENGTH_SHORT);
                toast.show();
            }

        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "There are missing fields", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void moveToNextRegistrationActivity() {
        //save session
        //SessionManagement sessionManagement = new SessionManagement(getApplicationContext());
        //User user = new User(userID, "", "", "0", "", "", "", 0, 0, intentFromRegistrationTypeSelection.getStringExtra("UserType"));
        //sessionManagement.saveSession(user);
        Intent intent = new Intent(RegistrationActivity.this, RegisterRemainingDetails.class);
        intent.putExtra("UserType", intentFromRegistrationTypeSelection.getStringExtra("UserType"));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * @param newUser
     */
    private void RegisterUserDetails(User newUser) {

        Call<User> call = userController.addUser(newUser);
        call.enqueue(new Callback<User>() {

            @Override
            public void onResponse(@NotNull Call<User> call, @NotNull Response<User> response) {

                if (!response.isSuccessful()) {
                    //textViewResult.setText("Code: " + response.code());
                    Toast.makeText(getApplicationContext(), "Registration Request was unsuccessful " + "Code: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                User user = response.body();
                userID = user.getId();
                //Client clientDetails = new Client(0, institution,user.getId(),null);
                //Make a session and move redirect to the home page
                if(!user.getUser_Discriminator().equals("Tutor"))
                {
                    // make a session if this is a user, tutor must be accepted first
                    SessionManagement sessionManagement = new SessionManagement(RegistrationActivity.this);
                    sessionManagement.saveSession(user);
                }

                moveToNextRegistrationActivity();
                //RegisterRemainingDetails();
                Toast.makeText(getApplicationContext(), "Registration Request was successful", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NotNull Call<User> call, @NotNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Registration Request was unsuccessful " + t.fillInStackTrace(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * redirects to the registration type UI
     *
     * @param view
     */
    public void goToRegister(View view) {
        Intent intent = new Intent(RegistrationActivity.this, RegistrationType.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    /**
     * to choose the Image
     *
     * @param view
     */
    public void chooseImage(View view) {
        //application/pdf
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, FileManagement.REQUEST_CODE_IMAGE);
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FileManagement.REQUEST_CODE_IMAGE && data != null) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);

                try {
                    InputStream is = getContentResolver().openInputStream(uri);

                    if (is != null)
                    {
                           String fileData = FileManagement.makeBase64(is);
                           int size = FileManagement.size(cursor);
                           cursor.moveToFirst();
                           String name = FileManagement.getFileName(cursor);
                           cursor.moveToFirst();
                           String extension = FileManagement.getExtension(name);
                           imageId = FileManagement.sendFile(0, size, fileData, name.substring(name.lastIndexOf(".")), this, name);
                           txtPath.setText(name);

                           Document doc = new Document();
                           doc.setDocumentData(fileData);
                           doc.setExtension(extension);
                           doc.setId(0);
                           doc.setSize(size);


                           Call<Integer> call = documentController.UploadDocument(doc);
                           call.enqueue(new Callback<Integer>() {
                               @Override
                               public void onResponse(@NotNull Call<Integer> call, @NotNull Response<Integer> response) {
                                   if (response.isSuccessful()) {
                                       if (response.body() != null) {
                                           imageId = response.body();
                                       }

                                   }
                               }

                               @Override
                               public void onFailure(@NotNull Call<Integer> call, @NotNull Throwable t) {

                               }
                           });

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void getGender() {

        Spinner spinner = findViewById(R.id.genderSpinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String itemValue = parent.getItemAtPosition(position).toString();
                gender = itemValue;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing, its handled above
            }
        });

    }

    private boolean strengthCheck(String password)
    {
        Pattern pattern = Pattern.compile("(.*[!@#$%^&*])");
        Matcher matcher = pattern.matcher(password);

        if(password.length() >= 6)
        {
            return matcher.find();
        }
        return false;
    }

    private boolean checkNumber(String number)
    {
        Pattern pattern = Pattern.compile("(0(6|7|8)[0-9][0-9]{7})");
        Matcher matcher = pattern.matcher(number);
        return matcher.find();
    }

    private boolean checkEmail(String email)
    {
        Pattern pattern = Pattern.compile("([A-Za-z0-9_\\-.])+@([A-Za-z0-9_\\-.])+\\.([A-Za-z]{2,4})");
        Matcher matcher = pattern.matcher(email);
        return matcher.find();
    }
    public void frontEndValidation()
    {
        txtEmail = findViewById(R.id.txtEmail);
        txtNumber = findViewById(R.id.numbers);
        txtPass = findViewById(R.id.txtRegisterPasswordBox);
        txtConfirm = findViewById(R.id.txtRegisterConPasswordBox);

        Objects.requireNonNull(txtPass.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {


                if(strengthCheck(editable.toString()))
                    txtPass.setError(null);
                else
                    txtPass.setError("password must be 6 characters long and must contain at least one special character");
            }
        });

        Objects.requireNonNull(txtConfirm.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().equals(txtPass.getEditText().getText().toString()))
                {
                    txtConfirm.setError(null);
                }
                else
                    txtConfirm.setError("please match with main password");
            }
        });

        Objects.requireNonNull(txtNumber.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(editable.toString().length() >= 10)
                {
                    if(checkNumber(editable.toString()))
                        txtNumber.setError(null);
                    else
                        txtNumber.setError("please enter a valid number");
                }

            }
        });

        Objects.requireNonNull(txtEmail.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(checkEmail(editable.toString()))
                    txtEmail.setError(null);
                else
                    txtEmail.setError("please enter a valid email address");

            }
        });
    }
}

