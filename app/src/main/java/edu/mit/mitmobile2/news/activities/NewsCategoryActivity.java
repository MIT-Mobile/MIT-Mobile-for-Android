package edu.mit.mitmobile2.news.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MITAPIClient;
import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.OttoBusEvent;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.news.NewsFragmentCallback;
import edu.mit.mitmobile2.news.adapters.MITNewsCategoryAdapter;
import edu.mit.mitmobile2.news.fragments.SearchFragment;
import edu.mit.mitmobile2.news.models.MITNewsStory;
import edu.mit.mitmobile2.news.utils.NewsUtils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import edu.mit.mitmobile2.shared.logging.LoggingManager.Timber;

public class NewsCategoryActivity extends ActionBarActivity implements AbsListView.OnScrollListener, NewsFragmentCallback {

    private static final int STORIES_PAGE_SIZE = 20;
    private static final int PAGINATION_THRESHOLD = 5;

    MITNewsCategoryAdapter adapter;
    SwipeRefreshLayout refreshLayout;

    private int maxItemsSeen;
    private int lastPageStart;
    private int prevFirstVisibleItem = -1;
    private boolean endOfList = false;
    private MITAPIClient apiClient;
    private List<MITNewsStory> stories;
    private boolean isFromSearchResult = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_category);

        boolean savedFromInstanceState = savedInstanceState != null && savedInstanceState.containsKey(Constants.News.STORIES_KEY);
        if (savedFromInstanceState) {
            stories = savedInstanceState.getParcelableArrayList(Constants.News.STORIES_KEY);
        } else {
            stories = getIntent().getParcelableArrayListExtra(Constants.News.STORIES_KEY);
            isFromSearchResult = getIntent().getBooleanExtra(SearchFragment.SEARCH_RESULT, false);
        }
        apiClient = new MITAPIClient(this);

        if (!isFromSearchResult) {
            getSupportActionBar().setTitle(stories.get(0).getCategory().getName());
        } else {
            getSupportActionBar().setTitle("");
        }


        ListView storiesListView = (ListView) findViewById(R.id.news_stories_list);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);

        adapter = new MITNewsCategoryAdapter(this, stories, this);
        storiesListView.setOnScrollListener(this);
        storiesListView.setAdapter(adapter);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getStoriesByCategory(apiClient, stories.get(0).getCategory().getId(), 0, STORIES_PAGE_SIZE, true);
            }
        });

        if (!savedFromInstanceState) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setRefreshing(true);
                }
            }, 200);

            getStoriesByCategory(apiClient, stories.get(0).getCategory().getId(), stories.size(), STORIES_PAGE_SIZE, false);
        }
    }

    private void getStoriesByCategory(MITAPIClient mitApiClient, String categoryId, int offset, int limit, final boolean clearPrevious) {
        if (clearPrevious) {
            lastPageStart = 0;
            maxItemsSeen = 0;
            prevFirstVisibleItem = -1;
        } else {
            lastPageStart = offset;
            Timber.d("OFFSET: " + offset + " LIMIT: " + limit);
        }

        HashMap<String, String> queryParams = new HashMap<>();
        if (isFromSearchResult) {
            queryParams.put("q", getIntent().getStringExtra(SearchFragment.SEARCH_QUERY));
            queryParams.put("limit", String.valueOf(limit));
            queryParams.put("offset", String.valueOf(offset));
        } else {
            queryParams.put("category", categoryId);
            queryParams.put("limit", String.valueOf(limit));
            queryParams.put("offset", String.valueOf(offset));
        }

        mitApiClient.get(Constants.NEWS, Constants.News.STORIES_PATH, null, queryParams, new Callback<List<MITNewsStory>>() {
            @Override
            public void success(List<MITNewsStory> stories, Response response) {
                Timber.d("Success!");
                refreshLayout.setRefreshing(false);
                if (clearPrevious) {
                    adapter.updateItems(stories);
                } else {
                    adapter.addItems(stories);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                MitMobileApplication.bus.post(new OttoBusEvent.RetrofitFailureEvent(error));
                refreshLayout.setRefreshing(false);
            }
        });
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
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        //only check if firstVisibleItem changes and we have not received the last items
        int newMax = firstVisibleItem + visibleItemCount - 1;
        if (newMax > maxItemsSeen) {
            maxItemsSeen = newMax;
        }
        if (firstVisibleItem <= prevFirstVisibleItem || endOfList) {
            return;
        }

        prevFirstVisibleItem = firstVisibleItem;
        if ((totalItemCount > 1)
                && (firstVisibleItem + visibleItemCount >= totalItemCount - PAGINATION_THRESHOLD)
                && (!refreshLayout.isRefreshing())) {
            Timber.d("Pagination update" + totalItemCount);
            refreshLayout.setRefreshing(true);

            getStoriesByCategory(apiClient, stories.get(0).getCategory().getId(), lastPageStart + STORIES_PAGE_SIZE, STORIES_PAGE_SIZE, false);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(Constants.News.STORIES_KEY, (ArrayList<MITNewsStory>) adapter.getStories());
    }

    @Override
    public void itemClicked(MITNewsStory story) {
        if (story.getCategory().getId().equals(Constants.News.IN_THE_MEDIA)) {
            NewsUtils.openWebsiteDialog(this, story.getSourceUrl());
        } else {
            Intent intent = new Intent(this, NewsStoryActivity.class);
            intent.putExtra(Constants.News.STORY, story);
            this.startActivity(intent);
        }
    }

    @Override
    public void itemSearch(String searchText) {

    }
}
