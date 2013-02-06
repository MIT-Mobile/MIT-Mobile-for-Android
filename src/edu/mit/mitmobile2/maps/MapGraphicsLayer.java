package edu.mit.mitmobile2.maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.esri.core.geometry.SpatialReference;

import edu.mit.mitmobile2.objs.MapItem;

public class MapGraphicsLayer {
	
	protected String layerName;
	protected Map<String,Integer> graphicIdMap; // map of graphic Id to the index of it's corresponding MapItem in the mapItems array
	protected ArrayList<MapItem> mapItems;
	
	public static int MODE_OVERWRITE = 1; // will clear the graphics layer before adding new graphics
	public static int MODE_APPEND = 2; // appends graphics to the graphics layer
		
	public MapGraphicsLayer() {
		this.layerName = "";
		this.mapItems = new ArrayList<MapItem>();
		this.graphicIdMap = new HashMap<String,Integer>();
	}

	public MapGraphicsLayer(String layerName) {
		this.layerName = layerName;
		this.mapItems = new ArrayList<MapItem>();
		this.graphicIdMap = new HashMap<String,Integer>();
	}

	public MapGraphicsLayer(String layerName,int wkid) {
		this.layerName = layerName;
		this.mapItems = new ArrayList<MapItem>();
		this.graphicIdMap = new HashMap<String,Integer>();
	}

	public String getLayerName() {
		return layerName;
	}

	public void setLayerName(String layerName) {
		this.layerName = layerName;
	}

	public Map<String, Integer> getGraphicIdMap() {
		return graphicIdMap;
	}

	public void setGraphicIdMap(Map<String, Integer> graphicIdMap) {
		this.graphicIdMap = graphicIdMap;
	}

	public ArrayList<MapItem> getMapItems() {
		return mapItems;
	}

	public void setMapItems(ArrayList<MapItem> mapItems) {
		this.mapItems = mapItems;
	}
	
}
