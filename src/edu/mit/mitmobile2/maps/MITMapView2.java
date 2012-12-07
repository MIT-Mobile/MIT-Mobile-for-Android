package edu.mit.mitmobile2.maps;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.esri.android.map.Callout;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.Layer;
import com.esri.android.map.MapView;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleLineSymbol;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.objs.MapItem;
import edu.mit.mitmobile2.objs.MapPoint;



public class MITMapView2 extends MapView  {

	private static final String TAG = "MITMapView";
	protected Map<String, Long> layerIdMap; // map of user selected "layer name" to arcgis generated layer ID
	protected Map<String,Integer> graphicIdMap; // map of graphic Id to the index of it's corresponding MapItem in the mapData mapItems array
	
	private static SpatialReference mercatorWeb; // spatial reference used by the base map
	private static SpatialReference wgs84; // spatial reference used by androids location service
	private GraphicsLayer gl;
	private MapData mapData;
	private Context mContext;
	
	public MITMapView2(Context context) {
		super(context);
		mContext = context;
		mercatorWeb = SpatialReference.create(102100);
    	wgs84 = SpatialReference.create(4326);	
	}

	public MITMapView2(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mercatorWeb = SpatialReference.create(102100);
    	wgs84 = SpatialReference.create(4326);	

	}

	public void addMapLayer(Layer layer, String layerName) {
		Long id = layer.getID();
		Log.d(TAG,"layer id from addMapLayer = " + id);
		layerIdMap.put(layerName, id);                	                	                   
        this.addLayer(layer);
	}
	
	public Layer getMapLayer(String layerName) {
		if (layerIdMap != null) {
			Long id = layerIdMap.get(layerName);                	                	                   
			Log.d(TAG,"get map layer id = " + id);
			return this.getLayerByID(id);
		}
		else {
			return null;
		}
	}

	protected int dislayMapItem(String layerName, MapItem mapItem) {
		
		int gId = 0;
		
		switch (mapItem.getGeometryType()) {
			case MapItem.TYPE_POINT:
				gId = displayMapPoint(mapItem);
			break;
			
			case MapItem.TYPE_POLYLINE:
				gId = displayMapPolyline(mapItem);
			break;
			
			case MapItem.TYPE_POLYGON:
				gId = displayMapPolygon(mapItem);
			break;
			
			default:
				gId = 0;
			break;
			
		}

		return gId;

	}

	public static Point toWebmercator(double lat, double lon) {
		Point point = new Point();
		point = (Point)GeometryEngine.project(new Point(lon,lat), wgs84,mercatorWeb);
		return point;
	}
	
	protected int displayMapPoint(MapItem mapItem) {
		if (mapItem.getMapPoints().size() > 0) {
			MapPoint mapPoint = mapItem.getMapPoints().get(0);
			Point point = toWebmercator(mapPoint.lat_wgs84,mapPoint.long_wgs84);

			Bitmap libImage = BitmapFactory.decodeResource(getResources(), mapItem.symbol);
			BitmapDrawable libDrawable = new BitmapDrawable(libImage);
			PictureMarkerSymbol pms = new PictureMarkerSymbol(libDrawable);       

			Map attributes = new HashMap();
			//attributes.put("mapItem", mapItem);
			//attributes.put("pointX", point.getX() + "");
			//attributes.put("pointY", point.getY() + "");
			
			Graphic g = new Graphic(point, pms,attributes, null);

			gl = (GraphicsLayer)this.getMapLayer(MapBaseActivity.DEFAULT_GRAPHICS_LAYER); // this should be a parameter
			int Uid = gl.addGraphic(g);	
	        return Uid;	
			
		}
		else {
			return 0;
		}
	}
	
	protected int displayMapPolyline(MapItem mapItem) {
		Log.d(TAG,"displayMapPolyline");
		Point point;
		Point startPoint;
		Polyline polyline = new Polyline();

		if (mapItem.getMapPoints().size() > 0) {
			
			startPoint = toWebmercator(mapItem.getMapPoints().get(0).lat_wgs84,mapItem.getMapPoints().get(0).long_wgs84);
			polyline.startPath(startPoint);
			for (int p = 0; p < mapItem.getMapPoints().size(); p++) {
				MapPoint mapPoint = mapItem.getMapPoints().get(p);
				Log.d(TAG,"polyline point x:" + mapPoint.long_wgs84 + " point y:" + mapPoint.lat_wgs84);
				point = toWebmercator(mapPoint.lat_wgs84,mapPoint.long_wgs84);
				polyline.lineTo(point);
			}
			
			Graphic g = new Graphic(polyline,new SimpleLineSymbol(mapItem.lineColor,mapItem.lineWidth));

			gl = (GraphicsLayer)this.getMapLayer(MapBaseActivity.DEFAULT_GRAPHICS_LAYER); // this should be a parameter
			int Uid = gl.addGraphic(g);	
	        return Uid;	
		}
		else {
			return 0;
		}
	}
	
	protected int displayMapPolygon(MapItem mapItem) {
		Log.d(TAG,"displayMapPolygon");
		Point point;
		Point startPoint;
		Polygon polygon = new Polygon();
	   
		if (mapItem.getMapPoints().size() > 0) {
			
			startPoint = toWebmercator(mapItem.getMapPoints().get(0).lat_wgs84,mapItem.getMapPoints().get(0).long_wgs84);
			polygon.startPath(startPoint);
			for (int p = 0; p < mapItem.getMapPoints().size(); p++) {
				MapPoint mapPoint = mapItem.getMapPoints().get(p);
				Log.d(TAG,"polyline point x:" + mapPoint.long_wgs84 + " point y:" + mapPoint.lat_wgs84);
				point = toWebmercator(mapPoint.lat_wgs84,mapPoint.long_wgs84);
				polygon.lineTo(point);
			}
			
			Graphic g = new Graphic(polygon,new SimpleLineSymbol(mapItem.lineColor,mapItem.lineWidth));

			gl = (GraphicsLayer)this.getMapLayer(MapBaseActivity.DEFAULT_GRAPHICS_LAYER); // this should be a parameter
			int Uid = gl.addGraphic(g);	
	        return Uid;	
		}
		else {
			return 0;
		}
	}
	
	protected int addPicture(String layerName, int pictureResource, Point point) {
		Bitmap libImage = BitmapFactory.decodeResource(getResources(), pictureResource);
		BitmapDrawable libDrawable = new BitmapDrawable(libImage);
        PictureMarkerSymbol pms = new PictureMarkerSymbol(libDrawable);
       
        Graphic g = new Graphic(point, pms);
        Long layerId = layerIdMap.get("layer_location");
        gl = (GraphicsLayer)this.getLayerByID(layerId);
        gl.addGraphic(g);
        return g.getUid();
	}

	protected void processMapData() {
		getCallout().hide();

		Log.d(TAG,"processMapData");
		
		int gId = 0; // ID of graphic object created by displayMapItem

		// get Graphics Layer
		gl = (GraphicsLayer)this.getMapLayer(mapData.getLayerName());
		Log.d(TAG,"test id of gl = " + gl.getID());
		
		// clear the layer if mode == MODE_OVERWRITE
		if (mapData.getMode() == MapData.MODE_OVERWRITE) {
			gl.removeAll();	
		}
    	
		Log.d("ZZZ","there are " + mapData.getMapItems().size() + " map items");
    	for (int i = 0; i < mapData.getMapItems().size(); i++) {
    		MapItem mapItem = mapData.getMapItems().get(i);

    		Log.d(TAG,"displayName before display item = " + mapItem.getItemData().get("displayName"));
    		
    		Log.d("ZZZ","map item " + i + " has " + mapItem.getMapPoints().size() + " map points");

    		// get the ID of the graphic once it has been added to the graphics layer
    		gId = this.dislayMapItem(mapData.getLayerName(),mapItem);

    		// store the index (i) of the mapItem in the graphicIdMap with the key of the graphic ID
    		// this will let ut use the ID of the tapped graphic to get the corresponding mapItem and create the callout
    		Log.d("ZZZ","graphic id " + gId + " maps to mapItem " + i);
    		graphicIdMap.put(Integer.toString(gId),Integer.valueOf(i));
    	}
    	
    	// If there is only one mapItem, display the callout
    	if (mapData.getMapItems().size() == 1) {
    		MapItem mapItem = mapData.getMapItems().get(0);
    		displayCallout(mContext, mapItem);
    	}
	}

	public void displayCallout(Context context, MapItem mapItem) {
		View calloutView = mapItem.getCallout(mContext);
		Callout callout = getCallout(); 	 
		Point calloutPoint = getCalloutPoint(mapItem);
		callout.setContent(calloutView);
    	callout.setCoordinates(calloutPoint);
    	callout.setStyle(R.xml.callout);
		callout.refresh();
		callout.show();		
	}
	
	public Point getCalloutPoint(MapItem mapItem) {
		
		// return a Point object with the coordinates to display a callout at
		// For now, method just uses the first point in the MapPoints array
		// Ultimately, is should be modified to properly place callouts for lines and polygons
		Point calloutPoint = MITMapView2.toWebmercator(mapItem.getMapPoints().get(0).lat_wgs84, mapItem.getMapPoints().get(0).long_wgs84); 
		return calloutPoint;
	}
	
	public Map<String, Long> getLayerIdMap() {
		return layerIdMap;
	}

	public void setLayerIdMap(Map<String, Long> layerIdMap) {
		this.layerIdMap = layerIdMap;
	}

	public Map<String, Integer> getGraphicIdMap() {
		return graphicIdMap;
	}

	public void setGraphicIdMap(Map<String, Integer> graphicIdMap) {
		this.graphicIdMap = graphicIdMap;
	}

	public SpatialReference getMercatorWeb() {
		return mercatorWeb;
	}

	public SpatialReference getWgs84() {
		return wgs84;
	}

	public void setMercatorWeb(SpatialReference mercatorWeb) {
		MITMapView2.mercatorWeb = mercatorWeb;
	}

	public void setWgs84(SpatialReference wgs84) {
		MITMapView2.wgs84 = wgs84;
	}

	public MapData getMapData() {
		return mapData;
	}

	public void setMapData(MapData mapData) {
		this.mapData = mapData;
	}
	

}

