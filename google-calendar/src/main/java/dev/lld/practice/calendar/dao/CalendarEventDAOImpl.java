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
        // Not performant
        // https://claude.ai/chat/6fa7f3a8-292a-43a9-8ae5-3f449460c0a5
        if (!eventMap.containsValue(calendarEvent)) {
            eventMap.putIfAbsent(eventId, calendarEvent);
            return eventId;
        } else {
            return eventMap.entrySet().stream().filter(
                    (entry -> entry.getValue().equals(calendarEvent)))
                    .findFirst().orElseThrow().getKey();
        }
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
