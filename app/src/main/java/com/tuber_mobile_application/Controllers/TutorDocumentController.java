package com.tuber_mobile_application.Controllers;

import com.tuber_mobile_application.Models.TutorDocument;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface TutorDocumentController
{
    @POST("/api/TutorDocument/UploadTutorDocument")
    Call<Integer> uploadTutorDocument(@Body TutorDocument tutorDocument);

}
