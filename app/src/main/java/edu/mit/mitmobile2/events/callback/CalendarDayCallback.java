package edu.mit.mitmobile2.events.callback;

import edu.mit.mitmobile2.events.model.MITCalendarEvent;

public interface CalendarDayCallback {
    void calendarDayDetail(MITCalendarEvent calendarEvent);
}
