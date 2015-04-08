package edu.mit.mitmobile2.mobius;

import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import edu.mit.mitmobile2.MitMapFragment;

/**
 * Created by sseligma on 4/5/15.
 */
public class ResourceListFragment extends MitMapFragment implements GoogleMap.InfoWindowAdapter {

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
