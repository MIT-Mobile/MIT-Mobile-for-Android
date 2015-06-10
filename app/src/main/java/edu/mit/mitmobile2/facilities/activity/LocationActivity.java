package edu.mit.mitmobile2.facilities.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.facilities.adapter.LocationAdapter;

/**
 * Created by serg on 6/9/15.
 */
public class LocationActivity extends AppCompatActivity {

    private ListView listView;

    private LocationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        listView = (ListView) findViewById(R.id.list);

        adapter = new LocationAdapter();
        listView.setAdapter(adapter);
    }
}
