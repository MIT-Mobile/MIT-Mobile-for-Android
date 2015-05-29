package edu.mit.mitmobile2.shared.fragment;

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

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.maps.MITMapView;
import edu.mit.mitmobile2.maps.MapItem;
import edu.mit.mitmobile2.shared.callback.FullscreenMapCallback;

public class FullscreenMapFragment extends Fragment implements GoogleMap.OnMapLoadedCallback,
        GoogleMap.InfoWindowAdapter,
        GoogleMap.OnInfoWindowClickListener,
        Animation.AnimationListener {

    private static final int DURATION_INCOMING_LOCATION = 200;
    private static final int DURATION_INCOMING_LIST = 201;
    private static final int DURATION_OUTGOING_LIST = 300;
    protected static final int DURATION_OUTGOING_LOCATION = 301;

    protected MITMapView mitMapView;
    private FloatingActionButton myLocationButton;
    private FloatingActionButton listButton;
    protected FullscreenMapCallback mapCallback;

    public FullscreenMapFragment() {
    }

    public static FullscreenMapFragment newInstance() {
        return new FullscreenMapFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tour_stop_map, null);

        MapView googleMapView = (MapView) view.findViewById(R.id.tour_map);
        googleMapView.onCreate(savedInstanceState);

        mitMapView = new MITMapView(getActivity(), googleMapView, this);
        mitMapView.setMapViewExpanded(true);
        mitMapView.mapBoundsPadding = (int) getActivity().getResources().getDimension(R.dimen.map_bounds_quarter_padding);
        mitMapView.getMap().setInfoWindowAdapter(this);
        mitMapView.getMap().setOnInfoWindowClickListener(this);

        setupFABs(view);

        return view;
    }

    private void startAnimation(FloatingActionButton button, float fromXDelt, float toXDelt, float fromYDelt, float toYDelt, int duration, int offset) {
        TranslateAnimation translateAnimation = new TranslateAnimation(fromXDelt, toXDelt, fromYDelt, toYDelt);
        translateAnimation.setDuration(duration);
        translateAnimation.setAnimationListener(this);
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

        animateFABs();
    }

    protected void animateFABs() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        startAnimation(myLocationButton, 0, 0, displayMetrics.heightPixels, myLocationButton.getY(), DURATION_INCOMING_LOCATION, 0);
        startAnimation(listButton, 0, 0, displayMetrics.heightPixels, listButton.getY(), DURATION_INCOMING_LIST, 100);
    }

    protected void updateMapItems(ArrayList mapItems, boolean clear, boolean fit) {
        if (mapItems.size() == 0 || ((MapItem) mapItems.get(0)).isDynamic()) {
            mitMapView.clearDynamic();
        }
        //noinspection unchecked
        mitMapView.addMapItemList(mapItems, clear, fit);
        mitMapView.setToDefaultBounds(false, 0);
        if (mapItems.size() == 1) {
            mitMapView.showSingleItem();
        }
    }

    protected void selectMarker(int position) {
        mitMapView.selectMapItem(position);
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
                mapCallback.switchViews(true);
                break;
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

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
    }

    @Override
    public void onPause() {
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
