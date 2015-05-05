package edu.mit.mitmobile2.events.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.events.model.MITCalendarEvent;
import edu.mit.mitmobile2.events.model.MITCalendarLocation;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class CalendarSearchEventAdapter extends CalendarEventAdapter implements StickyListHeadersAdapter {

    private static final String DATE_HEADER_FORMAT = "EEEE, MMMM dd";

    private SimpleDateFormat dateFormat;

    public CalendarSearchEventAdapter(Context context, List<MITCalendarEvent> events) {
        super(context, events);
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

        MITCalendarEvent event = (MITCalendarEvent) getItem(i);

        viewHolder.headerTextView.setText(dateFormat.format(event.getStartDate()));

        return view;
    }

    @Override
    public long getHeaderId(int i) {
        MITCalendarEvent event = (MITCalendarEvent) getItem(i);
        return event.getStartDate().getTime();
    }

    class ViewHolder {
        // header
        TextView headerTextView;
    }
}
