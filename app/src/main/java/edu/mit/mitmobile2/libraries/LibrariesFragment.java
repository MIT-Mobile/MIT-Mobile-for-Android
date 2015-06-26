package edu.mit.mitmobile2.libraries;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.OttoBusEvent;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.libraries.activities.AccountActivity;
import edu.mit.mitmobile2.libraries.adapter.LibraryLinksAdapter;
import edu.mit.mitmobile2.libraries.model.MITLibrariesLink;
import edu.mit.mitmobile2.shared.SharedIntentManager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LibrariesFragment extends Fragment {

    private static final String KEY_STATE_LIBRARY_LINKS = "state_library_links";

    @InjectView(R.id.library_links_listview)
    ListView linksListView;

    private List<MITLibrariesLink> links;
    private LibraryLinksAdapter adapter;

    public LibrariesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        links = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_libraries, null);

        ButterKnife.inject(this, view);

        getActivity().setTitle(getString(R.string.title_activity_libraries));

        View headerView = View.inflate(getActivity(), R.layout.libraries_list_header, null);
        TextView linkTitle = (TextView) headerView.findViewById(R.id.link_title);
        ImageView linkIcon = (ImageView) headerView.findViewById(R.id.link_icon);
        View centerView = headerView.findViewById(R.id.root);

        linkTitle.setText(getResources().getString(R.string.your_account));
        linkIcon.setImageResource(R.drawable.ic_lock_outline);
        linkIcon.setColorFilter(getResources().getColor(R.color.mit_grey));

        centerView.setBackgroundResource(R.color.white);

        linksListView.addHeaderView(headerView);

        adapter = new LibraryLinksAdapter(getActivity(), links);
        linksListView.setAdapter(adapter);

        linksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MITLibrariesLink link = adapter.getItem(position - 1);
                if (!TextUtils.isEmpty(link.getUrl())) {
                    getActivity().startActivity(SharedIntentManager.createBrowserIntent(link.getUrl()));
                } else {
                    // TODO: add fragment navigation logic here
                }
            }
        });

        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AccountActivity.class);
                startActivity(intent);
            }
        });

        if (savedInstanceState == null) {
            fetchLinks();
        } else {
            if (savedInstanceState.containsKey(KEY_STATE_LIBRARY_LINKS)) {
                ArrayList<MITLibrariesLink> savedLinks = savedInstanceState.getParcelableArrayList(KEY_STATE_LIBRARY_LINKS);
                refreshLinks(savedLinks);
            }
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(KEY_STATE_LIBRARY_LINKS, (ArrayList<? extends Parcelable>) links);
        super.onSaveInstanceState(outState);
    }

    /* Network */

    private void fetchLinks() {
        LibraryManager.getLinks(getActivity(), new Callback<List<MITLibrariesLink>>() {

            @Override
            public void success(List<MITLibrariesLink> mitLibrariesLink, Response response) {
                refreshLinks(mitLibrariesLink);
            }

            @Override
            public void failure(RetrofitError error) {
                MitMobileApplication.bus.post(new OttoBusEvent.RetrofitFailureEvent(error));
            }
        });
    }

    private void refreshLinks(List<MITLibrariesLink> mitLibrariesLink) {
        links.clear();
        if (mitLibrariesLink != null) {
            // append predefined links here
            String[] predefinedLinks = getResources().getStringArray(R.array.predefined_link_titles);
            for (String title : predefinedLinks) {
                links.add(new MITLibrariesLink(title));
            }
            links.addAll(mitLibrariesLink);
        }

        adapter.notifyDataSetChanged();
    }
}
