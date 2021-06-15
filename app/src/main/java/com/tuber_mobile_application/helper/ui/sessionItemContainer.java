package com.tuber_mobile_application.helper.ui;

import java.util.ArrayList;
import java.util.List;

public class sessionItemContainer {

    ArrayList<SessionItem> sessions;

    public sessionItemContainer() {
        sessions = new ArrayList<>();
    }

    public ArrayList<SessionItem> getSessions() {
        return sessions;
    }

    public void setSessions(ArrayList<SessionItem> sessions) {
        this.sessions = sessions;
    }
}
