package com.tuber_mobile_application.Models;

public class ClientBooking {
    private int id;
    private String date_Time;
    private int isActive;
    private int bookingDetails_BookingRequestTable_Reference;
    private int periods;
    private String endTime;
    private int tutor_Table_Reference;
    private int client_Table_Reference;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate_Time() {
        return date_Time;
    }

    public void setDate_Time(String date_Time) {
        this.date_Time = date_Time;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public int getBookingDetails_BookingRequestTable_Reference() {
        return bookingDetails_BookingRequestTable_Reference;
    }

    public void setBookingDetails_BookingRequestTable_Reference(int bookingDetails_BookingRequestTable_Reference) {
        this.bookingDetails_BookingRequestTable_Reference = bookingDetails_BookingRequestTable_Reference;
    }

    public int getPeriods() {
        return periods;
    }

    public void setPeriods(int periods) {
        this.periods = periods;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getTutor_Table_Reference() {
        return tutor_Table_Reference;
    }

    public void setTutor_Table_Reference(int tutor_Table_Reference) {
        this.tutor_Table_Reference = tutor_Table_Reference;
    }

    public int getClient_Table_Reference() {
        return client_Table_Reference;
    }

    public void setClient_Table_Reference(int client_Table_Reference) {
        this.client_Table_Reference = client_Table_Reference;
    }
}
