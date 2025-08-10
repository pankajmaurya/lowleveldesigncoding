package dev.lld.practice.calendar.dao;

import dev.lld.practice.calendar.model.CalendarEvent;

/**
 * Created by gss on 10/08/25
 **/
public interface CalendarEventDAO {

    String createCalendarEvent(CalendarEvent calendarEvent);

    CalendarEvent getCalendarEvent(String eventId);

    void updateCalendarEvent(CalendarEvent calendarEvent);

    void deleteCalendarEvent(String eventId);
}
