package com.tuber_mobile_application.helper.ui;

public class NotificationItem
{
    private int notificationID;
    private int eventID;
    private int requestID;
    private String notificationTitle;
    private String notificationBody;
    private String notificationTime;
    private String eventType;
    private String eventDescription;

    public NotificationItem(){

    }
    public NotificationItem(int nID,int eID,int rID, String eType, String eDesc, String nTitle, String nBody, String nTime){
        notificationID = nID;
        eventID = eID;
        requestID = rID;
        eventType = eType;
        notificationBody = nBody;
        notificationTitle = nTitle;
        notificationTime = nTime;
        eventDescription = eDesc;

    }

    public String getEventDescription() {
        return eventDescription;
    }

    public int getRequestID() {
        return requestID;
    }

    public int getEventID() {
        return eventID;
    }

    public String getEventType() {
        return eventType;
    }

    public int getNotificationID() {
        return notificationID;
    }

    public String getNotificationBody() {
        return notificationBody;
    }

    public String getNotificationTime() {
        return notificationTime;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationID(int notificationID) {
        this.notificationID = notificationID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public void setRequestID(int requestID) {
        this.requestID = requestID;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public void setNotificationBody(String notificationBody) {
        this.notificationBody = notificationBody;
    }

    public void setNotificationTime(String notificationTime) {
        this.notificationTime = notificationTime;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }
}
