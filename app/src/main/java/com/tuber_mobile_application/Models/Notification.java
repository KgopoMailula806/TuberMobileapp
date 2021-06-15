package com.tuber_mobile_application.Models;

public class Notification {
    private int id;
    private int seen;
    private String datePosted;
    private String time;
    private int user_Table_Reference;
    private int event_Table_Reference;
    private String personTheNotificationConcerns;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSeen() {
        return seen;
    }

    public void setSeen(int seen) {
        this.seen = seen;
    }

    public String getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(String datePosted) {
        this.datePosted = datePosted;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getUser_Table_Reference() {
        return user_Table_Reference;
    }

    public void setUser_Table_Reference(int user_Table_Reference) {
        this.user_Table_Reference = user_Table_Reference;
    }

    public int getEvent_Table_Reference() {
        return event_Table_Reference;
    }

    public void setEvent_Table_Reference(int event_Table_Reference) {
        this.event_Table_Reference = event_Table_Reference;
    }

    public String getPersonTheNotificationConcerns() {
        return personTheNotificationConcerns;
    }

    public void setPersonTheNotificationConcerns(String personTheNotificationConcerns) {
        this.personTheNotificationConcerns = personTheNotificationConcerns;
    }
}
