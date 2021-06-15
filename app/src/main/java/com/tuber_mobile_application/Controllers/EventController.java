package com.tuber_mobile_application.Controllers;

import com.tuber_mobile_application.Models.Event;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface EventController
{
    @GET("api/Event/GetEvent/{id}")
    Call<Event> GetEvent(@Path("id") int eventID);

    /**
     *
     * @param event - that describes the notification
     * @return - the either the primary key of the notification or 0
     */
    @POST("api/Event/pushEvent")
    Call<Integer> pushEvent(@Body Event event);

}
