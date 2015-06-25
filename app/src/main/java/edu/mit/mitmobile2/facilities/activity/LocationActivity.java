package edu.mit.mitmobile2.facilities.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.OttoBusEvent;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.facilities.FacilitiesManager;
import edu.mit.mitmobile2.facilities.adapter.FacilitiesSearchAdapter;
import edu.mit.mitmobile2.facilities.adapter.LocationAdapter;
import edu.mit.mitmobile2.facilities.callback.LocationCallback;
import edu.mit.mitmobile2.facilities.model.FacilitiesCategory;
import edu.mit.mitmobile2.facilities.model.FacilitiesPropertyOwner;
import edu.mit.mitmobile2.facilities.model.FacilitiesPropertyOwnerWrapper;
import edu.mit.mitmobile2.maps.MapManager;
import edu.mit.mitmobile2.maps.model.MITMapPlace;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LocationActivity extends AppCompatActivity implements LocationCallback {

    public static final String NEARBY_LOCATIONS = "Nearby Locations";
    private static final int REQUEST_CODE = 5;

    private ListView listView;
    private SearchView searchView;

    private LocationAdapter adapter;
    private FacilitiesSearchAdapter searchAdapter;
    private LocationCallback callback;

    private ArrayList<MITMapPlace> searchPlaces;
    private List<FacilitiesPropertyOwner> propertyOwners;

    private HashMap<String, FacilitiesCategory> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        listView = (ListView) findViewById(R.id.list);
        callback = (LocationCallback) this;

        adapter = new LocationAdapter(getApplicationContext(), callback);
        listView.setAdapter(adapter);

        adapter.updateCategories(null);

        searchPlaces = new ArrayList<>();

        fetchCategories();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            Intent result = new Intent();
            result.putExtra(Constants.FACILITIES_LOCATION, data.getStringExtra(Constants.FACILITIES_LOCATION));
            result.putExtra(Constants.FACILITIES_PROPERTYOWNER, data.getParcelableExtra(Constants.FACILITIES_PROPERTYOWNER));
            result.putExtra(Constants.FACILITIES_SEARCH_MODE, data.getBooleanExtra(Constants.FACILITIES_SEARCH_MODE, false));
            setResult(RESULT_OK, result);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_facility_location, menu);

        MenuItem menuItem = menu.findItem(R.id.search);
        searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.length() > 0) {
                    searchPlaces(s);
                } else {
                    listView.setAdapter(adapter);
                }

                return true;
            }
        });
        return true;
    }

    /* Network */

    private void searchPlaces(final String query){
        HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put("q", query);

        fetchLocationProperties();

        MapManager.getMapPlaces(this, queryParams, new Callback<ArrayList<MITMapPlace>>() {
            @Override
            public void success(ArrayList<MITMapPlace> places, Response response) {
                searchPlaces = places;
                searchAdapter = new FacilitiesSearchAdapter(getApplicationContext(), searchPlaces, query, callback);
                listView.setAdapter(searchAdapter);
                searchAdapter.notifyDataSetChanged();

                fetchLocationProperties();
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
    }

    private void fetchCategories() {
        FacilitiesManager.getLocationCategories(this, new Callback<HashMap<String, FacilitiesCategory>>() {

            @Override
            public void success(HashMap<String, FacilitiesCategory> facilitiesCategories, Response response) {
                categories = facilitiesCategories;

                if (adapter != null) {
                    adapter.updateCategories(new ArrayList<>(facilitiesCategories.values()));
                }
            }

            @Override
            public void failure(RetrofitError error) {
                MitMobileApplication.bus.post(new OttoBusEvent.RetrofitFailureEvent(error));
            }
        });
    }

    private void fetchLocationProperties() {
        propertyOwners = new ArrayList<>();
        FacilitiesManager.getLocationProperties(this, new Callback<FacilitiesPropertyOwnerWrapper>() {
            @Override
            public void success(FacilitiesPropertyOwnerWrapper facilitiesPropertyOwnerWrapper, Response response) {
                propertyOwners = facilitiesPropertyOwnerWrapper.getPropertyOwners();
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
    }

    @Override
    public void fetchPlacesByCategories(String name, HashSet<String> locations) {
        Intent intent = new Intent(this, PlaceActivity.class);
        intent.putExtra(Constants.FACILITIES_CATEGORY_KEY, name);
        if (!name.equals(NEARBY_LOCATIONS)) {
            ArrayList<String> locationList = new ArrayList<>(locations);
            intent.putStringArrayListExtra(Constants.FACILITIES_LOCATIONS_KEY, locationList);
        }
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void fetchPlace(String id, String name, boolean searchMode) {
        Intent result = new Intent();
        result.putExtra(Constants.FACILITIES_LOCATION, name);
        if (getPropertyOwner(id) != null) {
            result.putExtra(Constants.FACILITIES_PROPERTYOWNER, getPropertyOwner(id));
        }
        result.putExtra(Constants.FACILITIES_SEARCH_MODE, searchMode);
        setResult(RESULT_OK, result);
        finish();
    }

    private FacilitiesPropertyOwner getPropertyOwner(String id) {
        FacilitiesPropertyOwner propertyOwner = null;
        for (FacilitiesPropertyOwner owner : propertyOwners) {
            if (owner.getId().equals(id) && owner.getLeased().contains("YES")) {
                propertyOwner = owner;
                break;
            }
        }

        return propertyOwner;
    }
}
