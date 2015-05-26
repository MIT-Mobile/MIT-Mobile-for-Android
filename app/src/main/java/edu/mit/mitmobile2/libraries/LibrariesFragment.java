package edu.mit.mitmobile2.libraries;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.libraries.activities.AccountActivity;
import edu.mit.mitmobile2.libraries.adapter.LibraryLinksAdapter;

public class LibrariesFragment extends Fragment {

    @InjectView(R.id.library_links_listview)
    ListView linksListview;

    public LibrariesFragment() {
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

        linksListview.addHeaderView(headerView);

        LibraryLinksAdapter adapter = new LibraryLinksAdapter(getActivity(), getResources().getStringArray(R.array.link_urls));
        linksListview.setAdapter(adapter);

        linksListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getStringArray(R.array.link_urls)[position - 1]));
                startActivity(intent);
            }
        });

        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AccountActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*
        LibraryManager.getLinks(getActivity(), new Callback<List<MITLibrariesLink>>() {

            @Override
            public void success(List<MITLibrariesLink> mitLibrariesLink, Response response) {
                // TODO: handle data
            }

            @Override
            public void failure(RetrofitError error) {
                MitMobileApplication.bus.post(new OttoBusEvent.RetrofitFailureEvent(error));
            }
        });
        */

        /*
        LibraryManager.getLibraries(getActivity(), new Callback<List<MITLibrariesLibrary>>() {

            @Override
            public void success(List<MITLibrariesLibrary> mitLibrariesLibraries, Response response) {
                // TODO: handle data
            }

            @Override
            public void failure(RetrofitError error) {
                MitMobileApplication.bus.post(new OttoBusEvent.RetrofitFailureEvent(error));
            }
        });
        */

        /*
        // requires authentication
        LibraryManager.getAskUsTopics(getActivity(), new Callback<MITLibrariesAskUsModel>() {
            @Override
            public void success(MITLibrariesAskUsModel mitLibrariesAskUsModel, Response response) {
                // TODO: handle data
                MitMobileApplication.bus.post(new OttoBusEvent.RetrofitFailureEvent(null));
            }

            @Override
            public void failure(RetrofitError error) {
                MitMobileApplication.bus.post(new OttoBusEvent.RetrofitFailureEvent(error));
            }
        });
        */

        /*
        LibraryManager.search(getActivity(), "1", 1, new Callback<List<MITLibrariesWorldcatItem>>() {
            @Override
            public void success(List<MITLibrariesWorldcatItem> mitLibrariesWorldcatItems, Response response) {
                // TODO: handle data
            }

            @Override
            public void failure(RetrofitError error) {
                MitMobileApplication.bus.post(new OttoBusEvent.RetrofitFailureEvent(error));
            }
        });
        */

        /*
        MITLibrariesWorldcatItem item = new MITLibrariesWorldcatItem();
        item.setIdentifier("50553234");
        LibraryManager.getItemDetails(getActivity(), item, new Callback<MITLibrariesWorldcatItem>() {
            @Override
            public void success(MITLibrariesWorldcatItem mitLibrariesWorldcatItem, Response response) {
                // TODO: handle data
            }

            @Override
            public void failure(RetrofitError error) {
                MitMobileApplication.bus.post(new OttoBusEvent.RetrofitFailureEvent(error));
            }
        });
        */

        /*
        // requires authentication
        LibraryManager.getUser(getActivity(), new Callback<MITLibrariesUser>() {
            @Override
            public void success(MITLibrariesUser mitLibrariesUser, Response response) {
                // TODO: handle data
            }

            @Override
            public void failure(RetrofitError error) {
                MitMobileApplication.bus.post(new OttoBusEvent.RetrofitFailureEvent(error));
            }
        });
        */

        /*
        // requires authentication
        LibraryManager.getIdentity(getActivity(), new Callback<MITLibrariesMITIdentity>() {
            @Override
            public void success(MITLibrariesMITIdentity mitLibrariesMITIdentity, Response response) {
                // TODO: handle data
                int i = 0;
                i++;
            }

            @Override
            public void failure(RetrofitError error) {
                MitMobileApplication.bus.post(new OttoBusEvent.RetrofitFailureEvent(error));
            }
        });
        */
    }
}
