package edu.mit.mitmobile2;

import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

/**
 * This class fakes being endless by having a large count.
 * Be sure to set the starting position near the middle (realCount * NUMBER_OF_LOOPS / 2 + startingPosition).
 * Any adapter methods dealing with position will be arbitrarily large so be sure to use getRealPosition
 * to obtain the actual position relative to your data's size.
 */

public abstract class EndlessFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

    public static final int NUMBER_OF_LOOPS = 1000;

    protected int realCount;

    public EndlessFragmentStatePagerAdapter(FragmentManager fragmentManager, int realCount) {
        super(fragmentManager);
        this.realCount = realCount;
    }

    @Override
    public int getCount() {
        return NUMBER_OF_LOOPS * realCount;
    }

    public int getRealPosition(int position) {
        return position % realCount;
    }

    public int getRealCount() {
        return realCount;
    }
}
