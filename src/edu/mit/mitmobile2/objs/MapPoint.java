package edu.mit.mitmobile2.objs;

import android.os.Parcel;
import android.os.Parcelable;


public class MapPoint implements Parcelable {
	
	public MapPoint() {
		
	}
	
	public MapPoint(double lat_wgs84, double long_wgs84) {
		this.lat_wgs84 = lat_wgs84;
		this.long_wgs84 = long_wgs84;
	}

	public MapPoint(String lat_wgs84, String long_wgs84) {
		Double lat = Double.parseDouble(lat_wgs84);
		Double lon = Double.parseDouble(long_wgs84);
		this.lat_wgs84 = lat.doubleValue();
		this.long_wgs84 = lon.doubleValue();
	}

	public MapPoint(Parcel source){
        super(); 
        readFromParcel(source);
	}
	
	public double long_wgs84;
	public double lat_wgs84;

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		dest.writeDouble(long_wgs84);
		dest.writeDouble(lat_wgs84);
	}
	
	public void readFromParcel(Parcel source) {
		long_wgs84 = source.readDouble();
		lat_wgs84 = source.readDouble();
	}
	
    public static final Parcelable.Creator<MapPoint> CREATOR = new Parcelable.Creator<MapPoint>() {
        @Override
		public MapPoint createFromParcel(Parcel in) {
            return new MapPoint(in);
        }

        @Override
		public MapPoint[] newArray(int size) {
            return new MapPoint[size];
        }

    };
	
}
