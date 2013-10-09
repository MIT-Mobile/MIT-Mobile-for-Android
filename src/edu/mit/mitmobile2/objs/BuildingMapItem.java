package edu.mit.mitmobile2.objs;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.maps.MITMapDetailsSliderActivity;
import edu.mit.mitmobile2.maps.MITMapView;

public class BuildingMapItem extends MapItem implements Parcelable {
	
	public static String TAG = "BuildingMapItem";
	
	public BuildingMapItem() {
		super();
		// TODO Auto-generated constructor stub
	}

	public BuildingMapItem(Parcel source){
        super(); 
        readFromParcel(source);
	}
	
	@Override
	public View getCallout(Context mContext) {
		return null;
	}
	
	@Override
	public View getCallout(Context mContext, MapItem mapItem) {
		final ArrayList<MapItem> mapItems = new ArrayList<MapItem>();
		mapItems.add(mapItem);
		return this.getCallout(mContext, mapItems, 0);		
	}

	
	@Override
	public View getCallout(Context mContext, ArrayList<? extends MapItem> mapItems) {
		return this.getCallout(mContext, mapItems, 0);
	}

	
	@Override
	public View getCallout(final Context mContext, final ArrayList<? extends MapItem> mapItems, final int position) {

   		LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout calloutLayout = (LinearLayout) inflater.inflate(R.layout.map_item_callout, null);
		TextView calloutView = (TextView) inflater.inflate(R.layout.map_building_callout, null);
		calloutView.setText(this.getMapItemName());
		calloutLayout.addView(calloutView);		
		calloutLayout.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		
		calloutLayout.setOnClickListener(new View.OnClickListener() {
		
			@SuppressWarnings("unchecked")
				@Override
	            public void onClick(View v) {
	            	Intent i = new Intent(mContext, MITMapDetailsSliderActivity.class); 
	            	i.putParcelableArrayListExtra(MITMapView.MAP_ITEMS_KEY, (ArrayList<? extends Parcelable>) mapItems);
	            	i.putExtra(MITMapView.MAP_ITEM_INDEX_KEY, position);
	            	mContext.startActivity(i);
	            }
	        });
		
		return calloutLayout;
	}

	@Override
	public String getMapItemName() {
		String displayName = (String)this.getItemData().get("displayName");
		String name = (String)this.getItemData().get("name");
		String bldgnum = (String)this.getItemData().get("bldgnum");
		String calloutText = "";
		// Building # but no name
		if (name.equals("Building " + bldgnum)) {
			calloutText = name;			
		}

		// separate building number and name
		else if ( (bldgnum.length() > 0) && (!name.equals("Building " + bldgnum))) {
			calloutText = "Building " + bldgnum + " (" + name + ")";
		}
        
		// name but no building number
		else {
			if (displayName != null && !displayName.equals("")) {
				calloutText = displayName;			
			}
			else {
				calloutText = name;
			}
		}
		return calloutText;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		dest.writeInt(geometryType);
		dest.writeInt(horizontalAlign);
		dest.writeInt(index);
		dest.writeMap(itemData);
		dest.writeInt(lineColor);
		dest.writeInt(lineWidth);
		dest.writeString(mapItemClass);
		dest.writeInt(offsetX);		
		dest.writeInt(offsetY);
		dest.writeInt(symbol);
		dest.writeInt(verticalAlign);
		dest.writeString(query);
		dest.writeList(mapPoints);
		dest.writeList(contents);
	}
	
	public void readFromParcel(Parcel source) {
		geometryType = source.readInt();
		horizontalAlign = source.readInt();
		index = source.readInt();
		itemData = source.readHashMap(HashMap.class.getClassLoader());
		lineColor = source.readInt();
		lineWidth = source.readInt();
		mapItemClass = source.readString();
		offsetX = source.readInt();		
		offsetY = source.readInt();
		symbol = source.readInt();
		verticalAlign = source.readInt();
		query = source.readString();
		mapPoints = source.readArrayList(MapPoint.class.getClassLoader());
		contents = source.readArrayList(MapItemContent.class.getClassLoader());
	}
	
    public static final Parcelable.Creator<BuildingMapItem> CREATOR = new Parcelable.Creator<BuildingMapItem>() {
        @Override
		public BuildingMapItem createFromParcel(Parcel in) {
            return new BuildingMapItem(in);
        }

        public BuildingMapItem[] newArray(int size) {

            return new BuildingMapItem[size];
        }

    };

	
}
