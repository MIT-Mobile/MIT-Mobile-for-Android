package edu.mit.mitmobile2.maps;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;
import edu.mit.mitmobile2.LockingScrollView;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SliderInterface;
import edu.mit.mitmobile2.SliderListNewModuleActivity;
import edu.mit.mitmobile2.TabConfigurator;
import edu.mit.mitmobile2.objs.MapItem;
import edu.mit.mitmobile2.objs.PersonItem.PersonDetailViewMode;
import edu.mit.mitmobile2.people.PeopleDetailActivity;
import edu.mit.mitmobile2.people.PeopleDetailItemLayout;

public class MITMapDetailsSliderActivity extends SliderListNewModuleActivity {
	
	private final static String TAG = "MITMapDetailsSliderActivity";
	public static final String UID_KEY = "uid";
	public static final String MAP_ITEM_INDEX = "map_item_index";
	public static final String SEARCH_TERM_KEY = "search_term";
	public static final String RECENTLY_VIEWED_FLAG = "show_recents";

	private MapData mapData	= null;

	private List<MapItem> mMapItems = Collections.emptyList();
	private int mapItemIndex = 0;
	
	private final static int EDIT_CONTACT = 0;
	private final static int EDIT_CONTACT_REQUEST = 1;
	private final static String EDIT_CONTACT_TEXT = "Add to existing";
	
	private final static int NEW_CONTACT = 1;
	private final static String NEW_CONTACT_TEXT = "Create new contact";
	
	private Context mContext;
	Activity mActivity;
	
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
		
		Bundle extras = getIntent().getExtras();
		if(extras != null) {
			List<MapItem> mapItems = null;
			if(extras.containsKey(MapBaseActivity.MAP_DATA_KEY)) {
				String mapDataJson = extras.getString(MapBaseActivity.MAP_DATA_KEY);
				mapData = MapData.fromJSON(mapDataJson);	
				mapItems = mapData.getMapItems();
			} 	

			if(extras.containsKey(MapBaseActivity.MAP_ITEM_INDEX_KEY)) {
				mapItemIndex = extras.getInt(MapBaseActivity.MAP_ITEM_INDEX_KEY);
			}

			setMapItems(mapItems,mapItemIndex);
		} 
	}
	
	private void setMapItems(List<MapItem> mapItems, int position) {
		mMapItems = mapItems;
		int totalMapItems = mMapItems.size();
		
		for(int index = 0; index < totalMapItems; ++index) {
			MapItem mapItem = mMapItems.get(index);
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
			MapItem mapItem = mapData.getMapItems().get(mapItemIndex);

			LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mMainLayout = inflator.inflate(R.layout.map_details, null);
			TabHost tabHost;
			TabHost.TabSpec specHere;
			TabHost.TabSpec specPhotos;
			
			tabHost = (TabHost) mMainLayout.findViewById(R.id.mapDetailsTH);  
			tabHost.setup();  // NEEDED!!!

			TabConfigurator tabConfigurator = new TabConfigurator(mActivity, tabHost);
			tabConfigurator.addTab("What's Here", R.id.mapDetailsHereLL);
			tabConfigurator.addTab("Photo", R.id.mapDetailsPhotosLL);
			tabConfigurator.configureTabs();
			
			TextView mapDetailsQueryTV = (TextView) mMainLayout.findViewById(R.id.mapDetailsQueryTV);
			mapDetailsQueryTV.setText("'query' was found in");

			TextView mapDetailsTitleTV = (TextView) mMainLayout.findViewById(R.id.mapDetailsTitleTV);
			mapDetailsTitleTV.setText((String)mapItem.getItemData().get("name"));
			
			TextView mapDetailsSubtitleTV = (TextView) mMainLayout.findViewById(R.id.mapDetailsSubtitleTV);
			mapDetailsSubtitleTV.setText((String)mapItem.getItemData().get("street"));
			
			ImageView mThumbnailView = (ImageView) mMainLayout.findViewById(R.id.mapDetailsThumbnailIV);
			mThumbnailView.setScaleType(ScaleType.CENTER);
			Bitmap bitmap = mapItem.getThumbnail();
			mThumbnailView.setImageBitmap(mapItem.getThumbnail());
			//mThumbnailView.setImageResource(R.drawable.busybox);
			
			mThumbnailView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//MITMapActivity.viewMapItem(mActivity, mapItem);
				}
			});

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
		// TODO Auto-generated method stub
		
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
	
}
