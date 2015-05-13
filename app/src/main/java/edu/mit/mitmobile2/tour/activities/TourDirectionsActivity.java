package edu.mit.mitmobile2.tour.activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MapUtils;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.tour.model.MITTourStopDirection;
import edu.mit.mitmobile2.shared.logging.LoggingManager.Timber;

public class TourDirectionsActivity extends AppCompatActivity {

    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_directions);

        MITTourStopDirection direction = getIntent().getParcelableExtra(Constants.Tours.DIRECTION_KEY);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

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

        TextView sideTripTitle = (TextView) findViewById(R.id.side_trip_title);
        TextView sideTripContent = (TextView) findViewById(R.id.side_trip_content);
        WebView directionWebView = (WebView) findViewById(R.id.directions_html_view);

        if (direction != null) {
            sideTripContent.setVisibility(View.GONE);
            sideTripTitle.setVisibility(View.GONE);
            directionWebView.setVisibility(View.VISIBLE);

            String template = readInHtmlTemplate();
            template = template.replace("__TITLE__", direction.getTitle());
            template = template.replace("__BODY__", direction.getBodyHtml());
            template = template.replace("__WIDTH__", String.valueOf(displayMetrics.widthPixels));

            directionWebView.loadData(template, "text/html;charset=utf-8", "utf-8");

            LatLngBounds bounds = drawRoutePath(direction.getPathList());
            setToDefaultBounds(bounds, false, 0);
        } else {
            sideTripContent.setVisibility(View.VISIBLE);
            sideTripTitle.setVisibility(View.VISIBLE);
            directionWebView.setVisibility(View.GONE);

            sideTripTitle.setText(getString(R.string.side_trip_directions_1) + getIntent().getStringExtra(Constants.Tours.FIRST_TITLE_KEY) + getString(R.string.side_trip_directions_2) + getIntent().getStringExtra(Constants.Tours.TITLE_KEY));

            double[] currentStopCoords = getIntent().getDoubleArrayExtra(Constants.Tours.CURRENT_STOP_COORDS);
            double[] prevStopCoords = getIntent().getDoubleArrayExtra(Constants.Tours.PREV_STOP_COORDS);

            List<LatLng> points = new ArrayList<>();
            points.add(new LatLng(prevStopCoords[1], prevStopCoords[0]));
            points.add(new LatLng(currentStopCoords[1], currentStopCoords[0]));

            drawMarkers(points);
            setToDefaultBounds(new LatLngBounds.Builder().include(points.get(0)).include(points.get(1)).build(), false, 0);
        }
    }

    private String readInHtmlTemplate() {
        String template = "";
        try {
            InputStream is = getAssets().open("tours_directions_template.html");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            template = new String(buffer, "UTF-8");

        } catch (IOException e) {
            Timber.e(e, "HTML read Failed");
        }
        return template;
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

        drawMarkers(points);

        return defaultBounds.build();
    }

    private void drawMarkers(List<LatLng> points) {
        double startRotation = calculateRotation(points.get(0), points.get(1), true);
        double endRotation = calculateRotation(points.get(points.size() - 1), points.get(points.size() - 2), false);

        // Just first and last marker
        MarkerOptions startMarkerOpts = new MarkerOptions().position(points.get(0)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_direction)).flat(false).anchor(0.36f, 0.5f).rotation((float) startRotation);
        MarkerOptions endMarkerOpts = new MarkerOptions().position(points.get((points.size() - 1))).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_direction_red)).flat(false).anchor(0.64f, 0.5f).rotation((float) endRotation);

        mapView.getMap().addMarker(startMarkerOpts);
        mapView.getMap().addMarker(endMarkerOpts);
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
