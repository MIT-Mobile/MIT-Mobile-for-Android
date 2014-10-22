package edu.mit.mitmobile2.maps;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.esri.core.geometry.SpatialReference;

import edu.mit.mitmobile2.objs.MapItem;
import edu.mit.mitmobile2.objs.MapPoint;

public class MapData {

	private int mode;
	private LinkedHashMap<String,MapGraphicsLayer> mapGraphicsLayers;
	//private String layerName;
	//protected ArrayList<MapItem> mapItems;
	protected SpatialReference spatialReference;
	public static int DEFAULT_WKID = 4326; //wgs84(4326) is the default spatial reference for our mapdata items
	public static int MODE_OVERWRITE = 1; // will clear the graphics layer before adding new graphics
	public static int MODE_APPEND = 2; // appends graphics to the graphics layer

	public MapData() {
		super();
		this.mode = MapData.MODE_OVERWRITE;
		this.mapGraphicsLayers = new LinkedHashMap<String,MapGraphicsLayer>();
		this.mapGraphicsLayers.put(MITMapView.DEFAULT_GRAPHICS_LAYER,new MapGraphicsLayer());
		this.spatialReference = SpatialReference.create(MapData.DEFAULT_WKID); // wgs84(4326) is the default spatial reference for our mapdata items
	}
	
	public int getMode() {
		return mode;
	}
	public void setMode(int mode) {
		this.mode = mode;
	}
//	public String getLayerName() {
//		return layerName;
//	}
//	public void setLayerName(String layerName) {
//		this.layerName = layerName;
//	}
	
	public ArrayList<MapItem> getMapItems() {
		return getMapItems(MITMapView.DEFAULT_GRAPHICS_LAYER);
	}

	public ArrayList<MapItem> getMapItems(String layerName) {
		return mapGraphicsLayers.get(layerName).mapItems;
	}
	
	public void setMapItems(ArrayList<MapItem> mapItems) {
		this.mapGraphicsLayers.get(MITMapView.DEFAULT_GRAPHICS_LAYER).mapItems = mapItems;
	}
	
	public void setMapItems(String layerName,ArrayList<MapItem> mapItems) {
		this.mapGraphicsLayers.get(layerName).mapItems = mapItems;
	}

	public String toJSON() {
		JSONArray jMapItems = new JSONArray();
		
		// Get iterator for mapGraphicsLayers linked hashmap
		Iterator<Map.Entry<String,MapGraphicsLayer>> gl = this.mapGraphicsLayers.entrySet().iterator();

		// loop through graphics layers
		while (gl.hasNext()) {
	        Map.Entry<String,MapGraphicsLayer> glpairs = (Map.Entry<String,MapGraphicsLayer>)gl.next();
	        String layerName = (String)glpairs.getKey();       
	        MapGraphicsLayer mgl = this.mapGraphicsLayers.get(layerName);
	        
	        // loop through map items in graphics layer
			ArrayList<MapItem>mapItems = mgl.mapItems;
			for (int i = 0; i < mapItems.size(); i++) {
				MapItem mapItem = mapItems.get(i);
				mapItem.setGraphicsLayer(layerName);
				JSONObject jMapItem = new JSONObject();
				
				try {
					jMapItem.put("graphicsLayer",mapItem.getGraphicsLayer());
					jMapItem.put("mapItemClass",mapItem.getMapItemClass());
					jMapItem.put("geometryType",mapItem.getGeometryType());
					jMapItem.put("lineColor",mapItem.lineColor);
					jMapItem.put("lineWidth",mapItem.lineWidth);
					jMapItem.put("symbol",mapItem.symbol);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				// create JSON object for itemData
				JSONObject jItemData = new JSONObject();
				//mapItem.getItemData().keySet()
				Iterator<Map.Entry<String,Object>> it = mapItem.getItemData().entrySet().iterator();
			    while (it.hasNext()) {
			        Map.Entry<String,Object> pairs = (Map.Entry<String,Object>)it.next();
			        try {
				        jItemData.put((String)pairs.getKey(),pairs.getValue());
				        jMapItem.put("itemData",jItemData);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

			    }
			    
				// create JSON array for mapPoints
				JSONArray jMapPoints = new JSONArray();
				for (int j = 0; j < mapItem.getMapPoints().size(); j++) {
					MapPoint mapPoint = mapItems.get(i).getMapPoints().get(j);
					JSONObject jMapPoint = new JSONObject();
					try {
						jMapPoint.put("lat_wgs84",mapPoint.lat_wgs84);
						jMapPoint.put("long_wgs84",mapPoint.long_wgs84);
						jMapPoints.put(j,jMapPoint);
						jMapItem.put("mapPoints", jMapPoints);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				
				try {
					jMapItems.put(i,jMapItem);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
	        // end loop through map items in graphics layer
			
		}
		// end loop through graphics layers
		return jMapItems.toString();
	}

	//	public String toJSON() {
//		JSONArray jMapItems = new JSONArray();
//		Iterator gl = this.mapGraphicsLayers.entrySet().iterator();
//	    // GL Loop
//		while (gl.hasNext()) {
//	        Map.Entry glpairs = (Map.Entry)gl.next();
//	        String layerName = (String)glpairs.getKey();
//	        
//	        MapGraphicsLayer mgl = this.mapGraphicsLayers.get(layerName);
//			ArrayList<MapItem>mapItems = mgl.mapItems;
//			for (int i = 0; i < mapItems.size(); i++) {
//				MapItem mapItem = mapItems.get(i);
//				mapItem.setGraphicsLayer(layerName);
//				JSONObject jMapItem = new JSONObject();
//				jMapItem.put("graphicsLayer",mapItem.getGraphicsLayer());
//				jMapItem.put("mapItemClass",mapItem.getMapItemClass());
//				jMapItem.put("geometryType",mapItem.getGeometryType());
//				jMapItem.put("lineColor",mapItem.lineColor);
//				jMapItem.put("lineWidth",mapItem.lineWidth);
//				jMapItem.put("symbol",mapItem.symbol);
//				
//				// create JSON object for itemData
//				JSONObject jItemData = new JSONObject();
//				//mapItem.getItemData().keySet()
//				Iterator it = mapItem.getItemData().entrySet().iterator();
//			    while (it.hasNext()) {
//			        Map.Entry pairs = (Map.Entry)it.next();
//			        jItemData.put((String)pairs.getKey(),pairs.getValue());
//			    }
//				jMapItem.put("itemData",jItemData);				
//				
//				// create JSON array for mapPoints
//				JSONArray jMapPoints = new JSONArray();
//				for (int j = 0; j < mapItem.getMapPoints().size(); j++) {
//					MapPoint mapPoint = mapItems.get(i).getMapPoints().get(j);
//					JSONObject jMapPoint = new JSONObject();
//					jMapPoint.put("lat_wgs84",mapPoint.lat_wgs84);
//					jMapPoint.put("long_wgs84",mapPoint.long_wgs84);
//					try {
//						jMapPoints.put(j,jMapPoint);
//					} catch (JSONException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					try {
//						jMapItem.put("mapPoints", jMapPoints);
//					} catch (JSONException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//				
//				try {
//					jMapItems.put(i,jMapItem);
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			
//	    } // END GL LOOP
//			
//		return jMapItems.toString();
//		}
//	}
//	// END TOJSON

	public static MapData fromJSON(String mapDataJSON) {
		MapData m = new MapData();
		try {
			JSONArray jMapItems = new JSONArray(mapDataJSON);
            for (int i = 0; i < jMapItems.length(); i++) {
            	JSONObject jMapItem = jMapItems.getJSONObject(i);

            	// get the graphics layer of the map item
            	String graphicsLayer = jMapItem.getString("graphicsLayer");
            	
            	// if the graphics layer is not defined in mapData, create it.
            	if (!m.mapGraphicsLayers.containsKey(graphicsLayer)) {
            		m.mapGraphicsLayers.put(graphicsLayer, new MapGraphicsLayer());
            	}

				try {
					Class<?> cls = Class.forName(jMapItem.getString("mapItemClass"));
					try {
						MapItem mapItem = (MapItem) cls.newInstance();
						mapItem.setGeometryType(jMapItem.getInt("geometryType"));
						JSONObject jItemData = jMapItem.getJSONObject("itemData");
	
						// POPULATE itemDATA
						@SuppressWarnings("unchecked")
						Iterator<String> keys = jItemData.keys();
						while (keys.hasNext()) {
							String key = (String)keys.next();
							Object value = jItemData.get(key);
							mapItem.getItemData().put(key, value);
						}

						mapItem.setLineColor(jMapItem.getInt("lineColor"));
						mapItem.setLineWidth(jMapItem.getInt("lineWidth"));
						mapItem.setMapItemClass(jMapItem.getString("mapItemClass"));
						mapItem.setSymbol(jMapItem.getInt("symbol"));
						
						// get map points
						JSONArray jMapPoints = jMapItem.getJSONArray("mapPoints");
			            for (int j = 0; j < jMapPoints.length(); j++) {
			            	JSONObject jMapPoint = jMapPoints.getJSONObject(j);
			            	MapPoint mapPoint = new MapPoint();
			            	mapPoint.lat_wgs84 = jMapPoint.getDouble("lat_wgs84");
			            	mapPoint.long_wgs84 = jMapPoint.getDouble("long_wgs84");
			            	mapItem.getMapPoints().add(mapPoint);
			            }
				
			            m.getMapGraphicsLayers().get(graphicsLayer).getMapItems().add(mapItem);
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return m;
	}

	public LinkedHashMap<String, MapGraphicsLayer> getMapGraphicsLayers() {
		return mapGraphicsLayers;
	}

	public void setMapGraphicsLayers(
			LinkedHashMap<String, MapGraphicsLayer> mapGraphicsLayers) {
		this.mapGraphicsLayers = mapGraphicsLayers;
	}

	public SpatialReference getSpatialReference() {
		return spatialReference;
	}

	public void setSpatialReference(SpatialReference spatialReference) {
		this.spatialReference = spatialReference;
	}

}
