package edu.mit.mitmobile2.objs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;

import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.geometry.Unit;
import com.esri.core.geometry.Unit.UnitType;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.maps.MITMapView;
import edu.mit.mitmobile2.maps.MapAbstractionObject;
import edu.mit.mitmobile2.maps.MITMapActivity;
import edu.mit.mitmobile2.maps.MapData;
import edu.mit.mitmobile2.maps.MapModel;

public abstract class MapItem {
	
	public final static String TAG = "MapItem";
	public MapItem() {
		mapItemClass = this.getClass().getName();
		itemData = new HashMap<String,Object>();
		mapPoints = new ArrayList<MapPoint>();
		contents = new ArrayList<MapItemContent>();
		geometryType = MapItem.TYPE_POINT;
		symbol = MapItem.DEFAULT_SYMBOL;
		lineColor = MapItem.DEFAULT_LINE_COLOR;
		lineWidth = MapItem.DEFAULT_LINE_WIDTH;
		verticalAlign = MapItem.VALIGN_BOTTOM;
		horizontalAlign = MapItem.ALIGN_CENTER;
		graphicsLayer = MITMapView.DEFAULT_GRAPHICS_LAYER;
		wkid = MapAbstractionObject.DEFAULT_WKID;
	}
		
	public long sql_id = -1;  // not to confuse with "id"
	
	public abstract View getCallout(Context mContext);
	public abstract View getCallout(Context mContext, ArrayList<? extends MapItem> mapItems);
	public abstract View getCallout(Context mContext, ArrayList<? extends MapItem> mapItems, int position);

	//public abstract void initTimer(Context mContext);
	
	protected String mapItemClass; // this is a hack to recreate MapItem objects that are extended from the abstract class
	
	protected int index;
	private String graphicsLayer;
	
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

	protected int wkid; // the spatial reference the mapPoints are stored in, defaults to DEFAULT_WIKID 
	protected ArrayList<MapPoint> mapPoints; 
	protected ArrayList<MapItemContent> contents;
	
	public int geometryType;
	public int symbol; // symbol to show for points
	public int offsetX;
	public int offsetY; // the Y offset for displaying images and callouts, usually half the height of the image.	public int offset
	public int lineColor; // color to use for polylines and polygons
	public int lineWidth; // width tu use for polylines and polygons
	public int verticalAlign; // controls vertical alignment of image for map items of type point, default to center 
	public int horizontalAlign; // controls horizontal alignment of image for map items of type point, default to center 
	public String query; // query used if this map item was the result of a map search
	
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
//	public Bitmap getThumbnail() {
//		return thumbnail;
//	}
//	public void setThumbnail(Bitmap thumbnail) {
//		this.thumbnail = thumbnail;
//	}

	public Polygon getExtent(double padding) {
		//loops through all points for the map items and returns a polygon for the extent of those items with padding
		double minLong;
		double minLat;
		double maxLong;
		double maxLat;
		
		MapPoint mapPoint;
		Point point;
		
		if (mapPoints.size() > 0) {
			
			Log.d("MITMapDetailsSliderActivity","num map points = " + mapPoints.size());

			// set min and max values to first point
			minLong = mapPoints.get(0).long_wgs84;
			maxLong = mapPoints.get(0).long_wgs84;
			minLat = mapPoints.get(0).lat_wgs84;
			maxLat = mapPoints.get(0).lat_wgs84;
				
			for (int p = 0; p < mapPoints.size(); p++) {
				 mapPoint = mapPoints.get(p);
				 Log.d("MITMapDetailsSliderActivity","mapPoint " + p + ": x = " + mapPoint.long_wgs84 + " Y = " + mapPoint.lat_wgs84);
				 if (mapPoint.lat_wgs84 <= minLat) {
					 minLat = mapPoint.lat_wgs84;
				 }
				 
				 if (mapPoint.lat_wgs84 >= maxLat) {
					 maxLat = mapPoint.lat_wgs84;
				 }
				 
				 if (mapPoint.long_wgs84 <= minLong) {
					 minLong = mapPoint.long_wgs84;
				 }
				 
				 if (mapPoint.long_wgs84 >= maxLong) {
					 maxLong = mapPoint.long_wgs84;
				 }
			}
				
			// create Polygon from 4 points
			// start of the south west point
			Point SW = MITMapView.toWebmercator(minLat,minLong);
			Point NW = MITMapView.toWebmercator(maxLat,minLong);
			Point NE = MITMapView.toWebmercator(maxLat,maxLong);
			Point SE = MITMapView.toWebmercator(minLat,maxLong);

			Polygon polygon = new Polygon();
			polygon.startPath(SW);
			polygon.lineTo(SW);
			polygon.lineTo(NW);
			polygon.lineTo(NE);
			polygon.lineTo(SE);
			return polygon;
		}
		else {
			return null;
		}
	}
	
	public String getBoundingBox(SpatialReference targetSpatialReference) {

		String bbox = "";

		if (this.geometryType == MapItem.TYPE_POINT) {
			MapPoint centerPoint = this.getCenter();
			//Point thumbnailPoint = (Point)GeometryEngine.project(new Point(this.getMapPoints().get(0).long_wgs84,this.getMapPoints().get(0).lat_wgs84), SpatialReference.create(this.getWikid()),targetSpatialReference);
			Point thumbnailPoint = (Point)GeometryEngine.project(new Point(centerPoint.long_wgs84,centerPoint.lat_wgs84), SpatialReference.create(this.getWkid()),targetSpatialReference);
			double minX = thumbnailPoint.getX() - 100;
			double minY = thumbnailPoint.getY() - 100;
			double maxX = thumbnailPoint.getX() + 100;
			double maxY = thumbnailPoint.getY() + 100;
			Log.d(TAG,"bbox = " + minX + "," + minY + "," + maxX + "," + maxY);
			bbox = minX + "," + minY + "," + maxX + "," + maxY; 
		}
		return bbox;

	}
	
//	public static void getMapItemImage(Context mContext, Handler uiHandler,MapItem mapItem, final SpatialReference targetSpatialReference) {
//		MapItem.getMapItemImage(mContext,uiHandler,mapItem,SpatialReference.create(mapItem.wikid), targetSpatialReference);
//	}
//
//	public static void getMapItemImage(Context mContext, Handler uiHandler,MapItem mapItem,final SpatialReference sourceSpatialReference, final SpatialReference targetSpatialReference) {
//		String boundingBox; // comma separate string of doubles in form of minX,minY,maxX,maxY
//		if (mapItem.geometryType == MapItem.TYPE_POINT) {
//			Point thumbnailPoint = (Point)GeometryEngine.project(new Point(mapItem.getMapPoints().get(0).long_wgs84,mapItem.getMapPoints().get(0).lat_wgs84), SpatialReference.create(mapItem.getWikid()),targetSpatialReference);
//			double minX = thumbnailPoint.getX() - 100;
//			double minY = thumbnailPoint.getY() - 100;
//			double maxX = thumbnailPoint.getX() + 100;
//			double maxY = thumbnailPoint.getY() + 100;
//			Log.d(TAG,"bbox = " + minX + "," + minY + "," + maxX + "," + maxY);
//			boundingBox = minX + "," + minY + "," + maxX + "," + maxY; 
//			MapModel.exportMapBitmap(mContext,boundingBox, true, uiHandler);
//		}
//	}
	
	public int getWkid() {
		return wkid;
	}
	public void setWkid(int wkid) {
		this.wkid = wkid;
	}
	public String getGraphicsLayer() {
		return graphicsLayer;
	}
	public void setGraphicsLayer(String graphicsLayer) {
		this.graphicsLayer = graphicsLayer;
	}
	
	public MapPoint getCenter() {
		double sumX = 0;
		double sumY = 0;
		double centerX;
		double centerY;
		if (this.mapPoints != null) {
			if (this.geometryType == MapItem.TYPE_POINT) {
				return this.mapPoints.get(0);
			}
			else {
				for (int p = 0; p < this.mapPoints.size(); p++) {
					sumX += this.mapPoints.get(p).long_wgs84;
					sumY += this.mapPoints.get(p).lat_wgs84;
				}
				centerX = sumX / this.mapPoints.size();
				centerY = sumY / this.mapPoints.size();
				return new MapPoint(centerY,centerX);
			}
		}
		else {
			return null;
		}
	}
	
	public Double getSortWeight() {
		MapPoint mapPoint = this.getCenter();
		Double x = mapPoint.long_wgs84 * 10;
		Double y = mapPoint.lat_wgs84 * 100;
		return x + y;
	}
	
	public ArrayList<MapItemContent> getContents() {
		return contents;
	}
	public void setContents(ArrayList<MapItemContent> contents) {
		this.contents = contents;
	}
	
}

