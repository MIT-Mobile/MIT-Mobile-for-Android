package edu.mit.mitmobile2.libraries;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.OttoBusEvent;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.libraries.model.MITLibrariesAskUsModel;
import edu.mit.mitmobile2.libraries.model.MITLibrariesLibrary;
import edu.mit.mitmobile2.libraries.model.MITLibrariesLink;
import edu.mit.mitmobile2.libraries.model.MITLibrariesMITIdentity;
import edu.mit.mitmobile2.libraries.model.MITLibrariesUser;
import edu.mit.mitmobile2.libraries.model.MITLibrariesWorldcatItem;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LibrariesFragment extends Fragment {

    public LibrariesFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_libraries, null);
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
