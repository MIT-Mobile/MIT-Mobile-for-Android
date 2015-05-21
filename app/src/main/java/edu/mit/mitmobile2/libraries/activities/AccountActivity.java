package edu.mit.mitmobile2.libraries.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;

import butterknife.ButterKnife;
import butterknife.InjectView;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.libraries.adapter.AccountPagerAdapter;

public class AccountActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener,
        PagerSlidingTabStrip.OnTabReselectedListener {

    @InjectView(R.id.account_viewpager)
    ViewPager viewPager;

    @InjectView(R.id.account_tabhost)
    PagerSlidingTabStrip tabHost;

    private AccountPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        ButterKnife.inject(this);

        setupTabHost();

        adapter = new AccountPagerAdapter(getFragmentManager());
        viewPager.setAdapter(adapter);
        tabHost.setViewPager(viewPager);

    }

    private void setupTabHost() {
        tabHost.setOnTabReselectedListener(this);
        tabHost.setAllCaps(true);
        tabHost.setOnPageChangeListener(this);
        tabHost.setTextColorResource(R.color.mit_red);
        tabHost.setIndicatorColorResource(R.color.mit_red);
        tabHost.setShouldExpand(true);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onTabReselected(int i) {

    }
}
