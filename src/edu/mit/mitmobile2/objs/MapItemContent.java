package edu.mit.mitmobile2.objs;

import android.os.Parcel;
import android.os.Parcelable;

public class MapItemContent implements Parcelable {
	private String category;
	private String name;
	private String url;
	
	public MapItemContent() {
		category = "";
		name = "";
		url = "";
	}

	public MapItemContent(Parcel source){
        super(); 
        readFromParcel(source);
	}
	
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		dest.writeString(category);
		dest.writeString(name);
		dest.writeString(url);
	}
	
	public void readFromParcel(Parcel source) {
		category = source.readString();
		name = source.readString();
		url = source.readString();
	}
	
    public static final Parcelable.Creator<MapItemContent> CREATOR = new Parcelable.Creator<MapItemContent>() {
    	@Override
    	public MapItemContent createFromParcel(Parcel in) {
            return new MapItemContent(in);
        }

    	@Override
        public MapItemContent[] newArray(int size) {

            return new MapItemContent[size];
        }

    };

	
}
