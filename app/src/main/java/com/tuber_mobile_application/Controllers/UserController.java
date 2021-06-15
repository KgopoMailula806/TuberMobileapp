package com.tuber_mobile_application.Controllers;

import com.tuber_mobile_application.Models.User;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserController {

    /**
     *  only give the relative URL
     * @return - List of users
     */
    @GET("api/User/GetUsers")
    Call<List<User>> getUsers();

    /**
     *
     * @param id - Unique Id of the user
     * @return - user details
     */
    @GET("api/User/GetUser/{id}")
    Call<User> getUserDetails(@Path("id") int id);

    /**
     *  relieves user table details be client table ID
     * @param clientID
     * @return
     */
    @GET("api/User/GetUserDeatilsByClientID/{clientID}")
    Call<User> getUserDetailsByClientID(@Path("clientID") int clientID);

    @GET("api/User/GetUserDetailsByTutorID/{tutorPK}")
    Call<User> getUserDetailsByTutorID(@Path("tutorPK") int tutorPK);

    /**
     * This method is for logging in
     * @param email - object that has the password and email
     * @param password - user password
     * @return - call that has the user response
     */
    @GET("api/User/Login/{email}/{password}")
    Call<User> Login(@Path("email") String email,@Path("password") String password);

    /**
     *
     * @param user - user table details
     * @return
     */
    @POST("api/User/AddUser")
    Call<User> registerUser(@Body User user);

    /**
     *
     * @param id - user table Id
     * @return
     */
    @GET("api/User/getCurrentUserStatus/{id}")
    Call<String> getCurrentUserStatus(@Path("id") int id);

    /**
     * changes the current User status
     * @param id - current User table Id
     * @param User_Discriminator - the desired user Discriminator
     * @return
     */
    @GET("api/User/changeCurrentUserStatus/{id}/{User_Discriminator}")
    Call<String> changeCurrentUserStatus(@Path("id") int id,@Path("User_Discriminator") String User_Discriminator);

    @GET("api/User/CheckIfEmailExists/{email}")
    Call<String> CheckIfEmailExists(@Path("email") String email);

    @POST("api/User/AddUser")
    Call<User> addUser(@Body User user);
}
