package dev.lld.practice.calendar;

import dev.lld.practice.calendar.dao.CalendarEventDAOImpl;
import dev.lld.practice.calendar.model.CalendarEvent;
import dev.lld.practice.calendar.service.CalendarServiceImpl;
import dev.lld.practice.calendar.service.EmailNotificationService;

import java.util.*;

/**
 * Demo code
 *
 */
public class App {

    static class UserActivity implements Runnable {

        private final String email;
        private final EmailNotificationService notificationService;
        private final CalendarServiceImpl calendarService;

        private Set<String> eventsProcessed = new HashSet<>();

        public UserActivity(String email,
                            EmailNotificationService notificationService,
                            CalendarServiceImpl calendarService) {
            this.email = email;
            this.notificationService = notificationService;
            this.calendarService = calendarService;
        }

        @Override
        public void run() {
            while (true) {
                List<String> events = notificationService.notifications.get(email);

                if (events != null) {
                    for (String event : events) {
                        if (!eventsProcessed.contains(event)) {
                            calendarService.acceptEvent(event, this.email);
                            eventsProcessed.add(event);
                        }
                    }
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

        }
    }

    public static void main( String[] args ) throws InterruptedException {
        EmailNotificationService notificationService = new EmailNotificationService();
        CalendarServiceImpl calendarService = new CalendarServiceImpl(
                new CalendarEventDAOImpl(), notificationService);

        Thread bob = new Thread(new UserActivity("bob@abc.com", notificationService, calendarService));
        bob.start();

        ArrayList<String> inviteeEmails = new ArrayList<>();
        inviteeEmails.add("bob@abc.com");
        String calendarEventId = calendarService.createCalendarEvent(new CalendarEvent(
                "Test Event", "For demo", "alice@abc.com", inviteeEmails, new Date(), new Date()
        ));



        Thread.sleep(2000);

        CalendarEvent calendarEvent = calendarService.getCalendarEvent(calendarEventId);

        System.out.println(calendarEvent.getTitle());
        System.out.println(calendarEvent.getDescription());
        System.out.println(calendarEvent.getUserToStatusMap().get("bob@abc.com"));

        System.exit(0);


    }
}
