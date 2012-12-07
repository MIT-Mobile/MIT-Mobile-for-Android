package edu.mit.mitmobile2.maps;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import edu.mit.mitmobile2.objs.BuildingMapItem;
import edu.mit.mitmobile2.objs.MapItem;
import edu.mit.mitmobile2.objs.MapPoint;

public class MapParser {
	
	private static final String TAG = "MapParser"; 

	public static MapServerData parseMapServerData(JSONObject jobject) throws JSONException {
		MapServerData mapServerData = new MapServerData();
		try {
			// Get the default basemap set
			mapServerData.setDefaultBasemap(jobject.getString("defaultBasemap"));
			
			// populate the baseLayer groups
			JSONObject layerGroups = jobject.getJSONObject("basemaps");
			
			// loop through the layer groups
			Iterator g = layerGroups.keys();
			while (g.hasNext()) {
				String group = (String)g.next(); // name of the layer group, i.e. "default"
				JSONArray layers = layerGroups.getJSONArray(group); // basemap layers in this group
				ArrayList<MapBaseLayer> baseMaps = new ArrayList<MapBaseLayer>();
				// add all layers in the group
				for (int i = 0; i < layers.length(); i++) {
					JSONObject map = layers.getJSONObject(i);
					MapBaseLayer layer = new MapBaseLayer();
					layer.setLayerIdentifier(map.optString("layerIdentifier"));
					layer.setDisplayName(map.optString("displayName"));
					layer.setUrl(map.optString("url"));
					layer.setEnabled(map.optBoolean("isEnabled"));
					baseMaps.add(layer);
				}
				
				// add the baseMaps array to the baseLayerGroup hashmap
				mapServerData.getBaseLayerGroup().put(group, baseMaps);
			}
			
			JSONArray array = jobject.getJSONArray("features");
			Log.d("MapBaseActivity","num features = " + array.length());
			for (int i = 0; i < array.length(); i++) {
				JSONObject map = array.getJSONObject(i);
				MapFeatureLayer layer = new MapFeatureLayer();
				layer.setLayerIdentifier(map.optString("layerIdentifier"));
				layer.setDisplayName(map.optString("displayName"));
				layer.setUrl(map.optString("url"));
				layer.setTiledLayer(map.optBoolean("isTiledLayer"));
				layer.setDataLayer(map.optBoolean("isDataLayer"));
				mapServerData.getFeatures().add(layer);
			}

		}
		catch (JSONException e) {
			Log.d(TAG,"JSON exception " + e.getMessage());				
		}
		Log.d(TAG,"class = " + mapServerData.getClass());
		return mapServerData;
	}
	
	public static List<MapItem> parseMapItems(JSONArray jArray) throws JSONException {
	
		ArrayList<MapItem> mapItems = new ArrayList<MapItem>();
		try {
			for(int i = 0; i < jArray.length(); i++) {
				MapItem mapItem = parseMapItem(jArray.getJSONObject(i));
				mapItems.add(mapItem);
			}
		}
		catch (JSONException e) {
			
		}
		
		return mapItems;
	}
	
	public static MapItem parseMapItem(JSONObject jItem) throws JSONException {
		
		
        	BuildingMapItem mi = new BuildingMapItem();

        	ArrayList<String> category = new ArrayList();
	        JSONArray temp = jItem.optJSONArray("category");
	        if (temp!=null) {
	        	for (int index=0; index<temp.length(); index++) {
	        		String it = temp.getString(index);
	        		category.add(it);
	    	       // mi.category.add(it);
	        	}
	        	mi.getItemData().put("category", category);
	        }
	        

	        temp = jItem.optJSONArray("altname");
	        if (temp!=null) {
		        ArrayList<String> alts = new ArrayList();
	        	for (int index=0; index<temp.length(); index++) {
	        		String it = temp.getString(index);
	    	        alts.add(it);
	        	}
	        	mi.getItemData().put("alts", alts);	        	
	        }

	        temp = jItem.optJSONArray("contents");
	        if (temp!=null) {
	        	ArrayList<String> contents = new ArrayList();
	        	JSONObject j;
	        	String it;
	        	for (int index=0; index<temp.length(); index++) {
	        		j = temp.getJSONObject(index);
	        		it = j.getString("name");
	    	        contents.add(it);
	        	}
	        	mi.getItemData().put("contents",contents);
	        }

	        mi.getItemData().put("displayName",jItem.optString("displayName",""));
	        mi.getItemData().put("name", jItem.getString("name"));
	        mi.getItemData().put("id",jItem.getString("id"));
	        mi.getItemData().put("street",jItem.optString("street",""));
	        mi.getItemData().put("viewangle",jItem.optString("viewangle",""));
	        mi.getItemData().put("bldgimg",jItem.optString("bldgimg",""));
	        mi.getItemData().put("bldgnum",jItem.optString("bldgnum",""));

	        
	        temp = jItem.optJSONArray("snippets");
	        if (temp!=null) {
	        	String name = (String)mi.getItemData().get("name");
	        	String snippets = (String)mi.getItemData().get("snippets");
	        	snippets = temp.getString(0);
	        	if (snippets.equalsIgnoreCase(name)) snippets = "";
	        	if (!snippets.startsWith("Building ")) snippets = "Building " + snippets;
	        }

	        MapPoint mapPoint = new MapPoint(jItem.getDouble("lat_wgs84"),jItem.getDouble("long_wgs84"));

	        Log.d(TAG,"mappoint lat = " + mapPoint.lat_wgs84 + " long = " + mapPoint.long_wgs84);
	        mi.getMapPoints().add(mapPoint);
	        
	        Log.d("ZZZ","parseMapItem: mapItem has " + mi.getMapPoints().size() + " points");
	        //DEBUG itemData
	        Iterator it = mi.getItemData().entrySet().iterator();
	        while (it.hasNext()) {
	            Map.Entry pairs = (Map.Entry)it.next();
	            Log.d("ZZZ",pairs.getKey() + " = " + pairs.getValue());
	        }
	        //DEBUG itemData
	        
	        return mi;	        
	}

	
}
/*
[
{"category":["building"],
"long_wgs84":-71.083463199999997,
"name":"Badger Building",
"lat_wgs84":42.362749229999999,
"id":"object-E70",
"bldgimg":"http://web.mit.edu/campus-map/objimgs/object-E70.jpg",
"snippets":["Laboratory for Financial Engineering"],
"street":"1 Broadway, (8th FL)",
"bldgnum":"E70",
"floorplans":["8"],
"mailing":"77 Massachusetts Avenue",
"viewangle":"west side",
"contents":[
{"url":"http://lfe.mit.edu/","name":"Laboratory for Financial Engineering"},
{"url":"http://web.mit.edu/workplacecenter/","name":"Workplace Center, MIT","altname":["MIT Workplace Center"]},
{"url":"http://ocw.mit.edu/","name":"OpenCourseWare (OCW)"}]},


{"category":
*/
