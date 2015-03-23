package edu.mit.mitmobile2.maps;

import java.util.ArrayList;
import java.util.HashMap;


public class MapServerData {

	private String defaultBasemap = "";
	private HashMap<String,ArrayList<MapBaseLayer>> baseLayerGroup = new HashMap<String,ArrayList<MapBaseLayer>>();
	
	private ArrayList<MapFeatureLayer> features = new ArrayList<MapFeatureLayer>();

	public HashMap<String, ArrayList<MapBaseLayer>> getBaseLayerGroup() {
		return baseLayerGroup;
	}

	public void setBaseLayerGroup(HashMap<String, ArrayList<MapBaseLayer>> baseLayerGroup) {
		this.baseLayerGroup = baseLayerGroup;
	}

	public String getDefaultBasemap() {
		return defaultBasemap;
	}

	public void setDefaultBasemap(String defaultBasemap) {
		this.defaultBasemap = defaultBasemap;
	}

	public ArrayList<MapFeatureLayer> getFeatures() {
		return features;
	}

	public void setFeatures(ArrayList<MapFeatureLayer> features) {
		this.features = features;
	}
		
}
