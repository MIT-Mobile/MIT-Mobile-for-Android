package edu.mit.mitmobile2.events.adapters;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.events.model.MITCalendarEvent;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by serg on 4/30/15.
 */
public class CalendarAcademicAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private static final String DATE_HEADER_FORMAT = "EEEE, MMMM dd";

    private List<MITCalendarEvent> mitCalendarEvents;
    private SimpleDateFormat dateFormat;

    public CalendarAcademicAdapter(List<MITCalendarEvent> mitCalendarEvents) {
        this.mitCalendarEvents = mitCalendarEvents;
        dateFormat = new SimpleDateFormat(DATE_HEADER_FORMAT);
    }

    @Override
    public View getHeaderView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = new ViewHolder();

        if (view == null) {
            view = View.inflate(viewGroup.getContext(), R.layout.row_calendar_academic_header, null);

            viewHolder.headerTextView = (TextView) view.findViewById(R.id.event_header_title);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.headerTextView.setText(dateFormat.format(getItem(i).getStartDate()));

        return view;
    }

    @Override
    public long getHeaderId(int i) {
        Log.d("test", "position = " + i + "id = " + getItem(i).getStartDate().getTime());
        return getItem(i).getStartDate().getTime();
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
            convertView = View.inflate(parent.getContext(), R.layout.row_calendar_academic, null);

            viewHolder.eventTitleTextView = (TextView) convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        MITCalendarEvent event = getItem(position);

        viewHolder.eventTitleTextView.setText(event.getTitle());

        return convertView;
    }

    public List<MITCalendarEvent> getMitCalendarEvents() {
        return mitCalendarEvents;
    }

    public void setMitCalendarEvents(List<MITCalendarEvent> mitCalendarEvents) {
        this.mitCalendarEvents = mitCalendarEvents;
    }

    class ViewHolder {
        // header
        TextView headerTextView;

        // event
        TextView eventTitleTextView;
    }
}
