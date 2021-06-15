package com.tuber_mobile_application.Controllers;

import com.tuber_mobile_application.Models.Client_Module;
import com.tuber_mobile_application.Models.Tutor_Module;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface Tutor_ModuleController {

    /**
     *
     * @param userID
     * @param userStatus
     * @param tutor_module
     * @return
     */
    @POST("api/Tutor_Module/AddNewTutorModuleBridge_CollisionChecking/{userID}/{userStatus}")
    Call<String> AddNewTutorModuleBridge_CollisionChecking(@Path("userID") int userID,@Path("userStatus") String userStatus,@Body Tutor_Module tutor_module);
    /**
     *
     * @param userID_ModuleID
     * @return
     */
    @GET("api/Tutor_Module/removeTutorModule/{userID_ModuleID}")
    Call<Integer> removeModule(@Path("userID_ModuleID") String userID_ModuleID);
}
