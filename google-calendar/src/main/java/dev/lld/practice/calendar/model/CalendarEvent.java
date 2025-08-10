package dev.lld.practice.calendar.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by gss on 10/08/25
 **/
public class CalendarEvent {
    private String eventId;

    private String title;

    private String description;

    private String senderEmail;

    private List<String> inviteeEmails;

    private Date fromTime;

    private Date toTime;

    private Map<String, Boolean> userToStatusMap;

    public CalendarEvent() {
    }

    public CalendarEvent(String title, String description, String senderEmail, List<String> inviteeEmails,
                         Date fromTime, Date toTime) {
        this.title = title;
        this.description = description;
        this.senderEmail = senderEmail;
        this.inviteeEmails = inviteeEmails;
        this.fromTime = fromTime;
        this.toTime = toTime;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public List<String> getInviteeEmails() {
        return inviteeEmails;
    }

    public void setInviteeEmails(List<String> inviteeEmails) {
        this.inviteeEmails = inviteeEmails;
    }

    public Date getFromTime() {
        return fromTime;
    }

    public void setFromTime(Date fromTime) {
        this.fromTime = fromTime;
    }

    public Date getToTime() {
        return toTime;
    }

    public void setToTime(Date toTime) {
        this.toTime = toTime;
    }

    public Map<String, Boolean> getUserToStatusMap() {
        return userToStatusMap;
    }

    public void setUserToStatusMap(Map<String, Boolean> userToStatusMap) {
        this.userToStatusMap = userToStatusMap;
    }
}
