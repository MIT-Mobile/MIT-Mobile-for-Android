package edu.mit.mitmobile2.maps.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.DBAdapter;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.maps.adapters.MapBookmarksAdapter;
import edu.mit.mitmobile2.maps.model.MITMapPlace;

public class MapListBookmarkFragment extends MapListFragment {

    public MapListBookmarkFragment() {
    }

    public static MapListFragment newInstance() {
        return new MapListBookmarkFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        adapter = new MapBookmarksAdapter(getActivity(), DBAdapter.getInstance().getBookmarks());
        listView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_bookmark_fragment, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.done_button) {
            return true;
        } else if (item.getItemId() == R.id.edit_button) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent result = new Intent();
        result.putExtra(Constants.PLACES_KEY, ((MITMapPlace) adapter.getItem(position)).getId());
        result.putExtra(Constants.Map.TAB_TYPE, 1);
        getActivity().setResult(Activity.RESULT_OK, result);
        getActivity().finish();
    }
}
