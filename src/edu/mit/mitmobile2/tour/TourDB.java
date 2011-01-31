package edu.mit.mitmobile2.tour;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import edu.mit.mitmobile2.tour.Tour.SideTrip;
import edu.mit.mitmobile2.tour.Tour.Site;
import edu.mit.mitmobile2.tour.Tour.StartLocation;
import edu.mit.mitmobile2.tour.Tour.TourHeader;

import edu.mit.mitmobile2.tour.Tour.FooterLink;
import edu.mit.mitmobile2.tour.Tour.HtmlContentNode;
import edu.mit.mitmobile2.tour.Tour.Path;
import edu.mit.mitmobile2.tour.Tour.TourItemContent;
import edu.mit.mitmobile2.tour.Tour.TourItemContentNode;

import com.google.android.maps.GeoPoint;

public class TourDB {
	private static final long TOUR_CACHE_EXPIRES = 24 * 60 * 60 * 1000;
	
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "tour.db";
	private static final String TOURS_TABLE = "tours";
	private static final String SITES_TABLE = "sites";
	private static final String START_LOCATIONS_TABLE = "start_locations";
	private static final String CONTENT_NODES_TABLE = "content_nodes";
	private static final String PATHS_TABLE = "paths";
	private static final String FOOTER_LINKS_TABLE = "footer_links";

	private static final String ID = "_id";
	
	// tour table field names
	// ID
	private static final String TOUR_GUID = "tour_guid";
	private static final String TITLE = "title";
	private static final String DESCRIPTION_TOP = "description_top";
	private static final String DESCRIPTION_BOTTOM = "description_bottom";
	private static final String FEEDBACK_SUBJECT = "feedback_subject";
	private static final String START_LOCATIONS_HEADER = "start_locations_header";
	private static final String TOUR_ORDER = "tour_order";
	private static final String LAST_UPDATED = "last_updated";
	
	// site table field names
	// ID
	// TOUR_GUID
	private static final String SITE_ORDER = "site_order";
	private static final String SITE_GUID = "site_guid";
	
	private static final String SITE_NAME = "name";
	
	private static final String SITE_PHOTO_URL = "site_photo_url";
	private static final String SITE_THUMBNAIL_URL = "site_thumbnail_url";
	private static final String SITE_AUDIO_URL = "site_audio_url";
	
	private static final String LATITUDE = "latitude";
	private static final String LONGITUDE = "longitude";
	
	private static final String EXIT_DIRECTIONS_TITLE = "exit_directions_title";
	private static final String EXIT_DIRECTIONS_DESTINATION_GUID = "exit_direction_destination_guid";
	private static final String EXIT_DIRECTIONS_PHOTO_URL = "exit_directions_photo_url";
	private static final String EXIT_DIRECTIONS_AUDIO_URL = "exit_directions_audio_url";
	private static final String EXIT_DIRECTIONS_ZOOM = "exit_directions_zoom";
	
	// start locations field names
	// ID
	// TOUR_GUID
	private static final String START_LOCATION_ORDER = "start_location_order";
	private static final String START_LOCATION_ID = "start_location_guid";
	private static final String START_LOCATION_TITLE = "start_location_title";
	private static final String START_LOCATION_CONTENT = "start_location_content";
	private static final String START_LOCATION_PHOTO_URL = "photo_url";
	private static final String START_SITE_GUID = "start_site_guid";
	// LATITUDE
	// LONGITUDE
	
	// content node fields
	private static final String CONTENT_NODE_ORDER = "content_node_order";
	private static final String CONTENT_NODE_HTML = "content_node_html";
	private static final String CONTENT_NODE_TYPE = "content_node_type";
	private static final String SIDE_TRIP_ID = "side_trip_id";
	private static final String SIDE_TRIP_TITLE = "side_trip_title";
	private static final String SIDE_TRIP_PHOTO_URL = "side_trip_photo_url";
	private static final String SIDE_TRIP_AUDIO_URL = "side_trip_audio_url";
	private static final String PARENT_GUID = "parent_guid";
	private static final String PARENT_TYPE = "parent_type";
	
	// path fields
	private static final String PATH_ORDER = "path_order";
	// LATITUDE 
	// LONGITUDE
	// PARENT_GUID 
	// PARENT_TYPE 
	
	// footer link fields
	// TOUR_GUID
	private static final String FOOTER_LINK_ORDER = "footer_link_order";
	private static final String FOOTER_LINK_TITLE = "footer_link_title";
	private static final String FOOTER_LINK_URL = "footer_link_url";
	
	private static enum ContentNodeType {
		INLINE,
		SIDE_TRIP
	}
	
	private static enum ParentType {
		START_LOCATION,
		SITE,
		SITE_DIRECTIONS
	}
	
	SQLiteOpenHelper mTourDBHelper;
	
	private static TourDB sTourDBInstance = null;
	
	public static TourDB getInstance(Context context) {
		if(sTourDBInstance == null) {
			sTourDBInstance = new TourDB(context);
			return sTourDBInstance;
		} else {
			return sTourDBInstance;
		}
	}
	private TourDB(Context context) {
		Context applicationContext = context.getApplicationContext();		
		mTourDBHelper = new TourDatabaseHelper(applicationContext); 
	}
	
	synchronized void clearAllTours() {
		SQLiteDatabase db = mTourDBHelper.getWritableDatabase();
		db.delete(TOURS_TABLE, null, null);
		db.delete(SITES_TABLE, null, null);
		db.delete(START_LOCATIONS_TABLE, null, null);
		db.delete(CONTENT_NODES_TABLE, null, null);
		db.delete(PATHS_TABLE, null, null);
		db.delete(FOOTER_LINKS_TABLE, null, null);
	}
	
	synchronized void saveTourHeaders(List<Tour> tours) {
		SQLiteDatabase db = mTourDBHelper.getWritableDatabase();
		this.clearAllTours();
		
		int tourIndex = 0;
		for(Tour tour : tours) {
			ContentValues values = new ContentValues();
			values.put(TOUR_GUID, tour.getHeader().getGuid());
			values.put(TITLE, tour.getHeader().getTitle());
			values.put(DESCRIPTION_TOP, tour.getHeader().getDescriptionTop());
			values.put(DESCRIPTION_BOTTOM, tour.getHeader().getDescriptionBottom());
			values.put(TOUR_ORDER, tourIndex);
			values.put(LAST_UPDATED, System.currentTimeMillis());
			db.insert(TOURS_TABLE, TOUR_GUID, values);
			tourIndex++;
		}
		
		mTourDBHelper.close();
	}
	
	synchronized List<TourHeader> retrieveTourHeaders() {
		ArrayList<TourHeader> headers = new ArrayList<TourHeader>();
		SQLiteDatabase db = mTourDBHelper.getReadableDatabase();
		
		Cursor cursor = db.query(TOURS_TABLE, null, null, null, null, null, TOUR_ORDER + " ASC");
		
		int guidIndex = cursor.getColumnIndex(TOUR_GUID);
		int titleIndex = cursor.getColumnIndex(TITLE);
		int descriptionTopIndex = cursor.getColumnIndex(DESCRIPTION_TOP);
		int descriptionBottomIndex = cursor.getColumnIndex(DESCRIPTION_BOTTOM);
		
		if(cursor.moveToFirst()) {
			while(!cursor.isAfterLast()) {
				Tour tour = new Tour(
					cursor.getString(guidIndex), 
					cursor.getString(titleIndex), 
					cursor.getString(descriptionTopIndex),
					cursor.getString(descriptionBottomIndex)
				);
				
				headers.add(tour.getHeader());
				cursor.moveToNext();
			};
		}
		cursor.close();
		mTourDBHelper.close();
		return headers;
	}
	

	synchronized Long tourDetailsLastUpdated(String guid) {
		SQLiteDatabase db = mTourDBHelper.getReadableDatabase();
		
		Cursor cursor = db.query(TOURS_TABLE, null, TOUR_GUID + " =? ",  new String[] {guid}, null, null, null);
		int lastUpdatedIndex = cursor.getColumnIndex(LAST_UPDATED);
		
		Long lastUpdated = null;
		
		if(cursor.moveToFirst()) {
			lastUpdated = cursor.getLong(lastUpdatedIndex);
		}
		cursor.close();
		
		return lastUpdated;
	}
	
	synchronized void markTourFresh(String guid) {
		SQLiteDatabase db = mTourDBHelper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(LAST_UPDATED, System.currentTimeMillis());

		db.update(TOURS_TABLE, values, TOUR_GUID + " =? " , new String[] {guid});	
	}
	
	/*
	 * if isFresh is true, we require the cache to be fresh to use it
	 */
	boolean tourDetailsCached(String guid, boolean isFresh) {
		Long lastUpdated = tourDetailsLastUpdated(guid);
		if(lastUpdated == null) {
			return false;
		}
		
		if(isFresh) {
			return (System.currentTimeMillis() - lastUpdated) < TOUR_CACHE_EXPIRES;
		} else {
			return true;
		} 
	}
	
	
	
	synchronized void saveTourDetails(Tour tour) {
		SQLiteDatabase db = mTourDBHelper.getWritableDatabase();
		
		db.beginTransaction();
		
		// loop thru all the sites and save them.
		int siteOrder = 0;
		for(Site site : tour.getSites()) {
			ContentValues siteValues = new ContentValues();
			siteValues.put(TOUR_GUID, tour.getHeader().getGuid());
			siteValues.put(SITE_GUID, site.getSiteGuid());
			siteValues.put(SITE_ORDER, siteOrder);
			siteValues.put(SITE_NAME, site.getName());
			siteValues.put(SITE_PHOTO_URL, site.getPhotoInfo().getPhotoUrl());
			siteValues.put(SITE_THUMBNAIL_URL, site.getThumbnailUrl());
			siteValues.put(SITE_AUDIO_URL, site.getAudioUrl());
			siteValues.put(LATITUDE, site.getGeoPoint().getLatitudeE6());
			siteValues.put(LONGITUDE, site.getGeoPoint().getLongitudeE6());
			
			if(site.getExitDirections() != null) {
				siteValues.put(EXIT_DIRECTIONS_TITLE, site.getExitDirections().getTitle());
				siteValues.put(EXIT_DIRECTIONS_DESTINATION_GUID, site.getExitDirections().getDestinationGuid());
				siteValues.put(EXIT_DIRECTIONS_PHOTO_URL, site.getExitDirections().getPhotoInfo().getPhotoUrl());
				siteValues.put(EXIT_DIRECTIONS_AUDIO_URL, site.getExitDirections().getAudioUrl());
				siteValues.put(EXIT_DIRECTIONS_ZOOM, site.getExitDirections().getPath().getZoom());
				saveContentDetails(site.getExitDirections().getContent(), site.getSiteGuid(), ParentType.SITE_DIRECTIONS);
				savePath(site.getExitDirections().getPath(), site.getSiteGuid(), ParentType.SITE_DIRECTIONS);
			}
			
			db.insert(SITES_TABLE, SITE_ORDER, siteValues);
			
			saveContentDetails(site.getContent(), site.getSiteGuid(), ParentType.SITE);
			siteOrder++;
		}
		
		saveStartLocations(tour);		
		
		// save tour footer
		saveTourFooter(tour);
		
		db.setTransactionSuccessful();
		db.endTransaction();
		
		mTourDBHelper.close();
	}
	
	synchronized void saveStartLocations(Tour tour) {
		SQLiteDatabase db = mTourDBHelper.getWritableDatabase();
		ContentValues startLocationsHeaderValues = new ContentValues();
		startLocationsHeaderValues.put(START_LOCATIONS_HEADER, tour.getStartLocationsHeader());
		db.update(TOURS_TABLE, startLocationsHeaderValues, TOUR_GUID  + "= ?", new String[] {tour.getHeader().getGuid()});
		
		int startLocationOrder = 0;
		for(StartLocation location: tour.getStartLocations()) {
			ContentValues locationValue = new ContentValues();
			locationValue.put(TOUR_GUID, tour.getHeader().getGuid());
			locationValue.put(START_LOCATION_TITLE, location.getTitle());
			locationValue.put(START_LOCATION_ID, location.getLocationId());
			locationValue.put(START_LOCATION_ORDER, startLocationOrder);
			locationValue.put(START_LOCATION_CONTENT, location.getContent());
			locationValue.put(START_LOCATION_PHOTO_URL, location.getPhotoUrl());
			locationValue.put(START_SITE_GUID, location.getSiteGuid());

			// latlon
			locationValue.put(LATITUDE, location.getGeoPoint().getLatitudeE6());
			locationValue.put(LONGITUDE, location.getGeoPoint().getLongitudeE6());
			
			db.insert(START_LOCATIONS_TABLE, START_LOCATION_ID, locationValue);
						
			startLocationOrder++;
		}
	}
	
	synchronized void saveTourFooter(Tour tour) {
		SQLiteDatabase db = mTourDBHelper.getWritableDatabase();
		
		ContentValues footerValues = new ContentValues();
		footerValues.put(FEEDBACK_SUBJECT, tour.getFooter().getFeedbackSubject());
		db.update(TOURS_TABLE, footerValues, TOUR_GUID  + "= ?", new String[] {tour.getHeader().getGuid()});
		
		for(int i = 0; i < tour.getFooter().getLinks().size(); i++) {
			FooterLink link = tour.getFooter().getLinks().get(i);
			ContentValues linkValues = new ContentValues();
			linkValues.put(TOUR_GUID, tour.getHeader().getGuid());
			linkValues.put(FOOTER_LINK_ORDER, i);
			linkValues.put(FOOTER_LINK_TITLE, link.getTitle());
			linkValues.put(FOOTER_LINK_URL, link.getUrl());
			db.insert(FOOTER_LINKS_TABLE, FOOTER_LINK_ORDER, linkValues);
		}
	}
	
	synchronized void saveContentDetails(TourItemContent content, String guid, ParentType parentType) {
		SQLiteDatabase db = mTourDBHelper.getWritableDatabase();
		
		int contentNodeOrder = 0;
		for(TourItemContentNode contentNode : content.getContentNodes()) {
			ContentValues nodeValues = new ContentValues();
			
			nodeValues.put(CONTENT_NODE_ORDER, contentNodeOrder);
			nodeValues.put(CONTENT_NODE_HTML, contentNode.getHtml());
			nodeValues.put(PARENT_TYPE, parentType.ordinal());
			nodeValues.put(PARENT_GUID, guid);
			
			if(contentNode.getClass() == HtmlContentNode.class) {
				nodeValues.put(CONTENT_NODE_TYPE, ContentNodeType.INLINE.ordinal());
			} else if(contentNode.getClass() == SideTrip.class) {
				nodeValues.put(CONTENT_NODE_TYPE, ContentNodeType.SIDE_TRIP.ordinal());
				
				SideTrip sideTrip = (SideTrip) contentNode;
				nodeValues.put(SIDE_TRIP_ID, sideTrip.getId());
				nodeValues.put(SIDE_TRIP_TITLE, sideTrip.getTitle());
				nodeValues.put(SIDE_TRIP_PHOTO_URL, sideTrip.getPhotoUrl());
				nodeValues.put(SIDE_TRIP_AUDIO_URL, sideTrip.getAudioUrl());
			}
			
			
			db.insert(CONTENT_NODES_TABLE, PARENT_TYPE, nodeValues);
			contentNodeOrder++;
		}
	}
	
	synchronized void savePath(Path path, String guid, ParentType parentType) {
		SQLiteDatabase db = mTourDBHelper.getWritableDatabase();
		
		int pathOrder = 0;
		for(GeoPoint geoPoint : path.getGeoPoints()) {
			ContentValues pathPoint = new ContentValues();
			pathPoint.put(PATH_ORDER, pathOrder);
			pathPoint.put(LATITUDE, geoPoint.getLatitudeE6());
			pathPoint.put(LONGITUDE, geoPoint.getLongitudeE6());
			pathPoint.put(PARENT_GUID, guid);
			pathPoint.put(PARENT_TYPE, parentType.ordinal());
			
			db.insert(PATHS_TABLE, PARENT_TYPE, pathPoint);
			pathOrder++;
		}
	}
	
	synchronized void populateTourDetails(final Tour tour) {
		SQLiteDatabase db = mTourDBHelper.getReadableDatabase();
		
		Cursor cursor = db.query(SITES_TABLE, null, TOUR_GUID + " =? ",  new String[] {tour.getHeader().getGuid()}, null, null, SITE_ORDER + " ASC");
		
		int siteNameIndex = cursor.getColumnIndex(SITE_NAME);
		int siteGuidIndex = cursor.getColumnIndex(SITE_GUID);
		int sitePhotoUrlIndex = cursor.getColumnIndex(SITE_PHOTO_URL);
		int siteThumbnailIndex = cursor.getColumnIndex(SITE_THUMBNAIL_URL);
		int siteAudioUrlIndex = cursor.getColumnIndex(SITE_AUDIO_URL);
		int latitudeIndex = cursor.getColumnIndex(LATITUDE);
		int longitudeIndex = cursor.getColumnIndex(LONGITUDE);
		
		int exitDirectionsTitle = cursor.getColumnIndex(EXIT_DIRECTIONS_TITLE);
		int exitDirectionsDestination = cursor.getColumnIndex(EXIT_DIRECTIONS_DESTINATION_GUID);
		int exitDirectionsPhotoUrl = cursor.getColumnIndex(EXIT_DIRECTIONS_PHOTO_URL);
		int exitDirectionsAudio = cursor.getColumnIndex(EXIT_DIRECTIONS_AUDIO_URL);
		int exitDirectionsZoom = cursor.getColumnIndex(EXIT_DIRECTIONS_ZOOM);
		
		if(cursor.moveToFirst()) {
			while(!cursor.isAfterLast()) {
				Site site = tour.addSite(
					cursor.getString(siteGuidIndex),
					cursor.getString(siteNameIndex), 
					cursor.getString(sitePhotoUrlIndex),
					cursor.getString(siteThumbnailIndex),
					cursor.getString(siteAudioUrlIndex),
					new GeoPoint(cursor.getInt(latitudeIndex), cursor.getInt(longitudeIndex))
				);
				
				if(cursor.getString(exitDirectionsTitle) != null) {
					site.setExitDirections(
						cursor.getString(exitDirectionsTitle), 
						cursor.getString(exitDirectionsDestination),
						cursor.getInt(exitDirectionsZoom)
					);
					populateContentDetails(site.getExitDirections().getContent(), site.getSiteGuid(), ParentType.SITE_DIRECTIONS);
					populatePath(site.getExitDirections().getPath(), site.getSiteGuid(), ParentType.SITE_DIRECTIONS);
					site.getExitDirections().setPhotoUrl(cursor.getString(exitDirectionsPhotoUrl));
					site.getExitDirections().setAudioUrl(cursor.getString(exitDirectionsAudio));
				}
				
				populateContentDetails(site.getContent(), site.getSiteGuid(), ParentType.SITE);
				
				cursor.moveToNext();
			}
		}
		cursor.close();
		
		populateFooter(tour);
		populateStartLocations(tour);
		
		mTourDBHelper.close();
	}
	
	synchronized void populateFooter(Tour tour) {
		SQLiteDatabase db = mTourDBHelper.getReadableDatabase();
		
		// get the feedback subjects
		Cursor cursor = db.query(TOURS_TABLE, new String[] {FEEDBACK_SUBJECT}, TOUR_GUID + " =?", new String[] {tour.getHeader().getGuid()}, null, null, null);
		int feedbackSubjectIndex = cursor.getColumnIndex(FEEDBACK_SUBJECT);
		cursor.moveToFirst();
		tour.setFeedbackSubject(cursor.getString(feedbackSubjectIndex));
		cursor.close();
		
		
		// get the end of tour links
		Cursor linksCursor = db.query(FOOTER_LINKS_TABLE, null, TOUR_GUID + " =?", new String[] {tour.getHeader().getGuid()}, null, null, FOOTER_LINK_ORDER + " ASC");
		
		int titleIndex = linksCursor.getColumnIndex(FOOTER_LINK_TITLE);
		int urlIndex = linksCursor.getColumnIndex(FOOTER_LINK_URL);
		
		if(linksCursor.moveToFirst()) {
			while(!linksCursor.isAfterLast()) {
				tour.getFooter().addLink(
					linksCursor.getString(titleIndex), 
					linksCursor.getString(urlIndex)
				);
				
				linksCursor.moveToNext();
			}
		}
		linksCursor.close();
		
	}
	
	synchronized void populateStartLocations(Tour tour) {
		SQLiteDatabase db = mTourDBHelper.getReadableDatabase();
		Cursor headerCursor = db.query(TOURS_TABLE, new String[] {START_LOCATIONS_HEADER}, TOUR_GUID + " =?", new String[] {tour.getHeader().getGuid()}, null, null, null, "1");
		int headerIndex = headerCursor.getColumnIndex(START_LOCATIONS_HEADER);
		if(headerCursor.moveToFirst()) {
			tour.setStartLocationsHeader(headerCursor.getString(headerIndex));
		}
		headerCursor.close();
		
		
		Cursor cursor = db.query(START_LOCATIONS_TABLE, null,  TOUR_GUID + " =? ",  new String[] {tour.getHeader().getGuid()}, null, null, START_LOCATION_ORDER + " ASC");
		
		int titleIndex = cursor.getColumnIndex(START_LOCATION_TITLE);
		int locationGuidIndex = cursor.getColumnIndex(START_LOCATION_ID);
		int siteGuidIndex = cursor.getColumnIndex(START_SITE_GUID);
		int photoUrlIndex = cursor.getColumnIndex(START_LOCATION_PHOTO_URL);
		int contentIndex = cursor.getColumnIndex(START_LOCATION_CONTENT);
		
		int latIndex = cursor.getColumnIndex(LATITUDE);
		int lonIndex = cursor.getColumnIndex(LONGITUDE);
	
		if(cursor.moveToFirst()) {
			while(!cursor.isAfterLast()) {
				int lat = cursor.getInt(latIndex);
				int lon = cursor.getInt(lonIndex);
				GeoPoint geoPoint = new GeoPoint(lat,lon);
				
				tour.addStartLocation(
					cursor.getString(titleIndex),
					cursor.getString(locationGuidIndex),
					cursor.getString(siteGuidIndex),
					cursor.getString(contentIndex),
					cursor.getString(photoUrlIndex),
					geoPoint
				);
				
				cursor.moveToNext();
			}
		}
		cursor.close();
		
	}
	
	synchronized void populateContentDetails(TourItemContent content, String guid, ParentType parent) {
		SQLiteDatabase db = mTourDBHelper.getReadableDatabase();
		
		Cursor cursor = db.query(CONTENT_NODES_TABLE, null, 
			PARENT_GUID + "=? AND " + PARENT_TYPE + "=?" , 
			new String[] {guid, Integer.toString(parent.ordinal())},
			null, null, CONTENT_NODE_ORDER + " ASC");
		
		int htmlIndex = cursor.getColumnIndex(CONTENT_NODE_HTML);
		int typeIndex = cursor.getColumnIndex(CONTENT_NODE_TYPE);
		int sideTripIdIndex = cursor.getColumnIndex(SIDE_TRIP_ID);
		int sideTripTitleIndex = cursor.getColumnIndex(SIDE_TRIP_TITLE);
		int sideTripPhotoIndex = cursor.getColumnIndex(SIDE_TRIP_PHOTO_URL);
		int sideTripAudioIndex = cursor.getColumnIndex(SIDE_TRIP_AUDIO_URL);
		
		if(cursor.moveToFirst()) {
			while(!cursor.isAfterLast()) {
				ContentNodeType nodeType = ContentNodeType.values()[cursor.getInt(typeIndex)];
				if(nodeType == ContentNodeType.INLINE) {
					content.addHtml(cursor.getString(htmlIndex));
				} else if(nodeType == ContentNodeType.SIDE_TRIP) {
					SideTrip sideTrip = new SideTrip(
							cursor.getString(sideTripIdIndex),
							cursor.getString(sideTripTitleIndex),
							cursor.getString(htmlIndex),
							cursor.getString(sideTripPhotoIndex),
							cursor.getString(sideTripAudioIndex)
					);
					content.addSideTrip(sideTrip);
				}
				
				cursor.moveToNext();
			}
		}
		cursor.close();
	}
	
	synchronized void populatePath(Path path, String guid, ParentType parent) {
		SQLiteDatabase db = mTourDBHelper.getReadableDatabase();
		
		Cursor cursor = db.query(PATHS_TABLE, null, 
			PARENT_GUID + "=? AND " + PARENT_TYPE + "=?" , 
			new String[] {guid, Integer.toString(parent.ordinal())},
			null, null, PATH_ORDER + " ASC");
		
		int latitudeIndex = cursor.getColumnIndex(LATITUDE);
		int longitudeIndex = cursor.getColumnIndex(LONGITUDE);
		
		if(cursor.moveToFirst()) {
			while(!cursor.isAfterLast()) {
				GeoPoint geoPoint = new GeoPoint(
					cursor.getInt(latitudeIndex),
					cursor.getInt(longitudeIndex)
				);
				
				path.addGeoPoint(geoPoint);
				cursor.moveToNext();
			}
		}
		cursor.close();
	}

	/*
	private static String getStringOrNull(Cursor cursor, int columnIndex) {
		String value = cursor.getString(columnIndex);
		if(value.equals("")) {
			return null;
		} else {
			return value;
		}
	}
	*/
	
	private static class TourDatabaseHelper extends SQLiteOpenHelper {
		
		TourDatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + TOURS_TABLE + " ("
					+ ID + " INTEGER PRIMARY KEY,"
					+ TOUR_GUID + " TEXT,"
					+ TOUR_ORDER + " INTEGER,"
					+ TITLE + " TEXT,"
					+ DESCRIPTION_TOP + " TEXT,"
					+ DESCRIPTION_BOTTOM + " TEXT,"
					+ FEEDBACK_SUBJECT + " TEXT,"
					+ START_LOCATIONS_HEADER + " TEXT,"
					+ LAST_UPDATED + " INTEGER"
					+ ");");
			
			
			db.execSQL("CREATE TABLE " + SITES_TABLE + " ("
					+ ID + " INTEGER PRIMARY KEY,"
					+ TOUR_GUID + " TEXT, "
					+ SITE_GUID + " TEXT, "
					+ SITE_ORDER + " INTEGER, "
					
					+ SITE_NAME + " TEXT,"	
					
					+ SITE_PHOTO_URL + " TEXT,"
					+ SITE_THUMBNAIL_URL + " TEXT,"
					+ SITE_AUDIO_URL + " TEXT,"
					
					+ LATITUDE + " FLOAT,"
					+ LONGITUDE + " FLOAT,"
					
					+ EXIT_DIRECTIONS_TITLE  + " TEXT,"
					+ EXIT_DIRECTIONS_DESTINATION_GUID + " TEXT,"
					+ EXIT_DIRECTIONS_PHOTO_URL + " TEXT, "
					+ EXIT_DIRECTIONS_AUDIO_URL + " TEXT, "
					+ EXIT_DIRECTIONS_ZOOM + " INTEGER "
					
					+ ");");
			
			
			db.execSQL("CREATE TABLE " + START_LOCATIONS_TABLE + " ("
					+ ID + " INTEGER PRIMARY KEY,"
					+ TOUR_GUID + " TEXT, "
					+ START_LOCATION_ID + " TEXT, "
					+ START_LOCATION_ORDER + " INTEGER,"
					+ START_LOCATION_TITLE + " TEXT,"
					+ START_LOCATION_CONTENT + " TEXT,"
					+ START_SITE_GUID + " TEXT,"
					+ START_LOCATION_PHOTO_URL + " TEXT,"
					+ LATITUDE + " INTEGER,"
					+ LONGITUDE + " INTEGER"
					+ ");");
	
			db.execSQL("CREATE TABLE " + CONTENT_NODES_TABLE + " ("
					+ ID + " INTEGER PRIMARY KEY,"
					+ CONTENT_NODE_ORDER + " INTEGER,"
					+ CONTENT_NODE_TYPE + " INTEGER,"
					+ CONTENT_NODE_HTML + " TEXT,"
					+ SIDE_TRIP_ID + " TEXT,"
					+ SIDE_TRIP_TITLE + " TEXT,"
					+ SIDE_TRIP_PHOTO_URL + " TEXT,"
					+ SIDE_TRIP_AUDIO_URL + " TEXT,"
					+ PARENT_GUID + " TEXT,"
					+ PARENT_TYPE + " INTEGER"
					+ ");");
			
			db.execSQL("CREATE TABLE " + PATHS_TABLE + " ("
					+ ID + " INTEGER PRIMARY KEY,"
					+ PATH_ORDER + " INTEGER,"
					+ LATITUDE + " INTEGER, "
					+ LONGITUDE + " INTEGER,"
					+ PARENT_GUID + " TEXT,"
					+ PARENT_TYPE + " INTEGER"
					+ ");");	
			
			db.execSQL("CREATE TABLE " + FOOTER_LINKS_TABLE + " ("
					+ ID + " INTEGER PRIMARY KEY,"
					+ TOUR_GUID + " TEXT,"
					+ FOOTER_LINK_ORDER + " INTEGER,"
					+ FOOTER_LINK_TITLE + " TEXT, "
					+ FOOTER_LINK_URL + " TEXT"
					+ ");");	
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// no old versions exists
		}
	}
}
