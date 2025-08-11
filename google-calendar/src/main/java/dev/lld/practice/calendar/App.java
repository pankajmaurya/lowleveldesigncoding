package dev.lld.practice.calendar;

import dev.lld.practice.calendar.dao.CalendarEventDAOImpl;
import dev.lld.practice.calendar.model.CalendarEvent;
import dev.lld.practice.calendar.service.CalendarServiceImpl;
import dev.lld.practice.calendar.service.EmailNotificationService;

import java.util.ArrayList;
import java.util.Date;

/**
 * Demo code
 *
 */
public class App {
    public static void main( String[] args ) {
        CalendarServiceImpl calendarService = new CalendarServiceImpl(
                new CalendarEventDAOImpl(), new EmailNotificationService());
        String calendarEventId = calendarService.createCalendarEvent(new CalendarEvent(
                "Test Event", "For demo", "foo@bar.com", new ArrayList<>(), new Date(), new Date()
        ));

        CalendarEvent calendarEvent = calendarService.getCalendarEvent(calendarEventId);

        System.out.println(calendarEvent.getTitle());
        System.out.println(calendarEvent.getDescription());


    }
}
