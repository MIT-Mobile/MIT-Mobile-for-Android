package edu.mit.mitmobile2.maps;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.esri.core.geometry.SpatialReference;

public class MapAbstractionObject {
	private LinkedHashMap<String,MapBaseLayer> baseLayers;
	private LinkedHashMap<String,MapGraphicsLayer> graphicsLayers;
	private String baseLayerName = "";
	protected Map<String, Long> layerIdMap; // map of user selected "layer name" to arcgis generated layer ID
	protected SpatialReference spatialReference;
	public static int DEFAULT_WKID = 4326;
	
	public MapAbstractionObject() {
		this.baseLayers = new LinkedHashMap<String,MapBaseLayer>();
		this.graphicsLayers = new LinkedHashMap<String,MapGraphicsLayer>();
		this.layerIdMap = new HashMap<String, Long>();
		this.spatialReference = SpatialReference.create(MapAbstractionObject.DEFAULT_WKID);
	}

	public LinkedHashMap<String, MapBaseLayer> getBaseLayers() {
		return baseLayers;
	}

	public void setBaseLayers(LinkedHashMap<String, MapBaseLayer> baseLayers) {
		this.baseLayers = baseLayers;
	}

	public LinkedHashMap<String, MapGraphicsLayer> getGraphicsLayers() {
		return graphicsLayers;
	}

	public void setGraphicsLayers(
			LinkedHashMap<String, MapGraphicsLayer> graphicsLayers) {
		this.graphicsLayers = graphicsLayers;
	}

	public String getBaseLayerName() {
		return baseLayerName;
	}

	public void setBaseLayerName(String baseLayerName) {
		this.baseLayerName = baseLayerName;
	}

	public Map<String, Long> getLayerIdMap() {
		return layerIdMap;
	}

	public void setLayerIdMap(Map<String, Long> layerIdMap) {
		this.layerIdMap = layerIdMap;
	}

	public SpatialReference getSpatialReference() {
		return spatialReference;
	}

	public void setSpatialReference(SpatialReference spatialReference) {
		this.spatialReference = spatialReference;
	}

	public static int getDEFAULT_WKID() {
		return DEFAULT_WKID;
	}

	public static void setDEFAULT_WKID(int dEFAULT_WKID) {
		DEFAULT_WKID = dEFAULT_WKID;
	}
		
}
