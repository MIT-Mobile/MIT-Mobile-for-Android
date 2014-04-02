package edu.mit.mitmobile2.shuttles;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.OnRefreshListener;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import edu.mit.mitmobile2.Global;
import edu.mit.mitmobile2.LoaderBar;
import edu.mit.mitmobile2.LockingScrollView;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SliderInterface;
import edu.mit.mitmobile2.alerts.NotificationsAlarmReceiver;
import edu.mit.mitmobile2.objs.Predicted;
import edu.mit.mitmobile2.objs.RouteItem;
import edu.mit.mitmobile2.objs.RouteItem.Stops;
import edu.mit.mitmobile2.shuttles.ShuttleRouteArrayAdapter.SectionListItemView;


public class StopsAsyncView  extends LinearLayout implements SliderInterface , OnItemClickListener, OnRefreshListener {

	ArrayList<Stops> m_stops;
	
	MITStopsSliderActivity top;
	
	CheckStopsTask stopsTask;
	
	TextView routeTV;
	ListView stopsLV;
	ShuttleRouteArrayAdapter adapter;

	HashMap <String,Predicted> alert_pis = new HashMap <String,Predicted>();
	 
	LoaderBar lb;

	Stops si;
	
	Context ctx;

	private PullToRefreshAttacher mRefreshAttacher;
	
	/****************************************************/
	class CheckStopsTask extends AsyncTask<String, Void, Void> {
		
		StopsParser sp;
		boolean firstTime = true;
		 
	    @Override
		protected Void doInBackground(String... urls) {

	    	String url = urls[0];

	    	while(true) {
	    	 
	    		 // Update stops...
	    		 sp = new StopsParser();
	    		 sp.getJSON(url,true);
	    		 
	    		 // Update bcos Notification service may have changed alerts
	    		 MITStopsSliderActivity.alertIdx = ShuttleModel.getAlerts(top.pref);
	    		 
	    		 if (isCancelled()) {
	    			 return null; //FIXME this should not be necessary but cancel() doesn't end this loop
	    		 }
	    		 
	    		 publishProgress ((Void)null);

	    		 // Sleep...
	    		 try {
	    			 Thread.sleep(1000*30);
	    		 } catch (InterruptedException e) {
	    			 e.printStackTrace();
	    		 }
		    	 
	    	}
	    	 
	    }

	    @SuppressWarnings("unchecked")
		@Override
		protected void onProgressUpdate(Void... values) {
			
			super.onProgressUpdate(values);
			
			lb.setLastLoaded(new Date());
			lb.endLoading();
			mRefreshAttacher.setRefreshComplete();
			 
			boolean no_data = false;
	    	if (sp==null) no_data = true;
	    	else if (sp.items.size() == 0) no_data = true;
	    	if (no_data) {
	    		if(m_stops.size() == 0) {
	    			Toast.makeText(ctx, MobileWebApi.NETWORK_ERROR, Toast.LENGTH_LONG).show();
	    			lb.errorLoading();
	    		}
	    		return;
	    	}
				
    		 m_stops = (ArrayList<Stops>) sp.items;
	    	 
	    	 if (!m_stops.isEmpty()) {

				 Stops s;
	    			
	    		 // Initialize...
	    		 if (firstTime) {
					 	
					 	SectionListItemView itemBuilder = new SectionListItemView() {
					 		
					 		@Override
							public View getView(Object item, View convertView, ViewGroup parent) {
					 			
					 			View v = convertView;
					 			if (v == null) {
					 				LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					 				v = vi.inflate(R.layout.stops_row, null);
					 			}
					 			
					 			Predicted s = (Predicted) item;
								
								// Time
								TextView timeTV = (TextView) v.findViewById(R.id.stopsRowTimeTV);
								timeTV.setTextColor(0xFF000000);

								long curTime = System.currentTimeMillis();
								
								Date d = new Date();
								d.setTime(s.next*1000);
								
								SimpleDateFormat df = new SimpleDateFormat("h:mm a");
								String formatted = df.format(d);
								timeTV.setText(formatted);

								////////////
								// Mins
								
								long mins = (s.next*1000 - curTime)/1000/60;
								long hours = mins / 60;
								String text = null;

								if (hours>1) {
									mins = mins - (hours*60);
									text = "(" + String.valueOf(hours) + " hrs " + String.valueOf(mins) + " mins)";
								} else if (hours>0) {
									mins = mins - (hours*60);
									text = "(" + String.valueOf(hours) + " hr " + String.valueOf(mins) + " mins)";
								} else {
									if (mins==0) text = "(now)";
									else if (mins==1) text = "(1 min)";
									else text = "(" + String.valueOf(mins) + " mins)";
								}
								
								TextView minsTV = (TextView) v.findViewById(R.id.stopsRowMinsTV);
								minsTV.setText(text);

								////////////
								// Alarm
								
								ImageView routeIV = (ImageView) v.findViewById(R.id.stopsRowIV);
								
								if (s.showAlert) {
								    routeIV.setVisibility(View.VISIBLE);
									if (s.alertSet) {
										routeIV.setImageResource(R.drawable.shuttle_alert_toggle_on);
									} else {
										routeIV.setImageResource(R.drawable.shuttle_alert_toggle_off);
									}
								} else {
									    routeIV.setVisibility(View.INVISIBLE);
								}
								
								return v;
								
							}
						};
					 	
			    		// TODO rename ShuttleRouteArrayAdapter to something more generic
						adapter = new ShuttleRouteArrayAdapter(ctx, itemBuilder);
	    			 
	    			 
	    			firstTime = false;
	    		}
	    		 
	    		adapter.clear();

			 	
				HashMap<String, ArrayList<Predicted>> sections = new HashMap<String, ArrayList<Predicted>>();

				 
			
				
	    		// Update
	    		Predicted pi;
	    		Predicted prev_pi;

				long curTime = System.currentTimeMillis();
				
	    		for (int x=0; x<m_stops.size(); x++) {
					s = m_stops.get(x);
	    			 
	   				if (!sections.containsKey(s.route_id)) {
	   					sections.put(s.route_id, new ArrayList<Predicted>());
	   				}
	   				ArrayList<Predicted> predictions = sections.get(s.route_id);
	    			 
		    		Log.d("StopsAsyncView",""+ s.toString());

    				
	    			// first stop...
    				pi = new Predicted();
    				pi.next = s.next;
    				pi.stop_id = s.id;
    				pi.route_id = s.route_id;
    				pi.showAlert = false;

    				if (s.next*1000>curTime+5*60*1000) {
    					pi.showAlert = true;	 
    				}

    				predictions.add(pi);

    				
    				 // is there an alarm for this route?
    				 HashMap<String, Long> routes_times = MITStopsSliderActivity.alertIdx.get(s.id);  // TODO move out of loop
    				 Long alert_time = null;
    				 if (routes_times==null) {
    					 Log.d("StopsAsyncView", "shuttle-alerts: routes_times null for "+s.id);
    				 } else {
    					 alert_time = routes_times.get(s.route_id);
    				 }
    				 boolean found_alert = false;
    				 if (alert_time==null) {
    					 alert_time = new Long(-1);
    					 found_alert = true;  // prevents marking "Alarm set"
    				 } else {
    					 
    					 // delete past alerts (being safe - should have been updated by NotificationService)
        				 if (System.currentTimeMillis() - ShuttleModel.ALERT_EXPIRE_TIME > alert_time) {
        					 routes_times.remove(s.route_id);
        					 if (routes_times.isEmpty()) MITStopsSliderActivity.alertIdx.remove(s.id);
        					 else MITStopsSliderActivity.alertIdx.put(s.id,routes_times);
        					 ShuttleModel.saveAlerts(top.pref, MITStopsSliderActivity.alertIdx);
        					 alert_time = new Long(-1);
        					 found_alert = true;
        				 }
        				 
    					 Log.d("StopsAsyncView", "shuttle-alert: update=> alert_time="+alert_time);
    				 }

    				 
    				// the rest...
    				prev_pi = pi;
    				Integer p;
	    			for (int z=0; z<s.predictions.size(); z++) {
				
	    				p = s.predictions.get(z);
	    				
	    				pi = new Predicted();
	    				pi.next = s.now + p.longValue();
	    				pi.stop_id = s.id;
	    				pi.route_id = s.route_id;
	    				pi.alertSet = false;
	    				pi.showAlert = true;
	    				predictions.add(pi);

	    				// Alert time passed?
	    				if (((pi.next*1000)>alert_time)&&(!found_alert)) {
	    					found_alert = true;
	    					prev_pi.alertSet = true;
	    					alert_pis.put(s.route_id, prev_pi);
	    				}
	    				prev_pi = pi;
	    			}
	    			if (!found_alert) {
	    				prev_pi.alertSet = true;
	    				alert_pis.put(s.route_id, prev_pi);
	    			}
	    			
	    		}	 // for stops

	    		
				
				// TODO move this somewhere less transient than this
				HashMap<String, String> routeTitles = new HashMap<String, String>();
				for (RouteItem aRouteItem : ShuttleModel.getSortedRoutes()) {
					routeTitles.put(aRouteItem.route_id, aRouteItem.title);
				}
	    		 
				
				// Add current route first
				String routeTitle;
				ArrayList<Predicted> c = sections.get(top.routeId);
				if (c!=null) {
					routeTitle = routeTitles.get(top.routeId);
					if (routeTitle == null) routeTitle = top.routeId;
					sections.remove(top.routeId);
					adapter.addSection(routeTitle, c);
				}

				// Now add remainder...
				for (Entry<String, ArrayList<Predicted>> entry: sections.entrySet()) {
	    			routeTitle = routeTitles.get(entry.getKey());
					if (routeTitle == null) routeTitle = entry.getKey();
	    			adapter.addSection(routeTitle, entry.getValue());
	    		}
	    		
				
				stopsLV.setOnItemClickListener(StopsAsyncView.this);
				stopsLV.setAdapter(adapter);
	    		 
	    	}  // isEmpty
		    	
	    }  // progressUpdate
	
	}  // class CheckStopsTask 
	
	/****************************************************/
	void terminate() {
		
		if (stopsTask!=null) {
			boolean isCanceled;
			isCanceled = stopsTask.cancel(true);
			while (!isCanceled) {
				 // Sleep...
				 try {
					 Thread.sleep(1000*10);
				 } catch (InterruptedException e) {
					 e.printStackTrace();
				 }
				isCanceled = stopsTask.cancel(true);
			}
			stopsTask = null;
		}
		
	}
	
	/**
	 * @param stops **************************************************/

	public StopsAsyncView(Context context, Stops stops) {
		
		super(context);

		ctx = context;
		si = stops;
		
		top = (MITStopsSliderActivity) context;
		
		LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		LinearLayout topView = (LinearLayout) vi.inflate(R.layout.stops, null);

		stopsLV = (ListView) topView.findViewById(R.id.stopsLV);
		TextView titleTV = (TextView) topView.findViewById(R.id.stopsTitleTV);
		titleTV.setText(si.title);

		// FIXME this will break on screen rotation
		// FIXME HACK!!! neither FILL not layout_weight=1 with WRAP work 
        Display display = top.getWindowManager().getDefaultDisplay(); 
        int height = display.getHeight();
        topView.setMinimumHeight(height-30);
        
		lb = new LoaderBar(ctx);
		topView.addView(lb, 0);
		
		addView(topView);
		
		mRefreshAttacher = top.createPullToRefreshAttacher();
		mRefreshAttacher.setRefreshableView(stopsLV, this);

	}
	/****************************************************/
	void getData() {
		
		m_stops = new ArrayList<Stops>();

		if (stopsTask!=null) {
			if (!stopsTask.isCancelled()) {
				stopsTask.cancel(true);
				//throw new RuntimeException("should have been canceled");
			}
		}
		
		
		stopsTask = new CheckStopsTask();
		RoutesParser rp = new RoutesParser();
		stopsTask.execute(rp.getBaseUrl()+"?command=stopInfo&id="+si.id, null, null);
		
	}

	
	/****************************************************/

	@Override
	 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		
		ListView l = (ListView) parent;

		//ShuttleRouteArrayAdapter sa = (ShuttleRouteArrayAdapter) l.getAdapter();
		
		Predicted p = (Predicted) l.getItemAtPosition(position);
		
		
		Predicted alert_pi = alert_pis.get(p.route_id);
		

		long curTime = System.currentTimeMillis();
		
		if ((p.next*1000)<(curTime+5*60*1000)) return;
			 
		
		AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Service.ALARM_SERVICE);

		
		// Three cases:
		//
		// - setting new, no previous
		// - setting new, clear different previous
		// - clearing previous
		
		
		// Is there an existing alert?
		HashMap<String,Long> routes_times = MITStopsSliderActivity.alertIdx.get(si.id);
		Long alertTime = null;
		if (routes_times==null) {
			routes_times = new HashMap<String,Long>();
			MITStopsSliderActivity.alertIdx.put(si.id, routes_times);
		} else {
			alertTime = routes_times.get(p.route_id);
		}

		 
		if (alertTime!=null) {
			
			// Yes - remove previous alert 
			routes_times.remove(p.route_id);
			if (routes_times.isEmpty()) {
				MITStopsSliderActivity.alertIdx.remove(si.id);
			}
			
			if (alert_pi!=null) {
				// Cancel (needs to match previous one)
				Intent i = new Intent(ctx, NotificationsAlarmReceiver.class);
				i.setAction(NotificationsAlarmReceiver.ACTION_ALARM_SHUTTLE);
				Uri data = Uri.parse(alert_pi.stop_id + alert_pi.route_id);
				i.setData(data);
				PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 0, i, 0);
				alarmManager.cancel(pendingIntent);

	    		Log.d("StopsAsyncView", "shuttle-alerts: cancel: " + alert_pi.stop_id + " " + alert_pi.route_id);
	    		
				if (p!=alert_pi) alert_pi.alertSet = false;
				alert_pi = null;
				alert_pis.put(p.route_id, null);
			}
			
		}
		
		//
		// >>>>>> Alert times holds EXACT alert times while we schedule Alarms slightly in advance of this
		//
		
	
		// Cancel or Schedule alarm?
		if (p.alertSet) {
			/*
			alert_pi = null;
			
			// Cancel (needs to match previous one)
			Intent i = new Intent(ctx, NotificationsAlarmReceiver.class);
			i.setAction(NotificationsAlarmReceiver.ACTION_ALARM_SHUTTLE);
			Uri data = Uri.parse(p.stop_id + p.route_id);
			i.setData(data);
			
			PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 0, i, 0);
			alarmManager.cancel(pendingIntent);
			*/
		} else {

			// Schedule new alert

			alert_pi = p;
			alert_pis.put(p.route_id, p);
			
			long wakeTime;
			long busTime = p.next*1000;  
			long diff = busTime - curTime;

			// add to map
			alertTime = new Long(busTime);
			routes_times.put(p.route_id, alertTime);
			MITStopsSliderActivity.alertIdx.put(si.id,routes_times);
			
			// set wakeup time
			if (diff<5*60*1000) {
				// ignore if under 5 mins...
				return;
			} else if (diff<30*60*1000) {
				wakeTime = busTime - 5*60*1000;  // wake up 5 mins before...
			} else {
				wakeTime = busTime - 20*60*1000;  // wake up 20 mins before...
			}
			
			if (Global.DEBUG) {
				wakeTime = curTime + 30*1000;  // TODO DEBUG
				//busTime = curTime + 30*1000;
				busTime = curTime + 7*60*1000;
			}
			
			String title = si.title;
		 	if (title==null) {
		 		title = "unknown stop";
		 	}
		 	
		 	
		 	// FIXME need to differentiate alarms

    		Log.d("StopsAsyncView", "shuttle-alerts: set: " + alert_pi.stop_id + " @ " + alert_pi.route_id);
    		
			Intent i = new Intent(ctx, NotificationsAlarmReceiver.class);
			i.setAction(NotificationsAlarmReceiver.ACTION_ALARM_SHUTTLE);
			
			Uri data = Uri.parse(p.stop_id + p.route_id);
			i.setData(data);

			i.putExtra(ShuttleModel.KEY_STOP_TITLE, title);
			i.putExtra(ShuttleModel.KEY_STOP_ID, p.stop_id);
			i.putExtra(ShuttleModel.KEY_ROUTE_ID, p.route_id);
			i.putExtra(ShuttleModel.KEY_TIME, busTime);
			
			PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 0, i, 0);
			alarmManager.set(AlarmManager.RTC_WAKEUP,  wakeTime, pendingIntent);
			
		}

		p.alertSet = !p.alertSet;
		
		//l.invalidate();
		l.postInvalidate();
		
		/*
		// TODO checks
		int count = l.getCount();
		int num_set = 0;
		for (int i=0; i<count; i++) {
			p = (Predicted) l.getItemAtPosition(i);
			if (p.alertSet) num_set++;
			if (num_set>1) Log.e("StopsAsyncView", "StopsAsyncView: too many alerts i=" + i);
		}
		*/
		ShuttleModel.saveAlerts(top.pref,MITStopsSliderActivity.alertIdx);  // TODO better?

		adapter.notifyDataSetChanged();
		//sa.notifyDataSetChanged();
		
	}

	@Override
	public View getView() {
		return this;
	}

	@Override
	public void updateView() {
		//if (!updateThreadRunning) getData();
	}
	
	@Override
	public void onSelected() {
		if (stopsTask==null) {
			lb.startLoading();
			getData();
		}
	}

	@Override
	public LockingScrollView getVerticalScrollView() {
		return null;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRefreshStarted(View view) {
		getData();
	}
}
