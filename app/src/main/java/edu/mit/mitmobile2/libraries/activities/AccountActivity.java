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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_account, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
