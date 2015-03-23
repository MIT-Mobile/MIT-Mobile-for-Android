package edu.mit.mitmobile2.maps;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SimpleArrayAdapter;
import edu.mit.mitmobile2.TwoLineActionRow;
import edu.mit.mitmobile2.objs.MapItem;

public class MapItemsAdapter extends SimpleArrayAdapter<MapItem> {

	static final String TAG = "MapItemsAdapter"; 
	List<MapItem> mMapItems;
	Context mContext;
	String mCategory = "";
	
	public MapItemsAdapter(Context context, List<MapItem> items) {
		super(context, items, R.layout.boring_action_row);
		mMapItems = items;
		mContext = context;
		mCategory = "";
	}

	public MapItemsAdapter(Context context, List<MapItem> items, String category) {
		super(context, items, R.layout.boring_action_row);
		mMapItems = items;
		mContext = context;
		mCategory = category;
	}

	@Override
	public void updateView(MapItem mapItem, View view) {
		TwoLineActionRow row = (TwoLineActionRow) view;
		if (mCategory.equals("")) {
			row.setTitle(mapItem.getMapItemName());
		}
		else {
			String displayName = (String)mapItem.getItemData().get("displayName");
			String name = (String)mapItem.getItemData().get("name");
			String bldgnum = (String)mapItem.getItemData().get("bldgnum");
	
			// Building Number searches have categories with parenthesis
			if (mCategory.contains("(")) {
				row.setTitle(bldgnum);			
			}
			else if(displayName != null && !displayName.equals("")) {
				row.setTitle(displayName);
			} else {
				row.setTitle(name);
			}
		}
	}
	
	public AdapterView.OnItemClickListener showOnMapOnItemClickListener() {
		return new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View row, int position, long id) {
				ArrayList<MapItem> mapItems = new ArrayList<MapItem>();
				MapItem mapItem = getItem(position);
				mapItems.add(mapItem);
				
				Intent i = new Intent(mContext, MITMapActivity.class); 
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				i.putExtra(MITMapView.MAP_ITEMS_KEY, mapItems);
				mContext.startActivity(i);
			}
		};
	}
	
	public AdapterView.OnItemClickListener showMapDetailsOnItemClickListener() {
		return new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View row, int position, long id) {
				//MITMapDetailsSliderActivity.launchMapDetails(mContext, mMapItems, position);
			}
		};
	}
}