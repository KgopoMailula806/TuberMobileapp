package com.tuber_mobile_application.helper;

import com.tuber_mobile_application.Models.Event;
import com.tuber_mobile_application.Models.Notification;

public class EventNotificationPair
{
    private Notification notification;
    private Event event;

    public EventNotificationPair(Notification notification, Event event) {
        this.notification = notification;
        this.event = event;
    }

    public Notification getNotification() {
        return notification;
    }

    public Event getEvent() {
        return event;
    }
}
