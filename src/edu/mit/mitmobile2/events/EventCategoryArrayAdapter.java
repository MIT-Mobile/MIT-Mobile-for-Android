package edu.mit.mitmobile2.events;

import java.util.List;

import android.content.Context;
import android.view.View;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SimpleArrayAdapter;
import edu.mit.mitmobile2.TwoLineActionRow;
import edu.mit.mitmobile2.objs.EventCategoryItem;

public class EventCategoryArrayAdapter extends SimpleArrayAdapter<EventCategoryItem> {

	Integer mParentCategoryID;
	
	public EventCategoryArrayAdapter(Context context, List<EventCategoryItem> items, Integer parentCategoryID) {
		super(context, items, R.layout.events_category_row);
		mParentCategoryID = parentCategoryID;
	}

	@Override
	public void updateView(EventCategoryItem item, View view) {
		TwoLineActionRow actionRow = (TwoLineActionRow) view;
		
		// for subcategories we want the root category
		// to be call "All categoryName"
		if(mParentCategoryID != null && mParentCategoryID == item.catid) {
			actionRow.setTitle("All " + item.name);
		} else {
			actionRow.setTitle(item.name);
		}
	}	
}
