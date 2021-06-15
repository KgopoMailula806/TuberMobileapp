package com.tuber_mobile_application.Models;

public class Client {

    private int id;
    private String current_Grade;
    private String institution;
    private int user_Table_Reference;
    private Iterable<Client_Module> client_Modules;

    public Client(int id,String current_Grade, String institution, int user_Table_Reference, Client_Module client_Modules) {
        this.id = id;
        this.current_Grade = current_Grade;
        this.institution = institution;
        this.user_Table_Reference = user_Table_Reference;
        this.client_Modules = (Iterable<Client_Module>) client_Modules;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCurrent_Grade() {
        return current_Grade;
    }

    public void setCurrent_Grade(String current_Grade) {
        this.current_Grade = current_Grade;
    }
    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public int getUser_Table_Reference() {
        return user_Table_Reference;
    }

    public void setUser_Table_Reference(int user_Table_Reference) {
        this.user_Table_Reference = user_Table_Reference;
    }

    public Client_Module getClient_Modules() {
        return (Client_Module) client_Modules;
    }

    public void setClient_Modules(Client_Module client_Modules) {
        this.client_Modules = (Iterable<Client_Module>) client_Modules;
    }
}
