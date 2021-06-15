package com.tuber_mobile_application.Controllers;

import com.tuber_mobile_application.Models.BookingRequest;
import com.tuber_mobile_application.Models.ClientBooking;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ClientBookingController {
    @GET("/api/ClientBooking/getAllActiveClientBookings/{id}")
    Call<List<ClientBooking>> getAllActiveClientBookings(@Path("id") int id);

    @GET("/api/ClientBooking/getBookingAt/{bookingID}")
    Call<ClientBooking> getBookingAt(@Path("bookingID") int bookingID);

    @GET("api/ClientBooking/closeBooking/{BookingrequestId}")
    Call<String> closeBooking(@Path("BookingrequestId") int BookingrequestId);

    @POST("api/ClientBooking/AddClientBooking")
    Call<String> AddClientBooking(@Body ClientBooking clientBooking);

    @GET("api/ClientBooking/deactivateBooking/{userID}/{userStatus}")
    Call<String> deactivateBooking(@Path("userID") int userID,@Path("userStatus") String userStatus);
}
