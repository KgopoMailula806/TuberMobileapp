package com.tuber_mobile_application.Controllers;

import com.tuber_mobile_application.Models.Client;
import com.tuber_mobile_application.Models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ClientController {

    /**
     *
     * @param user
     * @return
     */
    @POST("api/Client/AddClient")
    Call<Client> recordUserClientDetails(@Body Client user);

    /**
     *
     * @param id
     * @return
     */
    @GET("api/Client/GetClientTableID/{id}")
    Call<String> getClientTableID(@Path("id") int id);

    /**
     *
     * @param id
     * @return
     */
    @GET("api/Client/GetClientTableID_/{id}")
    Call<String> getClientTableID_(@Path("id") int id);

    /**
     *
     * @param id - user table Id
     * @return
     */
    @GET("api/Client/doesClientExists/{id}")
    Call<String> doesClientExists(@Path("id") int id);

    /**
     *
     * @param id
     * @return
     */
    @GET("api/Client/GetUserTableDetails/{id}")
    Call<List<User>> GetUserTableDetails(@Path("id") int id);

    @GET("api/Client/GetClientByForeignKey/{id}")
    Call<Integer> getUserClientID(@Path("id") int id);

    @GET("api/Client/GetClientByForeignKey/{id}")
    Call<Client> getClient(@Path("id") int id);

    @GET("api/Client/GetUserTableDetails_Mobile/{id}")
    Call<User> GetUserTableDetails_Mobile(@Path("id") int id);

    @GET("api/Client/GetUserTableID/{id}")
    Call<String> GetUserTableID(@Path("id") int id);
}