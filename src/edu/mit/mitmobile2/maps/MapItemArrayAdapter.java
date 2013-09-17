package edu.mit.mitmobile2.maps;

import java.util.List;

import android.content.Context;
import android.view.View;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SimpleArrayAdapter;
import edu.mit.mitmobile2.TwoLineActionRow;
import edu.mit.mitmobile2.objs.MapItem;

public class MapItemArrayAdapter extends SimpleArrayAdapter<MapItem> {

	Integer mParentCategoryID;
	
	public MapItemArrayAdapter(Context context, List<MapItem> items, Integer parentCategoryID) {
		super(context, items, R.layout.map_bookmarks_row);
		mParentCategoryID = parentCategoryID;
	}

	@Override
	public void updateView(MapItem item, View view) {
		TwoLineActionRow actionRow = (TwoLineActionRow) view;
		
		// for subcategories we want the root category
		// to be call "All categoryName"
		//actionRow.setTitle((String)item.getItemData().get("displayName"));
		actionRow.setTitle((String)item.getMapItemName());		
	}	
}
