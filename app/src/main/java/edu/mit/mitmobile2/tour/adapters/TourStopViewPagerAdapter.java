package edu.mit.mitmobile2.tour.adapters;

import android.app.Fragment;
import android.app.FragmentManager;

import java.util.List;

import edu.mit.mitmobile2.EndlessFragmentStatePagerAdapter;
import edu.mit.mitmobile2.tour.fragment.TourStopViewPagerFragment;
import edu.mit.mitmobile2.tour.model.MITTourStop;

public class TourStopViewPagerAdapter extends EndlessFragmentStatePagerAdapter {

    private TourStopViewPagerFragment[] fragments;
    private List<MITTourStop> mitTourStops;

    public TourStopViewPagerAdapter(FragmentManager fragmentManager, List<MITTourStop> mitTourStops) {
        super(fragmentManager, mitTourStops.size());
        fragments = new TourStopViewPagerFragment[mitTourStops.size()];
        this.mitTourStops = mitTourStops;
    }

    @Override
    public Fragment getItem(int position) {
        int realPosition = getRealPosition(position);
        if (fragments[realPosition] == null) {
            TourStopViewPagerFragment fragment = TourStopViewPagerFragment.newInstance(mitTourStops.get(realPosition));
            fragments[realPosition] = fragment;
            return fragment;
        } else {
            return fragments[realPosition];
        }
    }
}
