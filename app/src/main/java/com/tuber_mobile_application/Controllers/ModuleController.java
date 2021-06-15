package com.tuber_mobile_application.Controllers;

import com.tuber_mobile_application.Models.Client_Module;
import com.tuber_mobile_application.Models.Module;
import com.tuber_mobile_application.Models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ModuleController {
    /**
     *  only give the relative URL
     * @return - List of all the modules
     */
    @GET("/api/Modules/GetModules")
    Call<List<Module>> getAllModules();

    /**
     *
     * @param id - Unique Id of the user
     * @return - List of modules
     */
    @GET("/api/Modules/GetClientModulesByUserTableID/{id}")
    Call<List<Module>> GetClientModulesByUserTableID(@Path("id") int id);

    /**
     *
     * @param id - Unique Id of the user
     * @return - List of modules
     */
    @GET("/api/Modules/GetClientModulesByUserTableID/{id}")
    Call<List<Module>> GetClientModulesByUserTableID_Mobile(@Path("id") int id);

    /**
     *
     * @param id - Unique Id of the user
     * @return - List of modules
     */
    @GET("/api/Modules/GetTutorModulesByUserTableID/{id}")
    Call<List<Module>> GetTutorModulesByUserTableID(@Path("id") int id);
    /**
     *
     * @param id - Unique Id of the user
     * @return - List of modules
     */
    @GET("api/Modules/GetTutorModulesByUserTableID/{id}")
    Call<List<Module>> GetTutorModulesByUserTableID_Mobile(@Path("id") int id);

    @POST("api/Client_Module/AddNewClientModuleBridge")
    Call<Integer> addModule(@Body Client_Module client_module);

    @GET("api/Module/GetModuleName/{id}")
    Call<String> getModuleName(@Path("id") int id);

    /**
     *
     * @param id
     * @return
     */
    @GET("api/Modules/GetModules/{id}")
    Call<Module> GetModules(@Path("id") int id);

}
