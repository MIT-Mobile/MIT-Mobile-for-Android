package edu.mit.mitmobile2.maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.esri.android.map.LocationService;

import edu.mit.mitmobile2.objs.MapItem;
import edu.mit.mitmobile2.objs.MapPoint;

public class MapData {

	private int mode;
	private String layerName;
	protected ArrayList<MapItem> mapItems;
	
	public static int MODE_OVERWRITE = 1; // will clear the graphics layer before adding new graphics
	public static int MODE_APPEND = 2; // appends graphics to the graphics layer

	public MapData() {
		super();
		this.mode = MapData.MODE_OVERWRITE;
		this.layerName = MITMapView2.DEFAULT_GRAPHICS_LAYER;
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
	
	public String toJSON() {
		JSONObject jMapData = new JSONObject();
		try {
			jMapData.put("mode",this.mode);
			jMapData.put("layerName",this.layerName);
			
			// create JSON array for mapItems
			JSONArray jMapItems = new JSONArray();
			for (int i = 0; i < this.mapItems.size(); i++) {
				MapItem mapItem = this.mapItems.get(i);
				JSONObject jMapItem = new JSONObject();
				jMapItem.put("mapItemClass",mapItem.getMapItemClass());
				jMapItem.put("geometryType",mapItem.getGeometryType());
				jMapItem.put("lineColor",mapItem.lineColor);
				jMapItem.put("lineWidth",mapItem.lineWidth);
				jMapItem.put("symbol",mapItem.symbol);
				
				// create JSON object for itemData
				JSONObject jItemData = new JSONObject();
				//mapItem.getItemData().keySet()
				Iterator it = mapItem.getItemData().entrySet().iterator();
			    while (it.hasNext()) {
			        Map.Entry pairs = (Map.Entry)it.next();
			        jItemData.put((String)pairs.getKey(),pairs.getValue());
			    }
				jMapItem.put("itemData",jItemData);				
				
				// create JSON array for mapPoints
				JSONArray jMapPoints = new JSONArray();
				for (int j = 0; j < mapItem.getMapPoints().size(); j++) {
					MapPoint mapPoint = mapItems.get(i).getMapPoints().get(j);
					JSONObject jMapPoint = new JSONObject();
					jMapPoint.put("lat_wgs84",mapPoint.lat_wgs84);
					jMapPoint.put("long_wgs84",mapPoint.long_wgs84);
					jMapPoints.put(j,jMapPoint);
					jMapItem.put("mapPoints", jMapPoints);
				}
				
				jMapItems.put(i,jMapItem);
			}
			jMapData.put("mapItems",jMapItems);
		} 
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.d("ZZZ","toJSON = " + jMapData.toString());
		return jMapData.toString();
	}
	
	public static MapData fromJSON(String mapDataJSON) {
		MapData m = new MapData();
		try {
			JSONObject jMapData = new JSONObject(mapDataJSON);
			m.setLayerName(jMapData.getString("layerName"));
			m.setMode(jMapData.getInt("mode"));
			
			// get mapItems
			JSONArray jMapItems = jMapData.getJSONArray("mapItems");
            for (int i = 0; i < jMapItems.length(); i++) {
            	JSONObject jMapItem = jMapItems.getJSONObject(i);

				try {
					Class cls = Class.forName(jMapItem.getString("mapItemClass"));
					try {
						MapItem mapItem = (MapItem) cls.newInstance();
						//Log.d("ZZZ","mapItem class = " + mapItem.getClass().getName());
						mapItem.setGeometryType(jMapItem.getInt("geometryType"));
						JSONObject jItemData = jMapItem.getJSONObject("itemData");

						// POPULATE itemDATA
						Iterator keys = jItemData.keys();
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
				
			            m.getMapItems().add(mapItem);
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
		//jMapData.

		return m;
	}

}
