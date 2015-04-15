package edu.mit.mitmobile2.news;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MITAPIClient;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.news.adapters.MITNewsStoryAdapter;
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

        MITAPIClient mitApiClient = new MITAPIClient(getActivity());
        mitApiClient.get(Constants.NEWS, Constants.News.STORIES_PATH, null, null, new Callback<List<MITNewsStory>>() {
            @Override
            public void success(List<MITNewsStory> stories, Response response) {
                Timber.d("Success!");
                //TODO: Group the items by category
                adapter.updateItems(stories);
            }

            @Override
            public void failure(RetrofitError error) {
                Timber.e(error, "Failed");
            }
        });

        return view;
    }
}
