package edu.mit.mitmobile2.news;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MITAPIClient;
import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.OttoBusEvent;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.news.adapters.MITNewsStoryAdapter;
import edu.mit.mitmobile2.news.models.MITNewsCategory;
import edu.mit.mitmobile2.news.models.MITNewsStory;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import timber.log.Timber;

public class NewsFragment extends Fragment {

    private SwipeRefreshLayout refreshLayout;
    private StickyListHeadersListView listView;
    private MITNewsStoryAdapter adapter;

    private List<MITNewsStory> stories;

    public NewsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_news, null);

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.news_refreshlayout);
        listView = (StickyListHeadersListView) view.findViewById(R.id.news_listview);

        adapter = new MITNewsStoryAdapter(getActivity(), new ArrayList<MITNewsStory>());

        listView.setAdapter(adapter);
        listView.setOnHeaderClickListener(new StickyListHeadersListView.OnHeaderClickListener() {
            @Override
            public void onHeaderClick(StickyListHeadersListView stickyListHeadersListView, View view, int i, long l, boolean b) {

            }
        });

        final MITAPIClient mitApiClient = new MITAPIClient(getActivity());

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNewStories(mitApiClient);
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        }, 500);

        getCategories(mitApiClient);
        getNewStories(mitApiClient);

        return view;
    }

    private void getCategories(MITAPIClient mitApiClient) {
        mitApiClient.get(Constants.NEWS, Constants.News.CATEGORIES_PATH, null, null, new Callback<List<MITNewsCategory>>() {
            @Override
            public void success(List<MITNewsCategory> mitNewsCategories, Response response) {
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
        groupedStories.addAll(mediaNews);
        groupedStories.addAll(campusNews);
        return groupedStories;
    }
}
