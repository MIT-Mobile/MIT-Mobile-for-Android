package edu.mit.mitmobile2.maps.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentStatePagerAdapter;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.maps.fragments.BookmarksFragment;
import edu.mit.mitmobile2.maps.fragments.CategoriesFragment;
import edu.mit.mitmobile2.maps.fragments.RecentsFragment;

public class CategoriesPagerAdapter extends FragmentStatePagerAdapter {

    private static final int FRAGMENTS_COUNT = 3;

    private String[] titles;
    private Fragment[] fragments;

    public CategoriesPagerAdapter(Context context, FragmentManager fm) {
        super(fm);

        fragments = new Fragment[FRAGMENTS_COUNT];
        fragments[0] = CategoriesFragment.newInstance();
        fragments[1] = BookmarksFragment.newInstance();
        fragments[2] = RecentsFragment.newInstance();

        titles = new String[FRAGMENTS_COUNT];
        titles[0] = context.getString(R.string.map_categories_title_categories);
        titles[1] = context.getString(R.string.map_categories_title_bookmarks);
        titles[2] = context.getString(R.string.map_categories_title_recents);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return FRAGMENTS_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}