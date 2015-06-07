package edu.mit.mitmobile2.links;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;

import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.OttoBusEvent;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.libraries.model.MITLibrariesLink;
import edu.mit.mitmobile2.links.adapters.LinksAdapter;
import edu.mit.mitmobile2.links.models.MITLink;
import edu.mit.mitmobile2.links.models.MITLinksCategory;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class LinksFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static String KEY_STATE_LINKS = "key_state_links";

    private StickyListHeadersListView listView;
    private SwipeRefreshLayout refreshLayout;

    private LinksAdapter adapter;

    private ArrayList<MITLinksCategory> linkCategories;

    public LinksFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_links, null);

        listView = (StickyListHeadersListView) view.findViewById(R.id.list);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshlayout);

        adapter = new LinksAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        linkCategories = new ArrayList<>();

        if (savedInstanceState == null || !savedInstanceState.containsKey(KEY_STATE_LINKS)) {
            fetchLinks();
        } else {
            linkCategories = savedInstanceState.getParcelableArrayList(KEY_STATE_LINKS);
            adapter.updateLinkCategories(linkCategories);
        }

        return view;
    }

    /* AdapterView.OnItemClickListener */

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MITLink link = (MITLink) adapter.getItem(position);
        if (!TextUtils.isEmpty(link.getUrl())) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link.getUrl()));
            startActivity(intent);
        }
    }

    /* Network */

    private void fetchLinks() {
        refreshLayout.setRefreshing(true);
        LinksManager.getLinks(getActivity(), new Callback<ArrayList<MITLinksCategory>>() {

            @Override
            public void success(ArrayList<MITLinksCategory> mitLinksCategories, Response response) {
                refreshLayout.setRefreshing(false);
                refreshLayout.setEnabled(false);
                linkCategories = mitLinksCategories;

                if (adapter != null) {
                    adapter.updateLinkCategories(mitLinksCategories);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                refreshLayout.setRefreshing(false);
                refreshLayout.setEnabled(false);
                MitMobileApplication.bus.post(new OttoBusEvent.RetrofitFailureEvent(error));
            }
        });
    }

}
