package edu.mit.mitmobile2.news;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
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
import java.util.List;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MITAPIClient;
import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.OttoBusEvent;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.news.activities.NewsCategoryActivity;
import edu.mit.mitmobile2.news.activities.NewsStoryActivity;
import edu.mit.mitmobile2.news.adapters.MITNewsStoryAdapter;
import edu.mit.mitmobile2.news.models.MITNewsCategory;
import edu.mit.mitmobile2.news.models.MITNewsStory;
import edu.mit.mitmobile2.news.utils.NewsUtils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import edu.mit.mitmobile2.shared.logging.LoggingManager.Timber;

public class NewsFragment extends Fragment implements NewsFragmentCallback {

    private MITAPIClient apiClient;

    private SwipeRefreshLayout refreshLayout;
    private StickyListHeadersListView listView;
    private ListView searchListView;
    private TextView noResultsTextView;

    private MITNewsStoryAdapter adapter;
    private MITNewsStoryAdapter searchAdapter;

    private List<MITNewsStory> stories;
    private List<MITNewsStory> searchStories;
    private List<MITNewsCategory> categories;

    public NewsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_news, null);

        this.setHasOptionsMenu(true);
        apiClient = new MITAPIClient(getActivity());

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.news_refreshlayout);
        listView = (StickyListHeadersListView) view.findViewById(R.id.news_listview);
        searchListView = (ListView) view.findViewById(R.id.search_listview);
        noResultsTextView = (TextView) view.findViewById(R.id.no_results_textview);

        final MITAPIClient mitApiClient = new MITAPIClient(getActivity());

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNewStories(mitApiClient);
            }
        });

        searchStories = new ArrayList<>();
        searchAdapter = new MITNewsStoryAdapter(getActivity(), searchStories, this);
        searchListView.setAdapter(searchAdapter);

        if (savedInstanceState != null && savedInstanceState.containsKey(Constants.News.STORIES_KEY) && savedInstanceState.containsKey(Constants.News.CATEGORIES_KEY)) {
            //noinspection unchecked
            stories = groupStories((List) savedInstanceState.getParcelableArrayList(Constants.News.STORIES_KEY));
            //noinspection unchecked
            categories = (List) savedInstanceState.getParcelableArrayList(Constants.News.CATEGORIES_KEY);
            adapter = new MITNewsStoryAdapter(getActivity(), stories, this);
            adapter.setHeaders(categories);
        } else {
            adapter = new MITNewsStoryAdapter(getActivity(), new ArrayList<MITNewsStory>(), this);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setRefreshing(true);
                }
            }, 500);

            getCategories(mitApiClient);
            getNewStories(mitApiClient);

        }

        listView.setAdapter(adapter);
        listView.setOnHeaderClickListener(new StickyListHeadersListView.OnHeaderClickListener() {
            @Override
            public void onHeaderClick(StickyListHeadersListView stickyListHeadersListView, View view, int i, long l, boolean b) {
                String headerId = adapter.getHeader((int) l);
                Intent intent = new Intent(getActivity(), NewsCategoryActivity.class);
                List<MITNewsStory> storiesByCategory = adapter.getStoriesByCategory(headerId);
                intent.putParcelableArrayListExtra(Constants.News.STORIES_KEY, (ArrayList<MITNewsStory>) storiesByCategory);
                startActivity(intent);
            }
        });

        return view;
    }

    private void getCategories(MITAPIClient mitApiClient) {
        mitApiClient.get(Constants.NEWS, Constants.News.CATEGORIES_PATH, null, null, new Callback<List<MITNewsCategory>>() {
            @Override
            public void success(List<MITNewsCategory> mitNewsCategories, Response response) {
                NewsFragment.this.categories = mitNewsCategories;
                adapter.setHeaders(mitNewsCategories);
            }

            @Override
            public void failure(RetrofitError error) {
                MitMobileApplication.bus.post(new OttoBusEvent.RetrofitFailureEvent(error));
            }
        });
    }

    private void getNewStories(final MITAPIClient mitApiClient) {
        mitApiClient.get(Constants.NEWS, Constants.News.STORIES_PATH, null, null, new Callback<List<MITNewsStory>>() {
            @Override
            public void success(List<MITNewsStory> stories, Response response) {
                Timber.d("Success!");
                NewsFragment.this.stories = stories;
                getMediaStories(mitApiClient);
            }

            @Override
            public void failure(RetrofitError error) {
                MitMobileApplication.bus.post(new OttoBusEvent.RetrofitFailureEvent(error));
            }
        });
    }

    private void getMediaStories(MITAPIClient mitApiClient) {
        HashMap<String, String> queryMap = new HashMap<>();
        queryMap.put("category", Constants.News.IN_THE_MEDIA);
        queryMap.put("limit", "5");
        mitApiClient.get(Constants.NEWS, Constants.News.STORIES_PATH, null, queryMap, new Callback<List<MITNewsStory>>() {
            @Override
            public void success(List<MITNewsStory> stories, Response response) {
                Timber.d("Success!");
                NewsFragment.this.stories.addAll(stories);
                adapter.updateItems(groupStories(NewsFragment.this.stories));
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void failure(RetrofitError error) {
                MitMobileApplication.bus.post(new OttoBusEvent.RetrofitFailureEvent(error));
            }
        });
    }

    private List<MITNewsStory> groupStories(List<MITNewsStory> stories) {
        List<MITNewsStory> mitNews = new ArrayList<>();
        List<MITNewsStory> mediaNews = new ArrayList<>();
        List<MITNewsStory> campusNews = new ArrayList<>();

        for (MITNewsStory story : stories) {
            switch (story.getCategory().getId()) {
                case Constants.News.MIT_NEWS:
                    if (mitNews.size() < 5) {
                        mitNews.add(story);
                    }
                    break;
                case Constants.News.AROUND_CAMPUS:
                    if (campusNews.size() < 5) {
                        campusNews.add(story);
                    }
                    break;
                case Constants.News.IN_THE_MEDIA:
                    if (mediaNews.size() < 5) {
                        mediaNews.add(story);
                    }
                    break;
            }
        }

        List<MITNewsStory> groupedStories = new ArrayList<>();
        groupedStories.addAll(mitNews);
        groupedStories.addAll(campusNews);
        groupedStories.addAll(mediaNews);

        return groupedStories;
    }

    @Override
    public void itemClicked(MITNewsStory story) {
        if (story.getCategory().getId().equals(Constants.News.IN_THE_MEDIA)) {
            NewsUtils.openWebsiteDialog(getActivity(), story.getSourceUrl());
        } else {
            Intent intent = new Intent(this.getActivity(), NewsStoryActivity.class);
            intent.putExtra(Constants.News.STORY, story);
            this.startActivity(intent);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(Constants.News.STORIES_KEY, (ArrayList<? extends Parcelable>) this.stories);
        outState.putParcelableArrayList(Constants.News.CATEGORIES_KEY, (ArrayList<? extends Parcelable>) categories);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem menuItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchView.setQueryHint(getString(R.string.search_hint));

        MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                refreshLayout.setVisibility(View.GONE);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                refreshLayout.setVisibility(View.VISIBLE);
                noResultsTextView.setVisibility(View.GONE);
                searchListView.setVisibility(View.GONE);
                searchStories.clear();
                return true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return performSearch(s);
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    private boolean performSearch(final String searchText) {
        HashMap<String, String> params = new HashMap<>();
        params.put("q", searchText);
        params.put("limit", Integer.toString(10));

        apiClient.get(Constants.NEWS, Constants.News.STORIES_PATH, null, params, new Callback<List<MITNewsStory>>() {
            @Override
            public void success(List<MITNewsStory> mitNewsStories, Response response) {
                if (mitNewsStories.size() > 0) {
                    searchListView.setVisibility(View.VISIBLE);
                    noResultsTextView.setVisibility(View.GONE);
                    searchStories = mitNewsStories;
                    searchAdapter.updateItems(searchStories);
                } else {
                    searchListView.setVisibility(View.GONE);
                    noResultsTextView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
        return true;
    }
}