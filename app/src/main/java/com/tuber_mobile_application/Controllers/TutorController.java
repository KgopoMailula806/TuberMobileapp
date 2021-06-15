package com.tuber_mobile_application.Controllers;

import com.tuber_mobile_application.Models.Tutor;
import com.tuber_mobile_application.Models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface TutorController {


    @GET("api/Tutor/GetTutorTablePrimaryKey/{id}")
    Call<String> GetTutorTablePrimaryKey(@Path("id") int id);

    @POST("api/Tutor/AddTutor")
    Call<Tutor> addTutor(@Body Tutor tutor);

    @PUT("api/Tutor/UpdateDetails/{id}")
    Call<Tutor> updateTutorDetails(@Path("id") int id, @Body Tutor tutor);
  
    @GET("api/Tutor/GetTutorId/{id}")
    Call<String> getTutorTableID(@Path("id") int id);

    @GET("/api/Tutor/GetUserTableIdByPK/{id}")
    Call<String> GetUserTableIdByPK(@Path("id") int id);

    @GET("api/Tutor/GetTutorsByTheRespectiveModuleTheyTutor/{module}")
    Call<List<User>> GetTutorsByTheRespectiveModuleTheyTutor(@Path("module") String module);

    /**
     *
     * @param id - user table Id
     * @return
     */
    @GET("api/Tutor/doesTutorExists/{id}")
    Call<String> doesTutorExists(@Path("id") int id);

    /**
     *
     * @param id
     * @return
     */
    @GET("api/Tutor/GetUserTableDetails/{id}")
    Call<User> GetUserTableDetails(@Path("id") int id);

    @GET("api/Tutor/GetUserTableID/{TutorTablePK}")
    Call<String> GetUserTableID(@Path("TutorTablePK") int TutorTablePK);

}
