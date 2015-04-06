package edu.mit.mitmobile2.maps;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Location;
import android.util.TypedValue;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.maps.android.ui.IconGenerator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.mit.mitmobile2.R;
import timber.log.Timber;

public class MITMapView {

    public int mapBoundsPadding;

    private MapView mapView;
    private GoogleMap mMap;
    private MapFragment mapFragment;
    private MapItem mItem;
    public static String MAP_ITEMS = "MAP_ITEMS";
    private FragmentManager mFm;
    private int mapResourceId;
    private Marker lastClickedMarker;
    private LatLngBounds defaultBounds;
    private boolean isMapExpanded = false;

    private ArrayList<MapItem> mapItems = new ArrayList<>();

    private List<Marker> dynamicMarkers = new ArrayList<>();
    private List<Marker> staticMarkers = new ArrayList<>();
    private List<Polyline> dynamicLines = new ArrayList<>();
    private List<Polygon> dynamicPolygons = new ArrayList<>();

    //set initial latlng for zoom in MIT area
    final LatLng initialLatLng = new LatLng(42.359858, -71.09913);
    public static final int INITIAL_ZOOM = 14;
    private Context mContext;

    public MITMapView(Context mContext, FragmentManager fm, int mapResourceId) {
        this.mContext = mContext;
        this.mFm = fm;
        this.mapResourceId = mapResourceId;
        this.mapFragment = (MapFragment) fm.findFragmentById(mapResourceId);
        mMap = this.mapFragment.getMap();
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLatLng, MITMapView.INITIAL_ZOOM));
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false); // delete default button
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mapBoundsPadding = (int) mContext.getResources().getDimension(R.dimen.map_bounds_padding);
    }

    public MITMapView(Context context, MapView mapView, final GoogleMap.OnMapLoadedCallback callback) {
        this.mContext = context;
        this.mapView = mapView;
        mMap = this.mapView.getMap();
        MapsInitializer.initialize(context);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLatLng, MITMapView.INITIAL_ZOOM));
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false); // delete default button
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setOnMapLoadedCallback(callback);
        mapBoundsPadding = (int) mContext.getResources().getDimension(R.dimen.map_bounds_padding);

        TileProvider provider = new TileProvider() {
            @Override
            public Tile getTile(int i, int i1, int i2) {
                // Use this hacky implementation until 512x512 images are available
                return getTileFromNextZoomLevel(i, i1, i2);
            }
        };

        TileOverlayOptions options = new TileOverlayOptions();
        options.tileProvider(provider);
        options.zIndex(14f);

        mMap.addTileOverlay(options);
    }

    public void show() {
        Fragment f = mFm.findFragmentById(mapResourceId);
        mFm.beginTransaction()
                .show(f)
                .commit();
    }

    public void hide() {
        Fragment f = mFm.findFragmentById(mapResourceId);
        mFm.beginTransaction()
                .hide(f)
                .commit();
    }

    public void addMapItem(MapItem mItem) {
        if (mMap != null) {
            if (MapItem.class.isAssignableFrom(mItem.getClass())) {
                int type = mItem.getMapItemType();

                switch (type) {
                    case 0:
                        break;

                    case MapItem.MARKERTYPE:
                        if (mItem.getMarkerText() != null) {
                            IconGenerator iconGenerator = new IconGenerator(mContext);
                            iconGenerator.setBackground(mContext.getResources().getDrawable(R.drawable.usermarker));

                            iconGenerator.setTextAppearance(10); //set font size?
                            Bitmap bitmap = iconGenerator.makeIcon(mItem.getMarkerText());
                            Marker marker = mMap.addMarker(mItem.getMarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
                            if (mItem.isDynamic()) {
                                dynamicMarkers.add(marker);
                            }
                        } else {
                            Marker marker;
                            if (mItem.isVehicle()) {
                                marker = mMap.addMarker(mItem.getMarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.maps_shuttle_indicator)));
                                marker.setRotation(getVehicleHeading(marker));
                            } else {
                                if (!isMapExpanded) {
                                    marker = mMap.addMarker(mItem.getMarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.map_stops)));
                                } else {
                                    marker = mMap.addMarker(mItem.getMarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin)));
                                }
                            }
                            if (mItem.isDynamic()) {
                                dynamicMarkers.add(marker);
                            } else {
                                staticMarkers.add(marker);
                            }
                        }
                        break;

                    case MapItem.POLYLINETYPE:
                        Polyline polyline = mMap.addPolyline(mItem.getPolylineOptions());
                        if (mItem.isDynamic()) {
                            dynamicLines.add(polyline);
                        }
                        break;

                    case MapItem.POLYGONTYPE:
                        Polygon polygon = mMap.addPolygon(mItem.getPolygonOptions());
                        if (mItem.isDynamic()) {
                            dynamicPolygons.add(polygon);
                        }
                        break;
                }
            }
        }
    }

    public void clearDynamic() {
        for (Marker m : dynamicMarkers) {
            m.remove();
        }
        dynamicMarkers.clear();

        for (Polyline pl : dynamicLines) {
            pl.remove();
        }
        dynamicLines.clear();

        for (Polygon pg : dynamicPolygons) {
            pg.remove();
        }
        dynamicPolygons.clear();

        removeDynamicItems();
    }

    public void updateStaticItems(boolean mapViewExpanded) {
        for (Marker m : staticMarkers) {
            if (mapViewExpanded) {
                m.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_stops));
            } else {
                m.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin));
            }
        }
    }

    public void removeDynamicItems() {
        List<Integer> indicesToRemove = new ArrayList<>();
        for (MapItem item : mapItems) {
            if (item.isDynamic()) {
                indicesToRemove.add(mapItems.indexOf(item));
            }
        }

        Collections.reverse(indicesToRemove);

        for (Integer i : indicesToRemove) {
            mapItems.remove(i.intValue());
        }
    }

    public void addMapItemList(ArrayList<MapItem> mapItems, Boolean clear, Boolean fit) {
        this.mapItems.addAll(mapItems);
        if (clear) {
            mMap.clear();
        }
        if (mMap != null && mapItems != null && mapItems.size() > 0) {
            for (MapItem item : mapItems) {
                addMapItem(item);
            }

            if (fit) {
                this.fitMapItems();
            }
        }
    }

    public void addMapItemList(ArrayList<MapItem> mapItems, boolean clear) {
        addMapItemList(mapItems, clear, true);
    }

    public void fitMapItems() {

        //Calculate the markers to get their position
        LatLngBounds.Builder b = new LatLngBounds.Builder();
        for (int i = 0; i < mapItems.size(); i++) {
            MapItem mItem = mapItems.get(i);
            if (mItem.getMapItemType() == MapItem.MARKERTYPE) {
                b.include(mItem.getMarkerOptions().getPosition());
            } else if (mItem.getMapItemType() == MapItem.POLYLINETYPE) {
                for (LatLng point : mItem.getPolylineOptions().getPoints()) {
                    b.include(point);
                }
            }
        }
        defaultBounds = b.build();
    }

    public void setToDefaultBounds(boolean animate, int animationLength) {
        Resources resources = mContext.getResources();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(defaultBounds, resources.getDisplayMetrics().widthPixels, (int) resources.getDimension(R.dimen.shuttle_routes_map_header_height), mapBoundsPadding);
        if (animate) {
            mMap.animateCamera(cameraUpdate, animationLength, null);
        } else {
            mMap.moveCamera(cameraUpdate);
        }
    }

    public void adjustCameraToShowInHeader(boolean animate, int animationLength, int orientation) {
        Resources resources = mContext.getResources();
        Projection projection = mMap.getProjection();

        TypedValue typedValue = new TypedValue();
        int actionBarHeight = 0;
        if (mContext.getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true)) {
            actionBarHeight = (int) TypedValue.complexToDimension(typedValue.data, resources.getDisplayMetrics());
        }

        int x, y;

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            x = resources.getDisplayMetrics().widthPixels / 2;
            y = resources.getDisplayMetrics().heightPixels - (int) resources.getDimension(R.dimen.shuttle_routes_map_header_center_y) - actionBarHeight - mapBoundsPadding;
        } else {
            x = resources.getDisplayMetrics().widthPixels - ((resources.getDisplayMetrics().widthPixels - (int) (resources.getDimension(R.dimen.shuttle_routes_listview_landscape_width))) / 2);
            y = (resources.getDisplayMetrics().heightPixels / 2) - actionBarHeight;
        }

        Point point = new Point(x, y);

        LatLng offsetCenter = projection.fromScreenLocation(point);
        float zoom = mMap.getCameraPosition().zoom;
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(offsetCenter, zoom);
        if (animate) {
            mMap.animateCamera(cameraUpdate, animationLength, null);
        } else {
            mMap.moveCamera(cameraUpdate);
        }
    }

    public int dpToPx(Resources res, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, res.getDisplayMetrics());
    }

    public GoogleMap getMap() {
        return this.mMap;
    }

    public void toggle() {
        if (isExpanded()) {
            float map_height = mContext.getResources().getDimension(R.dimen.map_height);
            mapFragment.getView().setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) map_height));
        } else {
            mapFragment.getView().setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        }
    }

    public Boolean isExpanded() {
        // return true if the current map height != the collapsed map height defined at R.dimen.map_height
        float h = mapFragment.getView().getHeight();
        float map_height = mContext.getResources().getDimension(R.dimen.map_height);
        return (h != map_height);
    }

    public void showLocation() {
        Location location = mMap.getMyLocation();
        CameraPosition position = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(MITMapView.INITIAL_ZOOM).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
    }

    public Marker getLastClickedMarker() {
        return lastClickedMarker;
    }

    public void setLastClickedMarker(Marker lastClickedMarker) {
        this.lastClickedMarker = lastClickedMarker;
    }

    public MapView getGoogleMapView() {
        return mapView;
    }

    public LatLngBounds getDefaultBounds() {
        return defaultBounds;
    }

    public void setMapViewExpanded(boolean isMapExpanded) {
        this.isMapExpanded = isMapExpanded;
    }

    public float getVehicleHeading(Marker marker) {
        float mapHeading = mMap.getCameraPosition().bearing;
        float vehicleHeading = Float.parseFloat(marker.getSnippet());

        float offsetHeading = mapHeading + vehicleHeading;

        while (offsetHeading > 360.0) {
            offsetHeading -= 360.0;
        }

        return offsetHeading;
    }

    public Tile getTileFromNextZoomLevel(int x, int y, int zoom) {

        String source = "http://m.mit.edu/api/arcgis/WhereIs_Base_Topo/MapServer/tile/{z}/{y}/{x}";

        final String topLeftTileUrl = source.replace("{z}", "" + (zoom + 1)).replace("{x}", "" + (x * 2)).replace("{y}", "" + (y * 2));
        final String topRightTileUrl = source.replace("{z}", "" + (zoom + 1)).replace("{x}", "" + (x * 2 + 1)).replace("{y}", "" + (y * 2));
        final String bottomLeftTileUrl = source.replace("{z}", "" + (zoom + 1)).replace("{x}", "" + (x * 2)).replace("{y}", "" + (y * 2 + 1));
        final String bottomRightTileUrl = source.replace("{z}", "" + (zoom + 1)).replace("{x}", "" + (x * 2 + 1)).replace("{y}", "" + (y * 2 + 1));

        final Bitmap[] tiles = new Bitmap[4];

        Thread t1 = new Thread() {

            @Override
            public void run() {
                tiles[0] = getBitmapFromURL(topLeftTileUrl);
            }
        };
        t1.start();

        Thread t2 = new Thread() {

            @Override
            public void run() {
                tiles[1] = getBitmapFromURL(topRightTileUrl);
            }
        };
        t2.start();

        Thread t3 = new Thread() {

            @Override
            public void run() {
                tiles[2] = getBitmapFromURL(bottomLeftTileUrl);
            }
        };
        t3.start();

        Thread t4 = new Thread() {

            @Override
            public void run() {
                tiles[3] = getBitmapFromURL(bottomRightTileUrl);
            }
        };
        t4.start();

        try {
            t1.join();
            t2.join();
            t3.join();
            t4.join();
        } catch (InterruptedException e) {
            Timber.e(e, "Failed");
        }

        byte[] tile = mergeBitmaps(tiles, Bitmap.CompressFormat.PNG); // PNG is a lot slower, use it only if you really need to

        return tile == null ? TileProvider.NO_TILE : new Tile(256, 256, tile);
    }

    public static byte[] mergeBitmaps(Bitmap[] parts, Bitmap.CompressFormat format) {

        // Check if all the bitmap are null (if so return null) :
        boolean allNulls = true;
        for (int i = 0; i < parts.length; i++) {
            if (parts[i] != null) {
                allNulls = false;
                break;
            }
        }

        if (allNulls) return null;

        Bitmap tileBitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(tileBitmap);
        Paint paint = new Paint();
        for (int i = 0; i < parts.length; i++) {

            if (parts[i] == null) {

                parts[i] = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888);
            }
            canvas.drawBitmap(parts[i], parts[i].getWidth() * (i % 2), parts[i].getHeight() * (i / 2), paint);
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        tileBitmap.compress(format, 100, stream);
        byte[] bytes = stream.toByteArray();

        return bytes;
    }

    public static Bitmap getBitmapFromURL(String urlString) {

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            Bitmap bitmap = BitmapFactory.decodeStream(connection.getInputStream());

            return bitmap;
        } catch (IOException e) {
            return null;
        }
    }
}

