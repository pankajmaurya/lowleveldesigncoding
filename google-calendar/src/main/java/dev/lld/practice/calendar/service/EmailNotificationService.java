package dev.lld.practice.calendar.service;

/**
 * Created by gss on 10/08/25
 **/
public class EmailNotificationService implements NotificationService {
    @Override
    public void sendNotification(String from, String to, String title, String body) {
        System.out.println(String.format("Sending email: from = %s , to = %s, title = %s, body = %s ",
                from, to, title, body));

    }
}
