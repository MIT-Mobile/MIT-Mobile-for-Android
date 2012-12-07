package edu.mit.mitmobile2.maps;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
	
	public MapItemsAdapter(Context context, List<MapItem> items) {
		super(context, items, R.layout.boring_action_row);
		mMapItems = items;
		mContext = context;
	}

	@Override
	public void updateView(MapItem mapItem, View view) {
		TwoLineActionRow row = (TwoLineActionRow) view;
		
		String displayName = (String)mapItem.getItemData().get("displayName");
		String name = (String)mapItem.getItemData().get("name");
		
		if(displayName != null && !displayName.equals("")) {
			row.setTitle(displayName);
		} else {
			row.setTitle(name);
		}
	}
	
	public AdapterView.OnItemClickListener showOnMapOnItemClickListener() {
		return new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View row, int position, long id) {
				Log.d(TAG,"adapter view click position = " + position + " id = " + id);
				MapItem mapItem = getItem(position);
				MapData mapData = new MapData();
				mapData.getMapItems().add(mapItem);
				
				//TEST JSON
				Log.d(TAG,"mapData json = " + mapData.toJSON());
				// END TEST GSON
				Intent i = new Intent(mContext, MITMapActivity.class); 
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				i.putExtra(MITMapActivity.MAP_DATA_KEY, mapData.toJSON());
				//MapBaseActivity.launchNewMapItem(mContext, mapItem);
				Log.d(TAG,"before activity launch there are " + mapData.getMapItems().size() + " map items");
				Log.d(TAG,"before activity launch there are " + mapData.getMapItems().get(0).getMapPoints().size() + " map points");
				mContext.startActivity(i);
			}
		};
	}
	
	public AdapterView.OnItemClickListener showMapDetailsOnItemClickListener() {
		return new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View row, int position, long id) {
				MITMapDetailsSliderActivity.launchMapDetails(mContext, mMapItems, position);
			}
		};
	}
}