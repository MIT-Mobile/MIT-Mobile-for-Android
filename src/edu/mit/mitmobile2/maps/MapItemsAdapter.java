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
		
		if(mapItem.displayName != null && !mapItem.displayName.equals("")) {
			row.setTitle(mapItem.displayName);
		} else {
			row.setTitle(mapItem.name);
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
				Intent i = new Intent(mContext, MITMapActivity2.class); 
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				i.putExtra(MITMapActivity2.MAP_DATA_KEY, mapData);
				//MapBaseActivity2.launchNewMapItem(mContext, mapItem);
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
