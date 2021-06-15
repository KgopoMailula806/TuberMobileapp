package com.tuber_mobile_application.Controllers;

import com.tuber_mobile_application.Models.Rating;
import com.tuber_mobile_application.Models.Tutorial_Session;
import com.tuber_mobile_application.Models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface Tutorial_SessionController {
    /**
     * Compulsory attributes apon creation:
     *  - session_Date
     *  - session_Start_Time
     *  - session_End_Time
     *  - geographic_Location
     *  - isCompleted: 0 (until the session is complete)
     *  - client_Reference
     *  - tutor_Id
     *
     * @param tutorial_session -
     * @return
     */
    @POST("api/TutorialSession/recordTutorialSession")
    Call<Integer> recordTutorialSession(@Body Tutorial_Session tutorial_session);

    /**
     *
     * @param rating
     * @return
     */
    @POST("api/TutorialSession/saveRating")
    Call<Rating> saveRating(@Body Rating rating);

    /**
     *
     * @return
     */
    @GET("api/TutorialSession/GetAllTutorSessions")
    Call<List<Tutorial_Session>> GetAllTutorSessions();

    /**
     *
     * @param clientBookingID
     * @return
     */
    @GET("api/TutorialSession/checkIfSessionExists/{clientBookingID}")
    Call<String> checkIfSessionExists(@Path("clientBookingID")int clientBookingID);

    /**
     *
     * @param TutorialSeesionID
     * @param clientBookingID
     * @return
     */
    @GET("api/TutorialSession/closeSession/{TutorialSeesionID}/{clientBookingID}")
    Call<String> closeSession(@Path("TutorialSeesionID")int TutorialSeesionID,@Path("clientBookingID")int clientBookingID);

    @GET("api/TutorialSession/getTutorial_SessionViaClientBookingID/{clientBookingID}")
    Call<List<Tutorial_Session>> getTutorial_SessionViaClientBookingID(@Path("clientBookingID")int clientBookingID);
}
