package edu.mit.mitmobile2.tour.fragment;

import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.OttoBusEvent;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.maps.MITMapView;
import edu.mit.mitmobile2.maps.MapItem;
import edu.mit.mitmobile2.tour.callbacks.TourSelfGuidedCallback;
import edu.mit.mitmobile2.tour.model.MITTour;
import edu.mit.mitmobile2.tour.model.MITTourStop;

public class TourStopMapFragment extends Fragment implements GoogleMap.OnMapLoadedCallback,
        GoogleMap.InfoWindowAdapter,
        GoogleMap.OnInfoWindowClickListener,
        Animation.AnimationListener {

    private static final int DURATION_INCOMING_LOCATION = 200;
    private static final int DURATION_INCOMING_LIST = 201;
    private static final int DURATION_OUTGOING_LIST = 300;
    private static final int DURATION_OUTGOING_LOCATION = 301;

    private MITMapView mitMapView;
    private MITTour tour;
    private TourSelfGuidedCallback callback;

    private FloatingActionButton myLocationButton;
    private FloatingActionButton listButton;

    public TourStopMapFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tour_stop_map, null);

        callback = (TourSelfGuidedCallback) getActivity();

        MapView googleMapView = (MapView) view.findViewById(R.id.tour_map);
        googleMapView.onCreate(savedInstanceState);

        mitMapView = new MITMapView(getActivity(), googleMapView, this);
        mitMapView.setMapViewExpanded(true);
        mitMapView.mapBoundsPadding = (int) getActivity().getResources().getDimension(R.dimen.map_bounds_quarter_padding);
        mitMapView.getMap().setInfoWindowAdapter(this);
        mitMapView.getMap().setOnInfoWindowClickListener(this);

        LinearLayout restrictions = (LinearLayout) view.findViewById(R.id.restrictions_text_view);
        restrictions.setAlpha(0.8f);
        restrictions.setBackgroundResource(R.drawable.map_header_selector);
        restrictions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.showTourDetailActivity(tour.getDescriptionHtml());
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(Constants.Tours.TOUR_KEY)) {
            tour = savedInstanceState.getParcelable(Constants.Tours.TOUR_KEY);
        } else {
            tour = callback.getTour();
        }

        if (tour != null) {
            updateMapItems((ArrayList) tour.getStops(), true);
            mitMapView.setToDefaultBounds(false, 0);
            drawRoutePath();
        }

        setupFABs(view);

        return view;
    }

    private void startAnimation(FloatingActionButton button, float fromXDelt, float toXDelt, float fromYDelt, float toYDelt, int duration, int offset) {
        TranslateAnimation translateAnimation = new TranslateAnimation(fromXDelt, toXDelt, fromYDelt, toYDelt);
        translateAnimation.setDuration(duration);
        translateAnimation.setAnimationListener(TourStopMapFragment.this);
        translateAnimation.setStartOffset(offset);
        button.startAnimation(translateAnimation);
    }

    private void setupFABs(View view) {
        myLocationButton = (FloatingActionButton) view.findViewById(R.id.my_location_button);
        myLocationButton.setColorNormalResId(R.color.white);
        myLocationButton.setColorPressedResId(R.color.medium_grey);
        myLocationButton.setSize(FloatingActionButton.SIZE_NORMAL);
        myLocationButton.setIcon(R.drawable.ic_my_location);
        myLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location myLocation = mitMapView.getMap().getMyLocation();
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 14f);
                mitMapView.getMap().animateCamera(update, 400, null);
            }
        });

        listButton = (FloatingActionButton) view.findViewById(R.id.list_button);
        listButton.setColorPressedResId(R.color.mit_red_dark);
        listButton.setColorNormalResId(R.color.mit_red);
        listButton.setSize(FloatingActionButton.SIZE_NORMAL);
        listButton.setIcon(R.drawable.ic_list);
        listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                startAnimation(listButton, 0, 0, 0, displayMetrics.heightPixels, DURATION_OUTGOING_LIST, 0);
                startAnimation(myLocationButton, 0, 0, 0, displayMetrics.heightPixels, DURATION_OUTGOING_LOCATION, 100);
            }
        });

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        startAnimation(myLocationButton, 0, 0, displayMetrics.heightPixels, myLocationButton.getY(), DURATION_INCOMING_LOCATION, 0);
        startAnimation(listButton, 0, 0, displayMetrics.heightPixels, listButton.getY(), DURATION_INCOMING_LIST, 100);
    }

    private void drawRoutePath() {
        for (MITTourStop stop : tour.getStops()) {
            if (stop.getDirection() != null) {
                PolylineOptions options = new PolylineOptions();

                for (List<Double> outerList : stop.getDirection().getPathList()) {
                    LatLng point = new LatLng(outerList.get(1), outerList.get(0));
                    options.add(point);
                }

                options.color(getResources().getColor(R.color.map_path_color));
                options.visible(true);
                options.width(12f);
                options.zIndex(100);

                mitMapView.getMap().addPolyline(options);
            }
        }
    }

    protected void updateMapItems(ArrayList mapItems, boolean fit) {
        if (mapItems.size() == 0 || ((MapItem) mapItems.get(0)).isDynamic()) {
            mitMapView.clearDynamic();
        }
        mitMapView.addMapItemList(mapItems, false, fit);
    }

    @Subscribe
    public void mitTourLoadedEvent(OttoBusEvent.TourInfoLoadedEvent event) {
        tour = event.getTour();
        updateMapItems((ArrayList) tour.getStops(), true);
        mitMapView.setToDefaultBounds(false, 0);
        drawRoutePath();
    }

    @Override
    public void onMapLoaded() {
    }

    @Override
    public void onDestroy() {
        mitMapView.getGoogleMapView().onDestroy();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        mitMapView.getGoogleMapView().onResume();
        MitMobileApplication.bus.register(this);
    }

    @Override
    public void onPause() {
        MitMobileApplication.bus.unregister(this);
        mitMapView.getGoogleMapView().onResume();
        super.onPause();
    }

    @Override
    public void onLowMemory() {
        mitMapView.getGoogleMapView().onLowMemory();
        super.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mitMapView.getGoogleMapView().onSaveInstanceState(outState);
        outState.putParcelable(Constants.Tours.TOUR_KEY, tour);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        if (marker.getSnippet() != null) {
            View view = getActivity().getLayoutInflater().inflate(R.layout.mit_map_info_window, null);
            TextView topTextView = (TextView) view.findViewById(R.id.top_textview);
            TextView bottomTextView = (TextView) view.findViewById(R.id.bottom_textview);

            topTextView.setTextSize(12f);
            bottomTextView.setTextSize(14f);

            Gson gson = new Gson();
            MITTourStop.InfoWindowSnippet snippet = gson.fromJson(marker.getSnippet(), MITTourStop.InfoWindowSnippet.class);

            topTextView.setText(snippet.type.toUpperCase());
            bottomTextView.setText(snippet.title);

            return view;
        } else {
            return null;
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        //TODO: Take user to individual stop screen when that is created

        /*Intent intent = new Intent(getActivity(), TourDirectionsActivity.class);
        intent.putExtra(Constants.Tours.DIRECTION_KEY, tour.getStops().get(8).getDirection());
        startActivity(intent);*/
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        int duration = (int) animation.getDuration();
        switch (duration) {
            case DURATION_INCOMING_LOCATION:
                myLocationButton.setVisibility(View.VISIBLE);
                break;
            case DURATION_INCOMING_LIST:
                listButton.setVisibility(View.VISIBLE);
                break;
            case DURATION_OUTGOING_LIST:
                listButton.setVisibility(View.INVISIBLE);
                break;
            case DURATION_OUTGOING_LOCATION:
                myLocationButton.setVisibility(View.INVISIBLE);
                callback.switchViews(true);
                break;
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
