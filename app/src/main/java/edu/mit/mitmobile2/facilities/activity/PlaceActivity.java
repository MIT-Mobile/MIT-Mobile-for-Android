package edu.mit.mitmobile2.facilities.activity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MITActivity;
import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.OttoBusEvent;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.facilities.FacilitiesManager;
import edu.mit.mitmobile2.facilities.adapter.PlaceAdapter;
import edu.mit.mitmobile2.facilities.callback.LocationCallback;
import edu.mit.mitmobile2.facilities.model.FacilitiesPropertyOwner;
import edu.mit.mitmobile2.facilities.model.FacilitiesPropertyOwnerWrapper;
import edu.mit.mitmobile2.maps.MapManager;
import edu.mit.mitmobile2.maps.model.MITMapPlace;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PlaceActivity extends MITActivity implements LocationCallback {

    private static final int NEARBY_LOCATION_MAX_COUNT = 10;
    private static final String NEARBY_LOCATIONS = "Nearby Locations";
    private static final String BUILDINGS = "Buildings";
    private static final String RESIDENCE = "Residences";

    @InjectView(R.id.place_list_view)
    ListView placeListView;

    private List<String> locationIdsbyCategory;
    private String name;
    private List<MITMapPlace> allLocations;
    private List<MITMapPlace> places;
    private PlaceAdapter placeAdapter;
    private LocationCallback callback;
    private List<FacilitiesPropertyOwner> propertyOwners;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        ButterKnife.inject(this);

        callback = (LocationCallback) this;
        name = getIntent().getStringExtra(Constants.FACILITIES_CATEGORY_KEY);
        setTitle(name);

        places = new ArrayList<>();
        fetchPlaces();
        placeAdapter = new PlaceAdapter(this, places, callback);
        placeListView.setAdapter(placeAdapter);
    }

    private void fetchPlaces() {
        allLocations = new ArrayList<>();
        MapManager.getMapPlaces(this, new Callback<ArrayList<MITMapPlace>>() {
            @Override
            public void success(ArrayList<MITMapPlace> places, Response response) {
                for (MITMapPlace place : places) {
                    if (name.equals(BUILDINGS) || name.equals(RESIDENCE)) {
                        if (place.getBuildingNumber() == null) {
                            place.parseNumber(place.getName());
                        } else {
                            place.parseNumber(place.getBuildingNumber());
                        }
                    } else {
                        place.parseNumber(place.getName());
                    }
                    allLocations.add(place);
                }

                fetchLocationProperties();
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

                removeHiddenLocations();

                if (name.equals(NEARBY_LOCATIONS)) {
                    updatePlacesByLocation();
                } else {
                    locationIdsbyCategory = getIntent().getStringArrayListExtra(Constants.FACILITIES_LOCATIONS_KEY);
                    updatePlacesByCategory();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                MitMobileApplication.bus.post(new OttoBusEvent.RetrofitFailureEvent(error));
            }
        });
    }

    private void removeHiddenLocations() {
        Iterator<MITMapPlace> iterator = allLocations.iterator();

        while (iterator.hasNext()) {
            MITMapPlace place = iterator.next();
            for (FacilitiesPropertyOwner owner : propertyOwners) {
                if (owner.getId().equals(place.getId()) && owner.getHidden().contains("YES")) {
                    iterator.remove();
                }
            }
        }
    }

    private void updatePlacesByCategory() {
        removeHiddenLocations();

        for (int i = 0; i < locationIdsbyCategory.size(); i++) {
            for (MITMapPlace location : allLocations) {
                if (location.getId().equals(locationIdsbyCategory.get(i))) {
                    places.add(location);
                }
            }
        }

        sortPlaces();
        placeAdapter.notifyDataSetChanged();
    }

    private void updatePlacesByLocation() {
        removeHiddenLocations();

        Collections.sort(allLocations, new Comparator<MITMapPlace>() {
            @Override
            public int compare(MITMapPlace lhs, MITMapPlace rhs) {
                Location placeLocation1 = new Location("place1");
                placeLocation1.setLatitude(lhs.getLatitude());
                placeLocation1.setLongitude(lhs.getLongitude());

                Location placeLocation2 = new Location("place2");
                placeLocation2.setLatitude(rhs.getLatitude());
                placeLocation2.setLongitude(rhs.getLongitude());

                float distance1 = 0;
                float distance2 = 0;

                if (location != null) {
                    distance1 = placeLocation1.distanceTo(location);
                    distance2 = placeLocation2.distanceTo(location);
                }

                return Float.compare(distance1, distance2);
            }
        });

        for (int i = 0; i < NEARBY_LOCATION_MAX_COUNT; i++) {
            places.add(allLocations.get(i));
        }

        placeAdapter.notifyDataSetChanged();
    }

    private void sortPlaces() {
        Collections.sort(places, new Comparator<MITMapPlace>() {
            @Override
            public int compare(MITMapPlace lhs, MITMapPlace rhs) {
                return lhs.numberCompare(rhs);
            }
        });
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

    @Override
    public void fetchPlace(String id, String name) {
        Intent result = new Intent();
        result.putExtra(Constants.FACILITIES_LOCATION, name);
        if (getPropertyOwner(id) != null) {
            result.putExtra(Constants.FACILITIES_PROPERTYOWNER, getPropertyOwner(id));
        }
        setResult(RESULT_OK, result);
        finish();
    }

    @Override
    public void fetchPlacesByCategories(String name, HashSet<String> locations) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_facility_location, menu);
        MenuItem menuItem = menu.findItem(R.id.search);

        if (name.equals(NEARBY_LOCATIONS)) {
            menuItem.setVisible(false);
        } else {
            menuItem.setVisible(true);
        }

        return true;
    }
}
