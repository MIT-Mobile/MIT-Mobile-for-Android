package edu.mit.mitmobile2.tour.activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;

import java.util.ArrayList;
import java.util.List;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MapUtils;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.tour.model.MITTourStopDirection;

public class TourDirectionsActivity extends ActionBarActivity {

    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_directions);

        MITTourStopDirection direction = getIntent().getParcelableExtra(Constants.Tours.DIRECTION_KEY);

        WebView directionWebView = (WebView) findViewById(R.id.directions_html_view);
        directionWebView.loadData(direction.getBodyHtml(), "text/html", "utf-8");

        TextView directionTitle = (TextView) findViewById(R.id.direction_title);
        directionTitle.setText(direction.getTitle());

        mapView = (MapView) findViewById(R.id.direction_map);
        mapView.onCreate(savedInstanceState);
        mapView.setEnabled(false);
        mapView.getMap().getUiSettings().setAllGesturesEnabled(false);
        MapsInitializer.initialize(this);

        TileProvider provider = new TileProvider() {
            @Override
            public Tile getTile(int i, int i1, int i2) {
                // Use this hacky implementation until 512x512 images are available
                return MapUtils.getTileFromNextZoomLevel(i, i1, i2);
            }
        };

        TileOverlayOptions options = new TileOverlayOptions();
        options.tileProvider(provider);

        mapView.getMap().addTileOverlay(options);

        LatLngBounds bounds = drawRoutePath(direction.getPathList());
        setToDefaultBounds(bounds, false, 0);
    }

    private LatLngBounds drawRoutePath(List<List<Double>> pathList) {
        LatLngBounds.Builder defaultBounds = new LatLngBounds.Builder();

        PolylineOptions options = new PolylineOptions();
        List<LatLng> points = new ArrayList<>();

        for (List<Double> list : pathList) {
            LatLng point = new LatLng(list.get(1), list.get(0));
            options.add(point);
            points.add(point);
            defaultBounds.include(point);
        }

        options.color(getResources().getColor(R.color.map_path_color));
        options.visible(true);
        options.width(8f);
        options.zIndex(50);

        mapView.getMap().addPolyline(options);

        double startRotation = calculateRotation(points.get(0), points.get(1), true);
        double endRotation = calculateRotation(points.get(points.size() - 1), points.get(points.size() - 2), false);

        // Just first and last marker
        MarkerOptions startMarkerOpts = new MarkerOptions().position(points.get(0)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_direction)).flat(false).anchor(0.36f, 0.5f).rotation((float) startRotation);
        MarkerOptions endMarkerOpts = new MarkerOptions().position(points.get((points.size() - 1))).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_direction_red)).flat(false).anchor(0.64f, 0.5f).rotation((float) endRotation);

        mapView.getMap().addMarker(startMarkerOpts);
        mapView.getMap().addMarker(endMarkerOpts);

        return defaultBounds.build();
    }

    private double calculateRotation(LatLng p1, LatLng p2, boolean isStart) {
        double x1 = p1.longitude - p2.longitude;
        double x = Math.abs(x1);
        double y1 = p1.latitude - p2.latitude;
        double y = Math.abs(y1);

        double rads = Math.atan(y / x);
        double degs = Math.toDegrees(rads);

        int adjustment = 6;

        if (!isStart && x1 < 0 && y1 < 0) {
            degs = 90 + (90 - degs);
            degs -= adjustment;
        } else if (!isStart && x1 > 0 && y1 > 0) {
            degs = -degs;
            degs -= adjustment;
        } else if (!isStart && x1 < 0 && y1 > 0) {
            degs = -90 - (90 - degs);
            degs += adjustment;
        } else {
            degs += adjustment;
        }

        if (isStart && x1 > 0 && y1 < 0) {
            degs = -90 - (90 - degs);
        } else if (isStart && x1 < 0 && y1 < 0) {
            degs = -degs;
        } else if (isStart && x1 > 0 && y1 > 0) {
            degs = 90 + (90 - degs);
            degs -= adjustment;
        }

        return degs;
    }

    public void setToDefaultBounds(LatLngBounds bounds, boolean animate, int animationLength) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, getResources().getDisplayMetrics().widthPixels, (int) getResources().getDimension(R.dimen.shuttle_routes_map_header_height), (int) getResources().getDimension(R.dimen.map_bounds_padding));
        if (animate) {
            mapView.getMap().animateCamera(cameraUpdate, animationLength, null);
        } else {
            mapView.getMap().moveCamera(cameraUpdate);
        }
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        mapView.onResume();
        super.onPause();
    }

    @Override
    public void onLowMemory() {
        mapView.onLowMemory();
        super.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
