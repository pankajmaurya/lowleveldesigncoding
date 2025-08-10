1. Users can create, update, delete events
2. Event can have auxiliary details like title, description etc
3. Users can accept or reject an event


## Design
- Classes: User, Event
- Event attributes: title, description, 
- User: email

## Sequence: Without Find time

title Create New Event (without find time)

Alice->GoogleCalendar:Propose new event (from, to, invitee list)
Alice<-GoogleCalendar:Status Code (conflicts with users availability)
Alice->GoogleCalendar:Create new event (from, to, invitee list)
Alice<-GoogleCalendar:Creation response with event-id.

GoogleCalendar->Bob: Event-Invite
GoogleCalendar<--Bob: Invite-Response
GoogleCalendar->Adam: Event-Invite
GoogleCalendar<--Adam: Invite-Response

Alice->GoogleCalendar:Get event (event-id)
Alice<-GoogleCalendar:event details response

Alice->GoogleCalendar:Update event (event-id)
Alice<-GoogleCalendar:event details response

## Sequence: With Find time
title Create New Event (with find time)

Alice->GoogleCalendar:Find time for event (invitee list, date)
Alice<-GoogleCalendar:busy slots for invitee list on date


Alice->GoogleCalendar:Create new event (from, to, invitee list)
Alice<-GoogleCalendar:Creation response with event-id.

GoogleCalendar->Bob: Event-Invite
GoogleCalendar<--Bob: Invite-Response
GoogleCalendar->Adam: Event-Invite
GoogleCalendar<--Adam: Invite-Response

Alice->GoogleCalendar:Get event (event-id)
Alice<-GoogleCalendar:event details response

Alice->GoogleCalendar:Update event (event-id)
Alice<-GoogleCalendar:event details response
