package dev.lld.practice.calendar.dao;

import dev.lld.practice.calendar.model.CalendarEvent;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by gss on 10/08/25
 **/
public class CalendarEventDAOImpl implements CalendarEventDAO {

    private ConcurrentHashMap<String, CalendarEvent> eventMap = new ConcurrentHashMap<>();
    @Override
    public String createCalendarEvent(CalendarEvent calendarEvent) {
        String eventId = UUID.randomUUID().toString();
        eventMap.put(eventId, calendarEvent);
        return eventId;
    }

    @Override
    public CalendarEvent getCalendarEvent(String eventId) {
        return eventMap.get(eventId);
    }

    @Override
    public void updateCalendarEvent(CalendarEvent calendarEvent) {
        // also need to handle aspects of notification etc.
        eventMap.replace(calendarEvent.getEventId(), calendarEvent);
    }

    @Override
    public void deleteCalendarEvent(String eventId) {
        eventMap.remove(eventId);
    }
}
