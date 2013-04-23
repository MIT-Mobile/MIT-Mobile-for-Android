package edu.mit.mitmobile2.maps;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.SpatialReference;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;
import android.widget.ToggleButton;
import edu.mit.mitmobile2.CommonActions;
import edu.mit.mitmobile2.IdEncoder;
import edu.mit.mitmobile2.LockingScrollView;
import edu.mit.mitmobile2.MITMenuItem;
import edu.mit.mitmobile2.MITPlainSecondaryTitleBar;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.OnMITMenuItemListener;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.RemoteImageView;
import edu.mit.mitmobile2.SliderInterface;
import edu.mit.mitmobile2.SliderListNewModuleActivity;
import edu.mit.mitmobile2.TabConfigurator;
import edu.mit.mitmobile2.news.NewsDetailsActivity;
import edu.mit.mitmobile2.objs.MapItem;
import edu.mit.mitmobile2.objs.MapItemContent;
import edu.mit.mitmobile2.objs.MapPoint;
import edu.mit.mitmobile2.objs.NewsItem;
import edu.mit.mitmobile2.objs.PersonItem.PersonDetailViewMode;
import edu.mit.mitmobile2.people.PeopleDetailActivity;
import edu.mit.mitmobile2.people.PeopleDetailItemLayout;

public class MITMapDetailsSliderActivity extends SliderListNewModuleActivity {
	
	private final static String TAG = "MITMapDetailsSliderActivity";
	public static final String UID_KEY = "uid";
	public static final String MAP_ITEM_INDEX = "map_item_index";
	public static final String SEARCH_TERM_KEY = "search_term";
	public static final String RECENTLY_VIEWED_FLAG = "show_recents";
	public static final int TARGET_WKID = 102113; // the wikid of the map used to export images 
	private static final String MENU_BOOKMARKS = "bookmarks";
	private static final String MENU_SHARE = "menu_share";

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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mContext = this;
		mActivity = (Activity) mContext;
		targetSpatialReference = SpatialReference.create(TARGET_WKID);
		
		Bundle extras = getIntent().getExtras();
		if(extras != null) {
			List<MapItem> mapItems = null;
			if(extras.containsKey(MITMapActivity.MAP_DATA_KEY)) {
				mapItems = (ArrayList)extras.getParcelableArrayList(MITMapView.MAP_DATA_KEY);
				Log.d(TAG,"number of map items = " + mapItems.size());
			} 	

			if(extras.containsKey(MITMapActivity.MAP_ITEM_INDEX_KEY)) {
				mapItemIndex = extras.getInt(MITMapActivity.MAP_ITEM_INDEX_KEY);
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
				Log.d(TAG,"first map point = " + mapItem.getMapPoints().get(0).lat_wgs84 + ":" + mapItem.getMapPoints().get(0).long_wgs84);
			}
			String headerTitle = Integer.toString(index+1) + " of " + Integer.toString(totalMapItems);
			addScreen(new MapSliderInterface(mapItem), (String)mapItem.getItemData().get("name"), headerTitle);
		}
		
		//int position = PeopleModel.getPosition(mPeople, uid);
		setPosition(position);
	}
	
//	private void populateAddContactIntent(Intent intent, PersonItem person, boolean editOrInsert) {
//		// the data that the add contact screen accepts does not quite match
//		// the structure of our data, so we do a reasonably good fit between the two
//		
//		// puts the full name in the firstname field (this is a limitation of android see link below)
//		// http://groups.google.com/group/android-developers/browse_thread/thread/39615ba0bbcfc62b
//		if(!editOrInsert) {
//			intent.putExtra(Insert.NAME, person.getName());
//		}
//		
//		addFields(intent, person.phone, 
//			new String[] {Insert.PHONE, Insert.SECONDARY_PHONE, Insert.TERTIARY_PHONE}, 
//			new String[] {Insert.PHONE_TYPE, Insert.SECONDARY_PHONE_TYPE, Insert.TERTIARY_PHONE_TYPE},	
//			CommonDataKinds.Phone.TYPE_WORK);
//
//		addFields(intent, person.email,
//			new String[] {Insert.EMAIL, Insert.SECONDARY_EMAIL, Insert.TERTIARY_EMAIL},
//			new String[] {Insert.EMAIL_TYPE, Insert.SECONDARY_EMAIL_TYPE, Insert.TERTIARY_EMAIL_TYPE},
//			CommonDataKinds.Email.TYPE_WORK);
//		
//		addField(intent, Insert.COMPANY, person.dept);
//		addField(intent, Insert.JOB_TITLE, person.title);
//		
//		addField(intent, Insert.POSTAL, person.office);
//		if(!person.office.isEmpty()) {
//			intent.putExtra(Insert.POSTAL_TYPE, ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK);
//		}
//	}
	
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
	
	private void addField(Intent intent, String extraField, List<String> values) {
		if(!values.isEmpty()) {
			intent.putExtra(extraField, values.get(0));
		}
	}
	
	private void addFields(Intent intent, List<String> values, String[] fields, String[] fieldTypes, int fieldType) {
		
		for(int i = 0; i < 3; i++) {
			if(values.size() > i) {
				intent.putExtra(fields[i], values.get(0));
				intent.putExtra(fieldTypes[i], fieldType);
			}
		}
	}
	
//	private void addToContact() {
//		final PersonItem person = mPeople.get(getPosition());
//		// create a dialog asking to create or add contact
//		AlertDialog.Builder builder = new AlertDialog.Builder(this);
//		builder.setTitle("Add Contact");
//		builder.setItems(new String[] {EDIT_CONTACT_TEXT, NEW_CONTACT_TEXT}, 
//			new DialogInterface.OnClickListener() {
//			
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					if(which == EDIT_CONTACT) {
//						Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
//						mPersonToAddToContacts = person;
//						PeopleDetailActivity.this.startActivityForResult(intent, EDIT_CONTACT_REQUEST);
//					} else if(which == NEW_CONTACT) {
//						Intent intent = new Intent(Insert.ACTION, ContactsContract.Contacts.CONTENT_URI);
//						populateAddContactIntent(intent, person, false);
//						PeopleDetailActivity.this.startActivity(intent);
//					}
//				}
//			}
//		);
//
//		builder.setNegativeButton("Cancel", null);
//		builder.create().show();
//	}
	
	private class MapSliderInterface implements SliderInterface {
		private MapItem mMapItem;
		private View mMainLayout;
		private ViewGroup mListItemsLayout;
		
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
			TabHost.TabSpec specHere;
			TabHost.TabSpec specPhotos;
			
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
					Intent mapCall = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:" + mMapItem.getCenter().lat_wgs84 + "," + mMapItem.getCenter().long_wgs84 + "?z=17"));
					startActivity(mapCall);  
				}
			});
			
			// Bookmark Button
			mapBookmarkBtn = (Button) mMainLayout.findViewById(R.id.mapBookmarkBtn);
			MapItem dbItem = db.retrieveMapItem((String)mMapItem.getItemData().get("id"));
			if (dbItem == null) {
				Log.d(TAG,"map item " + mMapItem.getItemData().get("id") + " is not bookmarked");
				mapBookmarkBtn.setBackgroundResource(R.drawable.button_bookmark_off);
				mapBookmarkBtn.setTag("off");
			}
			else {
				Log.d(TAG,"map item " + mMapItem.getItemData().get("id") + " is bookmarked");
				mapBookmarkBtn.setBackgroundResource(R.drawable.button_bookmark_on);
				mapBookmarkBtn.setTag("on");
			}

			
			mapBookmarkBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (v.getTag() == "off") {	
						MapsDB.getInstance(mContext).saveMapItem(mMapItem); 
						v.setBackgroundResource(R.drawable.button_bookmark_on);
						v.setTag("on");
						Toast.makeText(mContext, "map item saved", 1).show();
					}
					else {
						MapsDB.getInstance(mContext).delete(mMapItem);
						v.setBackgroundResource(R.drawable.button_bookmark_off);
						v.setTag("off");
						Toast.makeText(mContext, "map item removed", 1).show();						
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
			mapDetailsPhotosTV = (TextView) tabHost.findViewById(R.id.mapDetailsPhotosTV);
			
			if (imgUrl == null || imgUrl.trim().length() == 0) {
				mapDetailsPhotosTV.setText("(No Photo Available)");
				mapDetailsPhotoView.setVisibility(View.GONE);
			}
			else {
				Log.d(TAG,"image url = " + imgUrl);
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
		return new MapsModule();
	}

	@Override
	protected void onOptionSelected(String optionId) {
		Log.d(TAG,"optionId = " + optionId);
	    if (optionId.equals(MENU_BOOKMARKS)) {
			Intent i = new Intent(mContext, MapBookmarksActivity.class); 
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			mContext.startActivity(i);
	    }
		
	}
	
	private boolean hasAction(String type) {
	    List<String> typesWithActions = Arrays.asList("email", "phone", "office");
	    return typesWithActions.contains(type);
	}
	
	private void performAction(MapItem item) {
//	    if(item.getType().equals("email")) {
//		CommonActions.composeEmail(mContext, item.getValue());
//	    } else if(item.getType().equals("phone")) {
//		CommonActions.callPhone(mContext, item.getValue());
//	    } else if(item.getType().equals("office")) {
//		CommonActions.searchMap(mContext, item.getValue());
//	    }
	}
	
	private int getActionIconResourceId(String type) {
	    if (type.equals("email")) {
		return R.drawable.action_email;					
	    } else if(type.equals("phone")) {
		return R.drawable.action_phone;
	    } else if(type.equals("office")) {
		return R.drawable.action_map;
	    }
	    return -1;
	}
	
//    private Handler mapBitmapUiHandler = new Handler() {
//        @SuppressWarnings("unchecked")
//		@Override
//        public void handleMessage(Message msg) {
//
//            if (msg.arg1 == MobileWebApi.SUCCESS) {
//            	try {
//            		// set bitmap
//            		mThumbnailView.setImageBitmap((Bitmap)msg.obj);
//            		//map.getMapData().getMapItems().get(0).setThumbnail((Bitmap)msg.obj);
//            	}
//            	catch (Exception e) {
//            		Log.d(TAG,"mapBitmapUiHander exception");
//            		Log.d(TAG,e.getMessage());
//            	}
//            }
//            else if (msg.arg1 == MobileWebApi.ERROR) {
//
//            } 
//            else if (msg.arg1 == MobileWebApi.CANCELLED) {
//
//            }
//        }
//    };
//
//	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
//
//	    public DownloadImageTask(ImageView bmImage) {
//	    	mThumbnailView = bmImage;
//	    }
//
//	    protected Bitmap doInBackground(String... urls) {
//	    	Log.d(TAG,urls[0]);
//	        String urldisplay = urls[0];
//	        Bitmap mIcon11 = null;
//	        try {
//	            InputStream in = new java.net.URL(urldisplay).openStream();
//	            mIcon11 = BitmapFactory.decodeStream(in);
//	        } catch (Exception e) {
//	            Log.e("Error", e.getMessage());
//	            e.printStackTrace();
//	        }
//	        return mIcon11;
//	    }
//
//	    protected void onPostExecute(Bitmap result) {
//	        if (result == null) {
//	        	Log.d(TAG,"result is null");
//	        }
//	        else {
//	        	Log.d(TAG,"result height is " + result.getHeight());	        	
//	        	mThumbnailView.setImageBitmap(result);
//	        }
//	    }
//	}

}
