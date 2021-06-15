package com.tuber_mobile_application.Controllers;

import com.tuber_mobile_application.Models.Document;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface DocumentController
{
    @POST("api/Document/UploadDocument")
    Call<Integer> UploadDocument(@Body Document doc);

    @GET("/api/Document/GetDocument/{id}")
    Call<Document> getDocument(@Path("id") int fileID);

}
