package edu.mit.mitmobile2.shuttles.activities;

import android.content.Intent;
import android.os.Bundle;

import edu.mit.mitmobile2.MITActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.shuttles.callbacks.MapFragmentCallback;
import edu.mit.mitmobile2.shuttles.fragment.ShuttleRouteFragment;

public class ShuttleRouteActivity extends MITActivity implements MapFragmentCallback {

    ShuttleRouteFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_frame);

        if (savedInstanceState != null) {
            fragment = (ShuttleRouteFragment) getFragmentManager().findFragmentByTag(ShuttleRouteFragment.TAG);
        } else {
            fragment = new ShuttleRouteFragment();
            getFragmentManager().beginTransaction().replace(R.id.fragment_frame, fragment, ShuttleRouteFragment.TAG).commit();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        fragment = new ShuttleRouteFragment();
        getFragmentManager().beginTransaction().replace(R.id.fragment_frame, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        if (!fragment.isMapViewExpanded()) {
            super.onBackPressed();
        } else {
            fragment.showListView();
        }
    }

    @Override
    public void setActionBarTitle(String title) {
        setTitle(title);
    }

    @Override
    public void setActionBarSubtitle(String subtitle) {
        getSupportActionBar().setSubtitle(subtitle);
    }
}