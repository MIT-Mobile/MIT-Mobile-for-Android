package edu.mit.mitmobile2.shuttles;

import java.util.ArrayList;
import java.util.List;

import edu.mit.mitmobile2.SectionHeader;

import android.content.Context;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

public class ShuttleRouteArrayAdapter implements ListAdapter {

	private static final int HEADER_VIEW_TYPE = 0;
	private static final int ITEM_VIEW_TYPE = 1;
	private static final int NOT_FOUND = -1;
	
	private ArrayList<List<?>> mItemLists;
	private ArrayList<String> mTitles;
	private SectionListHeaderView mHeaderBuilder;
	private SectionListItemView mItemBuilder;
	private final DataSetObservable mDataSetObservable = new DataSetObservable();
	
	private Context mContext;
	
	public interface SectionListHeaderView {
		public View getView(String title, View convertView, ViewGroup parent);
	}
	
	public interface SectionListItemView {
		public View getView(Object item, View convertView, ViewGroup parent);
	}

	public ShuttleRouteArrayAdapter(Context context, SectionListItemView itemBuilder) {
		mTitles = new ArrayList<String>();
		mItemLists = new ArrayList<List<?>>();
		mContext = context;
		mItemBuilder = itemBuilder;
		mHeaderBuilder = defaultSectionListHeaderView();
	}

	public ShuttleRouteArrayAdapter(Context context, SectionListHeaderView headerBuilder, SectionListItemView itemBuilder) {
		mTitles = new ArrayList<String>();
		mItemLists = new ArrayList<List<?>>();
		mContext = context;
		mItemBuilder = itemBuilder;
		mHeaderBuilder = headerBuilder;
	}
	
	private SectionListHeaderView defaultSectionListHeaderView() {
		return new SectionListHeaderView() {
			public View getView(String title, View convertView, ViewGroup parent) {
				SectionHeader v = (SectionHeader) convertView;
				if (v == null) {
					v = new SectionHeader(mContext, title);
				}
				v.setText(title);
				return v;
			}
		};
	}
	
	public void addSection(String title, List<?> items) {
		if (title != null && items != null && items.size() != 0) {
			mTitles.add(title);
			mItemLists.add(items);
		}
		assert (mTitles.size() == mItemLists.size());
	}
	
	public void clear() {
		mTitles = new ArrayList<String>();
		mItemLists = new ArrayList<List<?>>();
	}
	

	/********************* ListAdapter interface **********************/
	
	@Override
	public int getCount() {
		int total = 0;
		for (List<?> items : mItemLists) {
			total += 1 + items.size();
		}
		return total;
	}

	@Override
	public Object getItem(int position) {
		int sectionCount = mTitles.size();
		int sectionId = 0;

		int itemId = position;

		for (List<?> items : mItemLists) {
			int count = items.size() + 1;
			if (itemId == 0 && sectionId < sectionCount) {
				return mTitles.get(sectionId);
			} else if (itemId > 0 && itemId < count) {
				return items.get(itemId - 1);
			}
			itemId -= count;
			sectionId++;
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO: what is this method supposed to do??
		Log.d("ShuttleRouteArrayAdapter", "getting item " + String.valueOf(position));
		return 0;
	}

	@Override
	public int getItemViewType(int position) {
		int currentMin = 0;
		for (List<?> items : mItemLists) {
			int count = items.size() + 1;
			if (position == currentMin)
				return HEADER_VIEW_TYPE;
			else if (position > currentMin && position - currentMin < count)
				return ITEM_VIEW_TYPE;
			currentMin += count;
		}
		
		return NOT_FOUND;
	}
	
	private int itemIdForPosition(int position) {
		int index = position;
		int count = 0;
		for (List<?> items : mItemLists) {
			if (position > count)
				index--;
			count += items.size() + 1;
		}
		return index;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View v = convertView;
		
		int viewType = this.getItemViewType(position);
		if (viewType == HEADER_VIEW_TYPE) {
			String title = (String) this.getItem(position);
			v = mHeaderBuilder.getView(title, convertView, parent);
		} else if (viewType == ITEM_VIEW_TYPE) {
			Object object = this.getItem(position);
			v = mItemBuilder.getView(object, v, parent);
			v.setTag(new Integer(itemIdForPosition(position)));
		}
		
		return v;
	}

	@Override
	public int getViewTypeCount() {
		// 1 header type, 1 regular item type
		return 2;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isEmpty() {
		// TODO Decide whether this behavior is desirable
		return false;
	}
	
	/******************** ArrayAdapter-like methods *******************/
	// implementations copied from BaseAdapter.java and ArrayAdapter.java
	
	public void notifyDataSetChanged() {
		mDataSetObservable.notifyChanged();
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		mDataSetObservable.registerObserver(observer);
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		mDataSetObservable.unregisterObserver(observer);
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public boolean isEnabled(int position) {
		int viewType = this.getItemViewType(position);
		return viewType == ITEM_VIEW_TYPE;
	}
	
}
