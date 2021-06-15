package com.tuber_mobile_application.Models;

public class Rating {

    private int id;
    private int tutor_ID;
    private int client_ID;
    private String comment;
    private double client_Rating;
    private double tutor_Rating;
    private int session_Reference;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTutor_ID() {
        return tutor_ID;
    }

    public void setTutor_ID(int tutor_ID) {
        this.tutor_ID = tutor_ID;
    }

    public int getClient_ID() {
        return client_ID;
    }

    public void setClient_ID(int client_ID) {
        this.client_ID = client_ID;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public double getClient_Rating() {
        return client_Rating;
    }

    public void setClient_Rating(double client_Rating) {
        this.client_Rating = client_Rating;
    }

    public double getTutor_Rating() {
        return tutor_Rating;
    }

    public void setTutor_Rating(double tutor_Rating) {
        this.tutor_Rating = tutor_Rating;
    }

    public int getSession_Reference() {
        return session_Reference;
    }

    public void setSession_Reference(int session_Reference) {
        this.session_Reference = session_Reference;
    }
}
