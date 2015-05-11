package edu.mit.mitmobile2.dining;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabWidget;

import java.util.List;

import edu.mit.mitmobile2.MITAPIClient;
import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.OttoBusEvent;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.dining.adapters.DiningPagerAdapter;
import edu.mit.mitmobile2.dining.model.MITDiningDining;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class DiningFragment extends Fragment implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {

    private static final String TAG_TABHOST_HOUSE_DINING = "tag_house_dining";
    private static final String TAG_TABHOST_RETAIL = "tag_retail";

    private static final String KEY_STATE_SELECTED_TAB = "state_selected_tab";

    private TabHost tabHost;
    private TabWidget tabWidget;
    private ViewPager viewPager;

    private DiningPagerAdapter pagerAdapter;

    private List<MITDiningDining> mitDiningDinings;

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
        setHasOptionsMenu(true);

        tabHost = (TabHost) view.findViewById(android.R.id.tabhost);
        tabWidget = (TabWidget) view.findViewById(android.R.id.tabs);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);

        pagerAdapter = new DiningPagerAdapter(getActivity().getFragmentManager());

        initTabHost();
        initViewPager();

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(KEY_STATE_SELECTED_TAB)) {
                tabHost.setCurrentTabByTag(savedInstanceState.getString(KEY_STATE_SELECTED_TAB));
            }
        } else {
            fetchDiningOptions();
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_dining, menu);
        super.onCreateOptionsMenu(menu, inflater);

        getActivity().setTitle(R.string.title_activity_dining);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_list_map_toggle: {
                // TODO: toggle list/map here
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_STATE_SELECTED_TAB, tabHost.getCurrentTabTag());
        super.onSaveInstanceState(outState);
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

    /* Network */

    private void fetchDiningOptions() {
        DiningManager.getDiningOptions(getActivity(), new Callback<List<MITDiningDining>>() {

            @Override
            public void success(List<MITDiningDining> mitDiningDinings, Response response) {
                DiningFragment.this.mitDiningDinings = mitDiningDinings;
            }

            @Override
            public void failure(RetrofitError error) {
                MitMobileApplication.bus.post(new OttoBusEvent.RetrofitFailureEvent(error));
            }
        });
    }

    /* Private methods */

    private void initViewPager() {
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnPageChangeListener(this);
    }

    private void initTabHost() {
        tabHost.setup();

        addTab(tabHost, tabHost.newTabSpec(TAG_TABHOST_HOUSE_DINING).setIndicator(getString(R.string.dining_tab_house_dining)));
        addTab(tabHost, tabHost.newTabSpec(TAG_TABHOST_RETAIL).setIndicator(getString(R.string.dining_tab_retail)));

        tabHost.setOnTabChangedListener(this);
    }

    private void addTab(TabHost tabHost, TabHost.TabSpec tabSpec) {
        tabSpec.setContent(new TabFactory(getActivity()));
        tabHost.addTab(tabSpec);
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
