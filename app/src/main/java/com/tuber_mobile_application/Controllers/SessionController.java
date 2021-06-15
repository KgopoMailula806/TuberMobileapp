package com.tuber_mobile_application.Controllers;

import com.tuber_mobile_application.Models.ClientBooking;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface SessionController {

    /**
     * Get the client booking that is shared by the Tutor
     * @param id -  client table reference
     * @return - list of active bookings listed to the client
     */
    @GET("api/ClientBooking/getAllActiveClientBookings/{id}")
    Call<List<ClientBooking>> getAllActiveClientBookings(@Path("id") int id);

    /**
     * Get the client booking that is shared by the client
     * @param id -  tutor table reference
     * @return - list of active bookings listed to the tutor
     */
    @GET("api/ClientBooking/getAllActiveTutorBookings/{id}")
    Call<List<ClientBooking>> getAllActiveTutorBookings(@Path("id") int id);

}
