package edu.mit.mitmobile2.maps;

import java.io.Serializable;
import java.util.ArrayList;

import edu.mit.mitmobile2.objs.MapItem;

public class MapData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int mode;
	private String layerName;
	private ArrayList<MapItem> mapItems;

	public static int MODE_OVERWRITE = 1; // will clear the graphics layer before adding new graphics
	public static int MODE_APPEND = 2; // appends graphics to the graphics layer

	public MapData() {
		super();
		this.mode = MapData.MODE_OVERWRITE;
		this.layerName = MapBaseActivity2.DEFAULT_GRAPHICS_LAYER;
		this.mapItems = new ArrayList<MapItem>();
	}
	public int getMode() {
		return mode;
	}
	public void setMode(int mode) {
		this.mode = mode;
	}
	public String getLayerName() {
		return layerName;
	}
	public void setLayerName(String layerName) {
		this.layerName = layerName;
	}
	public ArrayList<MapItem> getMapItems() {
		return mapItems;
	}
	public void setMapItems(ArrayList<MapItem> mapItems) {
		this.mapItems = mapItems;
	}
		
//	public static final Parcelable.Creator<MapItem> CREATOR = new Parcelable.Creator<MapItem>() {
//
//		@Override
//		public MapItem createFromParcel(Parcel source) {
//			MapItem mapItem = new MapItem();
//			mapItem.readFromParcel(source);
//			return mapItem;
//		}
//
//		@Override
//		public MapItem[] newArray(int size) {
//			return new MapItem[size];
//		}
//	};
//
//	@Override
//	public int describeContents() {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	@Override
//	public void writeToParcel(Parcel out, int flag) {
//		out.writeInt(mode);
//		out.writeString(layerName);	
//		out.writeTypedList(mapItems);
//	}
//	
//	@SuppressWarnings("unchecked")
//	private void readFromParcel(Parcel in) {
//		mode = in.readInt();
//        layerName = in.readString();
//        in.readTypedList(mapItems, MapItem.CREATOR);
//    }
	
}
