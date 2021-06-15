package com.tuber_mobile_application.Models;

public class Invoice {

    private int id;
    private int session_ID;
    private int client_ID;
    private String date_Issued;
    private String description;
    private String amount;
    private int is_Paid;
    private String Payment_Method;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSession_ID() {
        return session_ID;
    }

    public void setSession_ID(int session_ID) {
        this.session_ID = session_ID;
    }

    public int getClient_ID() {
        return client_ID;
    }

    public void setClient_ID(int client_ID) {
        this.client_ID = client_ID;
    }

    public String getDate_Issued() {
        return date_Issued;
    }

    public void setDate_Issued(String date_Issued) {
        this.date_Issued = date_Issued;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public int getIs_Paid() {
        return is_Paid;
    }

    public void setIs_Paid(int is_Paid) {
        this.is_Paid = is_Paid;
    }

    public String getPayment_Method() {
        return Payment_Method;
    }

    public void setPayment_Method(String payment_Method) {
        Payment_Method = payment_Method;
    }
}
