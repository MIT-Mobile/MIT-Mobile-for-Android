package edu.mit.mitmobile2.tour.adapters;

import android.app.Fragment;
import android.app.FragmentManager;

import java.util.List;

import edu.mit.mitmobile2.EndlessFragmentStatePagerAdapter;
import edu.mit.mitmobile2.tour.fragment.TourStopViewPagerFragment;
import edu.mit.mitmobile2.tour.model.MITTour;
import edu.mit.mitmobile2.tour.model.MITTourStop;

public class TourStopViewPagerAdapter extends EndlessFragmentStatePagerAdapter {

    private TourStopViewPagerFragment[] fragments;
    private MITTour tour;

    public TourStopViewPagerAdapter(FragmentManager fragmentManager, MITTour tour, List<MITTourStop> mainLoopStops) {
        super(fragmentManager, mainLoopStops.size());
        fragments = new TourStopViewPagerFragment[mainLoopStops.size()];
        this.tour = tour;
    }

    @Override
    public Fragment getItem(int position) {
        int realPosition = getRealPosition(position);
        if (fragments[realPosition] == null) {
            TourStopViewPagerFragment fragment = TourStopViewPagerFragment.newInstance(tour.getStops().get(realPosition), tour);
            fragments[realPosition] = fragment;
            return fragment;
        } else {
            return fragments[realPosition];
        }
    }
}
