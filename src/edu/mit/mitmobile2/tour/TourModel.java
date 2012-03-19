package edu.mit.mitmobile2.tour;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.MobileWebApi.JSONArrayResponseListener;
import edu.mit.mitmobile2.MobileWebApi.JSONObjectResponseListener;
import edu.mit.mitmobile2.MobileWebApi.ServerResponseException;
import edu.mit.mitmobile2.tour.Tour.Path;
import edu.mit.mitmobile2.tour.Tour.SideTrip;
import edu.mit.mitmobile2.tour.Tour.Site;
import edu.mit.mitmobile2.tour.Tour.TourHeader;
import edu.mit.mitmobile2.tour.Tour.TourItemContent;

import com.google.android.maps.GeoPoint;

import android.content.Context;
import android.os.Handler;

public class TourModel {
	
	private static String TOUR_GUID = "mit150";
	
	private static Tour sTour;
	
	public static Tour getTour() {
		return sTour;
	}
	
	/*
	 * Get tour fallback on db if not avaiable in memory
	 */
	public static Tour getTour(Context context) {
		if (sTour == null) {
			// retrieve tour from DB
			TourDB tourDB = TourDB.getInstance(context);
			List<TourHeader> tourHeaders = tourDB.retrieveTourHeaders();
			sTour = new Tour(tourHeaders.get(0));
			tourDB.populateTourDetails(sTour);
		}
		return sTour;
	}
	
	public static void fetchTour(final Context context, final Handler uiHandler) {	

		// we fetch on a thread, because saving to the database blocks for a couple seconds
		new Thread() {
			@Override
			public void run() {
				fetchTourHelper(context, uiHandler);
			}
		}.start();
	}
		
	/*
	 * this fetches the tour from the database or network
	 * if the tour db cache has timed out it will try to cache from network
	 * but if that fails revert to using the version cached in the DB
	 */		
	private static void fetchTourHelper(final Context context, final Handler uiHandler) {
		final TourDB tourDB = TourDB.getInstance(context);
		
		if(tourDB.tourDetailsCached(TOUR_GUID, true)) {
			List<TourHeader> tourHeaders = tourDB.retrieveTourHeaders();
			sTour = new Tour(tourHeaders.get(0));
			tourDB.populateTourDetails(sTour);
			MobileWebApi.sendSuccessMessage(uiHandler);
			return;
		}
		
		// check if stale version of tour exists in db
		if(tourDB.tourDetailsCached(TOUR_GUID, false)) {
			// stale version exists, so check if stale version is recent enough
			MobileWebApi webApi = new MobileWebApi(false, false, null, context, null);
			HashMap<String, String> query = new HashMap<String, String>();
			query.put("module", "tours");
			query.put("command", "toursList");
			
			webApi.requestJSONArray(query, new JSONArrayResponseListener(new TourRefreshErrorListener(tourDB, uiHandler), null) {
				
				@Override
				public void onResponse(JSONArray array) throws ServerResponseException, JSONException {
					Long lastModified = null;
					for(int i=0; i < array.length(); i++) {
						JSONObject tourSummary = array.getJSONObject(i);
						if(tourSummary.getString("id").equals(TOUR_GUID)) {
							lastModified = tourSummary.getLong("last-modified") * 1000;
						}
					}
					
					if(lastModified != null) {
						if(lastModified < tourDB.tourDetailsLastUpdated(TOUR_GUID)) {
							// cache is still fresh (use it, and mark it as fresh)
							tourDB.markTourFresh(TOUR_GUID);
							
							// retrieve tour from DB
							List<TourHeader> tourHeaders = tourDB.retrieveTourHeaders();
							sTour = new Tour(tourHeaders.get(0));
							tourDB.populateTourDetails(sTour);
							MobileWebApi.sendSuccessMessage(uiHandler);
							return;
						}
					} 
					
					// tour is not up-to-date we need to update it.
					fetchTourFromNetwork(context, uiHandler);
				}
			});
			
			return;
		} 
		
		// no fresh or stale version of tour exists must retrieve it from network
		fetchTourFromNetwork(context, uiHandler);
	}	
		
	private static void fetchTourFromNetwork(Context context, final Handler uiHandler) {
		final TourDB tourDB = TourDB.getInstance(context);
		
		MobileWebApi webApi = new MobileWebApi(false, true, "Tour", context, uiHandler);
		
		HashMap<String, String> query = new HashMap<String, String>();
		query.put("module", "tours");
		query.put("command", "tourDetails");
		query.put("tourId", TOUR_GUID);
		
		webApi.requestJSONObject(query, new JSONObjectResponseListener(new MobileWebApi.DefaultErrorListener(uiHandler), null) {

			@Override
			public void onResponse(JSONObject object)
					throws ServerResponseException, JSONException {
				
				// save header info
				String title = object.getString("title");
				String descriptionTop = object.getString("description-top");
				String descriptionBottom = object.getString("description-bottom");
				
				Tour tour = new Tour(TOUR_GUID, title, descriptionTop, descriptionBottom);
				
				// save footer info
				String feedbackSubject = object.getJSONObject("feedback").getString("subject");
				tour.setFeedbackSubject(feedbackSubject);
				
				// parse all the links for the tour footer
				JSONArray links = object.getJSONArray("links");
				for(int linkIndex = 0; linkIndex < links.length(); linkIndex++) {
					JSONObject linkJson = links.getJSONObject(linkIndex);
					tour.getFooter().addLink(linkJson.getString("title"), linkJson.getString("url"));
				}
				
				
				// parse all the sites data
				JSONArray sites = object.getJSONArray("sites");
				
				for(int siteIndex = 0; siteIndex < sites.length(); siteIndex++) {
					JSONObject siteJson = sites.getJSONObject(siteIndex);
					Site site = tour.addSite(
						siteJson.getString("id"),
						siteJson.getString("title"),
						siteJson.getString("photo-url"),
						siteJson.getString("thumbnail156-url"),
						optString(siteJson, "audio-url"),
						parseLatLon(siteJson.getJSONObject("latlon"))
					);
					
					if(siteJson.has("exit-directions")) {
						String destinationGuid;
						if(siteIndex + 1 < sites.length()) {
							destinationGuid = sites.getJSONObject(siteIndex+1).getString("id");
						} else {
							destinationGuid = sites.getJSONObject(0).getString("id");
						}
						
						site.setExitDirections(
							siteJson.getJSONObject("exit-directions").getString("title"),
							destinationGuid,
							siteJson.getJSONObject("exit-directions").getInt("zoom")
						);

						
						JSONObject exitDirectionsJson = siteJson.getJSONObject("exit-directions");
						populateContent(site.getExitDirections().getContent(), exitDirectionsJson);
						populatePath(site.getExitDirections().getPath(), exitDirectionsJson);
						

						site.getExitDirections().setPhotoUrl(optString(exitDirectionsJson, "photo-url"));
						site.getExitDirections().setAudioUrl(optString(exitDirectionsJson, "audio-url"));
					}
					
					populateContent(site.getContent(), siteJson);
				}
				
				// parse all the start locations data
				tour.setStartLocationsHeader(object.getJSONObject("start-locations").getString("header"));
				JSONArray startLocationsItems = object.getJSONObject("start-locations").getJSONArray("items");
				
				for(int startLocationIndex=0; startLocationIndex < startLocationsItems.length(); startLocationIndex++) {
					JSONObject startLocationJson = startLocationsItems.getJSONObject(startLocationIndex);
					tour.addStartLocation(
						startLocationJson.getString("title"), 
						startLocationJson.getString("id"), 
						startLocationJson.getString("start-site"), 
						startLocationJson.getString("content"), 
						optString(startLocationJson, "photo-url"),
						parseLatLon(startLocationJson.getJSONObject("latlon"))
					);
				}
				
				ArrayList<Tour> tours = new ArrayList<Tour>();
				tours.add(tour);
				tourDB.saveTourHeaders(tours);
				tourDB.saveTourDetails(tour);
				sTour = tour;
				MobileWebApi.sendSuccessMessage(uiHandler);
			}
		});
	}
		
	
	private static GeoPoint parseLatLon(JSONObject object) throws JSONException {
		return new GeoPoint(
			(int)Math.round(1000000 * object.getDouble("latitude")), 
			(int)Math.round(1000000 * object.getDouble("longitude"))
		);
	}
	
	private static void populateContent(TourItemContent content, JSONObject json) throws JSONException {
		JSONArray contentArray = json.getJSONArray("content");
		for(int i=0; i < contentArray.length(); i++) {
			JSONObject contentNodeJson = contentArray.getJSONObject(i);
			
			String contentNodeType = contentNodeJson.getString("type");
			if(contentNodeType.equals("inline")) {
				content.addHtml(contentNodeJson.getString("html"));
			} else if(contentNodeType.equals("sidetrip")) {
				
				// optional fields
				String thumbUrl = null;
				GeoPoint geoPoint = null;
				if(contentNodeJson.has("thumbnail156-url")) {
					thumbUrl = contentNodeJson.getString("thumbnail156-url");
				}
				if(contentNodeJson.has("latlon")) {
					geoPoint = parseLatLon(contentNodeJson.getJSONObject("latlon"));
				} else {
					// sidetrips off the coast of africa
					geoPoint = new GeoPoint(0,0);
				}
				
				content.addSideTrip(new SideTrip(
					contentNodeJson.getString("id"),
					contentNodeJson.getString("title"),
					contentNodeJson.getString("html"),
					optString(contentNodeJson, "photo-url"),
					thumbUrl,
					optString(contentNodeJson, "audio-url"),
					geoPoint
				));
			}
		}
	}
	
	private static void populatePath(Path path, JSONObject json) throws JSONException {
		JSONArray jsonPath = json.getJSONArray("path");
		for(int i=0; i < jsonPath.length(); i++) {
			GeoPoint geoPoint = parseLatLon(jsonPath.getJSONObject(i));
			path.addGeoPoint(geoPoint);
		}
	}
	
	private static String optString(JSONObject json, String keyName) throws JSONException {
		if(json.has(keyName)) {
			return json.getString(keyName);
		} else {
			return null;
		}
	}
	
	private static class TourRefreshErrorListener implements MobileWebApi.ErrorResponseListener {

		Handler mUIHandler;
		TourDB mTourDB;
		TourRefreshErrorListener(TourDB tourDB, Handler handler) {
			mUIHandler = handler;
			mTourDB = tourDB;
		}
		
		@Override
		public void onError() {
			// failed to retrieve last modified time from network, lets just assume
			// data in db is up-to-date
			
			// retrieve tour from DB
			List<TourHeader> tourHeaders = mTourDB.retrieveTourHeaders();
			sTour = new Tour(tourHeaders.get(0));
			mTourDB.populateTourDetails(sTour);
			MobileWebApi.sendSuccessMessage(mUIHandler);			
		}		
	}
}
