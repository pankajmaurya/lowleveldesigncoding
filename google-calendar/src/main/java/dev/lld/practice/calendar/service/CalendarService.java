package dev.lld.practice.calendar.service;

import dev.lld.practice.calendar.model.CalendarEvent;

/**
 * Created by gss on 10/08/25
 **/
interface CalendarService {
    String createCalendarEvent(CalendarEvent calendarEvent);

    CalendarEvent getCalendarEvent(String eventId);
}
