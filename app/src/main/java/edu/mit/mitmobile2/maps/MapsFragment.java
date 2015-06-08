package edu.mit.mitmobile2.maps;

import android.content.Intent;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.DBAdapter;
import edu.mit.mitmobile2.MITSearchAdapter;
import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.OttoBusEvent;
import edu.mit.mitmobile2.PreferenceUtils;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.maps.activities.MapItemPagerActivity;
import edu.mit.mitmobile2.maps.activities.MapPlaceDetailActivity;
import edu.mit.mitmobile2.maps.activities.MapSearchResultActivity;
import edu.mit.mitmobile2.maps.model.MITMapPlace;
import edu.mit.mitmobile2.shared.callback.FullscreenMapCallback;
import edu.mit.mitmobile2.shared.fragment.FullscreenMapFragment;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MapsFragment extends FullscreenMapFragment implements FullscreenMapCallback {

    private static final String MAPS_SEARCH_HISTORY = "mapSearchHistory";

    private ListView recentsListview;
    private Mode mode;
    private MITSearchAdapter<String> recentSearchAdapter;
    private HashSet<String> recentSearches;
    private SharedPreferences sharedPreferences;
    private SearchView searchView;
    private TextView searchTextView;
    private List<MITMapPlace> places;

    public MapsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        mapCallback = this;

        mitMapView.mapBoundsPadding = (int) getActivity().getResources().getDimension(R.dimen.map_bounds_padding);
        places = new ArrayList<>();

        this.setHasOptionsMenu(true);

        sharedPreferences = PreferenceUtils.getDefaultSharedPreferencesMultiProcess(getActivity());

        //noinspection ConstantConditions
        recentsListview = (ListView) view.findViewById(R.id.map_listview);

        recentSearches = new LinkedHashSet<>();
        Set<String> set = sharedPreferences.getStringSet(MAPS_SEARCH_HISTORY, null);

        if (set != null) {
            recentSearches.addAll(set);
        }

        recentSearchAdapter = new MITSearchAdapter<>(getActivity(), recentSearches, new MITSearchAdapter.FragmentCallback<String>() {
            @Override
            public void itemClicked(String item) { /* No-Op */ }

            @Override
            public void itemSearch(String searchText) {
                performSearch(recentsListview, new Pair<>(recentSearchAdapter, this), searchText);
                searchTextView.setText(searchText);
            }
        });

        recentsListview.setAdapter(recentSearchAdapter);

        return view;
    }

    private boolean searchTextChanged(View sender, Object handler, String s) {
        if (!TextUtils.isEmpty(s) && s.length() >= 0) {
            recentsListview.setVisibility(View.VISIBLE);
            recentSearchAdapter.getFilter().filter(s);
        } else {
            recentsListview.setVisibility(View.GONE);
        }
        return false;
    }

    private boolean performSearch(View sender, Object handler, String searchText) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (!recentSearches.contains(searchText)) {
            recentSearches.add(searchText);
            editor.putStringSet(MAPS_SEARCH_HISTORY, recentSearches);
            editor.apply();
            recentSearchAdapter.updateRecents(recentSearches);
        }

        this.setMode(Mode.LIST_BLANK);

        HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put("q", searchText);

        MapManager.getMapPlaces(getActivity(), queryParams, new Callback<ArrayList<MITMapPlace>>() {
            @Override
            public void success(ArrayList<MITMapPlace> mitMapPlaces, Response response) {
                for (MITMapPlace mapPlace : mitMapPlaces) {
                    int i = mitMapPlaces.indexOf(mapPlace);
                    String markerText = (i + 1) < 10 ? "   " + (i + 1) + "   " : "  " + (i + 1) + "  ";
                    mapPlace.setMarkerText(markerText);
                }

                updateMapItems(mitMapPlaces, true, true);
                setMode(Mode.NO_SEARCH);

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                places = mitMapPlaces;
            }

            @Override
            public void failure(RetrofitError error) {
                MitMobileApplication.bus.post(new OttoBusEvent.RetrofitFailureEvent(error));
            }
        });

        return true;
    }

    public void setMode(@NonNull Mode mode) {
        if (this.mode == mode) return;

        this.mode = mode;

        recentsListview.setVisibility(View.GONE);
    }

    @Override
    public View getInfoContents(Marker marker) {
        if (marker.getSnippet() != null) {
            View view = getActivity().getLayoutInflater().inflate(R.layout.mit_map_info_window, null);
            TextView topTextView = (TextView) view.findViewById(R.id.top_textview);
            TextView bottomTextView = (TextView) view.findViewById(R.id.bottom_textview);

            topTextView.setTextSize(16f);
            bottomTextView.setTextSize(14f);

            Gson gson = new Gson();
            MITMapPlace.MITMapPlaceSnippet snippet = gson.fromJson(marker.getSnippet(), MITMapPlace.MITMapPlaceSnippet.class);

            if (!TextUtils.isEmpty(snippet.getBuildingNumber())) {
                topTextView.setText("Building " + snippet.getBuildingNumber());
            } else {
                topTextView.setVisibility(View.GONE);
            }
            bottomTextView.setText(snippet.getName());

            return view;
        } else {
            return null;
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Gson gson = new Gson();

        MITMapPlace.MITMapPlaceSnippet snippet = gson.fromJson(marker.getSnippet(), MITMapPlace.MITMapPlaceSnippet.class);

        Intent intent = new Intent(getActivity(), MapPlaceDetailActivity.class);

        for (MITMapPlace place : places) {
            if (snippet.getId().equals(place.getId())) {
                intent.putExtra(Constants.PLACES_KEY, place);
                startActivity(intent);
                break;
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();

        inflater.inflate(R.menu.menu_search_maps, menu);

        super.onCreateOptionsMenu(menu, inflater);

        MenuItem menuItem = menu.findItem(R.id.search);
        searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return performSearch(searchView, this, s);
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return searchTextChanged(searchView, this, s);
            }
        });

        MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                recentsListview.setVisibility(View.VISIBLE);
                recentSearchAdapter.getFilter().filter("");
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                if (mode != Mode.NO_SEARCH) {
                    setMode(Mode.NO_SEARCH);
                }
                updateMapItems(new ArrayList(), true, true);
                places.clear();
                recentsListview.setVisibility(View.GONE);
                return true;
            }
        });

        if (getActivity().getIntent().getStringExtra(Constants.LOCATION_KEY) != null) {
            String queryText = getActivity().getIntent().getStringExtra(Constants.LOCATION_KEY);

            if (getActivity().getIntent().getBooleanExtra(Constants.LOCATION_SHOULD_SANITIZE_QUERY_KEY, false)) {
                queryText = sanitizeMapSearchString(queryText);
            }

            menuItem.expandActionView();
            searchView.setQuery(queryText, true);
        } else {
            searchView.setQueryHint(getString(R.string.maps_search_hint));
            menuItem.collapseActionView();
        }

        View searchPlate = searchView.findViewById(android.support.v7.appcompat.R.id.search_plate);

        //noinspection ConstantConditions IntelliJ/AndroidStudio incorrectly thinks this can never be null.
        assert searchPlate != null;

        searchTextView = (TextView) searchPlate.findViewById(android.support.v7.appcompat.R.id.search_src_text);

        assert searchTextView != null;

        searchTextView.setTextColor(Color.WHITE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.home) {
            if (this.mode != Mode.NO_SEARCH) {
                this.setMode(Mode.NO_SEARCH);
            }
        } else if (item.getItemId() == R.id.categories) {
            Intent intent = new Intent(getActivity(), MapItemPagerActivity.class);
            startActivityForResult(intent, 200);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mode == null) {
            this.setMode(Mode.getDefault());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        animateFABs();
    }

    @Override
    public void switchViews(boolean toList) {
        Intent intent = new Intent(getActivity(), MapSearchResultActivity.class);
        //noinspection unchecked
        intent.putParcelableArrayListExtra(Constants.PLACES_KEY, (ArrayList) places);
        startActivityForResult(intent, 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 100) {
                int position = data.getIntExtra(Constants.POSITION_KEY, -1);
                selectMarker(position);
                mitMapView.setToDefaultBounds(false, 0);
            } else if (requestCode == 200) {
                int type = data.getIntExtra(Constants.Map.TAB_TYPE, -1);

                switch (type) {
                    case 0: {
                        MITMapPlace place = data.getParcelableExtra(Constants.PLACES_KEY);
                        ArrayList<MITMapPlace> placesExtra = data.getParcelableArrayListExtra(Constants.PLACES_KEY);

                        places.clear();

                        if (place != null) {
                            places.add(place);
                        } else if (placesExtra != null) {
                            places.addAll(placesExtra);
                        }

                        updateMapItems((ArrayList) places, true, true);
                    }
                        break;
                    case 1: {
                        String id = data.getStringExtra(Constants.PLACES_KEY);
                        MITMapPlace place = DBAdapter.getInstance().getBookmark(id);

                        places.clear();
                        places.add(place);

                        updateMapItems((ArrayList) places, true, true);
                    }
                        break;
                    case 2: {
                        String query = data.getStringExtra(Constants.Map.RECENT_QUERY);
                        if (!TextUtils.isEmpty(query)) {
                            performSearch(searchView, this, query);
                        }
                    }
                        break;
                    default:
                }

            }
        }
    }

    public enum Mode {
        /**
         * This is the parimary or default screen that is shown with our quick dial and favorites
         */
        NO_SEARCH(false),
        /**
         * We are showing a list, but it is "blank" as no search has been performed.
         */
        LIST_BLANK(true),
        /**
         * We are showing a list, but it is "blank" as no data was found. (NYI?)
         */
        LIST_NODATA(true),
        /**
         * We are showing a list, with valid data.
         */
        LIST_DATA(true);

        private final boolean listViewVisible;

        Mode(boolean listViewVisible) {
            this.listViewVisible = listViewVisible;
        }

        public boolean isListViewVisible() {
            return listViewVisible;
        }

        public static Mode getDefault() {
            return NO_SEARCH;
        }
    }

    private String sanitizeMapSearchString(String query) {
        String buildingNumber = "";
        String[] roomComponents = query.split("-");
        String firstComponent = (roomComponents != null && roomComponents.length > 0) ? roomComponents[0] : null;
        if (!TextUtils.isEmpty(firstComponent) && firstComponent.length() == 1 && firstComponent.matches("[a-zA-Z]")) {
            // First component is a letter.  Someone probably put N-51 or E-15 instead of N51 or E15
            if (roomComponents.length >= 2) {
                String secondComponent = roomComponents[1];
                if (Integer.getInteger(secondComponent) != null && Integer.getInteger(secondComponent) > 0) {
                    buildingNumber = firstComponent + secondComponent;
                }
            } else {
                buildingNumber = query;
            }
        } else {
            buildingNumber = firstComponent;
        }

        return buildingNumber;
    }
}
