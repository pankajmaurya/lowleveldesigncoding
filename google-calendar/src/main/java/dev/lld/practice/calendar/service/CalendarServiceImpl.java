package dev.lld.practice.calendar.service;

import dev.lld.practice.calendar.dao.CalendarEventDAO;
import dev.lld.practice.calendar.model.CalendarEvent;

/**
 * Created by gss on 10/08/25
 **/
public class CalendarServiceImpl implements CalendarService {
    private CalendarEventDAO calendarEventDAO;

    private NotificationService notificationService;

    public CalendarServiceImpl(CalendarEventDAO calendarEventDAO, NotificationService notificationService) {
        this.calendarEventDAO = calendarEventDAO;
        this.notificationService = notificationService;
    }

    @Override
    public String createCalendarEvent(CalendarEvent calendarEvent) {
        String eventId = calendarEventDAO.createCalendarEvent(calendarEvent);

        // send async notification
        sendAsycNotification(calendarEvent);

        return eventId;
    }

    private void sendAsycNotification(CalendarEvent calendarEvent){
        new Thread(()-> {
            for (String inviteeEmail : calendarEvent.getInviteeEmails()) {
                notificationService.sendNotification(calendarEvent.getSenderEmail(), inviteeEmail, calendarEvent.getTitle(),
                        calendarEvent.getDescription());
            }
        }).start();
    }

}
