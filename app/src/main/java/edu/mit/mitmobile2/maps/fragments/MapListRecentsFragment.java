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

import java.util.ArrayList;
import java.util.Set;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.PreferenceUtils;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.maps.adapters.MapRecentsAdapter;

public class MapListRecentsFragment extends MapListFragment {

    public MapListRecentsFragment() {
    }

    public static MapListRecentsFragment newInstance() {
        return new MapListRecentsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        Set<String> set = PreferenceUtils.getDefaultSharedPreferencesMultiProcess(getActivity()).getStringSet(Constants.MAPS_SEARCH_HISTORY, null);
        if (set != null) {
            adapter = new MapRecentsAdapter(getActivity(), set);
        } else {
            adapter = new MapRecentsAdapter(getActivity(), new ArrayList<String>());
        }

        listView.setAdapter(adapter);
        if (adapter.getCount() == 0) {
            noResultsView.setVisibility(View.VISIBLE);
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_recents_fragment, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.done_button) {
            getActivity().setResult(Activity.RESULT_CANCELED);
            getActivity().finish();
            return true;
        } else if (item.getItemId() == R.id.clear_button) {
            PreferenceUtils.getDefaultSharedPreferencesMultiProcess(getActivity()).edit().remove(Constants.MAPS_SEARCH_HISTORY).apply();
            ((MapRecentsAdapter) adapter).updateItems(new ArrayList<String>());
            noResultsView.setVisibility(View.VISIBLE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent result = new Intent();
        result.putExtra(Constants.Map.RECENT_QUERY, ((String) adapter.getItem(position)));
        result.putExtra(Constants.Map.TAB_TYPE, 2);
        getActivity().setResult(Activity.RESULT_OK, result);
        getActivity().finish();
    }
}
