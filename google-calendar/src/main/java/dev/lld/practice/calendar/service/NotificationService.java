package dev.lld.practice.calendar.service;

/**
 * Created by gss on 10/08/25
 **/
public interface NotificationService {
    void sendNotification(String from, String to, String title, String eventId, String body);
}
