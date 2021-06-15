package com.tuber_mobile_application.Controllers;

import com.tuber_mobile_application.Models.BookingRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface BookingController {

    /**
     *
     * @param bookReq
     * @return
     */
    @POST("api/BookingRequest/AddBookingRequest")
    Call<BookingRequest> AddBookingRequest(@Body BookingRequest bookReq);

    @GET("api/BookingRequest/GetIndividualBookingRequest/{id}")
    Call<BookingRequest> getRequest(@Path("id") int id);
    /**
     *
     * @param BookingrequestId
     * @return
     */
    @GET("api/BookingRequest/GetBookingrequestLocation/{BookingrequestId}")
    Call<List<String>> GetBookingrequestLocation(@Path("BookingrequestId") int BookingrequestId);

    /**
     *
     * @param BookingrequestId
     * @return
     */
    @GET("api/BookingRequest/GetIndividualBookingRequest/{BookingrequestId}")
    Call<BookingRequest> GetIndividualBookingRequest(@Path("BookingrequestId") int BookingrequestId);

    /**
     *
     * @param bookingRequestID -
     * @return
     */
    @GET("api/BookingRequest/changeBookingRequestAcceptedStatus/{bookingRequestID}/{code}")
    Call<String> changeBookingRequestAcceptedStatus(@Path("bookingRequestID") int bookingRequestID,@Path("code") int code);




}
