package edu.mit.mitmobile2.facilities.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.widget.ListView;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.facilities.adapter.LocationAdapter;

public class LocationActivity extends AppCompatActivity {

    private ListView listView;

    private LocationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        listView = (ListView) findViewById(R.id.list);

        adapter = new LocationAdapter(getApplicationContext());
        listView.setAdapter(adapter);

        adapter.updateCategories(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_facility_location, menu);

        return true;
    }
}
