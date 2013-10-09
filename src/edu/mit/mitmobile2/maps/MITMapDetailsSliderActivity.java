package edu.mit.mitmobile2.maps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.core.geometry.SpatialReference;

import edu.mit.mitmobile2.LockingScrollView;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.RemoteImageView;
import edu.mit.mitmobile2.SliderInterface;
import edu.mit.mitmobile2.SliderListNewModuleActivity;
import edu.mit.mitmobile2.TabConfigurator;
import edu.mit.mitmobile2.objs.BuildingMapItem;
import edu.mit.mitmobile2.objs.MapItem;
import edu.mit.mitmobile2.objs.MapItemContent;
import edu.mit.mitmobile2.objs.PersonItem.PersonDetailViewMode;
import edu.mit.mitmobile2.people.PeopleDetailActivity;

public class MITMapDetailsSliderActivity extends SliderListNewModuleActivity {
	
	//private final static String TAG = "MITMapDetailsSliderActivity";
	public static final String UID_KEY = "uid";
	public static final String MAP_ITEM_INDEX = "map_item_index";
	public static final String SEARCH_TERM_KEY = "search_term";
	public static final String RECENTLY_VIEWED_FLAG = "show_recents";
	public static final int TARGET_WKID = 102113; // the wikid of the map used to export images 
	private static final String MENU_BOOKMARKS = "bookmarks";

	private List<MapItem> mMapItems = Collections.emptyList();
	private int mapItemIndex = 0;
		
	private Context mContext;
	final MapsDB db = MapsDB.getInstance(this);
	
	private SpatialReference targetSpatialReference;
	Activity mActivity;

	TextView mapDetailsQueryTV;
	TextView mapDetailsTitleTV;
	TextView mapDetailsSubtitleTV;
	RemoteImageView mThumbnailView;
	//MITMapView mThumbnailView;
	Button mapGoogleMapBtn;
	Button mapBookmarkBtn;
	
	String bbox;
	String imgUrl;
	TextView mapDetailsHereTV;
	RemoteImageView mapDetailsPhotoView;
	TextView mapDetailsPhotosTV;
	
	static void launchActivity(Context context, MapItem item, int viewMode, String extras) {
		// load the activity that shows all the detail search results
		Intent intent = new Intent(context, PeopleDetailActivity.class);
		if(viewMode == PersonDetailViewMode.SEARCH) {
			intent.putExtra(PeopleDetailActivity.SEARCH_TERM_KEY, extras);
		} else {
			intent.putExtra(PeopleDetailActivity.RECENTLY_VIEWED_FLAG, true);
		}
		//intent.putExtra(PeopleDetailActivity.UID_KEY, item.uid);
		//PeopleModel.markAsRecentlyViewed(item, context);
		context.startActivity(intent);
	}
	
	@Override
	protected int getPosition() {
		// TODO Auto-generated method stub
		return super.getPosition();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mContext = this;
		mActivity = (Activity) mContext;
		targetSpatialReference = SpatialReference.create(TARGET_WKID);
		
		Bundle extras = getIntent().getExtras();
		if(extras != null) {
			List<MapItem> mapItems = null;
			if(extras.containsKey(MITMapView.MAP_ITEMS_KEY)) {
				BuildingMapItem b;
				
				mapItems = (ArrayList)extras.getParcelableArrayList(MITMapView.MAP_ITEMS_KEY);
				//Log.d(TAG,"number of map items = " + mapItems.size());
			} 	

			if(extras.containsKey(MITMapView.MAP_ITEM_INDEX_KEY)) {
				mapItemIndex = extras.getInt(MITMapView.MAP_ITEM_INDEX_KEY);
			}

			setMapItems(mapItems,mapItemIndex);
		} 
	}
	
	private void setMapItems(List<MapItem> mapItems, int position) {
		mMapItems = mapItems;
		int totalMapItems = mMapItems.size();
		
		for(int index = 0; index < totalMapItems; ++index) {
			MapItem mapItem = mMapItems.get(index);
			if (mapItem.getMapPoints() != null) {
			}
			String headerTitle = Integer.toString(index+1) + " of " + Integer.toString(totalMapItems);
			addScreen(new MapSliderInterface(mapItem), (String)mapItem.getItemData().get("name"), headerTitle);
		}
		
		setPosition(position);
	}
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode != 0) {
			Cursor result = getContentResolver().query(data.getData(), new String[] {ContactsContract.Contacts._ID}, null, null, null);
			result.moveToFirst();
			int index = result.getColumnIndex(ContactsContract.Contacts._ID);
			long id = result.getLong(index);
			result.close();

			Uri myPerson = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
			Intent intent = new Intent(Intent.ACTION_EDIT, myPerson);
			//populateAddContactIntent(intent, mPersonToAddToContacts, true);
			startActivity(intent);
		}
	}
		
	private class MapSliderInterface implements SliderInterface {
		private MapItem mMapItem;
		private View mMainLayout;
		//private ViewGroup mListItemsLayout;
		
		MapSliderInterface(MapItem mapItem) {
			mMapItem = mapItem;
		}
		
		@Override
		public void onSelected() {
			//MapModel.markAsRecentlyViewed(mMapItem, mContext);
		}
			
		@Override
		public View getView() {
			//MapItem mapItem = mMapItems.get(getPosition());
			LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mMainLayout = inflator.inflate(R.layout.map_details, null);
			
			//initSecondaryTitleBar();
			
			TabHost tabHost;
			
			tabHost = (TabHost) mMainLayout.findViewById(R.id.mapDetailsTH);  
			tabHost.setup();  // NEEDED!!!

			TabConfigurator tabConfigurator = new TabConfigurator(mActivity, tabHost);
			tabConfigurator.addTab("What's Here", R.id.mapDetailsHereLL);
			tabConfigurator.addTab("Photo", R.id.mapDetailsPhotosLL);
			tabConfigurator.configureTabs();
					
			mapDetailsQueryTV = (TextView) mMainLayout.findViewById(R.id.mapDetailsQueryTV);
			if (mMapItem.query == null || mMapItem.query.trim().length() == 0) {
				mapDetailsQueryTV.setVisibility(View.GONE);
			}
			else {
				mapDetailsQueryTV.setText("'" + mMapItem.query + "' was found in");
			}
			mapDetailsTitleTV = (TextView) mMainLayout.findViewById(R.id.mapDetailsTitleTV);
			mapDetailsTitleTV.setText((String)mMapItem.getItemData().get("name"));
			
			mapDetailsSubtitleTV = (TextView) mMainLayout.findViewById(R.id.mapDetailsSubtitleTV);
			mapDetailsSubtitleTV.setText((String)mMapItem.getItemData().get("street"));
			
			mThumbnailView = (RemoteImageView) mMainLayout.findViewById(R.id.mapDetailsThumbnailIV);
			
			bbox = mMapItem.getBoundingBox(targetSpatialReference);
			imgUrl = "http://maps.mit.edu/pub/rest/services/basemap/WhereIs_Base_Topo/MapServer/export?format=png24&transparent=false&f=image&bbox=" + bbox;
			mThumbnailView.setURL(imgUrl);				
			
			mThumbnailView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//MITMapActivity.viewMapItem(mActivity, mMapItem);
				}
			});

			// Google Map Button
			mapGoogleMapBtn = (Button) mMainLayout.findViewById(R.id.mapGoogleMapBtn);

			mapGoogleMapBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String uri = "geo:0,0?q="+(String)mMapItem.getItemData().get("name")+"+near+"+ (String)mMapItem.getItemData().get("street")+",Cambridge,MA";
					Intent mapCall = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
					startActivity(mapCall);  
				}
			});
			
			// Bookmark Button
			mapBookmarkBtn = (Button) mMainLayout.findViewById(R.id.mapBookmarkBtn);
			MapItem dbItem = db.retrieveMapItem((String)mMapItem.getItemData().get("id"));
			if (dbItem == null) {
				mapBookmarkBtn.setText(R.string.map_bookmark_off);
				mapBookmarkBtn.setTag("off");
			}
			else {
				mapBookmarkBtn.setText(R.string.map_bookmark_on);
				mapBookmarkBtn.setTag("on");
			}

			
			mapBookmarkBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (v.getTag() == "off") {	
						MapsDB.getInstance(mContext).saveMapItem(mMapItem); 
						((Button)v).setText(R.string.map_bookmark_on);
						v.setTag("on");
						Toast.makeText(mContext, "map item saved", Toast.LENGTH_LONG).show();
					}
					else {
						MapsDB.getInstance(mContext).delete(mMapItem);
						((Button)v).setText(R.string.map_bookmark_off);
						v.setTag("off");
						Toast.makeText(mContext, "map item removed", Toast.LENGTH_LONG).show();						
					}
				}
			});

			
			// What's Here Contents
			mapDetailsHereTV = (TextView) tabHost.findViewById(R.id.mapDetailsHereTV);
			String bullet = new String(new int[] {0x2022}, 0 ,1);
			String text = "";
			ArrayList<MapItemContent> contents = mMapItem.getContents();
			for (int c = 0; c < contents.size(); c++) {
				text += " "  + bullet + " " + contents.get(c).getName() + "\n";				
			}
			
			if ("".equals(text)) text = "No Information Available";
			mapDetailsHereTV.setText(text);
			
			// Photo
			imgUrl = (String)mMapItem.getItemData().get("bldgimg") + "";
			mapDetailsPhotoView = (RemoteImageView) tabHost.findViewById(R.id.mapDetailsPhotoView);
			mapDetailsPhotoView.setScreenDensity(DisplayMetrics.DENSITY_MEDIUM);
			mapDetailsPhotosTV = (TextView) tabHost.findViewById(R.id.mapDetailsPhotosTV);
			
			if (imgUrl == null || imgUrl.trim().length() == 0) {
				mapDetailsPhotosTV.setText("(No Photo Available)");
				mapDetailsPhotoView.setVisibility(View.GONE);
			}
			else {
				mapDetailsPhotoView.setURL(imgUrl);				
				mapDetailsPhotosTV.setText("view from " + (String)mMapItem.getItemData().get("viewangle"));
			}
			
			tabHost.setFocusable(false);	
			
			return mMainLayout;
		}
		
		@Override
		public void updateView() {
			// all the populating of view is done in the constructor
		}


		@Override
		public LockingScrollView getVerticalScrollView() {
			return null;
		}


		@Override
		public void onDestroy() {
			// TODO Auto-generated method stub
			
		}
	}

	@Override
	public boolean isModuleHomeActivity() {
		return false;
	}

	@Override
	protected NewModule getNewModule() {
		// TODO Auto-generated method stub
		return new MapDetailsModule();
	}

	
	
	@Override
	protected void onOptionSelected(String optionId) {
	    if (optionId.equals(MENU_BOOKMARKS)) {
			Intent i = new Intent(mContext, MapBookmarksActivity.class); 
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			mContext.startActivity(i);
	    }
		
	}
		
}
