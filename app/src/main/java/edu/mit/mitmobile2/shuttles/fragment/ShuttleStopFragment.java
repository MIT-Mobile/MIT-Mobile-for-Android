package edu.mit.mitmobile2.shuttles.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.shuttles.ShuttleStopActivity;
import edu.mit.mitmobile2.shuttles.adapter.ShuttleStopViewPagerAdapter;

public class ShuttleStopFragment extends Fragment{

    @InjectView(R.id.prediction_viewpager)
    ViewPager predictionViewPager;

    @InjectView(R.id.transparent_map_overlay)
    View transparentView;

    private ShuttleStopViewPagerAdapter stopViewPagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_stop, container, false);

        ButterKnife.inject(this, view);

        stopViewPagerAdapter = new ShuttleStopViewPagerAdapter(getActivity().getSupportFragmentManager(), 3);

        predictionViewPager.setAdapter(stopViewPagerAdapter);
        predictionViewPager.setCurrentItem(0);

        ((ShuttleStopActivity)getActivity()).addTransparentView(transparentView);
        return view;
    }
}
