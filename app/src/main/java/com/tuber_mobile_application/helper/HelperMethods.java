package com.tuber_mobile_application.helper;

import android.annotation.SuppressLint;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class HelperMethods {

    public static String[] separateString(String tokens, String character) {

        //tokenize string
        String[] parts = tokens.split(character);
        return parts;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getCurrentDateTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getCurrentDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getCurrentTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    @SuppressLint("SimpleDateFormat")
    public static Date parseDate(String givenDate)
    {
        String[] formats = {"MM/dd/yyyy hh:mm:ss", "dd/M/yyyy hh:mm:ss", "M/d/y hh:mm:ss", "dd/MMM/yy hh:mm:ss", "yyyy/MM/dd hh:mm:ss"};

        for (String dateFormat : formats) {
            try {
                return new SimpleDateFormat(dateFormat).parse( givenDate.replace('-','/'));
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }
}
