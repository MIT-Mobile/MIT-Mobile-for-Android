package edu.mit.mitmobile2.dining;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabWidget;

import java.io.UnsupportedEncodingException;

import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.OttoBusEvent;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.dining.adapters.DiningPagerAdapter;
import edu.mit.mitmobile2.dining.interfaces.Updateable;
import edu.mit.mitmobile2.dining.model.MITDiningDining;
import edu.mit.mitmobile2.dining.model.MITDiningHouseDay;
import edu.mit.mitmobile2.dining.model.MITDiningHouseVenue;
import edu.mit.mitmobile2.dining.model.MITDiningMeal;
import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class DiningFragment extends Fragment implements MaterialTabListener, ViewPager.OnPageChangeListener {

    private static final String KEY_STATE_DINING = "state_dining";
    private static final String KEY_STATE_CURRENT_SCREEN_POSITION = "state_selected_tab";
    private static final String KEY_STATE_SCREEN_MODE = "state_screen_mode";

    private static final int SCREEN_MODE_LIST = 0;
    private static final int SCREEN_MODE_MAP = 1;

    private MaterialTabHost tabHost;
    private TabWidget tabWidget;
    private ViewPager viewPager;
    private MenuItem screenModeToggleMenuItem;

    private DiningPagerAdapter pagerAdapter;

    private MITDiningDining mitDiningDining;

    private int screenMode = SCREEN_MODE_LIST;

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

        tabHost = (MaterialTabHost) view.findViewById(android.R.id.tabhost);
        tabWidget = (TabWidget) view.findViewById(android.R.id.tabs);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);

        pagerAdapter = new DiningPagerAdapter(getActivity().getFragmentManager());

        initTabHost();
        initViewPager();

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(KEY_STATE_CURRENT_SCREEN_POSITION)) {
                tabHost.setSelectedNavigationItem(savedInstanceState.getInt(KEY_STATE_CURRENT_SCREEN_POSITION));
            }
            if (savedInstanceState.containsKey(KEY_STATE_SCREEN_MODE)) {
                screenMode = savedInstanceState.getInt(KEY_STATE_SCREEN_MODE);
            }
            if (savedInstanceState.containsKey(KEY_STATE_DINING)) {
                mitDiningDining = savedInstanceState.getParcelable(KEY_STATE_DINING);
            }
        } else {
            fetchDiningOptions();
        }

        /*

        final Intent intent = new Intent(getActivity(), DiningRetailActivity.class);

        TextView tv = (TextView) view.findViewById(R.id.module_title);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });

        MITAPIClient mitApiClient = new MITAPIClient(getActivity());
        mitApiClient.get(Constants.DINING, Constants.Dining.DINING_RETAIL_PATH, null, null, new Callback<List<MITDiningRetailVenue>>() {
            @Override
            public void success(List<MITDiningRetailVenue> mitDiningRetailVenues, Response response) {
                LoggingManager.Timber.d("Success!");
                Toast.makeText(getActivity(), "Success!", Toast.LENGTH_SHORT).show();
                intent.putExtra(Constants.DINING_VENUE_KEY, mitDiningRetailVenues.get(10));
            }

            @Override
            public void failure(RetrofitError error) {
                MitMobileApplication.bus.post(new OttoBusEvent.RetrofitFailureEvent(error));
            }
        });

        */

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

        screenModeToggleMenuItem = menu.findItem(R.id.action_list_map_toggle);

        super.onCreateOptionsMenu(menu, inflater);

        getActivity().setTitle(R.string.title_activity_dining);
        applyScreenMode();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_list_map_toggle: {
                toggleScreenMode();
                applyScreenMode();
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mitDiningDining != null) {
            outState.putParcelable(KEY_STATE_DINING, mitDiningDining);
        }
        outState.putInt(KEY_STATE_CURRENT_SCREEN_POSITION, viewPager.getCurrentItem());
        outState.putInt(KEY_STATE_SCREEN_MODE, screenMode);

        super.onSaveInstanceState(outState);
    }

    /* ViewPager.OnPageChangeListener */

    @Override
    public void onPageScrolled(int i, float v, int i1) {
        int pos = viewPager.getCurrentItem();
        tabHost.setSelectedNavigationItem(pos);
    }

    @Override
    public void onPageSelected(int i) {
        // empty
    }

    @Override
    public void onPageScrollStateChanged(int i) {
        // empty
    }

    /* MaterialTabListener */

    @Override
    public void onTabSelected(MaterialTab materialTab) {
        viewPager.setCurrentItem(materialTab.getPosition());
    }

    @Override
    public void onTabReselected(MaterialTab materialTab) {
        // empty
    }

    @Override
    public void onTabUnselected(MaterialTab materialTab) {
        // empty
    }

    /* Network */

    private void fetchDiningOptions() {
        DiningManager.getDiningOptions(getActivity(), new Callback<MITDiningDining>() {

            @Override
            public void success(MITDiningDining mitDiningDining, Response response) {
                DiningFragment.this.mitDiningDining = mitDiningDining;

                // set back references here
                for (MITDiningHouseVenue houseVenue : DiningFragment.this.mitDiningDining.getVenues().getHouse()) {
                    if (houseVenue.getMealsByDay() != null) {
                        for (MITDiningHouseDay day : houseVenue.getMealsByDay()) {
                            if (day.getMeals() != null) {
                                for (MITDiningMeal meal : day.getMeals()) {
                                    meal.setHouseDay(day);
                                }
                            }
                        }
                    }
                }

                notifyDiningUpdated(DiningFragment.this.mitDiningDining);
            }

            @Override
            public void failure(RetrofitError error) {
                MitMobileApplication.bus.post(new OttoBusEvent.RetrofitFailureEvent(error));
            }
        });
    }

    /* Private methods */

    private void toggleScreenMode() {
        switch (screenMode) {
            case SCREEN_MODE_MAP: {
                screenMode = SCREEN_MODE_LIST;
            }
            break;
            case SCREEN_MODE_LIST: {
                screenMode = SCREEN_MODE_MAP;
            }
            break;
        }
    }

    private void applyScreenMode() {
        if (viewPager != null && screenModeToggleMenuItem != null) {
            switch (screenMode) {
                case SCREEN_MODE_MAP: {
                    viewPager.setVisibility(View.GONE);
                    screenModeToggleMenuItem.setIcon(R.drawable.ic_list);
                }
                break;
                case SCREEN_MODE_LIST: {
                    viewPager.setVisibility(View.VISIBLE);
                    screenModeToggleMenuItem.setIcon(R.drawable.ic_map);
                }
                break;
            }
        }
    }

    private void notifyDiningUpdated(MITDiningDining mitDiningDining) {
        for (Fragment fragment : pagerAdapter.getFragments()) {
            if (fragment instanceof Updateable) {
                ((Updateable) fragment).onDining(mitDiningDining);
            }
        }
    }

    private void initViewPager() {
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnPageChangeListener(this);
    }

    private void initTabHost() {
        tabHost.addTab(tabHost.newTab().setText(getString(R.string.dining_tab_house_dining)).setTabListener(this));
        tabHost.addTab(tabHost.newTab().setText(getString(R.string.dining_tab_retail)).setTabListener(this));
    }
}
