package edu.mit.mitmobile2.dining;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabWidget;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.dining.adapters.DiningPagerAdapter;
import edu.mit.mitmobile2.dining.fragments.HouseDiningFragment;
import edu.mit.mitmobile2.dining.fragments.RetailFragment;

public class DiningFragment extends Fragment implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {

    private TabHost tabHost;
    private TabWidget tabWidget;
    private ViewPager viewPager;

    private DiningPagerAdapter pagerAdapter;

    public static DiningFragment newInstance() {
        DiningFragment fragment = new DiningFragment();
        return fragment;
    }

    public DiningFragment() {
        // called using reflection in MITMainActivity.class
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_dining, null);

        tabHost = (TabHost) view.findViewById(android.R.id.tabhost);
        tabWidget = (TabWidget) view.findViewById(android.R.id.tabs);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);

        pagerAdapter = new DiningPagerAdapter(getActivity().getFragmentManager());

        initTabHost();
        initViewPager();

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /* ViewPager.OnPageChangeListener */

    @Override
    public void onPageScrolled(int i, float v, int i1) {
        int pos = viewPager.getCurrentItem();
        tabHost.setCurrentTab(pos);
    }

    @Override
    public void onPageSelected(int i) {
        // empty
    }

    @Override
    public void onPageScrollStateChanged(int i) {
        // empty
    }

    /* TabHost.OnTabChangeListener */

    @Override
    public void onTabChanged(String tabId) {
        int pos = tabHost.getCurrentTab();
        viewPager.setCurrentItem(pos);
    }

    /* Private methods */

    private void initViewPager() {
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnPageChangeListener(this);
    }

    private void initTabHost() {
        tabHost.setup();

        TabInfo tabInfo = null;
        addTab(tabHost, tabHost.newTabSpec("Tab1").setIndicator("Tab 1"), (new TabInfo("Tab1", HouseDiningFragment.class, null)));
        addTab(tabHost, tabHost.newTabSpec("Tab2").setIndicator("Tab 2"), (new TabInfo("Tab2", RetailFragment.class, null)));

//        TabHost.TabSpec houseDiningTab = tabHost.newTabSpec("");
//        houseDiningTab.setIndicator("houseDiningTab");
//        tabHost.addTab(houseDiningTab);
//
//        TabHost.TabSpec retailTab = tabHost.newTabSpec("");
//        retailTab.setIndicator("retailTab");
//        tabHost.addTab(retailTab);

        tabHost.setOnTabChangedListener(this);
    }

    private void addTab(TabHost tabHost, TabHost.TabSpec tabSpec, TabInfo tabInfo) {
        tabSpec.setContent(new TabFactory(getActivity()));
        tabHost.addTab(tabSpec);
    }


    private class TabInfo {
        private String tag;
        private Class<?> clss;
        private Bundle args;
        private Fragment fragment;

        TabInfo(String tag, Class<?> clazz, Bundle args) {
            this.tag = tag;
            this.clss = clazz;
            this.args = args;
        }
    }

    class TabFactory implements TabHost.TabContentFactory {

        private final Context mContext;

        public TabFactory(Context context) {
            mContext = context;
        }

        public View createTabContent(String tag) {
            View v = new View(mContext);
            v.setMinimumWidth(0);
            v.setMinimumHeight(0);
            return v;
        }
    }

}
