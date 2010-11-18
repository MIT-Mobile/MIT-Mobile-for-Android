package edu.mit.mitmobile.maps;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;

import edu.mit.mitmobile.R;
import edu.mit.mitmobile.SimpleArrayAdapter;
import edu.mit.mitmobile.TwoLineActionRow;
import edu.mit.mitmobile.objs.MapItem;

public class MapItemsAdapter extends SimpleArrayAdapter<MapItem> {

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
				MapItem mapItem = getItem(position);
				MITMapActivity.launchNewMapItem(mContext, mapItem);
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
