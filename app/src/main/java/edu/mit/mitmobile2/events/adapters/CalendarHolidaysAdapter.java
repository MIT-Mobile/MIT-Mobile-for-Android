package edu.mit.mitmobile2.events.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.events.model.MITCalendarEvent;

/**
 * Created by serg on 5/1/15.
 */
public class CalendarHolidaysAdapter extends BaseAdapter {

    private static final String DATE_EVENT_FORMAT = "MMMM dd";

    private List<MITCalendarEvent> mitCalendarEvents;
    private SimpleDateFormat dateFormat;

    public CalendarHolidaysAdapter(List<MITCalendarEvent> mitCalendarEvents) {
        this.mitCalendarEvents = mitCalendarEvents;
        dateFormat = new SimpleDateFormat(DATE_EVENT_FORMAT);
    }

    @Override
    public int getCount() {
        return mitCalendarEvents.size();
    }

    @Override
    public MITCalendarEvent getItem(int position) {
        return mitCalendarEvents.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();

        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.row_calendar_holidays, null);

            viewHolder.titleTextView = (TextView) convertView.findViewById(R.id.event_holidays_title);
            viewHolder.titleTextDate = (TextView) convertView.findViewById(R.id.event_holidays_date);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        MITCalendarEvent event = getItem(position);

        viewHolder.titleTextView.setText(event.getTitle());
        viewHolder.titleTextDate.setText(dateFormat.format(event.getStartDate()));

        return convertView;
    }

    class ViewHolder {
        TextView titleTextView;
        TextView titleTextDate;
    }
}
