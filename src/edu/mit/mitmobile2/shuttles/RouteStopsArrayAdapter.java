package edu.mit.mitmobile2.shuttles;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.objs.RouteItem.Stops;

public class RouteStopsArrayAdapter extends ArrayAdapter<Stops> {

	Context ctx;
	
	//int nearest = 0;	
	
	public RouteStopsArrayAdapter(Context context, int resource, int textViewResourceId, List<Stops> stops) {
		super(context, resource, textViewResourceId, stops);
		this.ctx = context;
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
			
			ImageView routeIV = (ImageView) v.findViewById(R.id.routesRowIV);
			routeIV.setImageResource(s.upcoming ? R.drawable.shuttle_stop_dot_next : R.drawable.shuttle_stop_dot);
			nextTV.setTextAppearance(ctx, s.upcoming ? R.style.BoldRed : R.style.ListValue);
		}
		
		
		return v;
	}

}
