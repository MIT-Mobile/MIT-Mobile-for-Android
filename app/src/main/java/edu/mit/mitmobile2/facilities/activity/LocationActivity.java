package edu.mit.mitmobile2.facilities.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;

import com.google.gson.Gson;

import java.io.IOException;
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
import edu.mit.mitmobile2.shared.logging.LoggingManager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LocationActivity extends AppCompatActivity implements LocationCallback {

    public static final String NEARBY_LOCATIONS = "Nearby Locations";
    private static final int REQUEST_CODE = 5;

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            Intent result = new Intent();
            result.putExtra(Constants.FACILITIES_LOCATION, data.getStringExtra(Constants.FACILITIES_LOCATION));
            result.putExtra(Constants.FACILITIES_PROPERTYOWNER, data.getParcelableExtra(Constants.FACILITIES_PROPERTYOWNER));
            setResult(RESULT_OK, result);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
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
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void fetchPlace(String id, String name) {
    }
}
