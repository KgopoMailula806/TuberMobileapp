package com.tuber_mobile_application.Models;

public class Tutor_Module {
    private int id;
    private String date_Assigned;
    private int is_Active;
    private int tutor_Reference;
    private int module_Reference;
    private Module module;
    private Tutor tutor;

    public Tutor_Module(int id, String date_Assigned, int is_Active, int tutor_Reference, int module_Reference, Module module, Tutor tutor) {
        this.id = id;
        this.date_Assigned = date_Assigned;
        this.is_Active = is_Active;
        this.tutor_Reference = tutor_Reference;
        this.module_Reference = module_Reference;
        this.module = module;
        this.tutor = tutor;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate_Assigned() {
        return date_Assigned;
    }

    public void setDate_Assigned(String date_Assigned) {
        this.date_Assigned = date_Assigned;
    }

    public int getIs_Active() {
        return is_Active;
    }

    public void setIs_Active(int is_Active) {
        this.is_Active = is_Active;
    }

    public int getTutor_Reference() {
        return tutor_Reference;
    }

    public void setTutor_Reference(int tutor_Reference) {
        this.tutor_Reference = tutor_Reference;
    }

    public int getModule_Reference() {
        return module_Reference;
    }

    public void setModule_Reference(int module_Reference) {
        this.module_Reference = module_Reference;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public Tutor getTutor() {
        return tutor;
    }

    public void setTutor(Tutor tutor) {
        this.tutor = tutor;
    }
}
