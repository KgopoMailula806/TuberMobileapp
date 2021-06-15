package com.tuber_mobile_application;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.tuber_mobile_application.Controllers.DocumentController;
import com.tuber_mobile_application.Controllers.TutorController;
import com.tuber_mobile_application.Controllers.TutorDocumentController;
import com.tuber_mobile_application.Models.Document;
import com.tuber_mobile_application.Models.Tutor;
import com.tuber_mobile_application.Models.TutorDocument;
import com.tuber_mobile_application.helper.*;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tuber_mobile_application.Controllers.ClientController;
import com.tuber_mobile_application.Models.Client;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

import java.util.concurrent.atomic.AtomicReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.tuber_mobile_application.helper.FileManagement.makeBase64;

public class RegisterRemainingDetails extends AppCompatActivity
{
    private Intent intentFromRegistration;

    // controllers needed for the api communication
    private ClientController clientController;
    private TutorController tutorController;
    private DocumentController documentController;
    private TutorDocumentController tutorDocumentController;

    private boolean fieldsAreAllFilled = true;

    //ui variables, for displaying, paths
    private TextView policeClearance;
    private TextView CV;
    private TextView academicRecord;

    // document ID's
    private int cvID, pcID, acID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_remaining_details);

        setUpRetrofit(); //Set up retrofit communication hub

        //Get the type of user being registered
        intentFromRegistration = getIntent();

        if (intentFromRegistration != null) {
            // the user wishes to register as a client
            switch (intentFromRegistration.getStringExtra("UserType")) {
                case "Client": {
                    //make the registration fields that concern the client visible
                    EditText yearOfStudyEText = findViewById(R.id.txtYear);
                    EditText institution = findViewById(R.id.institution);
                    //By default its invisible
                    yearOfStudyEText.setVisibility(View.VISIBLE);
                    institution.setVisibility(View.VISIBLE);

                }
                break;
                case "Tutor": {
                    //make the registration fields that concern the client visible
                    CV = findViewById(R.id.txtCV);
                    CV.setVisibility(View.VISIBLE);

                    policeClearance = findViewById(R.id.txtPC);
                    policeClearance.setVisibility(View.VISIBLE);

                    academicRecord = findViewById(R.id.txtAC);
                    academicRecord.setVisibility(View.VISIBLE);
                }
                break;
            }
        }

    }

    private void moveToHomeActivity() {
        Intent intent = new Intent(RegisterRemainingDetails.this, Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void signUp(View view) {

        if (intentFromRegistration != null) {
            // the user wishes to register as a client
            switch (intentFromRegistration.getStringExtra("UserType")) {
                case "Client": {
                    TextView yearOfStudyTXT = findViewById(R.id.txtYear);
                    String yearOfStudy = yearOfStudyTXT.getText().toString();

                    if (!(yearOfStudy.length() > 0))
                        fieldsAreAllFilled = false;

                    TextView institutionTXT = findViewById(R.id.institution);
                    String institution = institutionTXT.getText().toString();
                    if (!(institution.length() > 0))
                        fieldsAreAllFilled = false;

                    // else the user wishes to register as a tutor
                }
                break;
                case "Tutor": {
                    if (policeClearance.getText().toString().equals("") ||
                            CV.getText().toString().equals("") || academicRecord.getText().toString().equals("")) {
                        fieldsAreAllFilled = false;
                    }
                }
                break;
            }
        }

        //Check if all of the fields are filled
        if (fieldsAreAllFilled) {
            //Invoke sign up method
            recordSecondaryUserDetails();
        }
        else {
            Toast.makeText(this, "Please Fill In All Required Fields!", Toast.LENGTH_SHORT).show();
        }

    }

    private void recordSecondaryUserDetails()
    {
        if (intentFromRegistration != null) {
            // the user wishes to register as a client
            switch (intentFromRegistration.getStringExtra("UserType")) {
                case "Client": {
                    //Register

                    TextView yearOfStudyTXT = findViewById(R.id.txtYear);
                    String yearOfStudy = yearOfStudyTXT.getText().toString();

                    TextView institutionTXT = findViewById(R.id.institution);
                    String institution = institutionTXT.getText().toString();

                    SessionManagement sessionManagement = new SessionManagement(RegisterRemainingDetails.this);
                    int userID = sessionManagement.getSession();

                    //if user is logged in move to Home
                    Client clientDetails = new Client(0, yearOfStudy, institution, userID, null);

                    Call<Client> call = clientController.recordUserClientDetails(clientDetails);
                    call.enqueue(new Callback<Client>() {
                        @Override
                        public void onResponse(@NotNull Call<Client> call, @NotNull Response<Client> response) {

                            if (!response.isSuccessful()) {

                                Toast.makeText(RegisterRemainingDetails.this, "Something went wrong, please try again later!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            moveToHomeActivity();
                        }

                        @Override
                        public void onFailure(@NotNull Call<Client> call, @NotNull Throwable t) {
                            t.fillInStackTrace();
                        }
                    });


                }
                break;
                case "Tutor": {

                    Tutor tutor = new Tutor(0, 0, RegistrationActivity.userID,null);
                    Call<Tutor> tutorCall = tutorController.addTutor(tutor);
                    tutorCall.enqueue(new Callback<Tutor>() {
                        @Override
                        public void onResponse(@NotNull Call<Tutor> call, @NotNull Response<Tutor> response) {
                            if (response.isSuccessful())
                            {
                                if(response.body() != null)
                                {
                                    int tutorId = response.body().getId();
                                    final TutorDocument pcDocument = new TutorDocument(0, "Police Clearance", pcID, tutorId);
                                    final TutorDocument acDocument = new TutorDocument(0, "Academic Record", acID, tutorId);
                                    final TutorDocument cvDocument = new TutorDocument(0, "Tutor CV", cvID, tutorId);

                                    Call<Integer> pc_call = tutorDocumentController.uploadTutorDocument(pcDocument);
                                    pc_call.enqueue(new Callback<Integer>()
                                    {
                                        @Override
                                        public void onResponse(@NotNull Call<Integer> call, @NotNull Response<Integer> response)
                                        {
                                            if(response.isSuccessful())
                                            {
                                                Call<Integer> ac_call = tutorDocumentController.uploadTutorDocument(acDocument);
                                                ac_call.enqueue(new Callback<Integer>() {
                                                    @Override
                                                    public void onResponse(@NotNull Call<Integer> call, @NotNull Response<Integer> ac_response)
                                                    {
                                                        if(ac_response.isSuccessful())
                                                        {
                                                            Call<Integer> cv_call = tutorDocumentController.uploadTutorDocument(cvDocument);
                                                            cv_call.enqueue(new Callback<Integer>() {
                                                                @Override
                                                                public void onResponse(@NotNull Call<Integer> call, @NotNull Response<Integer> pc_response)
                                                                {
                                                                    if(pc_response.isSuccessful())
                                                                    {
                                                                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterRemainingDetails.this);
                                                                        builder.setMessage("Your documents were upload successfully!")
                                                                                .setCancelable(true).show(); // success message
                                                                    }
                                                                }

                                                                @Override
                                                                public void onFailure(@NotNull Call<Integer> call, @NotNull Throwable t) {

                                                                }
                                                            });


                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(@NotNull Call<Integer> call, @NotNull Throwable t) {

                                                    }
                                                });
                                            }
                                        }
                                        @Override
                                        public void onFailure(@NotNull Call<Integer> call, @NotNull Throwable t) {

                                        }
                                    });
                                }
                                //moveToHomeActivity();
                                moveToWaitingArea();
                            }

                        }

                        @Override
                        public void onFailure(@NotNull Call<Tutor> call, @NotNull Throwable t) {

                        }
                    });
                }
                break;

            }
        }
    }

    private void moveToWaitingArea() {
        Intent intent = new Intent(RegisterRemainingDetails.this, WaitingAreaActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void setUpRetrofit()
    {
        AtomicReference<Retrofit> retrofit = new AtomicReference<>(new Retrofit.Builder()
                .baseUrl(App_Global_Variables.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build());

        clientController = retrofit.get().create(ClientController.class);
        tutorController = retrofit.get().create(TutorController.class);
        documentController = retrofit.get().create(DocumentController.class);
        tutorDocumentController = retrofit.get().create(TutorDocumentController.class);
    }

    public void ChooseCV(View view) {
        chooseFile("cv");
    }

    public void ChoosePoliceClearance(View view) {
        chooseFile("pc");
    }

    public void ChooseAcademicRecord(View view) {
        chooseFile("ac");
    }

    /**
     * @param requestCode
     * different code, for different files
     * @param resultCode
     * tells if the request was successful or not
     * @param data
     * data from the intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (FileManagement.isOurRequestCode(requestCode) && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();

                if(uri != null)
                {
                    @SuppressLint("Recycle")
                    Cursor cursor = getContentResolver().query(uri, null, null, null, null);

                    if(cursor != null)
                    {
                        try {
                            InputStream is = getContentResolver().openInputStream(uri);
                            String fileData = "";
                            if(is != null)
                                fileData = makeBase64(is);

                            int size = FileManagement.size(cursor);
                            cursor.moveToFirst();
                            final String name = FileManagement.getFileName(cursor);
                            cursor.moveToFirst();
                            String extension = FileManagement.getExtension(name);

                            Document doc = new Document();
                            doc.setDocumentData(fileData);
                            doc.setExtension(extension);
                            doc.setId(0);
                            doc.setSize(size);

                            switch (requestCode) {
                                case FileManagement.REQUEST_CODE_PC:
                                {
                                    policeClearance.setText(name); // setting the name of the file to the text view

                                    Call<Integer> call = documentController.UploadDocument(doc);
                                    call.enqueue(new Callback<Integer>() {
                                        @Override
                                        public void onResponse(@NotNull Call<Integer> call, @NotNull Response<Integer> response)
                                        {
                                            if(response.isSuccessful())
                                            {
                                                if(response.body() != null){
                                                    pcID = response.body();
                                                    Toast.makeText(RegisterRemainingDetails.this, name + " uploaded", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                        @Override
                                        public void onFailure(@NotNull Call<Integer> call, @NotNull Throwable t) {

                                        }
                                    });
                                }

                                break;

                                case FileManagement.REQUEST_CODE_AC: {

                                    academicRecord.setText(name);
                                    Call<Integer> call = documentController.UploadDocument(doc);
                                    call.enqueue(new Callback<Integer>() {
                                        @Override
                                        public void onResponse(@NotNull Call<Integer> call, @NotNull Response<Integer> response)
                                        {
                                            if(response.isSuccessful())
                                            {
                                                if(response.body() != null){
                                                    acID = response.body();
                                                    Toast.makeText(RegisterRemainingDetails.this, name + " uploaded", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                        @Override
                                        public void onFailure(@NotNull Call<Integer> call, @NotNull Throwable t) {

                                        }
                                    });
                                }
                                    break;

                                case FileManagement.REQUEST_CODE_CV: {

                                    CV.setText(name);

                                    Call<Integer> call = documentController.UploadDocument(doc);
                                    call.enqueue(new Callback<Integer>() {
                                        @Override
                                        public void onResponse(@NotNull Call<Integer> call, @NotNull Response<Integer> response)
                                        {
                                            if(response.isSuccessful())
                                            {
                                                if(response.body() != null){
                                                    cvID = response.body();
                                                    Toast.makeText(RegisterRemainingDetails.this, name + " uploaded", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                        @Override
                                        public void onFailure(@NotNull Call<Integer> call, @NotNull Throwable t) {

                                        }
                                    });
                                }
                                break;
                            }

                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }


    private void chooseFile(String doc) {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        switch (doc) {
            case "pc":
                startActivityForResult(intent, FileManagement.REQUEST_CODE_PC);
                break;
            case "cv":
                startActivityForResult(intent, FileManagement.REQUEST_CODE_CV);
                break;
            case "ac":
                startActivityForResult(intent, FileManagement.REQUEST_CODE_AC);
                break;

        }
    }
}
