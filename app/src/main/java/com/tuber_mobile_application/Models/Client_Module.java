package com.tuber_mobile_application.Models;

import retrofit2.Call;

public class Client_Module {
    private int id;
    private String dateAssigned;
    private int is_Active;
    private Module module_Reference;
    private Client client_Reference;
    private int moduleId;
    private int clientRef;

    public Client_Module(int id, String dateAssigned, int is_Active, Module module_Reference, Client cClient_Reference, int moduleId, int clientRef) {
        this.id = id;
        this.dateAssigned = dateAssigned;
        this.is_Active = is_Active;
        this.module_Reference = module_Reference;
        this.client_Reference = cClient_Reference;
        this.moduleId = moduleId;
        this.clientRef = clientRef;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDateAssigned() {
        return dateAssigned;
    }

    public void setDateAssigned(String dateAssigned) {
        this.dateAssigned = dateAssigned;
    }

    public int getIs_Active() {
        return is_Active;
    }

    public void setIs_Active(int is_Active) {
        this.is_Active = is_Active;
    }

    public Module getModule_Reference() {
        return module_Reference;
    }

    public void setModule_Reference(Module module_Reference) {
        this.module_Reference = module_Reference;
    }

    public Client getcClient_Reference() {
        return client_Reference;
    }

    public void setcClient_Reference(Client cClient_Reference) {
        this.client_Reference = cClient_Reference;
    }

    public int getModuleId() {
        return moduleId;
    }

    public void setModuleId(int moduleId) {
        this.moduleId = moduleId;
    }

    public int getClientRef() {
        return clientRef;
    }

    public void setClientRef(int clientRef) {
        this.clientRef = clientRef;
    }

}
