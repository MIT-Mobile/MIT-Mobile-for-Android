package edu.mit.mitmobile2.facilities.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.OttoBusEvent;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.facilities.FacilitiesManager;
import edu.mit.mitmobile2.facilities.adapter.LocationAdapter;
import edu.mit.mitmobile2.facilities.callback.LocationCallback;
import edu.mit.mitmobile2.facilities.model.FacilitiesCategory;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LocationActivity extends AppCompatActivity implements LocationCallback {

    public static final String NEARBY_LOCATIONS = "Nearby Locations";

    private ListView listView;

    private LocationAdapter adapter;
    private LocationCallback callback;

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

        fetchCategories();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_facility_location, menu);

        return true;
    }

    /* Network */

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

    @Override
    public void fetchPlacesByCategories(String name, HashSet<String> locations) {
        Intent intent = new Intent(this, PlaceActivity.class);
        intent.putExtra(Constants.FACILITIES_CATEGORY_KEY, name);
        if (!name.equals(NEARBY_LOCATIONS)) {
            ArrayList<String> locationList = new ArrayList<>(locations);
            intent.putStringArrayListExtra(Constants.FACILITIES_LOCATIONS_KEY, locationList);

        }
        startActivity(intent);
    }

    @Override
    public void fetchPlace(String name) {
    }
}
