package com.tuber_mobile_application.helper.ui;

import com.tuber_mobile_application.Models.User;

public class SessionItem
{
    private int bookingId;
    private String moduleName;
    private String tutorName;
    private String periods;
    private String venue;
    private String venueAndCoordinates;
    private String date, time;
    private String endTime;
    private int bookingRequestID;
    private int otherUserID;
    private int otherUserUserTableID;
    private int moduleID;


    public SessionItem(String moduleName, String tutorName, String periods, String venue, String date, String time, String endTime)
    {
        this.date = date;
        this.moduleName = moduleName;
        this.time = time;
        this.tutorName = tutorName;
        this.periods = periods;
        this.venue = venue;
        this.endTime = endTime;
    }
    public SessionItem() {
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getTutorName() {
        return tutorName;
    }

    public void setTutorName(String tutorName) {
        this.tutorName = tutorName;
    }

    public String getPeriods() {
        return periods;
    }

    public void setPeriods(String periods) {
        this.periods = periods;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getBookingRequestID() {
        return bookingRequestID;
    }

    public void setBookingRequestID(int bookingRequestID) {
        this.bookingRequestID = bookingRequestID;
    }

    public int getOtherUserID() {
        return otherUserID;
    }

    public void setOtherUserID(int otherUserID) {
        this.otherUserID = otherUserID;
    }

    public int getModuleID() {
        return moduleID;
    }

    public void setModuleID(int moduleID) {
        this.moduleID = moduleID;
    }

    public int getOtherUserUserTableID() {
        return otherUserUserTableID;
    }

    public void setOtherUserUserTableID(int otherUserUserTableID) {
        this.otherUserUserTableID = otherUserUserTableID;
    }

    public String getVenueAndCoordinates() {
        return venueAndCoordinates;
    }

    public void setVenueAndCoordinates(String venueAndCoordinates) {
        this.venueAndCoordinates = venueAndCoordinates;
    }
}
