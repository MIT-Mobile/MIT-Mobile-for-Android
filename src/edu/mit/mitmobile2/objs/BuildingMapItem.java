package edu.mit.mitmobile2.objs;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.mit.mitmobile2.CommonActions;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.maps.MITMapBrowseCatsActivity;
import edu.mit.mitmobile2.maps.MITMapBrowseResultsActivity;
import edu.mit.mitmobile2.maps.MITMapDetailsSliderActivity;
import edu.mit.mitmobile2.maps.MITMapView2;
import edu.mit.mitmobile2.maps.MapAbstractionObject;
import edu.mit.mitmobile2.maps.MapBaseActivity;
import edu.mit.mitmobile2.maps.MapData;

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
	public View getCallout(Context mContext, ArrayList<? extends MapItem> mapItems) {
		return null;
	}

	@Override
	public View getCallout(final Context mContext, final ArrayList<? extends MapItem> mapItems, final int position) {

		Log.d(TAG,"position = " + position);
		Log.d(TAG,"BuildingMapItem getCallout");
		Log.d(TAG,"map item index = " + position);
		Log.d(TAG,"displayName = " + this.getItemData().get("displayName"));
		String buildingName = (String)this.getItemData().get("displayName");
		String buildingNumber = (String)this.getItemData().get("bldgnum");

   		LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout calloutLayout = (LinearLayout) inflater.inflate(R.layout.map_building_callout, null);
		
		TextView calloutBuildingNumber = (TextView)calloutLayout.findViewById(R.id.callout_building_number);
		calloutBuildingNumber.setText(buildingNumber);

		TextView calloutBuildingName = (TextView)calloutLayout.findViewById(R.id.callout_building_name);
		calloutBuildingName.setText(buildingName);
		
		//calloutLayout.on
		calloutLayout.setOnClickListener(new View.OnClickListener() {
		
	            @Override
	            public void onClick(View v) {
	            	Intent i = new Intent(mContext, MITMapDetailsSliderActivity.class); 
	            	//i.putExtra(MapBaseActivity.MAP_DATA_KEY, mapItems);
	            	i.putParcelableArrayListExtra(MITMapView2.MAP_DATA_KEY, (ArrayList<? extends Parcelable>) mapItems);
	            	i.putExtra(MITMapView2.MAP_ITEM_INDEX_KEY, position);
	            	mContext.startActivity(i);
	            }
	        });
		
		return calloutLayout;
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
		offsetY = source.readInt();
		symbol = source.readInt();
		verticalAlign = source.readInt();
		query = source.readString();
		mapPoints = source.readArrayList(MapPoint.class.getClassLoader());
		contents = source.readArrayList(MapItemContent.class.getClassLoader());
	}
	
    public static final Parcelable.Creator<BuildingMapItem> CREATOR = new Parcelable.Creator<BuildingMapItem>() {
        public BuildingMapItem createFromParcel(Parcel in) {
            return new BuildingMapItem(in);
        }

        public BuildingMapItem[] newArray(int size) {

            return new BuildingMapItem[size];
        }

    };

	
}
