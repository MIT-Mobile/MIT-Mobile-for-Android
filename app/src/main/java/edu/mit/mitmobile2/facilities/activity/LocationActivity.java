package edu.mit.mitmobile2.facilities.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.OttoBusEvent;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.facilities.FacilitiesManager;
import edu.mit.mitmobile2.facilities.adapter.LocationAdapter;
import edu.mit.mitmobile2.facilities.model.FacilitiesCategory;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by serg on 6/9/15.
 */
public class LocationActivity extends AppCompatActivity {

    private ListView listView;

    private LocationAdapter adapter;

    private HashMap<String, FacilitiesCategory> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        listView = (ListView) findViewById(R.id.list);

        adapter = new LocationAdapter(getApplicationContext());
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
}
