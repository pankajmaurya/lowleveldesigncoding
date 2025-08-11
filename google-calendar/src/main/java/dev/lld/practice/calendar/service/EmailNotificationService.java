package dev.lld.practice.calendar.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gss on 10/08/25
 **/
public class EmailNotificationService implements NotificationService {

    public Map<String, List<String>> notifications = new HashMap<>();

    @Override
    public void sendNotification(String from, String to, String title, String eventId, String body) {
        System.out.println(String.format("Sending email: from = %s , to = %s, title = %s, body = %s ",
                from, to, title, body));
        if (notifications.containsKey(to)) {
            notifications.get(to).add(eventId);
        } else {
            ArrayList<String> eventsList = new ArrayList<>();
            eventsList.add(eventId);
            notifications.put(to, eventsList);
        }


    }
}
