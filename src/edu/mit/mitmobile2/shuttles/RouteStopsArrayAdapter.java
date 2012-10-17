package edu.mit.mitmobile2.shuttles;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;


import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.objs.RouteItem.Stops;
import edu.mit.mitmobile2.shuttles.ShuttlesStatusView.Position;
import edu.mit.mitmobile2.shuttles.ShuttlesStatusView.ShuttleStatus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RouteStopsArrayAdapter extends ArrayAdapter<Stops> {

	Context ctx;
	List<Stops> mStops;
	
	//int nearest = 0;	
	
	public RouteStopsArrayAdapter(Context context, int resource, int textViewResourceId, List<Stops> stops) {
		super(context, resource, textViewResourceId, stops);
		this.ctx = context;
		mStops = stops;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.routes_row, null);
		}
		
		Stops s =  (Stops) getItem(position);
		
		if (s != null) {
			
			TextView stopTV = (TextView) v.findViewById(R.id.routesRowStopTV);
			stopTV.setText(s.title);

			////////////
			
			TextView nextTV = (TextView) v.findViewById(R.id.routesRowNextTV);
			
			Date d = new Date();
			//d.setTime(s.next);
			d.setTime(s.next*1000);
			
			SimpleDateFormat df = new SimpleDateFormat("h:mm a");
			//SimpleDateFormat df = (SimpleDateFormat) SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT);
			String formatted = df.format(d);
			nextTV.setText(formatted);

			////////////
			
			ShuttlesStatusView statusView = (ShuttlesStatusView) v.findViewById(R.id.routesRowStatus);
			//routeIV.setImageResource(R.drawable.shuttle_stop_dot);
			
			Position shuttlePosition = Position.BETWEEN;	
			// first stop
			if (position == 0) {
				shuttlePosition = Position.START;
			}
			// last stop
			if (position == (mStops.size()-1)) {
				shuttlePosition = Position.END;
			}
			statusView.setPosition(shuttlePosition);
			
			
			if (s.upcoming) {
				statusView.setStatus(ShuttleStatus.ON);
				nextTV.setTextAppearance(ctx, R.style.BoldRed);
			} else {
				statusView.setStatus(ShuttleStatus.OFF);
				nextTV.setTextAppearance(ctx, R.style.ListValue);
			}
			
		}
		
		
		return v;
	}

}
