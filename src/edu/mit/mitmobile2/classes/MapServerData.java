package edu.mit.mitmobile2.classes;

import java.util.ArrayList;


public class MapServerData {
	
	private ArrayList<MapBaseLayer> baseMaps = new ArrayList<MapBaseLayer>();
	private ArrayList<MapFeatureLayer> features = new ArrayList<MapFeatureLayer>();

	public ArrayList<MapBaseLayer> getBaseMaps() {
		return baseMaps;
	}

	public void setBaseMaps(ArrayList<MapBaseLayer> baseMaps) {
		this.baseMaps = baseMaps;
	}

	public ArrayList<MapFeatureLayer> getFeatures() {
		return features;
	}

	public void setFeatures(ArrayList<MapFeatureLayer> features) {
		this.features = features;
	}
		
}
