package edu.mit.mitmobile2.dining.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;

import java.util.ArrayList;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.dining.activities.DiningRetailActivity;
import edu.mit.mitmobile2.dining.model.MITDiningRetailVenue;
import edu.mit.mitmobile2.dining.model.MITDiningVenues;
import edu.mit.mitmobile2.maps.MITMapView;
import edu.mit.mitmobile2.maps.MapItem;
import edu.mit.mitmobile2.tour.utils.TourUtils;

public class DiningMapFragment extends Fragment implements GoogleMap.OnMapLoadedCallback,
        GoogleMap.InfoWindowAdapter,
        GoogleMap.OnInfoWindowClickListener {

    private MITMapView mitMapView;
    private MITDiningVenues venues;

    public DiningMapFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dining_map, null);

        MapView googleMapView = (MapView) view.findViewById(R.id.dining_map);
        googleMapView.onCreate(savedInstanceState);

        mitMapView = new MITMapView(getActivity(), googleMapView, this);
        mitMapView.setMapViewExpanded(true);
        mitMapView.mapBoundsPadding = (int) getActivity().getResources().getDimension(R.dimen.map_bounds_quarter_padding);
        mitMapView.getMap().setInfoWindowAdapter(this);
        mitMapView.getMap().setOnInfoWindowClickListener(this);

        if (savedInstanceState != null && savedInstanceState.containsKey(Constants.Tours.TOUR_KEY)) {
            venues = savedInstanceState.getParcelable(Constants.DINING_VENUE_KEY);
        }

        if (venues != null) {
            updateMapItems((ArrayList) venues.getRetail(), true);
            mitMapView.setToDefaultBounds(false, 0);
        }

        FloatingActionButton myLocationButton = (FloatingActionButton) view.findViewById(R.id.my_location_button);
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

        return view;
    }

    protected void updateMapItems(ArrayList mapItems, boolean fit) {
        if (mapItems.size() == 0 || ((MapItem) mapItems.get(0)).isDynamic()) {
            mitMapView.clearDynamic();
        }
        mitMapView.addMapItemList(mapItems, false, fit);
        venues.getRetail();
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
//            MITTourStop.InfoWindowSnippet snippet = gson.fromJson(marker.getSnippet(), MITTourStop.InfoWindowSnippet.class);
//
//            topTextView.setText(snippet.type.toUpperCase());
//            bottomTextView.setText(snippet.title);

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
        // TODO: Decide whether to use GSON or simply store ID in snippet and grab remaining info from venues list
        String snippet = marker.getSnippet();
        Gson gson = new Gson();
        MITDiningRetailVenue venue = gson.fromJson(snippet, MITDiningRetailVenue.class);

        Intent intent = new Intent(getActivity(), DiningRetailActivity.class);
        intent.putExtra(Constants.DINING_VENUE_KEY, venue);
        startActivity(intent);
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
    }
}
