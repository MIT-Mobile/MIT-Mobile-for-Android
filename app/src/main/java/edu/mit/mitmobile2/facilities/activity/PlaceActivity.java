package edu.mit.mitmobile2.facilities.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MITActivity;
import edu.mit.mitmobile2.MITMainActivity;
import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.OttoBusEvent;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.facilities.FacilitiesManager;
import edu.mit.mitmobile2.facilities.adapter.PlaceAdapter;
import edu.mit.mitmobile2.facilities.callback.LocationCallback;
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
    private static final String HIDDEN = "hidden=YES";

    @InjectView(R.id.place_list_view)
    ListView placeListView;

    private List<String> locationIdsbyCategory;
    private String name;
    private List<MITMapPlace> allLocations;
    private List<MITMapPlace> places;
    private PlaceAdapter placeAdapter;
    private LocationCallback callback;
    private List<String> hiddenLocationIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        ButterKnife.inject(this);

        callback = (LocationCallback) this;
        name = getIntent().getStringExtra(Constants.Facilities.CATEGORY_NAME);
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
                if (name.equals(NEARBY_LOCATIONS)) {
                    updatePlacesByLocation();
                } else {
                    locationIdsbyCategory = getIntent().getStringArrayListExtra(Constants.Facilities.LOCATIONS);
                    updatePlacesByCategory();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                MitMobileApplication.bus.post(new OttoBusEvent.RetrofitFailureEvent(error));
            }
        });
    }

    private void fetchLocationProperties() {
        hiddenLocationIds = new ArrayList<>();
        FacilitiesManager.getLocationProperties(this, new Callback<HashMap<String, HashMap<String, String>>>() {
            @Override
            public void success(HashMap<String, HashMap<String, String>> stringHashMapHashMap, Response response) {
                Iterator iterator = stringHashMapHashMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry pair = (Map.Entry) iterator.next();
                    if (pair.getValue().toString().contains(HIDDEN)) {
                        hiddenLocationIds.add(pair.getKey().toString());
                    }
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
            for (String s : hiddenLocationIds) {
                if (s.equals(place.getId())) {
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

                if (distance1 > distance2) {
                    return 1;
                } else if (distance1 < distance2) {
                    return -1;
                } else {
                    return 0;
                }
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

    @Override
    public void fetchPlace(String name) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.FACILITIES_LOCATION, name);
        editor.commit();
        Intent intent = new Intent(this, MITMainActivity.class);
        startActivity(intent);
    }

    @Override
    public void fetchPlacesByCategories(String name, HashSet<String> locations) {
    }
}
