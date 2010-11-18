package edu.mit.mitmobile.maps;

import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.Overlay;
import edu.mit.mitmobile.MobileWebApi;
import edu.mit.mitmobile.R;
import edu.mit.mitmobile.objs.MapItem;
import edu.mit.mitmobile.objs.RouteItem;
import edu.mit.mitmobile.objs.RouteItem.Loc;
import edu.mit.mitmobile.objs.RouteItem.Stops;
import edu.mit.mitmobile.objs.RouteItem.Vehicle;
import edu.mit.mitmobile.shuttles.RoutesParser;
import edu.mit.mitmobile.shuttles.ShuttleModel;
import edu.mit.mitmobile.shuttles.StopsParser;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class MapUpdaterTask  extends AsyncTask<String, Void, Void> {
	
	Context ctx;
	
	RoutesParser rp;
	StopsParser sp;
	
	MITMapView mapView;
	MapController mctrl;

	MITVehicleOverlay vehicleMarkers;
	MITItemizedOverlay stopsMarkers;
	List<MapItem> mMapItems;

	int wait_delay;
	int last_upcoming = -1;
	
	List<Overlay>  ovrlys;
	
	List<Stops> mStops;
	RouteItem mRoute;
	
	Drawable pin,shuttleDrawable;
	PinItem oi;
	GeoPoint gpt;
	int lat,lon;

	Handler routeUpdateHandler;

	boolean addedPath = false;
	
	boolean pause = false;
	Boolean pauseLock = new Boolean(false);
	
	/***************************************************/
	MapUpdaterTask(Context ctx, MITMapView mv, RouteItem routeItem) {

		this.ctx = ctx;

		mapView = mv;
		
		
		// Initialize...
		//mRoute = Global.curRoute;
		//mStops = Global.curStops;
		ShuttleModel.addRoute(routeItem);
		mRoute = routeItem;
		mStops = mRoute.stops;		
		
		ovrlys = mapView.getOverlays();
				
		shuttleDrawable = ctx.getResources().getDrawable(R.drawable.shuttle_location_e);
	
		vehicleMarkers = new MITVehicleOverlay(shuttleDrawable,ctx);
		ovrlys.add(vehicleMarkers);
		
		pin = ctx.getResources().getDrawable(R.drawable.map_pin_shuttle_stop_complete);
		
		routeUpdateHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if(msg.arg1 == MobileWebApi.SUCCESS) {
					 RouteItem r = ShuttleModel.getUpdatedRoute(mRoute);
					 if (r!=null) {
						 mRoute = r;
						 mStops = mRoute.stops;
					 }
			    	 publishProgress ((Void)null);
				} else if (msg.arg1 == MobileWebApi.ERROR) {
					// TODO
					//Toast.makeText(ctx, MobileWebApi.NETWORK_ERROR, Toast.LENGTH_LONG).show();
				}
			}
		};
		
		updateMarkers();
		
	}

	/***************************************************/
	void addPath(Stops s) {
		
		// TODO sampling
		int step = 1;
		for (int index=0; index<s.path.size(); index+=step) {
			Loc l = s.path.get(index);
			lat = (int) (l.lat * 1000000.0);
			lon = (int) (l.lon * 1000000.0);
			gpt = new GeoPoint(lat,lon);
	        oi.detailed_path.add(gpt);
		}
		addedPath = true;
		
	}
	/***************************************************/
	@Override
	protected Void doInBackground(String... urls) {
		
		wait_delay = 1000*4;  // quicker first update...

		while(true) {
			
			if (isCancelled()) {
				return null; 
			}
			
			if (!pause) {
				ShuttleModel.fetchRouteDetails(ctx, mRoute, routeUpdateHandler);
			}
			
			/*
			if (pause) {
				try {
					pauseLock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			*/

			// Sleep...
			try {
				Thread.sleep(wait_delay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if (pause) {
				wait_delay = 1000*5;
			} else {
				wait_delay = 1000*13;
			}
			
		}
		
	}

	/***************************************************/
	protected void updateMarkers() {
		
		
		long curTime = System.currentTimeMillis();

		ovrlys = mapView.getOverlays();
		
		if (vehicleMarkers==null) {
			Log.e("MapUpdaterTask","vehicleMarkers null");
		}
		
		// TODO either clear or reuse
		//vehicleMarkers.mOverlays.clear();
		//stopsMarkers.mOverlays.clear();

		if (stopsMarkers!=null) {
			ovrlys.remove(stopsMarkers);
		}
		if (vehicleMarkers!=null) {
			ovrlys.remove(vehicleMarkers);
		}

		stopsMarkers = new MITItemizedOverlay(pin, ctx, mapView);
		stopsMarkers.shuttleMode = true;
		stopsMarkers.shuttleRouteId = mRoute.route_id;
		
		vehicleMarkers = new MITVehicleOverlay(shuttleDrawable,ctx);
		
		
		if (mStops==null) {
			Log.e("MapUpdaterTask","mStops null");
		}
		if (mRoute==null) {
			Log.e("MapUpdaterTask","mRoute null");
		}
	

		boolean upcoming = false;
		int index = 0;
		
		// Convert Stops to PinItems
		for (Stops s : mStops) {
			
			lat = (int) (Double.valueOf(s.lat) * 1000000.0);
			lon = (int) (Double.valueOf(s.lon) * 1000000.0);
			gpt = new GeoPoint(lat,lon);
			
			// Snippet
			int mins = (int) (s.next*1000 - curTime)/1000/60;
			int hours = mins / 60;
			String arriving = null;

			if (s.next==0) {
				arriving = "";
			} else if (hours>1) {
				mins = mins - (hours*60);
				arriving = "arriving in " + String.valueOf(hours) + " hrs " + String.valueOf(mins) + " mins";
			} else if (hours>0) {
				mins = mins - (hours*60);
				arriving = "arriving in " + String.valueOf(hours) + " hr " + String.valueOf(mins) + " mins";
			} else {
				if (mins==0) arriving = "arriving now!";
				else if (mins==1) arriving = "arriving in 1 min";
				else arriving = "arriving in " + String.valueOf(mins) + " mins";
			}
			
			// note: cannot set title or snipper so must make new pins each refresh

			oi = new PinItem(gpt, s.title, arriving, s);
			oi.twoLines = true;
			if (s.upcoming) {
				upcoming = true;
				oi.upcoming = true;
				last_upcoming = index;
			}
			stopsMarkers.addOverlay(oi);

			//this adds to "oi"
			addPath(s);
			
			index++;
		}

		if (!upcoming) {
			if (mRoute.vehicleLocations.size()>0) {
				Log.e("MapUpdaterTask","upcoming all false");
				throw new RuntimeException("upcoming all false");
			}
			// TODO 
			//Stops ss = mStops.get(last_upcoming);
			//if (last_upcoming>-1) ss.upcoming = true;
		}
		
		if (mRoute.vehicleLocations==null) {
			Log.e("MapUpdaterTask","vehicleLocations null");
		}
		
		// Vehicles
		VehicleOverlayItem voi;
		for (Vehicle v : mRoute.vehicleLocations) {
			lat = (int) (v.lat * 1000000.0);
			lon = (int) (v.lon * 1000000.0);
			gpt = new GeoPoint(lat,lon);
			voi = new VehicleOverlayItem(gpt,"","");
			voi.v = v;
			vehicleMarkers.addOverlay(voi);
		}

		if (stopsMarkers==null) {
			Log.e("MapUpdaterTask","stopsMarkers null");
		}
		int size = stopsMarkers.size();
		if (size>0) ovrlys.add(stopsMarkers);
		
		if (vehicleMarkers!=null) {
			if (vehicleMarkers.size()>0) {
				ovrlys.add(vehicleMarkers);
			}
		}

		mapView.postInvalidate();
		
	}
	/***************************************************/
	@Override
	protected void onProgressUpdate(Void... values) {

		super.onProgressUpdate(values);

		updateMarkers();
		
	}

}
