package edu.mit.mitmobile2.dining;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
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
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;

import java.util.ArrayList;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MITAPIClient;
import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.OttoBusEvent;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.dining.activities.DiningRetailActivity;
import edu.mit.mitmobile2.dining.adapters.DiningPagerAdapter;
import edu.mit.mitmobile2.dining.interfaces.Updateable;
import edu.mit.mitmobile2.dining.model.MITDiningDining;
import edu.mit.mitmobile2.dining.model.MITDiningHouseDay;
import edu.mit.mitmobile2.dining.model.MITDiningHouseVenue;
import edu.mit.mitmobile2.dining.model.MITDiningMeal;
import edu.mit.mitmobile2.dining.model.MITDiningRetailVenue;
import edu.mit.mitmobile2.dining.model.MITDiningVenues;
import edu.mit.mitmobile2.maps.MITMapView;
import edu.mit.mitmobile2.maps.MapItem;
import edu.mit.mitmobile2.shared.logging.LoggingManager;
import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class DiningFragment extends Fragment implements MaterialTabListener, ViewPager.OnPageChangeListener, GoogleMap.OnMapLoadedCallback,
        GoogleMap.InfoWindowAdapter,
        GoogleMap.OnInfoWindowClickListener {

    private static final String KEY_STATE_DINING = "state_dining";
    private static final String KEY_STATE_CURRENT_SCREEN_POSITION = "state_selected_tab";
    private static final String KEY_STATE_SCREEN_MODE = "state_screen_mode";

    private static final int SCREEN_MODE_LIST = 0;
    private static final int SCREEN_MODE_MAP = 1;

    private MaterialTabHost tabHost;
    private TabWidget tabWidget;
    private ViewPager viewPager;
    private MenuItem screenModeToggleMenuItem;
    private MITMapView mitMapView;
    private FloatingActionButton myLocationButton;

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

        setupMap(view, savedInstanceState);

        MITAPIClient mitApiClient = new MITAPIClient(getActivity());
        mitApiClient.get(Constants.DINING, Constants.Dining.DINING_PATH, null, null, new Callback<MITDiningDining>() {
            @Override
            public void success(MITDiningDining mitDiningDining, Response response) {
                LoggingManager.Timber.d("Success!");
                MITDiningVenues venues = mitDiningDining.getVenues();
                for (MITDiningHouseVenue houseVenue : venues.getHouse()) {
                    int i = venues.getHouse().indexOf(houseVenue);
                    String markerText = (i + 1) < 10 ? "   " + (i + 1) + "   " : "  " + (i + 1) + "  ";
                    houseVenue.setMarkerText(markerText);
                }

                for (MITDiningRetailVenue retailVenue : venues.getRetail()) {
                    int i = venues.getRetail().indexOf(retailVenue);
                    String markerText = (i + 1) < 10 ? "   " + (i + 1) + "   " : "  " + (i + 1) + "  ";
                    retailVenue.setMarkerText(markerText);
                }

                DiningFragment.this.mitDiningDining = mitDiningDining;
                updateMapItems((ArrayList) DiningFragment.this.mitDiningDining.getVenues().getRetail(), true);
                mitMapView.setToDefaultBounds(false, 0);
            }

            @Override
            public void failure(RetrofitError error) {
                MitMobileApplication.bus.post(new OttoBusEvent.RetrofitFailureEvent(error));
            }
        });


        return view;
    }

    private void setupMap(View view, Bundle savedInstanceState) {
        MapView googleMapView = (MapView) view.findViewById(R.id.dining_map);
        googleMapView.onCreate(savedInstanceState);

        mitMapView = new MITMapView(getActivity(), googleMapView, this);
        mitMapView.setMapViewExpanded(true);
        mitMapView.mapBoundsPadding = (int) getActivity().getResources().getDimension(R.dimen.map_bounds_quarter_padding);
        mitMapView.getMap().setInfoWindowAdapter(this);
        mitMapView.getMap().setOnInfoWindowClickListener(this);

        if (savedInstanceState != null && savedInstanceState.containsKey(Constants.Tours.TOUR_KEY)) {
            mitDiningDining = savedInstanceState.getParcelable(Constants.ALL_DINING_KEY);
        }

        if (mitDiningDining != null) {
            updateMapItems((ArrayList) mitDiningDining.getVenues().getRetail(), true);
            mitMapView.setToDefaultBounds(false, 0);
        }

        myLocationButton = (FloatingActionButton) view.findViewById(R.id.my_location_button);
        myLocationButton.setColorNormalResId(R.color.white);
        myLocationButton.setColorPressedResId(R.color.medium_grey);
        myLocationButton.setSize(FloatingActionButton.SIZE_NORMAL);
        myLocationButton.setIcon(R.drawable.ic_my_location);
        myLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location myLocation = mitMapView.getMap().getMyLocation();
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 14f);
                mitMapView.getMap().animateCamera(update, 400, null);
            }
        });
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
        mitMapView.getGoogleMapView().onSaveInstanceState(outState);

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
                                    meal.setHouseDateString(day.getDateString());
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
                    mitMapView.getGoogleMapView().setVisibility(View.VISIBLE);
                    myLocationButton.setVisibility(View.VISIBLE);
                    screenModeToggleMenuItem.setIcon(R.drawable.ic_list);
                }
                break;
                case SCREEN_MODE_LIST: {
                    viewPager.setVisibility(View.VISIBLE);
                    mitMapView.getGoogleMapView().setVisibility(View.GONE);
                    myLocationButton.setVisibility(View.GONE);
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

    private void addTab(TabHost tabHost, TabHost.TabSpec tabSpec) {
        tabSpec.setContent(new TabFactory(getActivity()));
        tabHost.addTab(tabSpec);
    }

    protected void updateMapItems(ArrayList mapItems, boolean fit) {
        if (mapItems.size() == 0 || ((MapItem) mapItems.get(0)).isDynamic()) {
            mitMapView.clearDynamic();
        }
        mitMapView.addMapItemList(mapItems, false, fit);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        if (marker.getSnippet() != null) {
            View view = getActivity().getLayoutInflater().inflate(R.layout.dining_window, null);
            TextView title = (TextView) view.findViewById(R.id.dining_venue_title);

            String snippet = marker.getSnippet();
            Gson gson = new Gson();
            MITDiningRetailVenue venue = gson.fromJson(snippet, MITDiningRetailVenue.class);
            title.setText(venue.getName());

            return view;
        } else {
            return null;
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        // TODO: Decide whether to use GSON or simply store ID in snippet and grab remaining info from venues list
        String snippet = marker.getSnippet();
        Gson gson = new Gson();

        MITDiningRetailVenue venue = gson.fromJson(snippet, MITDiningRetailVenue.class);

        Intent intent = new Intent(getActivity(), DiningRetailActivity.class);
        intent.putExtra(Constants.DINING_VENUE_KEY, venue);
        startActivity(intent);
    }

    @Override
    public void onMapLoaded() {

    }

    @Override
    public void onDestroy() {
        mitMapView.getGoogleMapView().onDestroy();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        mitMapView.getGoogleMapView().onResume();
        MitMobileApplication.bus.register(this);
    }

    @Override
    public void onPause() {
        MitMobileApplication.bus.unregister(this);
        mitMapView.getGoogleMapView().onResume();
        super.onPause();
    }

    @Override
    public void onLowMemory() {
        mitMapView.getGoogleMapView().onLowMemory();
        super.onLowMemory();
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
