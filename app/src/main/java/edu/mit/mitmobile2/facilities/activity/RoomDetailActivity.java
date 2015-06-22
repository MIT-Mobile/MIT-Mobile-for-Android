package edu.mit.mitmobile2.facilities.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;

import java.util.ArrayList;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MITMainActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.facilities.adapter.RoomsAdapter;
import edu.mit.mitmobile2.facilities.model.FacilitiesBuilding;
import edu.mit.mitmobile2.maps.MapManager;
import edu.mit.mitmobile2.shared.logging.LoggingManager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class RoomDetailActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String BUILDING = "building";

    private SearchView searchView;
    private FacilitiesBuilding building;
    private RoomsAdapter adapter;
    private StickyListHeadersListView listView;
    private View headerTop;
    private View headerBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_detail);

        setTitle(getString(R.string.where));

        adapter = new RoomsAdapter(this, new ArrayList<FacilitiesBuilding.Floor>());

        listView = (StickyListHeadersListView) findViewById(R.id.rooms_list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listView.setFastScrollEnabled(true);

        headerTop = View.inflate(this, R.layout.room_list_header_top, null);
        headerBottom = View.inflate(this, R.layout.room_list_header_bottom, null);
        listView.addHeaderView(headerTop);
        listView.addHeaderView(headerBottom);

        if (savedInstanceState != null && savedInstanceState.containsKey(BUILDING)) {
            building = savedInstanceState.getParcelable(BUILDING);
            if (building != null) {
                adapter.updateItems(building.getFloors());
            }
        } else {
            MapManager.getBuildingDetails(this, "W20", new Callback<FacilitiesBuilding>() {
                @Override
                public void success(FacilitiesBuilding facilitiesBuilding, Response response) {
                    LoggingManager.Timber.d("Success!");
                    building = facilitiesBuilding;
                    adapter.updateItems(facilitiesBuilding.getFloors());
                }

                @Override
                public void failure(RetrofitError error) {
                    LoggingManager.Timber.e("Building details", error);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_room_detail, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);
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
                    adapter.setSearchMode(true);
                    listView.setFastScrollEnabled(false);
                    listView.removeHeaderView(headerTop);
                    listView.removeHeaderView(headerBottom);
                } else {
                    adapter.setSearchMode(false);
                    listView.setFastScrollEnabled(true);
                    listView.addHeaderView(headerTop);
                    listView.addHeaderView(headerBottom);
                }
                performSearch(s);
                return true;
            }
        });

        MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return true;
            }
        });
        return true;
    }

    private boolean performSearch(String query) {
        adapter.getFilter().filter(query);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String room;
        if (!adapter.isSearchMode()) {
            if (position == 0) {
                room = "Inside";
            } else if (position == 1) {
                room = "Outside";
            } else {
                room = (String) parent.getItemAtPosition(position);
            }
        } else {
            if (position == 0) {
                String rawString = (String) parent.getItemAtPosition(position);
                room = rawString.substring(rawString.indexOf("\"") + 1, rawString.lastIndexOf("\""));
            } else {
                room = (String) parent.getItemAtPosition(position);
            }
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.FACILITIES_ROOM_NUMBER, room);
        editor.commit();
        Intent intent = new Intent(this, MITMainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUILDING, building);
    }
}