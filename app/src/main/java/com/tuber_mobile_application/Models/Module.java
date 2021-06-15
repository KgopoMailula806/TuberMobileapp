package com.tuber_mobile_application.Models;

public class Module {
    private int id;
    private String module_Name;
    private String module_Code;
    private String module_Type;
    private Iterable<Tutor_Module>  tutor_Modules;
    private Iterable<Client_Module>  client_Modules;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getModule_Name() {
        return module_Name;
    }

    public void setModule_Name(String module_Name) {
        this.module_Name = module_Name;
    }

    public String getModule_Code() {
        return module_Code;
    }

    public void setModule_Code(String module_Code) {
        this.module_Code = module_Code;
    }

    public String getModule_Type() {
        return module_Type;
    }

    public void setModule_Type(String module_Type) {
        this.module_Type = module_Type;
    }

    public Iterable<Tutor_Module> getTutor_Modules() {
        return tutor_Modules;
    }

    public void setTutor_Modules(Iterable<Tutor_Module> tutor_Modules) {
        this.tutor_Modules = tutor_Modules;
    }

    public Iterable<Client_Module> getClient_Modules() {
        return client_Modules;
    }

    public void setClient_Modules(Iterable<Client_Module> client_Modules) {
        this.client_Modules = client_Modules;
    }
}
