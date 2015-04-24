package edu.mit.mitmobile2.news.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.InjectView;
import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MITAPIClient;
import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.OttoBusEvent;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.MITSearchAdapter;
import edu.mit.mitmobile2.news.NewsFragment;
import edu.mit.mitmobile2.news.NewsFragmentCallback;
import edu.mit.mitmobile2.news.activities.NewsCategoryActivity;
import edu.mit.mitmobile2.news.models.MITNewsStory;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SearchFragment extends Fragment implements NewsFragmentCallback {

    @InjectView(R.id.recent_search_listview)
    ListView recentSearchListView;

    @InjectView(R.id.no_results_textview)
    TextView noResultsTextView;

    private static final String NEWS_SEARCH_HISTORY = "newsSearchHistory";
    public static final String SEARCH_RESULT = "searchResult";
    public static final String SEARCH_QUERY = "searchQuery";

    private MITAPIClient apiClient;

    private List<String> recentSearches;
    private SharedPreferences sharedPreferences;
    private MITSearchAdapter mitSearchAdapter;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        this.setHasOptionsMenu(true);
        ButterKnife.inject(this, view);

        apiClient = new MITAPIClient(getActivity());

        recentSearches = new ArrayList<>();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        Set<String> set = sharedPreferences.getStringSet(NEWS_SEARCH_HISTORY, null);
        if (set != null) {
            recentSearches.addAll(set);
            mitSearchAdapter = new MITSearchAdapter(getActivity().getApplicationContext(), recentSearches, this);
            recentSearchListView.setAdapter(mitSearchAdapter);
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search, menu);

        final MenuItem menuItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchView.setQueryHint(getString(R.string.search_hint));
        menuItem.expandActionView();

        MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                NewsFragment newsFragment = new NewsFragment();
                getFragmentManager().beginTransaction().replace(R.id.content_frame, newsFragment).commit();
                return true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getSearchResult(query);
                SharedPreferences.Editor edior = sharedPreferences.edit();
                boolean queryExisted = false;
                for (String s : recentSearches) {
                    if (s.equals(query)) {
                        queryExisted = true;
                        break;
                    }
                }
                if (!queryExisted) {
                    recentSearches.add(query);
                    Set<String> set = new HashSet<String>(recentSearches);
                    edior.putStringSet(NEWS_SEARCH_HISTORY, set);
                    edior.commit();
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                recentSearchListView.setVisibility(View.VISIBLE);
                noResultsTextView.setVisibility(View.GONE);
                mitSearchAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    private void getSearchResult(final String searchText) {
        HashMap<String, String> params = new HashMap<>();
        params.put("q", searchText);
        params.put("limit", String.valueOf(20));

        apiClient.get(Constants.NEWS, Constants.News.STORIES_PATH, null, params, new retrofit.Callback<List<MITNewsStory>>() {
            @Override
            public void success(List<MITNewsStory> mitNewsStories, Response response) {
                if (mitNewsStories.size() > 0) {
                    Intent intent = new Intent(getActivity(), NewsCategoryActivity.class);
                    intent.putParcelableArrayListExtra(Constants.News.STORIES_KEY, (ArrayList<MITNewsStory>) mitNewsStories);
                    intent.putExtra(SEARCH_RESULT, true);
                    intent.putExtra(SEARCH_QUERY, searchText);
                    startActivity(intent);
                } else {
                    noResultsTextView.setVisibility(View.VISIBLE);
                    recentSearchListView.setVisibility(View.GONE);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                MitMobileApplication.bus.post(new OttoBusEvent.RetrofitFailureEvent(error));
            }
        });
    }

    @Override
    public void itemSearch(String searchText) {
        getSearchResult(searchText);
    }

    @Override
    public void itemClicked(MITNewsStory story) {
    }
}
