package edu.mit.mitmobile2.facilities.activity;

import android.content.Intent;
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
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.facilities.adapter.RoomsAdapter;
import edu.mit.mitmobile2.facilities.model.FacilitiesRoom;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class RoomDetailActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_detail);

        RoomsAdapter adapter = new RoomsAdapter(this, new ArrayList<FacilitiesRoom>());

        StickyListHeadersListView listView = (StickyListHeadersListView) findViewById(R.id.rooms_list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        View header = View.inflate(this, R.layout.rooms_list_header, null);
        listView.addHeaderView(header);
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
                return performSearch();
            }

            @Override
            public boolean onQueryTextChange(String s) {
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

    private boolean performSearch() {
        //TODO: Query MIT api for building
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
        String problem = (String) parent.getItemAtPosition(position);
        Intent result = new Intent();
        result.putExtra(Constants.FACILITIES_ROOM_NUMBER, problem);
        setResult(RESULT_OK, result);
        finish();
    }
}
