package com.tuber_mobile_application.Models;

public class Tutorial_Session {

    private int Id;
    private String Session_Date ;
    private String Session_Start_Time ;
    private String Session_End_Time;
    private int Tutors_Client_Rating ;
    private int Clients_tutor_Rating;
    private String Tutors_Session_FeedBack;
    private String Clients_Session_FeedBack;
    private String Geographic_Location;
    private int IsCompleted ;
    private String Tutors_Paths;
    private String Clients_Paths;
    private String QRContent;
    //[ForeignKey("ClientBooking")]
    private int ClientBookingID;
    //[ForeignKey("Client")]
    private int Client_Reference;
    //[ForeignKey("Module")]
    private int Tutor_Id;


    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getSession_Date() {
        return Session_Date;
    }

    public void setSession_Date(String session_Date) {
        Session_Date = session_Date;
    }

    public String getSession_Start_Time() {
        return Session_Start_Time;
    }

    public void setSession_Start_Time(String session_Start_Time) {
        Session_Start_Time = session_Start_Time;
    }

    public String getSession_End_Time() {
        return Session_End_Time;
    }

    public void setSession_End_Time(String session_End_Time) {
        Session_End_Time = session_End_Time;
    }

    public int getTutors_Client_Rating() {
        return Tutors_Client_Rating;
    }

    public void setTutors_Client_Rating(int tutors_Client_Rating) {
        Tutors_Client_Rating = tutors_Client_Rating;
    }

    public int getClients_tutor_Rating() {
        return Clients_tutor_Rating;
    }

    public void setClients_tutor_Rating(int clients_tutor_Rating) {
        Clients_tutor_Rating = clients_tutor_Rating;
    }

    public String getTutors_Session_FeedBack() {
        return Tutors_Session_FeedBack;
    }

    public void setTutors_Session_FeedBack(String tutors_Session_FeedBack) {
        Tutors_Session_FeedBack = tutors_Session_FeedBack;
    }

    public String getClients_Session_FeedBack() {
        return Clients_Session_FeedBack;
    }

    public void setClients_Session_FeedBack(String clients_Session_FeedBack) {
        Clients_Session_FeedBack = clients_Session_FeedBack;
    }

    public String getGeographic_Location() {
        return Geographic_Location;
    }

    public void setGeographic_Location(String geographic_Location) {
        Geographic_Location = geographic_Location;
    }

    public int getIsCompleted() {
        return IsCompleted;
    }

    public void setIsCompleted(int isCompleted) {
        IsCompleted = isCompleted;
    }

    public String getTutors_Paths() {
        return Tutors_Paths;
    }

    public void setTutors_Paths(String tutors_Paths) {
        Tutors_Paths = tutors_Paths;
    }

    public String getClients_Paths() {
        return Clients_Paths;
    }

    public void setClients_Paths(String clients_Paths) {
        Clients_Paths = clients_Paths;
    }

    public int getClient_Reference() {
        return Client_Reference;
    }

    public void setClient_Reference(int client_Reference) {
        Client_Reference = client_Reference;
    }

    public int getTutor_Id() {
        return Tutor_Id;
    }

    public void setTutor_Id(int tutor_Id) {
        Tutor_Id = tutor_Id;
    }

    public int getClientBookingID() {
        return ClientBookingID;
    }

    public void setClientBookingID(int clientBookingID) {
        ClientBookingID = clientBookingID;
    }

    public String getQRContent() {
        return QRContent;
    }

    public void setQRContent(String QRContent) {
        this.QRContent = QRContent;
    }
}
