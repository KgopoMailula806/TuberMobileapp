package com.tuber_mobile_application.Models;

public class Tutor {

    private int id;
    private int is_Accepted;
    private int user_Table_Reference;
    private Iterable<Tutor_Module> tutor_Modules;

    public Tutor(int id, int is_Accepted, int user_Table_Reference, Iterable<Tutor_Module> tutor_Modules) {
        this.id = id;
        this.is_Accepted = is_Accepted;
        this.user_Table_Reference = user_Table_Reference;
        this.tutor_Modules = tutor_Modules;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIs_Accepted() {
        return is_Accepted;
    }

    public void setIs_Accepted(int is_Accepted) {
        this.is_Accepted = is_Accepted;
    }

    public int getUser_Table_Reference() {
        return user_Table_Reference;
    }

    public void setUser_Table_Reference(int user_Table_Reference) {
        this.user_Table_Reference = user_Table_Reference;
    }

    public Iterable<Tutor_Module> getTutor_Modules() {
        return tutor_Modules;
    }

    public void setTutor_Modules(Iterable<Tutor_Module> tutor_Modules) {
        this.tutor_Modules = tutor_Modules;
    }
}
