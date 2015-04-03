package edu.mit.mitmobile2.tour;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.maps.MITMapView;

public class TourStopMapFragment extends Fragment implements GoogleMap.OnMapLoadedCallback {

    private MITMapView mitMapView;

    public TourStopMapFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tour_stop_map, container);

        MapView googleMapView = (MapView) view.findViewById(R.id.route_map);
        googleMapView.onCreate(savedInstanceState);

        mitMapView = new MITMapView(getActivity(), googleMapView, this);


        return view;
    }


    @Override
    public void onMapLoaded() {
        // animate camera
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
