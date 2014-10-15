package edu.mit.mitmobile2.maps;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.TruncatingTextView;
import edu.mit.mitmobile2.objs.MapItem;

public class MapAdapterHelper  {

	// cache bitmaps in memory
	// so png png decompression does not need to done
	// while scrolling the list
	// story_ids -> bitmaps
	HashMap<Integer, SoftReference<Bitmap>> mThumbnails;
	//private MapModel mMapModel;
	//private ListView mListView;
	
	public static String TAG = "NewsAdapterHelper";
	
	public MapAdapterHelper(ListView listView, MapModel mapModel) {
		mThumbnails = new HashMap<Integer, SoftReference<Bitmap>>();
		//mMapModel = mapModel;
		//mListView = listView;
	}

	public void populateView(View view, final MapItem mapItem, boolean saveThumbnail) {
		if (mapItem != null) {
			TruncatingTextView mapRowTV = (TruncatingTextView) view.findViewById(R.id.mapRowTV);
			mapRowTV.setText((String)mapItem.getItemData().get("displayName"));
			mapRowTV.requestLayout();
		}
	}
		
	public View createBlankView(Context context) {
		LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return vi.inflate(R.layout.map_row, null);
	}
}
