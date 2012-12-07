package edu.mit.mitmobile2.objs;

import java.io.Serializable;

public class MapPoint implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	
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

	public double long_wgs84;
	public double lat_wgs84;

}
