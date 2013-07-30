package edu.mit.mitmobile2.maps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.esri.android.map.Callout;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.Layer;
import com.esri.android.map.LocationService;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.android.map.event.OnZoomListener;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.portal.BaseMap;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleLineSymbol;

import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.objs.MapItem;
import edu.mit.mitmobile2.objs.MapPoint;

public class MITMapView extends MapView  {

	private static final String TAG = "MITMapView";
	private MapAbstractionObject mao;
	private static SpatialReference mercatorWeb; // spatial reference used by the base map
	private static SpatialReference wgs84; // spatial reference used by androids location service
	public static String DEFAULT_GRAPHICS_LAYER = "LAYER_GRAPHICS";
	public static int DEFAULT_PIN = R.drawable.map_red_pin;
	private Polygon selectedExtent; // selected extent for use with fitMapItems
	private boolean showCallout = true;
	PictureMarkerSymbol pms;
	private static int MAP_PADDING = 100;
	private static Double WGS84_PADDING = 0.0005; // use to padd wgs84 map points before they are projected to webmercator
	private GraphicsLayer gl;
	private Context mContext;
	protected LocationService ls;
	protected boolean baseLayersLoaded = false;
	public static final String MAP_ITEMS_KEY = "map_items";
	public static final String MAP_ITEM_INDEX_KEY = "map_item_index";	

	public MITMapView(Context context) {
		super(context);
		mContext = context;
		init(context);
	}

	public MITMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init(context);
	}

	public void addMAOBaseLayer(MapBaseLayer mapBaseLayer) {
		mao.getBaseLayers().put(mapBaseLayer.getLayerIdentifier(), mapBaseLayer);
	}

	public void addMAOGraphicsLayer(String layerName) {
		MapGraphicsLayer mapGraphicsLayer = new MapGraphicsLayer(layerName);
		mao.getGraphicsLayers().put(layerName, mapGraphicsLayer);
	}
	
	public void addMapLayer(Layer layer, String layerName) {
		this.addLayer(layer);
		mao.getLayerIdMap().put(layerName, layer.getID());
	}
	
	public Layer getMapLayer(String layerName) {
		if (mao.getLayerIdMap() != null) {
			Long id = mao.getLayerIdMap().get(layerName);                	                	                   
			return this.getLayerByID(id);
		}
		else {
			return null;
		}
	}

	public void updateBaseLayersStatus() {
		// loop through base layers
		Iterator it = mao.getBaseLayers().entrySet().iterator();
		while (it.hasNext()) {
	        Map.Entry glpairs = (Map.Entry)it.next();
	        String layerName = (String)glpairs.getKey();
	        Layer layer = this.getMapLayer(layerName);
	        if (!layer.isInitialized()) {
	        	this.baseLayersLoaded = false;
	        }
		}
    	this.baseLayersLoaded = true;
    	MobileWebApi.sendSuccessMessage(mapInitUiHandler);
	}

	public void addMapItems(ArrayList<? extends MapItem> mapItems) {
		addMapItems(mapItems,MITMapView.DEFAULT_GRAPHICS_LAYER,MapGraphicsLayer.MODE_OVERWRITE);		
	}
	
	public void addMapItems(ArrayList<? extends MapItem> mapItems, int mode) {
		addMapItems(mapItems,MITMapView.DEFAULT_GRAPHICS_LAYER,mode);		
	}

	public void addMapItems(ArrayList<? extends MapItem> mapItems, String layerName) {		
		addMapItems(mapItems,layerName,MapGraphicsLayer.MODE_OVERWRITE);		
	}
	
	public void addMapItems(ArrayList<? extends MapItem> mapItems, String layerName, int mode) {
		if (mapItems != null) {
			if (!mao.getGraphicsLayers().containsKey(layerName)) {
				addMAOGraphicsLayer(layerName);
			}
			
			if (mao.getGraphicsLayers().get(layerName).getMapItems() != null && mode == MapGraphicsLayer.MODE_OVERWRITE) {
				mao.getGraphicsLayers().get(layerName).getMapItems().clear();
			}
			
			for (int i = 0; i < mapItems.size(); i++) {
				MapItem mapItem = mapItems.get(i);
				mapItem.setGraphicsLayer(layerName);
				if (!mao.getGraphicsLayers().containsKey(layerName)) {
					addMAOGraphicsLayer(layerName);
				}
				
				mapItem.setIndex(mao.getGraphicsLayers().get(layerName).getMapItems().size());
				mao.getGraphicsLayers().get(layerName).getMapItems().add(mapItem);
			}
			
		}
		
		if (baseLayersLoaded) {
			syncGraphicsLayers();
		}
	}

	public void addMapItem(MapItem mapItem) {
		mapItem.setGraphicsLayer(MITMapView.DEFAULT_GRAPHICS_LAYER);
		addMapItem(mapItem,MITMapView.DEFAULT_GRAPHICS_LAYER);
	}

	public void addMapItem(MapItem mapItem, String layerName) {
		mapItem.setGraphicsLayer(layerName);
		if (!mao.getGraphicsLayers().containsKey(layerName)) {
			addMAOGraphicsLayer(layerName);
		}
		
		mapItem.setIndex(mao.getGraphicsLayers().get(layerName).getMapItems().size());
		mao.getGraphicsLayers().get(layerName).getMapItems().add(mapItem);

		// add the graphic for the map item if the base layers are loaded
		//if (baseLayersLoaded()) {
	    //	syncGraphicsLayers();
	    //}
		
	}

	public void clearMapItems() {
		// clears all graphics layers
		Iterator it = mao.getGraphicsLayers().entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry glpairs = (Map.Entry)it.next();
		    String layerName = (String)glpairs.getKey();
		    clearMapItems(layerName);
		}

	}
	
	public void clearMapItems(String layerName) {
		// clears specified graphics layer
		if (mao.getGraphicsLayers().get(layerName) != null) {
			mao.getGraphicsLayers().get(layerName).getMapItems().clear();
		}
	}
	
	protected int dislayMapItem(MapItem mapItem) {
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
	
	Point projectMapPoint(MapPoint mapPoint) {
		Point point = (Point)GeometryEngine.project(new Point(mapPoint.long_wgs84,mapPoint.lat_wgs84), mao.getSpatialReference(),this.getSpatialReference());		
		return point;
	}

	private Point projectMapPoint(Double lat,Double lon) {
		MapPoint mapPoint = new MapPoint(lat,lon);
		return projectMapPoint(mapPoint);
	}

	Polygon projectMapPolygon(Polygon mapPolygon) {
		Polygon polygon = (Polygon)GeometryEngine.project(mapPolygon, mao.getSpatialReference(),this.getSpatialReference());		
		return polygon;
	}

	protected int displayMapPoint(MapItem mapItem) {
		if (mapItem.getMapPoints().size() > 0) {
			MapPoint mapPoint = mapItem.getMapPoints().get(0);

			Point point = projectMapPoint(mapPoint);
			
			Bitmap libImage = BitmapFactory.decodeResource(getResources(), mapItem.symbol);
			BitmapDrawable libDrawable = new BitmapDrawable(libImage);
			PictureMarkerSymbol pms = new PictureMarkerSymbol(libDrawable);       

			switch (mapItem.verticalAlign) {
				case MapItem.VALIGN_TOP:
					mapItem.offsetY = -(libDrawable.getIntrinsicHeight() / 2);
				break;
				
				case MapItem.VALIGN_CENTER:
					mapItem.offsetY = 0;
				break;
				
				case MapItem.VALIGN_BOTTOM:
					mapItem.offsetY = libDrawable.getIntrinsicHeight() / 2;
				break;
				
				default:
					mapItem.offsetY = libDrawable.getIntrinsicHeight() / 2;
				break;
				
			}

			switch (mapItem.horizontalAlign) {
			case MapItem.ALIGN_LEFT:
				mapItem.offsetX = -(libDrawable.getIntrinsicWidth() / 2);
			break;
			
			case MapItem.ALIGN_CENTER:
				mapItem.offsetX = 0;
			break;
			
			case MapItem.ALIGN_RIGHT:
				mapItem.offsetX = libDrawable.getIntrinsicWidth() / 2;
			break;
			
			default:
				mapItem.offsetX = 0;
			break;
			
		}

			pms.setOffsetY(mapItem.offsetY);
			pms.setOffsetX(mapItem.offsetX);
			
			Map attributes = new HashMap();
		
			Graphic g = new Graphic(point, pms,attributes, null);
			gl = (GraphicsLayer)this.getMapLayer(mapItem.getGraphicsLayer()); 
			int Uid = gl.addGraphic(g);	
	        return Uid;				
		}
		else {
			return 0;
		}
	}
	
	protected int displayMapPolyline(MapItem mapItem) {
		Point point;
		Point startPoint;
		Polyline polyline = new Polyline();

		if (mapItem.getMapPoints().size() > 0) {
			
			startPoint = projectMapPoint(mapItem.getMapPoints().get(0));

			polyline.startPath(startPoint);
			for (int p = 0; p < mapItem.getMapPoints().size(); p++) {
				MapPoint mapPoint = mapItem.getMapPoints().get(p);
				point = (Point)GeometryEngine.project(new Point(mapPoint.long_wgs84,mapPoint.lat_wgs84), mao.getSpatialReference(),this.getSpatialReference());
				polyline.lineTo(point);
			}
			
			Graphic g = new Graphic(polyline,new SimpleLineSymbol(mapItem.lineColor,mapItem.lineWidth));

			gl = (GraphicsLayer)this.getMapLayer(mapItem.getGraphicsLayer()); 
			int Uid = gl.addGraphic(g);	
	        return Uid;	
		}
		else {
			return 0;
		}
	}
	
	protected int displayMapPolygon(MapItem mapItem) {
		Point point;
		Point startPoint;
		Polygon polygon = new Polygon();
	   
		if (mapItem.getMapPoints().size() > 0) {
			
			//startPoint = toWebmercator(mapItem.getMapPoints().get(0).lat_wgs84,mapItem.getMapPoints().get(0).long_wgs84);
			startPoint = (Point)GeometryEngine.project(new Point(mapItem.getMapPoints().get(0).long_wgs84,mapItem.getMapPoints().get(0).lat_wgs84), mao.getSpatialReference(),this.getSpatialReference());

			polygon.startPath(startPoint);
			for (int p = 0; p < mapItem.getMapPoints().size(); p++) {
				MapPoint mapPoint = mapItem.getMapPoints().get(p);
				//point = toWebmercator(mapPoint.lat_wgs84,mapPoint.long_wgs84);
				point = (Point)GeometryEngine.project(new Point(mapPoint.long_wgs84,mapPoint.lat_wgs84), mao.getSpatialReference(),this.getSpatialReference());

				polygon.lineTo(point);
			}
			
			Graphic g = new Graphic(polygon,new SimpleLineSymbol(mapItem.lineColor,mapItem.lineWidth));

			gl = (GraphicsLayer)this.getMapLayer(mapItem.getGraphicsLayer()); 
			int Uid = gl.addGraphic(g);	
	        return Uid;	
		}
		else {
			return 0;
		}
	}
	
	public void displayCallout(Context context, MapItem mapItem) {
		View calloutView = mapItem.getCallout(mContext,mao.getGraphicsLayers().get(mapItem.getGraphicsLayer()).getMapItems(),mapItem.getIndex());
		Callout callout = getCallout();
		int dpi = getResources().getDisplayMetrics().densityDpi;
		if (dpi == 480) {
			callout.setOffset(0, mapItem.offsetY * 6);
		}
		else {
			callout.setOffset(0, mapItem.offsetY * 2);			
		}
		Point calloutPoint = getCalloutPoint(mapItem);
		callout.setContent(calloutView);
    	callout.setCoordinates(calloutPoint);
    	callout.setStyle(R.xml.callout);
    	callout.setMaxWidth(800);
    	callout.setMaxHeight(300);
		callout.refresh();
		callout.show();		
	}
	
	public Point getCalloutPoint(MapItem mapItem) {
		
		// return a Point object with the coordinates to display a callout at
		// For now, method just uses the first point in the MapPoints array
		// Ultimately, is should be modified to properly place callouts for lines and polygons
		//Point calloutPoint = MITMapView.toWebmercator(mapItem.getMapPoints().get(0).lat_wgs84, mapItem.getMapPoints().get(0).long_wgs84); 
		Point calloutPoint = (Point)GeometryEngine.project(new Point(mapItem.getMapPoints().get(0).long_wgs84,mapItem.getMapPoints().get(0).lat_wgs84), mao.getSpatialReference(),this.getSpatialReference());

		return calloutPoint;
	}

	public Polygon getGraphicExtent() {
		//loops through all points for all map items and returns a polygon for the extent of those items
		Double minLong = null;
		Double minLat = null;
		Double maxLong = null;
		Double maxLat = null;
		MapPoint mapPoint;
		
		if (mao != null) {
			Iterator it = mao.getGraphicsLayers().entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry glpairs = (Map.Entry)it.next();
			    String layerName = (String)glpairs.getKey();
				MapGraphicsLayer mgl = mao.getGraphicsLayers().get(layerName);
				ArrayList<MapItem> mapItems = mgl.getMapItems();	
				if (mapItems.size() > 0) {
											
					for (int i = 0; i < mapItems.size(); i++) {
						for (int p = 0; p < mapItems.get(i).getMapPoints().size(); p++) {
							 mapPoint = mapItems.get(i).getMapPoints().get(p);
							 if (minLong == null) {
								minLong = mapPoint.long_wgs84;
								minLat = mapPoint.lat_wgs84;
								maxLong = mapPoint.long_wgs84;
								maxLat = mapPoint.lat_wgs84;
							 }
							 else {
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
						}
					}

					// if there is only 1 map point, add padding to create the polygon
					if( mapItems.size() == 1) {
						minLat -= WGS84_PADDING;
						maxLat += WGS84_PADDING;
						minLong -= WGS84_PADDING;
						maxLong += WGS84_PADDING;					
					}

				}


			}
			
			// create Polygon from 4 points
			// start of the south west point
//			Log.d(TAG,"fitMapItems minLat = " + minLat);
//			Log.d(TAG,"fitMapItems maxLat = " + maxLat);
//			Log.d(TAG,"fitMapItems minLong = " + minLong);
//			Log.d(TAG,"fitMapItems maxLong = " + maxLong);

			Point SW = new Point(minLong,minLat);
			Point NW = new Point(minLong,maxLat);
			Point NE = new Point(maxLong,maxLat);
			Point SE = new Point(maxLong,minLat);

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
							
	public SpatialReference getMercatorWeb() {
		return mercatorWeb;
	}

	public SpatialReference getWgs84() {
		return wgs84;
	}

	public void setMercatorWeb(SpatialReference mercatorWeb) {
		MITMapView.mercatorWeb = mercatorWeb;
	}

	public void setWgs84(SpatialReference wgs84) {
		MITMapView.wgs84 = wgs84;
	}

	public void init(final Context mContext) {
		
	    //Log.d(TAG,"mapInit");
	    
	    mao = new MapAbstractionObject();
		final MITMapView mapView = this;

	    // OnStatusChangedListener
        this.setOnStatusChangedListener(new OnStatusChangedListener() {
            @Override
			public void onStatusChanged(Object source, STATUS status) {
            	//Log.d(TAG,"map status changed: " + source.getClass().getSimpleName() + " status = " + status.getValue());
            	updateBaseLayersStatus();
            }
        });
        // END OnStatusChangedListener

        // OnSingleTapListener
        this.setOnSingleTapListener(new OnSingleTapListener() {
    		private static final long serialVersionUID = 1L;
    		
    		@Override
    		public void onSingleTap(float x, float y) {
    			//Log.d(TAG,"x: " + x + " y:" + y);
    			Callout callout = mapView.getCallout(); 

    			if (!mapView.isLoaded()) {
    				return;
    			}
    			
    			if (mao.getGraphicsLayers() != null) {
    		    	Iterator it = mao.getGraphicsLayers().entrySet().iterator();
    				while (it.hasNext()) {
    			        Map.Entry glpairs = (Map.Entry)it.next();
    			        String layerName = (String)glpairs.getKey();
    			        MapGraphicsLayer mapGraphicsLayer = mapView.getMao().getGraphicsLayers().get(layerName);
    	    			GraphicsLayer gl = (GraphicsLayer)mapView.getMapLayer(layerName);
    	    			
    	    			if (gl != null) {
	    	    			int[] graphicId = gl.getGraphicIDs(x, y, 10);
	    	    			
	    	    			if (graphicId.length > 0) {
	    	    				for (int i = 0; i < graphicId.length; i++) {
	    	    	    			Graphic g = gl.getGraphic(graphicId[i]);
	    	    	    			
	    	    	    			// get the index of the mapItem from the GraphicIdMap
	    	    	    			Integer mapItemIndex = mapView.getMao().getGraphicsLayers().get(layerName).getGraphicIdMap().get(Integer.toString(g.getUid()));
	    	    	    			
	    	    	    			// get the mapItem
	    	    	    			MapItem mapItem = mapView.getMao().getGraphicsLayers().get(layerName).getMapItems().get(mapItemIndex);
									
	    	    	    			// Display the Callout if it is defined
	    	    	    			if (mapItem.getCallout(mapView.getContext(),mapGraphicsLayer.getMapItems(),mapItemIndex.intValue()) != null) {
	    	
	    	    	    				// center to the selected item
		    	    	    			Point centerPt = mapView.projectMapPoint(mapItem.getCenter());
		    	    	    			mapView.centerAt(centerPt, false);
		    	    	    
	    	    	    				mapView.displayCallout(mContext, mapItem);
	    	    	    				return; // quit after the first callout is displayed
	    	    	    			}
	    	    				}
	    	    			}
	    	    			else {
	    	    				callout.hide();
	    	    			}
    	    			} // end if gl != null
    				} // end while
    			} // end if mao.getGraphicsLayers() != null
    		}
        	
        });
        // END OnSingleTapListener
        
        MapModel.fetchMapServerData(mContext, mapUiHandler);    			        
	}
	
	
	public Handler mapUiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //mLoadingView.setVisibility(View.GONE);

            if (msg.arg1 == MobileWebApi.SUCCESS) {
            	//Log.d(TAG,"MobileWebApi success");
                @SuppressWarnings("unchecked")
                MapServerData mapServerData = (MapServerData)msg.obj;
                // get the layers for the default group
                String defaultBasemap = mapServerData.getDefaultBasemap();
                
                // add the base layers to the map
                ArrayList<MapBaseLayer> baseMaps = mapServerData.getBaseLayerGroup().get(defaultBasemap);
                if (baseMaps != null) {
	                for (int i = 0; i < baseMaps.size(); i++) {
	                	MapBaseLayer layer = baseMaps.get(i);
	                	addMAOBaseLayer(layer);
	                }
	            }
                                
                // add all defined base layers to the mapView 
                syncLayers();
                                
        		// Define a listener that responds to location updates
        		LocationListener locationListener = new LocationListener() {

        			@Override
					public void onLocationChanged(Location location) {
        		      // Called when a new location is found by the network location provider.
        		      makeUseOfNewLocation(location);
        		    }

        		    private void makeUseOfNewLocation(Location location) {
        		    	//Log.d(TAG,"makeUseOfNewLocation");
        				// TODO Auto-generated method stub
        				if (location != null) {
	        		    	//Log.d(TAG,"lat = " + location.getLatitude());
	        				//Log.d(TAG,"lon = " + location.getLongitude());
        				}
        		    }

        			@Override
					public void onStatusChanged(String provider, int status, Bundle extras) {
        				//Log.d(TAG,"map status = " + status);
        				//Log.d(TAG,"initialized = " + OnStatusChangedListener.STATUS.INITIALIZED);
        			}

        		    @Override
					public void onProviderEnabled(String provider) {}

        		    @Override
					public void onProviderDisabled(String provider) {}
        		};

        		// Initialize location service
        		ls = getLocationService();
    			Log.d(TAG,"new Locationistener");
        		ls.setLocationListener(locationListener);
        		ls.setAutoPan(false);
        		ls.setAllowNetworkLocation(true);
        		ls.start();
        		
        		onMapLoaded();
       		        		
            } else if (msg.arg1 == MobileWebApi.ERROR) {
                //mLoadingView.showError();
            } else if (msg.arg1 == MobileWebApi.CANCELLED) {
                //mLoadingView.showError();
            }
        }
    };

    public void syncLayers() {
    	syncBaseLayers();
    }
    
    public void syncBaseLayers() {
    	// loops through all base layers defined in the map abstraction object, mao, and adds them to the mapView if they don't already exists

    	Iterator it = mao.getBaseLayers().entrySet().iterator();
		while (it.hasNext()) {
	        Map.Entry glpairs = (Map.Entry)it.next();
	        String layerName = (String)glpairs.getKey();
	        MapBaseLayer layer = mao.getBaseLayers().get(layerName);
	        Long layerId = mao.getLayerIdMap().get(layerName);
	        if (layerId == null) {
	        	// create and add the layer
            	ArcGISTiledMapServiceLayer serviceLayer = new ArcGISTiledMapServiceLayer(layer.getUrl());
            	serviceLayer.setName(layer.getLayerIdentifier());
            	Log.d(TAG,"adding service layer " + serviceLayer.getName());
                addMapLayer(serviceLayer, serviceLayer.getName());
      	
	        }
		}
		
    }
    
    public void syncGraphicsLayers() {
    	Log.d(TAG,"syncGraphicsLayers()");
    	// loops through all graphics layers defined in mao, adding them if they dont exist
    	// adds mapitem from each graphics layer in mao, overwriting mapitems

    	Iterator it = mao.getGraphicsLayers().entrySet().iterator();
		while (it.hasNext()) {
	        Map.Entry glpairs = (Map.Entry)it.next();
	        final String layerName = (String)glpairs.getKey();
	        MapGraphicsLayer layer = mao.getGraphicsLayers().get(layerName);
	        Long layerId = mao.getLayerIdMap().get(layerName);
	        if (layerId == null) {
	        	// create and add the layer
            	GraphicsLayer graphicsLayer = new GraphicsLayer();
            	graphicsLayer.setName(layerName);
            	Log.d(TAG,"adding graphics layer " + layerName);
                graphicsLayer.setOnStatusChangedListener(new OnStatusChangedListener() {
                    public void onStatusChanged(Object source, STATUS status) {
                        if (OnStatusChangedListener.STATUS.INITIALIZED == status){
                        	Log.d(TAG,source.getClass().getName() + " " +  ((GraphicsLayer)source).getName() + " is initialized");
                        	if (baseLayersLoaded) {
                        		Log.d(TAG,"all base layers loaded, ready to add graphics");
                        		processMapItems(layerName);
                        	}
                        	else {
                        		Log.d(TAG,"still waiting for base layers to load");
                        	}
                        }
                    }
                });

                addMapLayer(graphicsLayer, layerName);
      	
	        }
	        else {
	        	if (baseLayersLoaded) {
	        		Log.d(TAG,"all base layers loaded, ready to add graphics");
	        		processMapItems(layerName);
	        	}
	        	else {
	        		Log.d(TAG,"still waiting for base layers to load");
	        	}
	        }
		}

		// Display the callout if there is only one map item with a defined callout, and showCallout is true
		ArrayList<MapItem> calloutItems = getCalloutItems();
		if (calloutItems.size() == 1 && showCallout) {
			displayCallout(mContext, calloutItems.get(0));
		}
    }

    final class MyOnStatusChangedListener implements OnStatusChangedListener {

    	private static final long serialVersionUID = 1L;

        public void onStatusChanged(Object source, STATUS status) {
            //conditional checks if mapView's status has changed to initialized 
             if (OnStatusChangedListener.STATUS.INITIALIZED == status && source == this) { 
            	 Log.d(TAG,"mapView is initiallized");
             }
         }
    }
    
    public Handler mapInitUiHandler = new Handler() {
        @SuppressWarnings("unchecked")
		@Override
        public void handleMessage(Message msg) {
        	Log.d(TAG,"mapInitUiHandler success");
            if (msg.arg1 == MobileWebApi.SUCCESS) {
            	
            	try {
            		Log.d(TAG,"map is initialized, synching graphics");
            		syncGraphicsLayers(); 
            		if (selectedExtent != null) {
            			setExtent(projectMapPolygon(selectedExtent),MAP_PADDING);		
            		}

            	}
            	catch (Exception e) {
            	}
            }
            else if (msg.arg1 == MobileWebApi.ERROR) {

            } 
            else if (msg.arg1 == MobileWebApi.CANCELLED) {

            }
        }
    };

    private void onMapLoaded() {

    	
    }
    
	public void fitMapItems() {

		if (baseLayersLoaded) {
			setExtent(projectMapPolygon(getGraphicExtent()),MAP_PADDING);		
		}
		else {
			this.selectedExtent = getGraphicExtent();		
		}
	}

	private MapItem getFirstMapItem() {
		MapItem firstItem = null;
		//loops through layers in MAO and returns the first map item found
		Iterator it = mao.getBaseLayers().entrySet().iterator();
		while (it.hasNext()) {
	        Map.Entry glpairs = (Map.Entry)it.next();
	        String layerName = (String)glpairs.getKey();
	        MapGraphicsLayer mgl = mao.getGraphicsLayers().get(layerName);
	        if (mgl != null) {
	        	if (mgl.getMapItems() != null) {
	        		if (mgl.getMapItems().size() > 0) {
	        			firstItem = mgl.getMapItems().get(0);
	        			return firstItem;
	        		}
	        	}
	        }
		}

		return firstItem;
	}
	
    private void processMapItems(final String layerName) {
    	this.getCallout().hide();
    	this.pause();
    	Log.d(TAG,"processing map Items for " + layerName);
		int gId = 0; // ID of graphic object created by displayMapItem
    	
		// clear the graphics layer
		gl = (GraphicsLayer)getMapLayer(layerName);
		gl.removeAll();
		Log.d(TAG,"clearing graphics on layer " + layerName);
		// get MapGraphicsLayer
		MapGraphicsLayer mapGraphicsLayer = mao.getGraphicsLayers().get(layerName);
		ArrayList<MapItem> mapItems = mapGraphicsLayer.getMapItems();
		
		// sort map items by weight of  x and y coords
		Collections.sort(mapItems, new CustomComparator());
		
		Log.d(TAG,"num map items = " + mapItems.size());
    	for (int i = 0; i < mapItems.size(); i++) {
    		try {
	    		MapItem mapItem = mapItems.get(i);
	    		mapItem.setIndex(i);
	    		// get the ID of the graphic once it has been added to the graphics layer
	    		gId = dislayMapItem(mapItem);

	    		// store the index (i) of the mapItem in the graphicIdMap with the key of the graphic ID
	    		// this will let ut use the ID of the tapped graphic to get the corresponding mapItem and create the callout
    			mapGraphicsLayer.getGraphicIdMap().put(Integer.toString(gId),Integer.valueOf(i));    		
    		}
    		catch (Exception e) {
    			Log.d(TAG, "error processing map items");
    			Log.d(TAG, e.getStackTrace().toString());
    		}
    		
    	}
    	this.unpause();    	
    }

	public MapAbstractionObject getMao() {
		return mao;
	}

	public void setMao(MapAbstractionObject mao) {
		this.mao = mao;
	}
	
	public class CustomComparator implements Comparator<MapItem> {
	    @Override
	    public int compare(MapItem o1, MapItem o2) {
	        return o2.getSortWeight().compareTo(o1.getSortWeight());
	    }
	}

	public void debugMAO() {
		Iterator it = mao.getGraphicsLayers().entrySet().iterator();
		while (it.hasNext()) {
	        Map.Entry glpairs = (Map.Entry)it.next();
	        final String layerName = (String)glpairs.getKey();
	        MapGraphicsLayer layer = mao.getGraphicsLayers().get(layerName);
	        Log.d(TAG,"Graphics Layer: " + layerName);
	        Log.d(TAG,layer.getMapItems().size() + " map Items for layer");
		}
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		super.pause();
		Log.d(TAG,"pause map");
		
	}

	@Override
	public void unpause() {
		// TODO Auto-generated method stub
		super.unpause();
		Log.d(TAG,"unpause map");
	}
	
	private ArrayList<Graphic> getCalloutGraphics() {
		ArrayList<Graphic> calloutGraphics = new ArrayList<Graphic>();
		if (mao.getGraphicsLayers() != null) {
	    	Iterator it = mao.getGraphicsLayers().entrySet().iterator();
			while (it.hasNext()) {
		        Map.Entry glpairs = (Map.Entry)it.next();
		        String layerName = (String)glpairs.getKey();
		        MapGraphicsLayer mapGraphicsLayer = this.getMao().getGraphicsLayers().get(layerName);
    			GraphicsLayer gl = (GraphicsLayer)this.getMapLayer(layerName);
    			
    			if (gl != null) {
	    			int[] graphicId = gl.getGraphicIDs();
	    			
	    			if (graphicId.length > 0) {
	    				for (int i = 0; i < graphicId.length; i++) {
	    	    			Graphic g = gl.getGraphic(graphicId[i]);
	    	    			
	    	    			// get the index of the mapItem from the GraphicIdMap
	    	    			Integer mapItemIndex = this.getMao().getGraphicsLayers().get(layerName).getGraphicIdMap().get(Integer.toString(g.getUid()));
	    	    			
	    	    			// get the mapItem
	    	    			MapItem mapItem = this.getMao().getGraphicsLayers().get(layerName).getMapItems().get(mapItemIndex);
							
	    	    			// If a callout is defined for the mapItem, add its graphic to the graphics array
	    	    			if (mapItem.getCallout(this.getContext(),mapGraphicsLayer.getMapItems(),mapItemIndex.intValue()) != null) {
	    	    				calloutGraphics.add(g);
	    	    			}
	    				}
	    			}
    			}
			}
		}
		return calloutGraphics;
	}
	
	private ArrayList<MapItem> getCalloutItems() {
		ArrayList<MapItem> calloutItems = new ArrayList<MapItem>();
		if (mao.getGraphicsLayers() != null) {
	    	Iterator it = mao.getGraphicsLayers().entrySet().iterator();
			while (it.hasNext()) {
		        Map.Entry glpairs = (Map.Entry)it.next();
		        String layerName = (String)glpairs.getKey();
		        MapGraphicsLayer mapGraphicsLayer = this.getMao().getGraphicsLayers().get(layerName);
    			 
		        if (mapGraphicsLayer.getMapItems() != null) {
		        	for (int m = 0; m < mapGraphicsLayer.getMapItems().size(); m++) {
    	    			MapItem mapItem = mapGraphicsLayer.getMapItems().get(m);
    	    			if (mapItem.getCallout(this.getContext()) != null) {
    	    				calloutItems.add(mapItem);
    	    			}
 		        	}
		        }
			}
		}
		return calloutItems;
	}

}

