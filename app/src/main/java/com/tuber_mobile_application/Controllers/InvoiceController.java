package com.tuber_mobile_application.Controllers;

import com.tuber_mobile_application.Models.Invoice;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface InvoiceController {

    @POST("api/Invoice/recordNewInvoice")
    Call<Invoice> recordNewInvoice(@Body Invoice invoice);

    @GET("api/Invoice/GetUserInvoicesUnPaidInvoices/{id}")
    Call<List<Invoice>>  getUnpaidInvoices(@Path("id") int userID);

    @GET("api/Invoice/setInvoiceToPaid/{id}")
    Call<Integer>  changePaymentStatus(@Path("id") int userID);

}
