package edu.mit.mitmobile2.events;

import java.util.List;


import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.objs.EventDetailsItem;
import edu.mit.mitmobile2.objs.EventDetailsItem.TimeSummaryMode;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class EventsArrayAdapter extends ArrayAdapter<EventDetailsItem> {

	Context ctx;
	
	TimeSummaryMode mTimeSummaryMode;
	
	public EventsArrayAdapter(Context context, int resource, int textViewResourceId, List<EventDetailsItem> events, TimeSummaryMode summaryMode) {
		super(context, resource, textViewResourceId,events);
		this.ctx = context;
		mTimeSummaryMode = summaryMode;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.events_row, null);
		}
		
		
		EventDetailsItem ei = (EventDetailsItem) getItem(position);
		
		if (ei != null) {
			TextView titleTV = (TextView) v.findViewById(R.id.eventTitleTV);
			TextView dateTV = (TextView) v.findViewById(R.id.eventDateTV);
			TextView locTV = (TextView) v.findViewById(R.id.eventLocTV);			
			
			titleTV.setText(ei.title);			
			dateTV.setText(ei.getTimeSummary(mTimeSummaryMode));		
			locTV.setText(ei.shortloc);
			
		}
		
		return v;
	}

}
