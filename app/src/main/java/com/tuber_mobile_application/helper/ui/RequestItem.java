package com.tuber_mobile_application.helper.ui;

public class RequestItem
{
    private String name;
    private String number;
    private String date;
    private String startDate;
    private String endDate;
    private String location;
    private String periods;
    private String module;
    private String reason;

    public RequestItem(String name, String number, String date, String startDate, String endDate, String location, String periods, String module, String reason) {
        this.name = name;
        this.number = number;
        this.date = date;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.periods = periods;
        this.module = module;
        this.reason = reason;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPeriods() {
        return periods;
    }

    public void setPeriods(String periods) {
        this.periods = periods;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
