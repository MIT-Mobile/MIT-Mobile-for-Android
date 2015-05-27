package edu.mit.mitmobile2.maps;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.mit.mitmobile2.MitMapFragment;
import edu.mit.mitmobile2.maps.activities.MapsCategoriesActivity;

public class MapsFragment extends MitMapFragment {

    public MapsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        Intent i = new Intent(getActivity(), MapsCategoriesActivity.class);
        startActivity(i);

        return view;
    }
}
