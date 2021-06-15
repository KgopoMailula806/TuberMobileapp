package com.tuber_mobile_application.Controllers;

import com.tuber_mobile_application.Models.Client_Module;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface Client_ModuleController {

    /**
     *
     * @param userID
     * @param userStatus
     * @param client_module
     * @return
     */
    @POST("api/Client_Module/AddNewClientModuleBridge_CollisionChecking/{userID}/{userStatus}")
    Call<String> AddNewClientModuleBridge_CollisionChecking(@Path("userID") int userID,@Path("userStatus") String userStatus,@Body Client_Module client_module);

    /**
     *
     * @param userID_ModuleID
     * @return
     */
    @GET("api/Client_Module/removeModule/{userID_ModuleID}")
    Call<Integer> removeModule(@Path("userID_ModuleID") String userID_ModuleID);


}
