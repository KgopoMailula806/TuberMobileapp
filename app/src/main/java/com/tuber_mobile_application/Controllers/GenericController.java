package com.tuber_mobile_application.Controllers;

import com.tuber_mobile_application.Models.Currencies;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GenericController
{
    @GET("/api/latest?base=USD&symbols=ZAR")
    Call<Currencies> getCurrencies();

}
