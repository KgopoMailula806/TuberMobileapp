package com.tuber_mobile_application.Models;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Currencies
{
    @SerializedName("base")
    @Expose
    private String base;
    @SerializedName("rates")
    @Expose
    private Rates rates;
    @SerializedName("date")
    @Expose
    private String date;

    public Rates getRates() {
        return rates;
    }

}
