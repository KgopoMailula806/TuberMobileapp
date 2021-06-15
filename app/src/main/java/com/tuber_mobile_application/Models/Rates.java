package com.tuber_mobile_application.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Rates {

    @SerializedName("ZAR")
    @Expose
    private Double zAR;

    public Double getZAR() {
        return zAR;
    }

}
