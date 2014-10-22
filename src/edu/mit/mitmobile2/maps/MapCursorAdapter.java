package edu.mit.mitmobile2.maps;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import edu.mit.mitmobile2.objs.MapItem;

public class MapCursorAdapter extends CursorAdapter {
	
	//private final static String TAG = "MapCursorAdapter";
	MapAdapterHelper mMapAdapterHelper;
	
	public MapCursorAdapter(Context context, ListView listView, MapModel mapModel, Cursor cursor) {
		super(context, cursor);
		mMapAdapterHelper  = new MapAdapterHelper(listView, mapModel);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		final MapItem mi = MapsDB.retrieveMapItem(cursor);
		mMapAdapterHelper.populateView(view, mi, true);
	}
	
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = mMapAdapterHelper.createBlankView(context);
		
		bindView(view, context, cursor);
		
		return view;
	}

}
