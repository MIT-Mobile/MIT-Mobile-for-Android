package edu.mit.mitmobile2.maps.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import edu.mit.mitmobile2.R;

public class MapListFragment extends Fragment implements AdapterView.OnItemClickListener {

    @InjectView(R.id.map_item_listview)
    protected ListView listView;

    @InjectView(R.id.no_results_textview)
    protected TextView noResultsView;

    protected BaseAdapter adapter;

    public MapListFragment() {
    }

    public static MapListFragment newInstance() {
        return new MapListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_item_pager, container, false);

        ButterKnife.inject(this, view);

        this.setHasOptionsMenu(true);

        listView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return item.getItemId() == R.id.home || super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }
}
