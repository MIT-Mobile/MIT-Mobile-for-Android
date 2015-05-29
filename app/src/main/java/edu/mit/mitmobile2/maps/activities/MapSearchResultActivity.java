package edu.mit.mitmobile2.maps.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.maps.adapters.MapSearchResultAdapter;
import edu.mit.mitmobile2.maps.callbacks.SearchResultListCallback;
import edu.mit.mitmobile2.maps.model.MITMapPlace;

public class MapSearchResultActivity extends AppCompatActivity implements SearchResultListCallback {

    @InjectView(R.id.map_search_listview)
    ListView listView;

    @InjectView(R.id.no_results_textview)
    TextView noResultsTextView;

    private MapSearchResultAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_search_result);

        ButterKnife.inject(this);

        List<MITMapPlace> places = getIntent().getParcelableArrayListExtra(Constants.PLACES_KEY);

        if (places.size() > 0) {
            listView.setVisibility(View.VISIBLE);
            adapter = new MapSearchResultAdapter(this, places, this);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    returnToMapView(position);
                }
            });
        } else {
            listView.setVisibility(View.GONE);
            noResultsTextView.setVisibility(View.VISIBLE);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    private void returnToMapView(int position) {
        Intent result = new Intent();
        result.putExtra("position", position);
        setResult(RESULT_OK, result);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map_search_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.done_button) {
            setResult(RESULT_CANCELED);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void goToPlaceDetail(int position) {
        MITMapPlace place = adapter.getItem(position);
        Intent intent = new Intent(this, MapPlaceDetailActivity.class);
        intent.putExtra(Constants.PLACES_KEY, place);
        startActivity(intent);
    }
}
