package edu.mit.mitmobile2.libraries.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import edu.mit.mitmobile2.libraries.fragments.FinesFragment;
import edu.mit.mitmobile2.libraries.fragments.HoldsFragment;
import edu.mit.mitmobile2.libraries.fragments.LoansFragment;

public class AccountPagerAdapter extends FragmentStatePagerAdapter {

    private final String[] TITLES = {"Loans", "Fines", "Holds"};

    Fragment[] fragments;

    public AccountPagerAdapter(FragmentManager fm) {
        super(fm);

        fragments = new Fragment[3];
        fragments[0] = LoansFragment.newInstance();
        fragments[1] = FinesFragment.newInstance();
        fragments[2] = HoldsFragment.newInstance();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }
}
