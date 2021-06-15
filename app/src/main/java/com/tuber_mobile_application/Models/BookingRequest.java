package com.tuber_mobile_application.Models;

public class BookingRequest {

    private int id;
    private String requestDate;
    private String requestTime;
    private int periods;
    private String endTime;
    private int is_Accepted;
    private int moduleID1;
    private int isRespondedTo;
    private String clientProposedLocation;
    private String tutorProposedLocation;
    private int tutor_Reference;
    private int client_Reference;

    public int getModuleID() {
        return moduleID1;
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

    public int getModuleID1() {
        return moduleID1;
    }

    public void setModuleID1(int moduleID1) {
        this.moduleID1 = moduleID1;
    }

    public BookingRequest() {
    }

    public void setModuleID(int moduleID) {
        this.moduleID1 = moduleID;
    }

    public BookingRequest(int id, String requestDate, String requestTime, int periods, String endTime, int is_Accepted, int moduleID1, int isRespondedTo, String clientProposedLocation, String tutorProposedLocation, int tutor_Reference, int client_Reference) {
        this.id = id;
        this.requestDate = requestDate;
        this.requestTime = requestTime;
        this.periods = periods;
        this.endTime = endTime;
        this.is_Accepted = is_Accepted;
        this.moduleID1 = moduleID1;
        this.isRespondedTo = isRespondedTo;
        this.clientProposedLocation = clientProposedLocation;
        this.tutorProposedLocation = tutorProposedLocation;
        this.tutor_Reference = tutor_Reference;
        this.client_Reference = client_Reference;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public int getIs_Accepted() {
        return is_Accepted;
    }

    public void setIs_Accepted(int is_Accepted) {
        this.is_Accepted = is_Accepted;
    }

    public int getIsRespondedTo() {
        return isRespondedTo;
    }

    public void setIsRespondedTo(int isRespondedTo) {
        this.isRespondedTo = isRespondedTo;
    }

    public String getClientProposedLocation() {
        return clientProposedLocation;
    }

    public void setClientProposedLocation(String clientProposedLocation) {
       this.clientProposedLocation = clientProposedLocation;
    }

    public String getTutorProposedLocation() {
        return tutorProposedLocation;
    }

    public void setTutorProposedLocation(String tutorProposedLocation) {
        this.tutorProposedLocation = tutorProposedLocation;
    }

    public int getTutor_Reference() {
        return tutor_Reference;
    }

    public void setTutor_Reference(int tutor_Reference) {
        this.tutor_Reference = tutor_Reference;
    }

    public int getClient_Reference() {
        return client_Reference;
    }

    public void setClient_Reference(int client_Reference) {
        this.client_Reference = client_Reference;
    }

}
