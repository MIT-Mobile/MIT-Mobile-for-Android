package edu.mit.mitmobile2.objs;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import edu.mit.mitmobile2.R;

public abstract class MapItem {
	
	public MapItem() {
		mapItemClass = this.getClass().getName();
		itemData = new HashMap();
		mapPoints = new ArrayList<MapPoint>();
		geometryType = MapItem.TYPE_POINT;
		symbol = MapItem.DEFAULT_SYMBOL;
		lineColor = MapItem.DEFAULT_LINE_COLOR;
		lineWidth = MapItem.DEFAULT_LINE_WIDTH;
	}
		
	public abstract View getCallout(Context mContext);
	
	protected String mapItemClass; // this is a hack to recreate MapItem objects that are extended from the abstract class
	
	protected HashMap itemData;
	
	public static final int TYPE_POINT = 1;
	public static final int TYPE_POLYLINE = 2;
	public static final int TYPE_POLYGON = 3;

	public static final int DEFAULT_SYMBOL = R.drawable.map_red_pin;
	public static final int DEFAULT_LINE_COLOR = Color.RED; // 
	public static final int DEFAULT_LINE_WIDTH = 1; //

	protected ArrayList<MapPoint> mapPoints; 
	
	public int geometryType;
	public int symbol; // symbol to show for points
	public int lineColor; // color to use for polylines and polygons
	public int lineWidth; // width tu use for polylines and polygons
	
	public long sql_id = -1;  // not to confuse with "id"


	public HashMap getItemData() {
		return itemData;
	}

	public void setItemData(HashMap itemData) {
		this.itemData = itemData;
	}

	public ArrayList<MapPoint> getMapPoints() {
		return mapPoints;
	}

	public void setMapPoints(ArrayList<MapPoint> mapPoints) {
		this.mapPoints = mapPoints;
	}

	public int getGeometryType() {
		return geometryType;
	}

	public void setGeometryType(int geometryType) {
		this.geometryType = geometryType;
	}

	public int getLineColor() {
		return lineColor;
	}

	public void setLineColor(int lineColor) {
		this.lineColor = lineColor;
	}

	public int getLineWidth() {
		return lineWidth;
	}

	public void setLineWidth(int lineWidth) {
		this.lineWidth = lineWidth;
	}

	public String getMapItemClass() {
		return mapItemClass;
	}

	public void setMapItemClass(String mapItemClass) {
		this.mapItemClass = mapItemClass;
	}

	public int getSymbol() {
		return symbol;
	}

	public void setSymbol(int symbol) {
		this.symbol = symbol;
	}

}