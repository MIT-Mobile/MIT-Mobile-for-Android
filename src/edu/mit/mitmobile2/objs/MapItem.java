package edu.mit.mitmobile2.objs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.maps.MapData;

public abstract class MapItem {
	
	public MapItem() {
		mapItemClass = this.getClass().getName();
		itemData = new HashMap<String,Object>();
		mapPoints = new ArrayList<MapPoint>();
		geometryType = MapItem.TYPE_POINT;
		symbol = MapItem.DEFAULT_SYMBOL;
		lineColor = MapItem.DEFAULT_LINE_COLOR;
		lineWidth = MapItem.DEFAULT_LINE_WIDTH;
		verticalAlign = MapItem.VALIGN_CENTER;
		horizontalAlign = MapItem.ALIGN_CENTER;
	}
		
	public long sql_id = -1;  // not to confuse with "id"
	
	public abstract View getCallout(Context mContext);
	public abstract View getCallout(Context mContext, MapData mapData);
	public abstract View getCallout(Context mContext, MapData mapData, int position);

	//public abstract void initTimer(Context mContext);
	
	protected String mapItemClass; // this is a hack to recreate MapItem objects that are extended from the abstract class
	
	private int index;
	private Bitmap thumbnail;
	
	protected HashMap<String,Object> itemData;
	
	public static final int TYPE_POINT = 1;
	public static final int TYPE_POLYLINE = 2;
	public static final int TYPE_POLYGON = 3;

	public static final int DEFAULT_SYMBOL = R.drawable.map_red_pin;
	public static final int DEFAULT_LINE_COLOR = Color.RED; // 
	public static final int DEFAULT_LINE_WIDTH = 1; //

	public static final int VALIGN_BOTTOM = 1;
	public static final int VALIGN_CENTER = 2;
	public static final int VALIGN_TOP = 3;
	
	public static final int ALIGN_LEFT = 1;
	public static final int ALIGN_CENTER = 2;
	public static final int ALIGN_RIGHT = 3;

	protected ArrayList<MapPoint> mapPoints; 
	
	public int geometryType;
	public int symbol; // symbol to show for points
	public int offsetY; // the Y offset for displaying images and callouts, usually half the height of the image.
	public int lineColor; // color to use for polylines and polygons
	public int lineWidth; // width tu use for polylines and polygons
	public int verticalAlign; // controls vertical alignment of image for map items of type point, default to center 
	public int horizontalAlign; // controls horizontal alignment of image for map items of type point, default to center 
	
	public HashMap<String,Object> getItemData() {
		return itemData;
	}

	public void setItemData(HashMap<String,Object> itemData) {
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
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public Bitmap getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(Bitmap thumbnail) {
		this.thumbnail = thumbnail;
	}

}