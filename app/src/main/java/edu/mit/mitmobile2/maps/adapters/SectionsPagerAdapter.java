package edu.mit.mitmobile2.maps.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentStatePagerAdapter;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.maps.fragments.CategoriesFragment;
import edu.mit.mitmobile2.maps.fragments.MapListBookmarkFragment;
import edu.mit.mitmobile2.maps.fragments.MapListFragment;
import edu.mit.mitmobile2.maps.fragments.MapListRecentsFragment;

public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

    private MapListFragment[] fragments;
    private Context context;

    public SectionsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        fragments = new MapListFragment[3];
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (fragments[position] == null) {
            switch (position) {
                case 0:
                    fragments[position] = CategoriesFragment.newInstance();
                    break;
                case 1:
                    fragments[position] = MapListBookmarkFragment.newInstance();
                    break;
                case 2:
                    fragments[position] = MapListRecentsFragment.newInstance();
                    break;
            }
        }

        return fragments[position];
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getString(R.string.categories);
            case 1:
                return context.getString(R.string.bookmarks);
            case 2:
                return context.getString(R.string.recents);
        }
        return null;
    }
}
