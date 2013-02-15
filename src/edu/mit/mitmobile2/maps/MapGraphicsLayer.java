package edu.mit.mitmobile2.maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.os.Parcel;
import android.os.Parcelable;

import com.esri.core.geometry.SpatialReference;

import edu.mit.mitmobile2.objs.BuildingMapItem;
import edu.mit.mitmobile2.objs.MapItem;
import edu.mit.mitmobile2.objs.MapItemContent;
import edu.mit.mitmobile2.objs.MapPoint;

public class MapGraphicsLayer implements Parcelable {
	
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

	public MapGraphicsLayer(Parcel source){
        super(); 
        readFromParcel(source);
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

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		dest.writeString(layerName);
		dest.writeMap(graphicIdMap);
		dest.writeList(mapItems);
	}
	
	public void readFromParcel(Parcel source) {
		layerName = source.readString();
		graphicIdMap = source.readHashMap(HashMap.class.getClassLoader());
		mapItems = source.readArrayList(MapItem.class.getClassLoader());
	}
	
    public static final Parcelable.Creator<MapGraphicsLayer> CREATOR = new Parcelable.Creator<MapGraphicsLayer>() {
        public MapGraphicsLayer createFromParcel(Parcel in) {
            return new MapGraphicsLayer(in);
        }

        public MapGraphicsLayer[] newArray(int size) {

            return new MapGraphicsLayer[size];
        }

    };
}
