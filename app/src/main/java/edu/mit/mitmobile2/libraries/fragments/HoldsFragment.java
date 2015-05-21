package edu.mit.mitmobile2.libraries.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.libraries.model.MITLibrariesMITHoldItem;

public class HoldsFragment extends AccountPageFragment {

    public static HoldsFragment newInstance() {
        return new HoldsFragment();
    }

    public HoldsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        // TODO: Get holds
        List<MITLibrariesMITHoldItem> holdItems = new ArrayList<>();

        int pickupCount = 0;

        for (MITLibrariesMITHoldItem holdItem : holdItems) {
            if (holdItem.isReadyForPickup()) {
                pickupCount++;
            }
        }

        buildAndAddHeaderView(String.format(getString(R.string.library_holds_pickup), holdItems.size(), pickupCount), null, pickupCount > 0 ? R.color.open_green : R.color.black);

        return view;
    }
}
