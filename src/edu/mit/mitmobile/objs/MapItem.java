package edu.mit.mitmobile.objs;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class MapItem implements Parcelable {
	
	public MapItem() {
		category = new ArrayList<String>();
		contents = new ArrayList<String>();
	}
	
	
	public double long_wgs84;
	public double lat_wgs84;
	
	public long sql_id = -1;  // not to confuse with "id"
	
	public ArrayList<String> category;
	public ArrayList<String> alts;
	public ArrayList<String> contents;
	public String name;
	public String displayName;
	public String id;
	public String snippets;
	public String street;
	public String floorplans;
	public String bldgimg;
	public String viewangle;
	public String bldgnum;
	
	public String query = "";
	
	public String toString() {
		return name;
	}
	
	public static final Parcelable.Creator<MapItem> CREATOR = new Parcelable.Creator<MapItem>() {

		@Override
		public MapItem createFromParcel(Parcel source) {
			MapItem mapItem = new MapItem();
			mapItem.readFromParcel(source);
			return mapItem;
		}

		@Override
		public MapItem[] newArray(int size) {
			return new MapItem[size];
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flag) {
		out.writeDouble(long_wgs84);
		out.writeDouble(lat_wgs84);
		
		out.writeLong(sql_id);
		
		out.writeList(category);
		out.writeList(contents);
		out.writeList(alts);
		
		out.writeString(name);
		out.writeString(displayName);
		out.writeString(id);
		out.writeString(snippets);
		out.writeString(street);
		out.writeString(floorplans);
		out.writeString(bldgimg);
		out.writeString(viewangle);
		out.writeString(bldgnum);
		
		out.writeString(query);
	}
	
	@SuppressWarnings("unchecked")
	private void readFromParcel(Parcel in) {
		long_wgs84 = in.readDouble();
		lat_wgs84 = in.readDouble();
		
		sql_id = in.readLong();
		
		category = in.readArrayList(String.class.getClassLoader());
		contents = in.readArrayList(String.class.getClassLoader());
		alts = in.readArrayList(String.class.getClassLoader());
		
		name = in.readString();
		displayName = in.readString();
		id = in.readString();
		snippets = in.readString();
		street = in.readString();
		floorplans = in.readString();
		bldgimg = in.readString();
		viewangle = in.readString();
		bldgnum = in.readString();
		
		query = in.readString();
	}
}
