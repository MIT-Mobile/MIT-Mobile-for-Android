package edu.mit.mitmobile2.maps.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.maps.adapter.CategoriesPagerAdapter;
import edu.mit.mitmobile2.maps.fragments.BookmarksFragment;
import edu.mit.mitmobile2.maps.fragments.CategoriesFragment;
import edu.mit.mitmobile2.maps.fragments.RecentsFragment;

public class MapsCategoriesActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, PagerSlidingTabStrip.OnTabReselectedListener, BookmarksFragment.OnBookmarksFragmentInteractionListener, RecentsFragment.OnRecentsFragmentInteractionListener {

    private ViewPager viewPager;
    private PagerSlidingTabStrip tabHost;

    private CategoriesPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_categories);

        viewPager = (ViewPager) findViewById(R.id.map_categories_viewpager);
        tabHost = (PagerSlidingTabStrip) findViewById(R.id.map_categories_tabhost);

        setupTabHost();

        adapter = new CategoriesPagerAdapter(this, getFragmentManager());
        viewPager.setAdapter(adapter);
        tabHost.setViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_maps_categories, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupTabHost() {
        tabHost.setOnTabReselectedListener(this);
        tabHost.setAllCaps(true);
        tabHost.setOnPageChangeListener(this);
        tabHost.setTextColorResource(R.color.mit_red);
        tabHost.setIndicatorColorResource(R.color.mit_red);
        tabHost.setShouldExpand(true);
    }

    /* ViewPager.OnPageChangeListener */

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /* PagerSlidingTabStrip.OnTabReselectedListener */

    @Override
    public void onTabReselected(int i) {

    }
}
