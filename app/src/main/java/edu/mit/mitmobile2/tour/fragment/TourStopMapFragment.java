package edu.mit.mitmobile2.tour.fragment;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import edu.mit.mitmobile2.shared.callback.FullscreenMapCallback;
import edu.mit.mitmobile2.shared.fragment.FullscreenMapFragment;
import edu.mit.mitmobile2.tour.activities.TourStopActivity;
import edu.mit.mitmobile2.tour.callbacks.TourSelfGuidedCallback;
import edu.mit.mitmobile2.tour.model.MITTour;
import edu.mit.mitmobile2.tour.model.MITTourStop;
import edu.mit.mitmobile2.tour.utils.TourUtils;

public class TourStopMapFragment extends FullscreenMapFragment {

    private MITTour tour;
    private TourSelfGuidedCallback callback;

    public TourStopMapFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        mapCallback = (FullscreenMapCallback) getActivity();
        callback = (TourSelfGuidedCallback) getActivity();

        //noinspection ConstantConditions
        LinearLayout restrictions = (LinearLayout) view.findViewById(R.id.restrictions_text_view);
        restrictions.setVisibility(View.VISIBLE);
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
            updateMapItems((ArrayList) tour.getStops(), false, true);
            drawRoutePath();
        }

        return view;
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

    @Subscribe
    public void mitTourLoadedEvent(OttoBusEvent.TourInfoLoadedEvent event) {
        tour = event.getTour();
        updateMapItems((ArrayList) tour.getStops(), false, true);
        drawRoutePath();
    }

    @Override
    public void onResume() {
        super.onResume();
        MitMobileApplication.bus.register(this);
    }

    @Override
    public void onPause() {
        MitMobileApplication.bus.unregister(this);
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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
            TextView distanceView = (TextView) view.findViewById(R.id.distance);
            distanceView.setVisibility(View.VISIBLE);

            topTextView.setTextSize(12f);
            bottomTextView.setTextSize(14f);

            Gson gson = new Gson();
            MITTourStop.InfoWindowSnippet snippet = gson.fromJson(marker.getSnippet(), MITTourStop.InfoWindowSnippet.class);

            topTextView.setText(snippet.type.toUpperCase());
            bottomTextView.setText(snippet.title);

            Location markerLocation = new Location("us");
            markerLocation.setLatitude(marker.getPosition().latitude);
            markerLocation.setLongitude(marker.getPosition().longitude);

            Location myLocation = mitMapView.getMap().getMyLocation();

            float distance = myLocation.distanceTo(markerLocation);
            distanceView.setText(TourUtils.formatStopDistance(distance));

            return view;
        } else {
            return null;
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Gson gson = new Gson();

        MITTourStop.InfoWindowSnippet snippet = gson.fromJson(marker.getSnippet(), MITTourStop.InfoWindowSnippet.class);
        String type = snippet.type;
        int index = snippet.index;

        Intent intent = new Intent(getActivity(), TourStopActivity.class);
        intent.putExtra(Constants.Tours.TOUR_KEY, tour);
        intent.putExtra(Constants.Tours.TOUR_STOP_TYPE, type);
        intent.putExtra(Constants.Tours.CURRENT_MAIN_LOOP_STOP, index);
        if (type.equals(Constants.Tours.SIDE_TRIP)) {
            intent.putExtra(Constants.Tours.TOUR_STOP, tour.getStops().get(index));
        }
        startActivity(intent);
    }
}
