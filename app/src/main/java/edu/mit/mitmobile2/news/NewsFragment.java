package edu.mit.mitmobile2.news;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
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

public class NewsFragment extends Fragment implements NewsFragmentCallback {

    private SwipeRefreshLayout refreshLayout;
    private StickyListHeadersListView listView;
    private MITNewsStoryAdapter adapter;

    private List<MITNewsStory> stories;
    private List<MITNewsCategory> categories;

    public NewsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_news, null);

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.news_refreshlayout);
        listView = (StickyListHeadersListView) view.findViewById(R.id.news_listview);
        final MITAPIClient mitApiClient = new MITAPIClient(getActivity());

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNewStories(mitApiClient);
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(Constants.News.STORIES_KEY) && savedInstanceState.containsKey(Constants.News.CATEGORIES_KEY)) {
            //noinspection unchecked
            stories = groupStories((List) savedInstanceState.getParcelableArrayList(Constants.News.STORIES_KEY));
            //noinspection unchecked
            categories = (List) savedInstanceState.getParcelableArrayList(Constants.News.CATEGORIES_KEY);
            adapter = new MITNewsStoryAdapter(getActivity(), stories, this);
            adapter.setHeaders(categories);
            listView.setAdapter(adapter);
        } else {
            adapter = new MITNewsStoryAdapter(getActivity(), new ArrayList<MITNewsStory>(), this);

            listView.setAdapter(adapter);
            listView.setOnHeaderClickListener(new StickyListHeadersListView.OnHeaderClickListener() {
                @Override
                public void onHeaderClick(StickyListHeadersListView stickyListHeadersListView, View view, int i, long l, boolean b) {

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

        }
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
        groupedStories.addAll(mediaNews);
        groupedStories.addAll(campusNews);
        return groupedStories;
    }

    public void openWebsiteDialog(final String url) {
        new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.open_in_browser_q))
                .setMessage(url)
                .setPositiveButton(getResources().getString(R.string.open_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    @Override
    public void itemClicked(MITNewsStory story) {
        if (story.getCategory().getId().equals(Constants.News.IN_THE_MEDIA)) {
            openWebsiteDialog(story.getSourceUrl());
        } else {
            // TODO: Go to next screen, passing the Story object in as an extra

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(Constants.News.STORIES_KEY, (ArrayList<? extends Parcelable>) this.stories);
        outState.putParcelableArrayList(Constants.News.CATEGORIES_KEY, (ArrayList<? extends Parcelable>) categories);
    }
}
